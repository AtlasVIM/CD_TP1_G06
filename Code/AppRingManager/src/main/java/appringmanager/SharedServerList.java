package appringmanager;

import sharedstubs.PrimeServerAddress;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class SharedServerList {
    private final CopyOnWriteArrayList<PrimeServerAddress> servers = new CopyOnWriteArrayList<>(); // Lista de servidores
    private final ConcurrentHashMap<PrimeServerAddress, Integer> clientCountMap = new ConcurrentHashMap<>(); // Mapeia servidores à contagem de clientes

    // Método para adicionar um servidor à lista
    public void addServer(PrimeServerAddress server) {
        servers.add(server);
        clientCountMap.put(server, 0); // Inicializa a contagem de clientes para o novo servidor
    }

    // Método para obter o próximo servidor de forma cíclica
    public PrimeServerAddress getNextServer(int currentIndex) {

        PrimeServerAddress server = null;
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

    // Método para incrementar a contagem de clientes de um servidor
    public void incrementClientCount(PrimeServerAddress server) {
        clientCountMap.merge(server, 1, Integer::sum);
    }

    // Método para obter o servidor com a menor contagem de clientes
    public PrimeServerAddress getServerWithLeastClients() {
        return clientCountMap.entrySet()
                .stream()
                .min((entry1, entry2) -> entry1.getValue().compareTo(entry2.getValue()))
                .map(ConcurrentHashMap.Entry::getKey)
                .orElse(null);
    }
}
