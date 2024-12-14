package svcserver;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

public class ServerManager {
    private static final ConcurrentHashMap<Long, Server> servers = new ConcurrentHashMap<>(); //Gerenciar a concorrencia
    private static final Semaphore semaphore = new Semaphore(1);

    public static Server getServer(long groupMemberId){
        return servers.get(groupMemberId);
    }

    public static void addNewServer(long groupMemberId, Server server){
        servers.putIfAbsent(groupMemberId, server);
    }

    public static void removeServer(long groupMemberId){
        servers.remove(groupMemberId);
    }

    public static long getNewLeader() throws InterruptedException {
        semaphore.acquire();
        try {
            var serversKeys = servers.keySet();
            Iterator<Long> iterator = serversKeys.iterator();

            long newLeaderId = Long.MIN_VALUE;
            while (iterator.hasNext()) {
                var key = iterator.next();
                if (key > newLeaderId) {
                    newLeaderId = key;
                }
            }
            return newLeaderId;
        }
        finally {
            semaphore.release();
        }
    }

    public static void setNewLeader(long groupMemberId){
        servers.computeIfPresent(groupMemberId, (key, server) -> {
            server.setGroupLeader(true);
            return server;
        });
    }

    public static List<Server> getAllServers() {
        List<Server> serverList = new ArrayList<>();
        serverList.addAll(servers.values());
        return serverList;
    }

    public static void updateServers(List<Server> newServers){
        try {
            semaphore.acquire();
            for (Server server : newServers) {
                servers.put(server.getGroupMemberId(), server);
            }
        } catch (InterruptedException e) {
            System.out.println("ServerManager. An unexpected error occur when updateServers");
            e.printStackTrace();
        } finally {
            semaphore.release();
        }
    }
}
