package org.fatec.scs.reconhecimentofacial.dto.request;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

public class ReconheceRequest{
    private byte[] video;

    public byte[] getVideo() {
        return video;
    }

    public void setVideo(byte[] video) {
        this.video = video;
    }
}
