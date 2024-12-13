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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpreadGroupManager {

    private SpreadConnection connection;
    public static List<Server> servers;

    private SpreadAdvancedMessageListener msgHandlingAdvanced;
    private SpreadBasicMessageListener msgHandlingBasic;

    public SpreadGroupManager(String memberName, String address, int port) {
        // Establish the spread connection.
        try  {
            connection = new SpreadConnection();
            connection.connect(InetAddress.getByName(address), port, memberName, false, true);

            msgHandlingAdvanced = new SpreadAdvancedMessageListener(connection);
            msgHandlingBasic = new SpreadBasicMessageListener(connection);
            connection.add(msgHandlingAdvanced);
            //connection.add(msgHandlingBasic);
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

    public void groupLeave(String nameToLeave) throws SpreadException {
        /*SpreadGroup group=groupsBelonging.get(nameToLeave);
        if(group != null) {
            group.leave();
            groupsBelonging.remove(nameToLeave);
            System.out.println("Left from " + group + ".");
        } else  { System.out.println("No group to leave."); }*/
    }

    public void close() throws SpreadException {
        // remove listener
        connection.remove(msgHandlingAdvanced);
        connection.remove(msgHandlingBasic);
        // Disconnect.
        connection.disconnect();
    }
}
