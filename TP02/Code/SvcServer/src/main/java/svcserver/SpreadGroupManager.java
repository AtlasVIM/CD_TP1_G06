package svcserver;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import spread.SpreadConnection;
import spread.SpreadException;
import spread.SpreadGroup;
import spread.SpreadMessage;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

public class SpreadGroupManager {

    private SpreadConnection connection;

    private SpreadAdvancedMessageListener msgHandlingAdvanced;

    public SpreadGroupManager(String memberName, String address, int port) {
        // Establish the spread connection.
        try  {
            connection = new SpreadConnection();
            connection.connect(InetAddress.getByName(address), port, memberName, false, true);

            msgHandlingAdvanced = new SpreadAdvancedMessageListener(connection);
            connection.add(msgHandlingAdvanced);
            System.out.println("Connected to Spread at "+address+":"+port);
        }
        catch(SpreadException e)  {
            System.err.println("There was an error connecting to the daemon Spread.");
            e.printStackTrace();
            System.exit(1);
        }
        catch(UnknownHostException e) {
            System.err.println("Can't find the daemon " + address);
            System.exit(1);
        }
    }

    public void joinToGroup(String groupName) throws SpreadException {
        SpreadGroup newGroup = new SpreadGroup();
        newGroup.join(connection, groupName);
    }

    public void sendMessage(SpreadGroupMessage grpMessage) throws SpreadException {
        SpreadMessage msg = new SpreadMessage();
        msg.setSafe();
        msg.addGroup(SvcServer.SpreadGroup);
        msg.setData(convertSpreadGroupMessageToBytes(grpMessage));
        connection.multicast(msg);
    }

    public void sendMessageAsLeader() {
        try {
            var message = new SpreadGroupMessage(true);
            SvcServer.spreadManager.sendMessage(message);
            if (SvcServer.debugMode)
                System.out.println("Message sent to group as Leader. Qty Processes: "+message.processes.size()+ " Qty Servers: "+message.servers.size());

        } catch (SpreadException e) {
            System.out.println("An unexpected error occurred when sendMessageAsLeader to group");
            e.printStackTrace();
        }
    }

    //Serialize object SpreadGroupMessage into byte[]
    private byte[] convertSpreadGroupMessageToBytes(SpreadGroupMessage grpMessage){
        Gson gson = new GsonBuilder().create();
        String newJsonString = gson.toJson(grpMessage);
        return newJsonString.getBytes(StandardCharsets.UTF_8);
    }

    public SpreadGroupMessage convertBytesToSpreadGroupMessage(byte[] grpMessage){
        Gson gson = new GsonBuilder().create();
        String newJsonString = new String(grpMessage, StandardCharsets.UTF_8);
        return gson.fromJson(newJsonString, SpreadGroupMessage.class);
    }

    public void handleDisconnectedMember(SpreadGroup member) throws InterruptedException {
        var spreadMemberId = SvcServer.getSpreadMemberId(member.toString());
        var server = ServerManager.getServer(spreadMemberId);

        ServerManager.removeServer(spreadMemberId); //Atualiza Lista
        System.out.println("GroupManager. Member disconnected: "+spreadMemberId);

        if (server.isGroupLeader() && ServerManager.getNewLeader() == SvcServer.mySpreadId){ //Se o server que saiu for um Leader, promove nova eleição
            //Possivel problema se nesse momento getNewLeader, entrar novo server e este tiver um numero maior,
            // será escolhido como leader, mas nao tem a lista de servers ou processos atualizada e possivelmente nao receberá este evento de disconnect..
            // será utilizado semaforo para evitar essa situação
            SvcServer.iAmGroupLeader = true;
            ServerManager.setNewLeader(SvcServer.mySpreadId);
            sendMessageAsLeader();
            System.out.println("GroupManager. New Leader: "+SvcServer.mySpreadId);

        }
    }

    public void close() throws SpreadException {
        // remove listener
        connection.remove(msgHandlingAdvanced);
        // Disconnect.
        connection.disconnect();
    }
}
