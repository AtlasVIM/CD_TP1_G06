package appringmanager;

public class MyServer {

    private String ip;
    private int port;
    private int connectedClients;

    public MyServer(String ip, int port) {
        this.ip = ip;
        this.port = port;
        this.connectedClients = 0;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getConnectedClients() {
        return connectedClients;
    }

    public void setConnectedClients(int connectedClients) {
        this.connectedClients = connectedClients;
    }

    public void incrementClients() {
        this.connectedClients++;
    }

    public void decrementClients() {
        if (this.connectedClients > 0) {
            this.connectedClients--;
        }
    }

    @Override
    public String toString() {
        return "MyServer{" +
                "ip='" + ip + '\'' +
                ", port=" + port +
                ", connectedClients=" + connectedClients +
                '}';
    }
}
