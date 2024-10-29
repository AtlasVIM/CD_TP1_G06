package appringmanager;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import java.io.IOException;

public class AppRingManager {

    private Server server;

    public void start(int port) throws IOException {
        // Criação de uma lista compartilhada para armazenar os servidores
        SharedServerList sharedServerList = new SharedServerList();

        PrimeServerService primeServerService = new PrimeServerService(sharedServerList);
        ClientService clientService = new ClientService(sharedServerList);

        server = ServerBuilder.forPort(port)
                .addService(primeServerService) // Adiciona o serviço PrimeServer
                .addService(clientService) // Adiciona o serviço Client
                .build()
                .start();

        System.out.println("Server started, listening on " + port);
    }

    public void stop() {
        if (server != null) {
            server.shutdown();
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        final AppRingManager appRingManager = new AppRingManager();
        int port = 50051;

        appRingManager.start(port);

        // Manter o servidor ativo até receber um sinal de parada
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.err.println("Shutting down gRPC server since JVM is shutting down");
            appRingManager.stop();
        }));

        // Manter o servidor ativo indefinidamente
        appRingManager.server.awaitTermination();
    }
}
