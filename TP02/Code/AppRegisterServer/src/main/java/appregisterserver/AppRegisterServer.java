package appregisterserver;

import io.grpc.ServerBuilder;
import spread.SpreadException;

public class AppRegisterServer {

    public static String myIp;
    public final static boolean debugMode = true;
    public static SpreadGroupManager spreadManager;
    public final static String SpreadUser = "Servers";
    public final static String SpreadGroup = "Servers";


    public static void main(String[] args) {
        try {
            int port = 50051;
            String spreadIp = "34.78.207.63";
            if (args.length > 1) {
                myIp = args[0];
                port = Integer.parseInt(args[1]);
                spreadIp = args[2];
            }

            io.grpc.Server svc = ServerBuilder
                    .forPort(port)
                    .addService(new ClientService())
                    .build();

            svc.start();
            System.out.println(String.format("RegisterServer: %s started. Listening on Port: %s ",myIp, port));

            connectToSpread(myIp, port, spreadIp);
            if(debugMode) {
                System.out.println("CONNECTED TO SPREAD GROUP");
            }
            svc.awaitTermination();
            svc.shutdown();

        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public static void connectToSpread(String myIp, int myPort, String ipSpread) throws SpreadException {
        spreadManager = new SpreadGroupManager(SpreadUser, ipSpread, 4803);
        spreadManager.joinToGroup(SpreadGroup);
        spreadManager.sendMessage(SpreadGroup, new SpreadGroupMessage(myIp, myPort));

        if (AppRegisterServer.debugMode)
            System.out.println("Sent Message to Group");
    }

}
