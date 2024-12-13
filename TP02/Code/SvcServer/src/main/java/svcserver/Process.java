package svcserver;

import java.io.ByteArrayOutputStream;

public class Process {
    private String id;
    private String imageName;
    private String imageNameMarks;
    private transient ByteArrayOutputStream uploadRequestObject; //tansient ignorar serializacao para o gson
    private ProcessStatus status;
    private int chunkIndex;
    private int totalChunks;

    public int getChunkIndex() {
        return chunkIndex;
    }

    public int getTotalChunks() {
        return totalChunks;
    }

    public void setStatus(ProcessStatus status) {
        this.status = status;
    }

    public void setChunkIndex(int chunkIndex) {
        this.chunkIndex = chunkIndex;
    }

    public void setTotalChunks(int totalChunks) {
        this.totalChunks = totalChunks;
    }

    public Process(String id, int totalChunks){
        this.id = id;
        status = ProcessStatus.RECEIVING;
        this.totalChunks = totalChunks;
        uploadRequestObject = new ByteArrayOutputStream();
    }

    public String getId(){
        return id;
    }

    public ProcessStatus getStatus(){
        return status;
    }

    public void setImageName(String imageName){
        this.imageName = imageName;
    }

    public String getImageName(){
        return imageName;
    }

    public void setUploadRequestObject(ByteArrayOutputStream uploadRequestObject){
        this.uploadRequestObject = uploadRequestObject;
    }

    public ByteArrayOutputStream getUploadRequestObject(){
        return uploadRequestObject;
    }

    public String getImageNameMarks() {
        return imageNameMarks;
    }

    public void setImageNameMarks(String imageNameMarks) {
        this.imageNameMarks = imageNameMarks;
    }
}
