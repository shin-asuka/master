package com.vipkid.background.api.sterling.dto;

import java.io.Serializable;

/**
 * Created by luning on 2017/3/22.
 */
public class BackgroundFileStatusDto implements Serializable {

    private static final long serialVersionUID = -2683350126316117645L;

    private String nationality;

    private String fileStatus;

    private String fileResult;

    private boolean hasFile;

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getFileStatus() {
        return fileStatus;
    }

    public void setFileStatus(String fileStatus) {
        this.fileStatus = fileStatus;
    }

    public String getFileResult() {
        return fileResult;
    }

    public void setFileResult(String fileResult) {
        this.fileResult = fileResult;
    }

    public boolean isHasFile() {
        return hasFile;
    }

    public void setHasFile(boolean hasFile) {
        this.hasFile = hasFile;
    }
}
