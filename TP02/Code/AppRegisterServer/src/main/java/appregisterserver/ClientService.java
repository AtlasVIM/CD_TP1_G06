package appregisterserver;

import io.grpc.stub.StreamObserver;
import registerclientstubs.RegisterClientServiceGrpc;
import registerclientstubs.SvcServerAddress;
import registerclientstubs.VoidRequest;

public class ClientService extends RegisterClientServiceGrpc.RegisterClientServiceImplBase {

    public ClientService() {
    }

    @Override
    public void getSvcServer(VoidRequest request, StreamObserver<SvcServerAddress> responseObserver) {
        System.out.println("Trying to get new SVC Server for Client!");
        // Obtém o próximo servidor com a menor contagem de clientes
        Server server = ServerManager.getServerWithLeastClients();
        if (server != null) {

            SvcServerAddress svcServer = SvcServerAddress.newBuilder()
                    .setIp(server.getIp())
                    .setPort(server.getPort())
                    .build();

            // Incrementa contagem de clientes
            server.incrementClients();
            responseObserver.onNext(svcServer);
            System.out.println("Sent SVC Server to Client!");
        } else {
            responseObserver.onError(new RuntimeException("No available servers"));
        }
        responseObserver.onCompleted();

    }
}
