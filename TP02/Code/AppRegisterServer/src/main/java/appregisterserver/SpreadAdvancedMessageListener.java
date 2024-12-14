package appregisterserver;

import spread.*;

import java.util.List;

public class SpreadAdvancedMessageListener implements AdvancedMessageListener {
    private final SpreadConnection connection;
    private final ServerManager serverManager;

    public SpreadAdvancedMessageListener(SpreadConnection connection, ServerManager serverManager) {
        this.connection = connection;
        this.serverManager = serverManager;
    }

    @Override
    public void regularMessageReceived(SpreadMessage spreadMessage) {
            // Converte a mensagem recebida para o tipo SpreadGroupMessage
            var message = AppRegisterServer.spreadManager.convertBytesToSpreadGroupMessage(spreadMessage.getData());
            // Processa a mensagem dependendo do tipo de servidor (LEADER ou SVC)
            if (message.getTypeServer() == SpreadTypeServer.LEADER) {
                // Atualiza a lista de servidores e processos
                List<Server> servers = message.servers;
                updateServers(servers);

            } else if (message.getTypeServer() == SpreadTypeServer.SVC) {
                // A mensagem é do tipo SVC (servidor específico)
                // Mensagem para adicionar um novo servidor SVC
                Server newSvc = message.newSvc;
                System.out.println("New SVCServer connected: " + newSvc);
            }
    }

    private void updateServers(List<Server> servers) {
            for (Server server : servers) {
                if (!serverManager.getAllServers().contains(server)) {
                    serverManager.addServer(server);
                }
            }
    }

    @Override
    public void membershipMessageReceived(SpreadMessage spreadMessage) {

    }

}
