package svcserver;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

public class ServerManager {
    private static final ConcurrentHashMap<Long, Server> servers = new ConcurrentHashMap<>(); //Gerenciar a concorrencia
    private static final ExecutorService executor = Executors.newFixedThreadPool(10);
    private static final Semaphore semaphore = new Semaphore(1);

    public static Server getServer(long groupMemberId){
        return servers.get(groupMemberId);
    }

    public static void addNewServer(long groupMemberId, Server server){
        executor.submit(() -> {
            servers.putIfAbsent(groupMemberId, server);
        });
    }

    public static void removeServer(long groupMemberId){
        executor.submit(() -> {
            servers.remove(groupMemberId);
        });
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
        executor.submit(() -> {
            servers.computeIfPresent(groupMemberId, (key, server) ->{
                server.setGroupLeader(true);
                return server;
            });
        });
    }

    public static List<Server> getAllServers() {
        List<Server> serverList = new ArrayList<>();
        serverList.addAll(servers.values());
        return serverList;
    }
}
