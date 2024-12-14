package svcserver;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import io.grpc.ServerBuilder;
import org.slf4j.Logger;
import org.slf4j.simple.SimpleLoggerFactory;
import spread.SpreadException;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SvcServer {
    public static Channel channelRabbitMq;
    public static SpreadGroupManager spreadManager;
    public final static String SpreadGroup = "Servers";
    public final static boolean debugMode = true;
    static Logger logger = new SimpleLoggerFactory().getLogger("RabbitMQ-configurator");
    public static String myIp;
    public static int myPort;
    public static long mySpreadId;
    public static boolean iAmGroupLeader = false;

    public static void main(String[] args){

        try {
            myPort = 50051;
            myIp = "34.76.222.27";
            String rabbitMQ_Ip = "34.78.172.32";
            String spreadIp = "34.78.172.32";
            if (args.length == 4) {
                myIp = args[0];
                myPort = Integer.parseInt(args[1]);
                rabbitMQ_Ip = args[2];
                spreadIp = args[3];
            }

            io.grpc.Server svc = ServerBuilder
                    .forPort(myPort)
                    .addService(new ClientService())
                    .build();

            svc.start();
            System.out.println(String.format("SvcServer started. Listening on Port: %s ", myPort));
            connectToRabbitMq(rabbitMQ_Ip);
            connectToSpread(myIp, myPort, spreadIp);

            svc.awaitTermination();
            svc.shutdown();

        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public static void connectToRabbitMq(String ipRabbitMQ) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(ipRabbitMQ);
        factory.setPort(5672);

        Connection connection = factory.newConnection();
        channelRabbitMq = connection.createChannel();
        channelRabbitMq.addReturnListener(new MessageRabbitMQ());
        System.out.println("Connected to RabbitMQ at "+ipRabbitMQ+":5672");
    }

    public static void connectToSpread(String myIp, int myPort, String ipSpread) throws SpreadException {
        mySpreadId = generateSpreadMemberId();
        spreadManager = new SpreadGroupManager(mySpreadId+"", ipSpread, 4803);
        spreadManager.joinToGroup(SpreadGroup);
        spreadManager.sendMessage(new SpreadGroupMessage(myIp, myPort, mySpreadId));

        if (SvcServer.debugMode)
            System.out.println("Sent Message to Group");
    }

    public static long generateSpreadMemberId() {
        SecureRandom secureRandom = new SecureRandom();
        long min = 1_000_000_000L;  // Mínimo de 10 dígitos
        long max = 9_999_999_999L;  // Máximo de 10 dígitos
        return min + (long)(secureRandom.nextDouble() * (max - min)) & Long.MAX_VALUE; // Gera um número long positivo aleatório
    }

    public static long getSpreadMemberId(String str) {
        Pattern pattern = Pattern.compile("#(\\d+)#Servers#");
        Matcher matcher = pattern.matcher(str);
        if (matcher.find()) {
            String numberString = matcher.group(1);
            try {
                var spreadMemberId = Long.parseLong(numberString);
                return spreadMemberId;
            } catch (NumberFormatException e) {
                return 0;
            }
        } else {
            return 0; // A string não possui o formato esperado
        }
    }



}
