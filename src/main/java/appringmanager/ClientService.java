package appringmanager;

import io.grpc.stub.StreamObserver;
import ringmanagerclientstubs.VoidRequest;
import ringmanagerclientstubs.RingManagerClientServiceGrpc;
import ringmanagerclientstubs.PrimeServerAddress;
import ringmanagerprimestubs.NextPrimeServerAddress;

public class ClientService extends RingManagerClientServiceGrpc.RingManagerClientServiceImplBase {

    private final SharedServerList sharedServerList; // Referência à lista compartilhada

    // Construtor que recebe a lista compartilhada
    public ClientService(SharedServerList sharedServerList) {
        this.sharedServerList = sharedServerList;
    }

    @Override
    public void getPrimeServer(VoidRequest request, StreamObserver<PrimeServerAddress> responseObserver) {
        // Obtém o próximo servidor com a menor contagem de clientes
        MyServer server = sharedServerList.getServerWithLeastClients();

        if (server != null) {

            PrimeServerAddress primeServer = PrimeServerAddress.newBuilder()
                    .setIp(server.getIp())
                    .setPort(server.getPort())
                    .build();

             // Incrementa contagem de clientes
            server.incrementClients();
            responseObserver.onNext(primeServer);
        } else {
            responseObserver.onError(new RuntimeException("No available servers"));
        }
        responseObserver.onCompleted();
    }
}