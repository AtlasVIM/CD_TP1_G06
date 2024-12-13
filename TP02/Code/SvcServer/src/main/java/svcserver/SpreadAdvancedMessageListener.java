package svcserver;

import spread.*;

import java.nio.charset.StandardCharsets;

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
            //TODO se for do tipo svc e a lista estiver vazia, add e ele é o lider
            //TODO se a lista tiver apenas 1, add e começa nova eleição
            //TODO recebe novo processo
            //TODO recebe novo svc
            //TODO atualiza lista de processo e servidores se o svc atual for o leader
            //TODO envia lista atualizada a todos.
        }
        else if (message.getTypeServer() == SpreadTypeServer.LEADER){
            //TODO se receber msg do leader e o id for diferente do atual, quer dizer q o leader mudou, seta variavel para false
            //TODO se receber msg do leader com o id igual ao svc atual, quer dizer que ganhou a eleição, seta variavel para true
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

        //TODO se o LEADER for desconectado e o id do svc atual for o primeiro da lista, inicia nova eleição
        //TODO o primeiro da lista comunica a todos o resultado da eleição, manda msg como leader.
        if (SvcServer.debugMode)
            System.out.println("membershipMessageReceived SpreadMessage Received ThreadID="+Thread.currentThread().getId()+":");

        System.out.println("MemberShip ThreadID:" + Thread.currentThread().getId());
        MembershipInfo info = spreadMessage.getMembershipInfo();
        if (info.isSelfLeave()) {
            System.out.println("Left group:"+info.getGroup().toString());
        } else {
            //if (info.getMembers() != null) {
            SpreadGroup[] members = info.getMembers();
            System.out.println("members of belonging group:"+info.getGroup().toString());
            for (int i = 0; i < members.length; ++i) {
                System.out.print(members[i] + ":");
            }
            System.out.println();
        }


    }
}
