package appregisterserver;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ServerManager {
    private static final List<Server> localServersList = new ArrayList<>(); // Lista de servidores
    private static final ConcurrentHashMap<Long, Server> servers = new ConcurrentHashMap<>(); //Gerenciar a concorrencia

    // Método para adicionar um servidor à lista
    public void addServer(Server server) {
        localServersList.add(server);
    }

    public void removeServer(int index){
        localServersList.remove(index);
    }

    // Método para obter o próximo servidor de forma cíclica
    public Server getNextServer(int currentIndex) {

        Server server = null;
        // Ultimo da lista tem como proximo o primeiro da lista
        if (currentIndex == getServerCount() - 1){
            server = localServersList.get(0);
        } else {
            server = localServersList.get(currentIndex + 1);
        }
        return server;
    }

    // Método para obter o número de servidores registrados
    public int getServerCount() {
        return localServersList.size();
    }


    // Método para obter o servidor com a menor contagem de clientes
    public Server getServerWithLeastClients() {

        return localServersList.stream()
                .min((server1, server2) -> Integer.compare(server1.getConnectedClients(), server2.getConnectedClients()))
                .orElse(null);
    }

    public static List<Server> getAllServers() {
        List<Server> serverList = new ArrayList<>();
        serverList.addAll(servers.values());
        return serverList;
    }
}
