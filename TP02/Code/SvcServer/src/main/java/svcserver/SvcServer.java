package svcserver;

import io.grpc.ServerBuilder;

import java.io.ByteArrayOutputStream;

public class SvcServer {


    public static void main(String[] args){

        try {
            int port = 50051;
            if (args.length == 1) {
                port = Integer.parseInt(args[0]);
            }

            io.grpc.Server svc = ServerBuilder
                    .forPort(port)
                    .addService(new ClientService())
                    .build();

            svc.start();
            System.out.println(String.format("SvcServer started. Listening on Port: %s ", port));

            svc.awaitTermination();
            svc.shutdown();

        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

}
