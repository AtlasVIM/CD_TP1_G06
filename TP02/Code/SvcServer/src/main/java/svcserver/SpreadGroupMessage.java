package svcserver;

import java.util.List;

public class SpreadGroupMessage {

    public static List<Process> processes; //Quando typeServer == LEADER ou SVC
    public static List<Server> servers; //Quando typeServer == LEADER
    private SpreadTypeServer typeServer;
    private Server newSvc; //Quando typeServer == SVC
    public static String requestId; //Quando typeServer == WORKER
    public static String imageNameMarks; //Quando typeServer == WORKER

    //quando for newSvc
    public SpreadGroupMessage(String ip, int port) {
        this.typeServer = SpreadTypeServer.SVC;
        this.newSvc = new Server(ip, port);
    }
}
