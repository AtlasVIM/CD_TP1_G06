package appregisterserver;

import spread.*;

import java.util.List;

public class SpreadAdvancedMessageListener implements AdvancedMessageListener {
    private final SpreadConnection connection;

    public SpreadAdvancedMessageListener(SpreadConnection connection) {
        this.connection = connection;
    }

    @Override
    public void regularMessageReceived(SpreadMessage spreadMessage) {

            var message = AppRegisterServer.spreadManager.convertBytesToSpreadGroupMessage(spreadMessage.getData());
            if (message.getTypeServer() == SpreadTypeServer.LEADER) {
                //List<Server> servers = message.servers;
                ServerManager.updateServers(message.servers);
                System.out.println("Message Received from LEADER! "+message.servers.size());
                System.out.println("There are "+ServerManager.getAllServers().size()+" servers in the system!");
            }
    }

    @Override
    public void membershipMessageReceived(SpreadMessage spreadMessage) {

    }

}
