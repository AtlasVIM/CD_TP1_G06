package appregisterserver;

import io.grpc.stub.StreamObserver;
import registerclientstubs.RegisterClientServiceGrpc;
import registerclientstubs.SvcServerAddress;
import registerclientstubs.VoidRequest;

public class ClientService extends RegisterClientServiceGrpc.RegisterClientServiceImplBase {

    @Override
    public void getSvcServer(VoidRequest request, StreamObserver<SvcServerAddress> responseObserver) {
        super.getSvcServer(request, responseObserver);

        // Obtém o próximo servidor com a menor contagem de clientes
        Server server = sharedServerList.getServerWithLeastClients();

        if (server != null) {

            SvcServerAddress svcServer = SvcServerAddress.newBuilder()
                    .setIp("")
                    .setPort(123)
                    .build();

            // Incrementa contagem de clientes
            server.incrementClients();
            responseObserver.onNext(svcServer);
        } else {
            responseObserver.onError(new RuntimeException("No available servers"));
        }
        responseObserver.onCompleted();

    }
}
