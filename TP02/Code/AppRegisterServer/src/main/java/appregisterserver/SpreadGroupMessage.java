package appregisterserver;

import java.util.List;

public class SpreadGroupMessage {

    public List<Server> servers; //Quando typeServer == LEADER
    public SpreadTypeServer typeServer;



    public SpreadTypeServer getTypeServer() {
        return typeServer;
    }
}
