package svcserver;

public class Server {
    private String ip;
    private int port;
    private long groupMemberId;
    private boolean isGroupLeader;

    public Server(String ip, int port, long groupMemberId, boolean isGroupLeader) {
        this.ip = ip;
        this.port = port;
        this.groupMemberId = groupMemberId;
        this.isGroupLeader = isGroupLeader;
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

    public long getGroupMemberId() {
        return groupMemberId;
    }

    public void setGroupMemberId(long groupMemberId) {
        this.groupMemberId = groupMemberId;
    }

    public boolean isGroupLeader() {
        return isGroupLeader;
    }

    public void setGroupLeader(boolean groupLeader) {
        isGroupLeader = groupLeader;
    }

    @Override
    public String toString() {
        return "Server{" +
                "ip='" + ip + '\'' +
                ", port=" + port +
                ", groupMemberId=" + groupMemberId +
                '}';
    }
}
