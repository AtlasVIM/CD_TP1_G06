import java.util.List;

public class SpreadGroupMessage {

    public SpreadTypeServer typeServer;
    public String requestId; //Quando typeServer == WORKER
    public String imageNameMarks; //Quando typeServer == WORKER

    //quando for newSvc
    public SpreadGroupMessage(String requestId, String imageNameMarks) {
        this.typeServer = SpreadTypeServer.WORKER;
        this.imageNameMarks = imageNameMarks;
    }


}
