package svcserver;

import spread.*;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class SpreadAdvancedMessageListener implements AdvancedMessageListener {
    private final SpreadConnection connection;

    public SpreadAdvancedMessageListener(SpreadConnection connection) {
        this.connection = connection;
    }

    @Override
    public void regularMessageReceived(SpreadMessage spreadMessage) {
        if (SvcServer.debugMode)
            System.out.println("AdvancedMessageListener regularMessageReceived Regular SpreadMessage Received ThreadID="+Thread.currentThread().getId()+":");

        var message = SvcServer.spreadManager.convertBytesToSpreadGroupMessage(spreadMessage.getData());
        if (message.getTypeServer() == SpreadTypeServer.SVC){
            if (SvcServer.iAmGroupLeader){
                if ()
            }

            //TODO recebe novo processo
            //TODO recebe novo svc
            //TODO atualiza lista de processo e servidores se o svc atual for o leader
            //TODO envia lista atualizada a todos.
        }
        else if (message.getTypeServer() == SpreadTypeServer.LEADER){
            //TODO se receber msg do leader e o id for diferente do atual, e a variavel esta como true, quer dizer q o leader mudou, seta variavel para false
            //TODO se receber msg do leader com o id igual ao svc atual, mas a variavel for false, quer dizer que ganhou a eleição, seta variavel para true
            //TODO atualiza lista local com processos
            //TODO atualiza lista local de servidores
        }
        else if (message.getTypeServer() == SpreadTypeServer.WORKER){
            //TODO atualiza lista de processo se o svc atual for o leader
            //TODO envia lista atualizada a todos.
        }

        System.out.println("AdvancedMessageListener regularMessageReceived The message is: " + new String(spreadMessage.getData()));

    }

    @Override
    public void membershipMessageReceived(SpreadMessage spreadMessage) {
        //Recebe informações sobre membros que entraram no grupo ou sairam do grupo

        if (SvcServer.debugMode)
            System.out.println("membershipMessageReceived SpreadMessage Received ThreadID="+Thread.currentThread().getId()+":");

        MembershipInfo info = spreadMessage.getMembershipInfo();
        if (info.isSelfLeave()) {
            var member = info.getLeft();
            if (SvcServer.debugMode)
                System.out.println("Left group:" + info.getGroup().toString()+ " "+member);

            try {
                SvcServer.spreadManager.handleDisconnectedMember(member);
            } catch (InterruptedException e) {
                System.out.println("An unexpected error occurred when handleDisconnectedMember");
                e.printStackTrace();
            }
        }
        else if (info.isCausedByDisconnect() || info.isCausedByLeave()){
            var member = info.getLeft();
            var member2 = info.getDisconnected();
            if (SvcServer.debugMode)
                System.out.print("Member disconnected" + member);

            try {
                SvcServer.spreadManager.handleDisconnectedMember(member);
            } catch (InterruptedException e) {
                System.out.println("An unexpected error occurred when handleDisconnectedMember");
                e.printStackTrace();
            }
        }
        /*else if (info.isCausedByLeave()){
            var member = info.getLeft();
            if (SvcServer.debugMode)
                System.out.print("Member isCausedByLeave: " + member);

            try {
                SvcServer.spreadManager.handleDisconnectedMember(member);
            } catch (InterruptedException e) {
                System.out.println("An unexpected error occurred when handleDisconnectedMember");
                e.printStackTrace();
            }
        }*/
        else {
            SpreadGroup[] members = info.getMembers();

            if (members.length == 1){//Isso quer dizer que tem apenas 1 Svc, Inicio do processo e esse vai ser o leader
                ServerManager.addNewServer(SvcServer.mySpreadId,
                                            new Server(SvcServer.myIp,
                                                       SvcServer.myPort,
                                                       SvcServer.mySpreadId,
                                                    true));

                SvcServer.iAmGroupLeader = true;
                System.out.println("New Leader: "+SvcServer.mySpreadId);
                //Não manda mensagem ao grupo de novo leader, porque não tem mais ninguem no grupo. Não é necessario
            }

            if (SvcServer.debugMode) {
                System.out.println("Members of group:" + info.getGroup().toString());
                for (int i = 0; i < members.length; ++i) {
                    System.out.print(members[i] + "; ");
                }
                System.out.println();
            }
        }


    }
}
