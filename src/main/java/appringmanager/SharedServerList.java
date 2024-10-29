package appringmanager;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class SharedServerList {
    private final List<MyServer> servers = new ArrayList<>(); // Lista de servidores

    // Método para adicionar um servidor à lista
    public void addServer(MyServer server) {
        servers.add(server);
    }

    // Método para obter o próximo servidor de forma cíclica
    public MyServer getNextServer(int currentIndex) {

        MyServer server = null;
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
    public MyServer getServerWithLeastClients() {

        return servers.stream()
                .min((server1, server2) -> Integer.compare(server1.getConnectedClients(), server2.getConnectedClients()))
                .orElse(null);
    }
}