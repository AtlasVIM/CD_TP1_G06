package appregisterserver;

import java.util.ArrayList;
import java.util.List;

public class ServerManager {
    private final List<Server> servers = new ArrayList<>(); // Lista de servidores

    // Método para adicionar um servidor à lista
    public void addServer(Server server) {
        servers.add(server);
    }

    public void removeServer(int index){
        servers.remove(index);
    }

    // Método para obter o próximo servidor de forma cíclica
    public Server getNextServer(int currentIndex) {

        Server server = null;
        // Ultimo da lista tem como proximo o primeiro da lista
        if (currentIndex == getServerCount() - 1){
            server = servers.get(0);
        } else {
            server = servers.get(currentIndex + 1);
        }
        return server;
    }

    // Método para obter o número de servidores registrados
    public int getServerCount() {
        return servers.size();
    }


    // Método para obter o servidor com a menor contagem de clientes
    public Server getServerWithLeastClients() {

        return servers.stream()
                .min((server1, server2) -> Integer.compare(server1.getConnectedClients(), server2.getConnectedClients()))
                .orElse(null);
    }
}
