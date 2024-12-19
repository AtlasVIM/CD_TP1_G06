package appregisterserver;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ServerManager {
    private static final ConcurrentHashMap<Long, Server> servers = new ConcurrentHashMap<>(); //Gerenciar a concorrencia

    // Método para obter o servidor com a menor contagem de clientes
    public static Server getServerWithLeastClients() {
        var allServers = getAllServers();

        return allServers.stream()
                .min((server1, server2) -> Integer.compare(server1.getConnectedClients(), server2.getConnectedClients()))
                .orElse(null);
    }

    public static List<Server> getAllServers() {
        List<Server> serverList = new ArrayList<>();
        serverList.addAll(servers.values());
        return serverList;
    }

    public static Server getServer(Long id){
        return servers.get(id);
    }

    public static void addConnectedClients(Server server){
        servers.put(server.getGroupMemberId(), server);
    }

    public static void updateServers(List<Server> newServers) {
        try {
            for (Server server : newServers) {
                var oldServer = getServer(server.getGroupMemberId());
                if (oldServer != null){
                    server.setConnectedClients(oldServer.getConnectedClients());
                }
                servers.put(server.getGroupMemberId(), server);
            }

            // If server is not on newList but is in oldList, remove
            var oldServers = servers.values();
            Iterator<Server> iterator = oldServers.iterator();

            while (iterator.hasNext()){
                var oldServer = iterator.next();
                if (!newServers.contains(oldServer)){
                    servers.remove(oldServer.getGroupMemberId());
                }
            }
        } catch (Exception e) {
            System.out.println("ServerManager. An unexpected error occur when updateServers");
            e.printStackTrace();
        }
    }
}
