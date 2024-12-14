package svcserver;

import java.util.List;

public class SpreadGroupMessage {

    public List<Process> processes; //Quando typeServer == LEADER
    public List<Server> servers; //Quando typeServer == LEADER
    public SpreadTypeServer typeServer;
    public Server newSvc; //Quando typeServer == SVC
    public Process newProcess; //Quando typeServer == SVC
    public String requestId; //Quando typeServer == WORKER
    public String imageNameMarks; //Quando typeServer == WORKER

    //quando for newSvc
    public SpreadGroupMessage(String ip, int port, long groupMemberId) {
        this.typeServer = SpreadTypeServer.SVC;
        this.newSvc = new Server(ip, port, groupMemberId, false);
    }

    public SpreadGroupMessage(boolean sendMessageAsLeader) {
        this.typeServer = SpreadTypeServer.LEADER;
        this.servers = ServerManager.getAllServers();
        this.processes = ProcessManager.getAllProcesses();
    }

    public SpreadGroupMessage(String id, String imgName) {
        this.newProcess = new Process(id, imgName);
    }

    public SpreadTypeServer getTypeServer() {
        return typeServer;
    }
}
