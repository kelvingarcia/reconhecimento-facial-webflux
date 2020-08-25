package org.fatec.scs.reconhecimentofacial.dto.faces;

import org.bytedeco.opencv.opencv_core.Mat;

public record FaceCapturada(Mat face, int classe) {
}
