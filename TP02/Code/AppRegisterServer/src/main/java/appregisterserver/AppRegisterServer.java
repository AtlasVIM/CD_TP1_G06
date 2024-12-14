package appregisterserver;

import io.grpc.ServerBuilder;
import spread.SpreadException;

public class AppRegisterServer {

    public static String myIp;
    public final static boolean debugMode = true;
    public static SpreadGroupManager spreadManager;
    public final static String SPREAD_USER = "Register";
    public final static String SPREAD_GROUP = "Servers";


    public static void main(String[] args) {
        try {
            int myPort = 50051;
            String spreadIp = "34.78.207.63";
            if (args.length == 2) {
                spreadIp = args[0];
                myPort = Integer.parseInt(args[1]);
            }else {
                System.out.println("Number of parameters incorrect!");
                return;
            }

            io.grpc.Server svc = ServerBuilder
                    .forPort(myPort)
                    .addService(new ClientService())
                    .build();

            svc.start();
            System.out.println(String.format("Listening on Port: %s ", myPort));

            connectToSpread(spreadIp);
            if(debugMode) {
                System.out.println("Register connected to "+ SPREAD_GROUP +" group!");
            }
            svc.awaitTermination();
            svc.shutdown();

        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public static void connectToSpread(String ipSpread) throws SpreadException {
        spreadManager = new SpreadGroupManager(SPREAD_USER, ipSpread, 4803);
        spreadManager.joinToGroup(SPREAD_GROUP);

    }

}
