package org.fatec.scs.reconhecimentofacial.component;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;
import org.fatec.scs.reconhecimentofacial.dto.faces.FaceCapturada;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;

import static org.bytedeco.opencv.global.opencv_imgproc.*;

@Component
public class IdentificadorFaces {
    private static final Logger logger = LoggerFactory.getLogger(IdentificadorFaces.class);

    private CascadeClassifier identificador;

    public IdentificadorFaces() {
        this.identificador = new CascadeClassifier("src\\main\\resources\\classificador\\haarcascade-frontalface-alt.xml");
    }

    public CascadeClassifier getIdentificador() {
        return identificador;
    }

    public void setIdentificador(CascadeClassifier identificador) {
        this.identificador = identificador;
    }

    public RectVector getFacesDetectadas(Mat imagemCinza){
        var facesDetectadas = new RectVector();
        this.identificador.detectMultiScale(imagemCinza, facesDetectadas, 1.1, 1, 0, new Size(150, 150), new Size(500, 500));
        return facesDetectadas;
    }

    public void quantidadeDeFacesMobile(byte[] video){
        Integer quantidadeFaces = 0;
        try {
            var converteMat = new OpenCVFrameConverter.ToMat();
            var frameGrabber = new FFmpegFrameGrabber(new ByteArrayInputStream(video));
            frameGrabber.start();

            Frame frameCapturado = null;
            var imagemColorida = new Mat();
            int lengthInVideoFrames = frameGrabber.getLengthInVideoFrames();
            for (int frameCount = 0; frameCount < lengthInVideoFrames; frameCount++) {
                frameCapturado = frameGrabber.grab();
                imagemColorida = converteMat.convert(frameCapturado);
                var imagemCinza = new Mat();
                if (imagemColorida != null) {
                    var imagemRotacionada = new Mat();

                    var rawCenter = new Point2f(imagemColorida.cols() / 2.0F, imagemColorida.rows() / 2.0F);

                    double scale = 1.0;
                    int rotation = 90;

                    var rotationMatrix = getRotationMatrix2D(rawCenter, rotation, scale);

                    warpAffine(imagemColorida, imagemRotacionada, rotationMatrix, imagemColorida.size());
                    cvtColor(imagemRotacionada, imagemCinza, COLOR_BGRA2GRAY);
                    var facesDetectadas = this.getFacesDetectadas(imagemCinza);
                    for (int i = 0; i < facesDetectadas.size(); i++) {
                        Rect dadosFace = facesDetectadas.get(i);
                        var faceCapturadaMat = new Mat(imagemCinza, dadosFace);
                        resize(faceCapturadaMat, faceCapturadaMat, new Size(160, 160));
                        quantidadeFaces++;
                    }
                }
            }
            frameGrabber.stop();
            System.out.println("Quantidade de faces: " + quantidadeFaces);
        } catch (FrameGrabber.Exception e) {
            e.printStackTrace();
            logger.error("erro ao extrair fotos de vídeo", e);
        }
    }

    public void quantidadeDeFaces(byte[] video){
        Integer quantidadeFaces = 0;
        try {
            var converteMat = new OpenCVFrameConverter.ToMat();
            var frameGrabber = new FFmpegFrameGrabber(new ByteArrayInputStream(video));
            frameGrabber.start();

            Frame frameCapturado = null;
            var imagemColorida = new Mat();
            int lengthInVideoFrames = frameGrabber.getLengthInVideoFrames();
            for (int frameCount = 0; frameCount < lengthInVideoFrames; frameCount++) {
                frameCapturado = frameGrabber.grab();
                imagemColorida = converteMat.convert(frameCapturado);
                var imagemCinza = new Mat();
                if (imagemColorida != null) {
//                    var imagemRotacionada = new Mat();
//
//                    var rawCenter = new Point2f(imagemColorida.cols() / 2.0F, imagemColorida.rows() / 2.0F);
//
//                    double scale = 1.0;
//                    int rotation = 90;
//
//                    var rotationMatrix = getRotationMatrix2D(rawCenter, rotation, scale);
//
//                    warpAffine(imagemColorida, imagemRotacionada, rotationMatrix, imagemColorida.size());
                    cvtColor(imagemColorida, imagemCinza, COLOR_BGRA2GRAY);
                    var facesDetectadas = this.getFacesDetectadas(imagemCinza);
                    for (int i = 0; i < facesDetectadas.size(); i++) {
                        Rect dadosFace = facesDetectadas.get(i);
                        var faceCapturadaMat = new Mat(imagemCinza, dadosFace);
                        resize(faceCapturadaMat, faceCapturadaMat, new Size(160, 160));
                        quantidadeFaces++;
                    }
                }
            }
            frameGrabber.stop();
            System.out.println("Quantidade de faces: " + quantidadeFaces);
        } catch (FrameGrabber.Exception e) {
            e.printStackTrace();
            logger.error("erro ao extrair fotos de vídeo", e);
        }
    }
}
