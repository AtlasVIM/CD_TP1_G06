package appringmanager;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import ringmanagerprimestubs.NextPrimeServerAddress;
import ringmanagerprimestubs.PrimeServerAddress;
import ringmanagerprimestubs.RingManagerPrimeServiceGrpc;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class PrimeServerService extends RingManagerPrimeServiceGrpc.RingManagerPrimeServiceImplBase {

    private final SharedServerList sharedServerList;
    private final List<StreamObserver<NextPrimeServerAddress>> clients = new ArrayList<>(); // Lista de observadores para respostas
    private final List<ManagedChannel> channels = new CopyOnWriteArrayList<>(); // Lista de canais para cada servidor

    // Construtor que recebe a lista compartilhada
    public PrimeServerService(SharedServerList sharedServerList) {
        this.sharedServerList = sharedServerList;
    }

    @Override
    public void registServer(PrimeServerAddress request, StreamObserver<NextPrimeServerAddress> responseObserver) {
        // Adiciona o novo servidor à lista compartilhada
        sharedServerList.addServer(new MyServer(request.getIp(), request.getPort()));

        clients.add(responseObserver);

        System.out.println("Server registered: " + request.getIp() + ":" + request.getPort());

        // Envia a atualização do próximo servidor para todos os servidores

        //System.out.println("RESPONSE OBSERVER RING MANAGER PRIME SERVER SERVICE ON NEXT");
        //System.out.println("Teste "+responseObserver.onNext());
        //TODO

        sendNextServerUpdate();
    }

    private void sendNextServerUpdate() {
        // Envia a atualização de próximo servidor para todos os servidores na lista
        for (int i = 0; i < clients.size(); i++) {
            MyServer nextServer = sharedServerList.getNextServer(i);

            NextPrimeServerAddress update = NextPrimeServerAddress.newBuilder()
                    .setNextIp(nextServer.getIp())
                    .setNextPort(nextServer.getPort())
                    .build();

            clients.get(i).onNext(update);
        }
    }

    // Método opcional para encerrar todos os canais ao fechar o serviço
    public void shutdown() {
        for (ManagedChannel channel : channels) {
            channel.shutdown();
        }
    }
}
