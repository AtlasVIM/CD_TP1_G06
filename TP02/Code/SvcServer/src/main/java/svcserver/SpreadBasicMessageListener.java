package svcserver;

import spread.BasicMessageListener;
import spread.SpreadConnection;
import spread.SpreadGroup;
import spread.SpreadMessage;

public class SpreadBasicMessageListener implements BasicMessageListener {
    private final SpreadConnection connection;

    public SpreadBasicMessageListener(SpreadConnection connection) {
        this.connection = connection;
    }

    @Override
    public void messageReceived(SpreadMessage spreadMessage) {
        try {

            if (SvcServer.debugMode)
                System.out.println("Message Received ThreadID="+Thread.currentThread().getId()+":");





            SpreadPrintMessages.MessageDetails(spreadMessage);

            // enviar reply direto para o sender se a mensagem tiver conteudo "request"
            if (!spreadMessage.isMembership()) {
                SpreadGroup myPrivateGroup = connection.getPrivateGroup();
                //System.out.println("myPrivateGroup=" + myPrivateGroup.toString());
                SpreadGroup senderPrivateGroup = spreadMessage.getSender();
                //System.out.println("senderPrivateGroup=" + senderPrivateGroup.toString());
                if (!myPrivateGroup.equals(senderPrivateGroup)) {
                    String txtMsg = new String(spreadMessage.getData());
                    if (txtMsg.equalsIgnoreCase("request")) {
                        SpreadMessage msg = new SpreadMessage();
                        msg.setSafe();
                        msg.addGroup(senderPrivateGroup.toString());
                        msg.setData(("Hello i am " + myPrivateGroup + ":I was received your group request").getBytes());
                        //System.out.println("enviar reply direto");
                        connection.multicast(msg);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

