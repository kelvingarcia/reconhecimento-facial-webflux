package org.fatec.scs.reconhecimentofacial.component;

import static org.bytedeco.opencv.global.opencv_core.CV_32SC1;
import static org.bytedeco.opencv.global.opencv_imgcodecs.imwrite;
import static org.bytedeco.opencv.global.opencv_imgproc.COLOR_BGRA2GRAY;
import static org.bytedeco.opencv.global.opencv_imgproc.cvtColor;
import static org.bytedeco.opencv.global.opencv_imgproc.getRotationMatrix2D;
import static org.bytedeco.opencv.global.opencv_imgproc.resize;
import static org.bytedeco.opencv.global.opencv_imgproc.warpAffine;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.IntBuffer;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.bytedeco.javacpp.DoublePointer;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.MatVector;
import org.bytedeco.opencv.opencv_core.Point2f;
import org.bytedeco.opencv.opencv_core.Rect;
import org.bytedeco.opencv.opencv_core.Size;
import org.bytedeco.opencv.opencv_face.FaceRecognizer;
import org.bytedeco.opencv.opencv_face.LBPHFaceRecognizer;
import org.fatec.scs.reconhecimentofacial.dto.auxiliar.PredicaoConfianca;
import org.fatec.scs.reconhecimentofacial.dto.auxiliar.Reconhecimento;
import org.fatec.scs.reconhecimentofacial.dto.enums.StatusReconhecimento;
import org.fatec.scs.reconhecimentofacial.dto.faces.FaceCapturada;
import org.fatec.scs.reconhecimentofacial.dto.response.PessoaDTO;
import org.fatec.scs.reconhecimentofacial.model.Pessoa;
import org.fatec.scs.reconhecimentofacial.repository.PessoaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.mongodb.core.ChangeStreamEvent;
import org.springframework.data.mongodb.core.ChangeStreamOptions;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class ReconhecedorFacial {
    private static final Logger logger = LoggerFactory.getLogger(ReconhecedorFacial.class);

    @Autowired
    private ReactiveMongoTemplate mongoTemplate;

    @Autowired
    private IdentificadorFaces identificadorFaces;

    @Autowired
    private PessoaRepository pessoaRepository;

    private FaceRecognizer reconhecedor;
    private AtomicInteger proximaClasse;

    public ReconhecedorFacial() {
        this.reconhecedor = LBPHFaceRecognizer.create();
    }

    @EventListener(ApplicationReadyEvent.class)
    public void escutaBanco() {
        Flux<Pessoa> pessoaFlux = this.pessoaRepository.findAll();
        pessoaFlux.subscribe(pessoa -> System.out.println(pessoa.getId() + ", " + pessoa.getNome()));
        pessoaFlux.count().subscribe(valor -> this.proximaClasse = new AtomicInteger(Math.toIntExact(valor) + 1));
        this.treinaReconhecedor(pessoaFlux);
        mongoTemplate
                .changeStream("reconhecimentofacial",
                        "pessoa", ChangeStreamOptions.empty(), Pessoa.class)
                .map(ChangeStreamEvent::getBody)
                .doOnNext(pessoa -> this.treinaReconhecedor(this.pessoaRepository.findAll()))
                .doOnError(pessoa -> this.treinaReconhecedor(this.pessoaRepository.findAll()))
                .subscribe();
    }

    @SuppressWarnings("resource")
    private void treinaReconhecedor(Flux<Pessoa> pessoasFlux) {
        List<FaceCapturada> listaFacesCapturadas = new ArrayList<>();
        pessoasFlux
                .doAfterTerminate(() -> {
                    try {
                        var fotos = new MatVector(listaFacesCapturadas.size());
                        var rotulos = new Mat(listaFacesCapturadas.size(), 1, CV_32SC1);
                        IntBuffer rotulosBuffer = rotulos.createBuffer();
                        AtomicInteger contador = new AtomicInteger();
                        listaFacesCapturadas.forEach(faceCapturada -> {
                            resize(faceCapturada.face(), faceCapturada.face(), new Size(160, 160));
                            fotos.put(contador.get(), faceCapturada.face());
                            rotulosBuffer.put(contador.getAndIncrement(), faceCapturada.classe());
                        });
                        this.reconhecedor.train(fotos, rotulos);
                        logger.info("Finalizou treinamento");
                    } catch (Exception e) {
                        logger.error("Banco de dados vazio, ignorando treinamento: ", e.getMessage());
                    }
                })
                .subscribe(pessoa -> {
                    try {
                        var converteMat = new OpenCVFrameConverter.ToMat();
                        var frameGrabber = new FFmpegFrameGrabber(new ByteArrayInputStream(pessoa.getVideo()));
                        frameGrabber.start();

                        Frame frameCapturado = null;
                        var imagemColorida = new Mat();
                        var imagemCinza = new Mat();
                        var imagemRotacionada = new Mat();
                        for (int frameCount = 0; frameCount < 375; frameCount++) {
                            frameCapturado = frameGrabber.grab();
                            imagemColorida = converteMat.convert(frameCapturado);
                            if (imagemColorida != null) {
                                if (pessoa.isMobile()) {

                                    var rawCenter = new Point2f(imagemColorida.cols() / 2.0F, imagemColorida.rows() / 2.0F);

                                    double scale = 1.0;
                                    int rotation = 90;

                                    var rotationMatrix = getRotationMatrix2D(rawCenter, rotation, scale);

                                    warpAffine(imagemColorida, imagemRotacionada, rotationMatrix, imagemColorida.size());
                                } else {
                                    imagemRotacionada = imagemColorida;
                                }
                                cvtColor(imagemRotacionada, imagemCinza, COLOR_BGRA2GRAY);
                                var facesDetectadas = this.identificadorFaces.getFacesDetectadas(imagemCinza);
                                for (int i = 0; i < facesDetectadas.size(); i++) {
                                    Rect dadosFace = facesDetectadas.get(i);
                                    var faceCapturadaMat = new Mat(imagemCinza, dadosFace);
                                    resize(faceCapturadaMat, faceCapturadaMat, new Size(160, 160));
                                    listaFacesCapturadas.add(new FaceCapturada(faceCapturadaMat, pessoa.getClasse()));
                                }
                            }
                        }

                        frameGrabber.stop();
                        logger.info("Finalizou a extração");
                        System.out.println("Quantidade de faces: " + listaFacesCapturadas.size());
                    } catch (FrameGrabber.Exception e) {
                        e.printStackTrace();
                        logger.error("erro ao extrair fotos de vídeo", e);
                    }
                });
    }

    public Mono<Reconhecimento> reconhecePessoa(byte[] video){
        Reconhecimento reconhecimento = new Reconhecimento();
        return this.predicaoVideo(video)
                .flatMap(predicaoConfianca -> {
                    reconhecimento.setPredicaoConfianca(predicaoConfianca);
                    return pessoaRepository.findByClasse(predicaoConfianca.predicao());
                })
                .map(pessoa -> {
                    reconhecimento.setPessoa(new PessoaDTO(pessoa.getId(), pessoa.getNome(), pessoa.getEmail(), pessoa.getClasse()));
                    reconhecimento.setStatusReconhecimento(StatusReconhecimento.RECONHECIDO_CORRETAMENTE);
                    return reconhecimento;
                });
    }

    private Mono<PredicaoConfianca> predicaoVideo(byte[] video) {
        try {
            Integer predicaoClasse = 0;
            List<Integer> predicaoList = new ArrayList<>();
            var converteMat = new OpenCVFrameConverter.ToMat();
            var frameGrabber = new FFmpegFrameGrabber(new ByteArrayInputStream(video));
            frameGrabber.start();

            Frame frameCapturado = null;
            var imagemColorida = new Mat();
            var imagemCinza = new Mat();
            var imagemRotacionada = new Mat();

            IntPointer rotulo = new IntPointer(1);
            DoublePointer confiancaPointer = new DoublePointer(1);
            for (int frameCount = 0; frameCount < 125; frameCount++) {
                frameCapturado = frameGrabber.grab();
                imagemColorida = converteMat.convert(frameCapturado);
                if (imagemColorida != null) {
                    imwrite("src\\main\\resources\\fotos\\frame" + frameCount + ".jpg", imagemColorida);

                    var rawCenter = new Point2f(imagemColorida.cols() / 2.0F, imagemColorida.rows() / 2.0F);

                    double scale = 1.0;
                    int rotation = 90;

                    var rotationMatrix = getRotationMatrix2D(rawCenter, rotation, scale);

                    warpAffine(imagemColorida, imagemRotacionada, rotationMatrix, imagemColorida.size());

                    cvtColor(imagemRotacionada, imagemCinza, COLOR_BGRA2GRAY);
                    var facesDetectadas = this.identificadorFaces.getFacesDetectadas(imagemCinza);
                    for (int i = 0; i < facesDetectadas.size(); i++) {
                        Rect dadosFace = facesDetectadas.get(i);
                        var faceCapturadaMat = new Mat(imagemCinza, dadosFace);
                        resize(faceCapturadaMat, faceCapturadaMat, new Size(160, 160));
                        reconhecedor.predict(faceCapturadaMat, rotulo, confiancaPointer);
                        int predicao = rotulo.get(0);
                        if(confiancaPointer.get() < 90.0)
                            predicaoList.add(predicao);
                    }
                }
            }
            frameGrabber.stop();

            Optional<Map.Entry<Integer, Long>> maisFrequenteOpt = predicaoList.stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                    .entrySet()
                    .stream()
                    .max(Comparator.comparing(Map.Entry::getValue));
            Double confianca = 0.0;
            if(maisFrequenteOpt.isPresent()) {
                Map.Entry<Integer, Long> maisFrequente = maisFrequenteOpt.get();
                System.out.println(maisFrequente);
                confianca = ((double) maisFrequente.getValue() * 100.0) / ((double) predicaoList.size());
                if (maisFrequente.getValue() >= predicaoList.size() * 0.8) {
                    predicaoClasse = maisFrequente.getKey();
                }

                logger.info("Finalizou o reconhecimento");
                return Mono.just(new PredicaoConfianca(predicaoClasse, confianca, predicaoList.size(), maisFrequente.getValue()));
            } else {
                return Mono.empty();
            }
        } catch (FrameGrabber.Exception e) {
            e.printStackTrace();
            logger.error("erro ao extrair fotos de vídeo", e);
            return Mono.empty();
        }
    }

//    public PredicaoConfianca reconhecePessoaFoto(byte[] fotoPessoa) {
//        OpenCVFrameConverter.ToIplImage cv = new OpenCVFrameConverter.ToIplImage();
//        Java2DFrameConverter jcv = new Java2DFrameConverter();
//        BufferedImage fotoConvertida = null;
//        try {
//            fotoConvertida = ImageIO.read(new ByteArrayInputStream(fotoPessoa));
//        } catch (IOException e) {
//            logger.error("Erro na conversão da imagem", e.getMessage());
//        }
//        if (fotoConvertida != null) {
//            Mat foto = cv.convertToMat(jcv.convert(fotoConvertida));
//            var imagemCinza = new Mat();
//            cvtColor(foto, imagemCinza, COLOR_BGRA2GRAY);
//            var facesDetectadas = this.identificadorFaces.getFacesDetectadas(imagemCinza);
//            Rect dadosFace = facesDetectadas.get(0);
//            var faceCapturadaMat = new Mat(imagemCinza, dadosFace);
//            resize(faceCapturadaMat, faceCapturadaMat, new Size(160, 160));
//
//            IntPointer rotulo = new IntPointer(1);
//            DoublePointer confianca = new DoublePointer(1);
//            resize(foto, foto, new Size(160, 160));
//            reconhecedor.predict(faceCapturadaMat, rotulo, confianca);
//            int predicao = rotulo.get(0);
//
//            return new PredicaoConfianca(predicao, confianca.get());
//        } else {
//            return new PredicaoConfianca(0, 0.0);
//        }
//    }

    public FaceRecognizer getReconhecedor() {
        return reconhecedor;
    }

    public AtomicInteger getProximaClasse() {
        return proximaClasse;
    }

}

