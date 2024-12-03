package appregisterserver;

import io.grpc.ServerBuilder;

public class AppRegisterServer {
    //private SharedServerList sharedServerList;

    public static void main(String[] args) {
        try {
            int port = 50051;
            if (args.length == 1) {
                port = Integer.parseInt(args[0]);
            }

            io.grpc.Server svc = ServerBuilder
                    .forPort(port)
                    //.addService(new RingManagerService(myAddress, managerAddress))
                    //.addService(new PrimeClientService())
                    //.addService(new ClientService())
                    .build();

            svc.start();
            System.out.println(String.format("RegisterServer: %s started. Listening on Port: %s ", port));
            //startRedisContainer();
            //registPrimeServer(myAddress);

            svc.awaitTermination();
            svc.shutdown();

        } catch (Exception ex){
            ex.printStackTrace();
        }
    }


}
