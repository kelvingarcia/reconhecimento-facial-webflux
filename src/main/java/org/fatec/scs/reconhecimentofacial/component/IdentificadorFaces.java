package org.fatec.scs.reconhecimentofacial.component;

import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.RectVector;
import org.bytedeco.opencv.opencv_core.Size;
import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;
import org.springframework.stereotype.Component;

@Component
public class IdentificadorFaces {
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
}
