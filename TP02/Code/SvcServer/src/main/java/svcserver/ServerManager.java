package svcserver;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerManager {
    private static final ConcurrentHashMap<Long, Server> servers = new ConcurrentHashMap<>(); //Gerenciar a concorrencia
    private static final ExecutorService executor = Executors.newFixedThreadPool(10);

    public static void addNewServer(long groupMemberId, Server server){
        executor.submit(() -> {
            servers.putIfAbsent(groupMemberId, server);
        });
    }
}
