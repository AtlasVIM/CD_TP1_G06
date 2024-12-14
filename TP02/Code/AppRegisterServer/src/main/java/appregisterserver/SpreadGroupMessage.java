package appregisterserver;

import java.util.List;

public class SpreadGroupMessage {

    public List<Process> processes; //Quando typeServer == LEADER
    public List<Server> servers; //Quando typeServer == LEADER
    public SpreadTypeServer typeServer;
    public Server newSvc; //Quando typeServer == SVC


    //quando for newSvc
    public SpreadGroupMessage(String ip, int port) {
        this.typeServer = SpreadTypeServer.SVC;
        this.newSvc = new Server(ip, port);
    }

    public SpreadGroupMessage(boolean sendMessageAsLeader) {
        this.typeServer = SpreadTypeServer.LEADER;
        this.servers = ServerManager.getAllServers();
        this.processes = ProcessManager.getAllProcesses();
    }

    public SpreadTypeServer getTypeServer() {
        return typeServer;
    }
}
