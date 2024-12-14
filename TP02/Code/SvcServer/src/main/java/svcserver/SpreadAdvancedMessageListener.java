package svcserver;

import spread.*;

public class SpreadAdvancedMessageListener implements AdvancedMessageListener {
    private final SpreadConnection connection;

    public SpreadAdvancedMessageListener(SpreadConnection connection) {
        this.connection = connection;
    }

    @Override
    public void regularMessageReceived(SpreadMessage spreadMessage) {
        if (SvcServer.debugMode)
            System.out.println("regularMessageReceived Regular SpreadMessage Received ThreadID="+Thread.currentThread().getId()+":");

        var message = SvcServer.spreadManager.convertBytesToSpreadGroupMessage(spreadMessage.getData());
        if (message.getTypeServer() == SpreadTypeServer.SVC){
            if (message.newSvc != null) {
                ServerManager.addNewServer(message.newSvc.getGroupMemberId(), message.newSvc);
                if (SvcServer.debugMode)
                    System.out.println("Svc message received. New server add to list "+message.newSvc.toString());
            }

            if (message.newProcess != null) {
                ProcessManager.addNewProcess(message.newProcess.getId(), message.newProcess.getImageName());
                if (SvcServer.debugMode)
                    System.out.println("Svc message received. New process add "+message.newProcess.toString());
            }

            //Possivel problema se o leader for disconectado nesse momento e o proximo leader for o svc que acabou de entrar,
            // mas ainda nao tem a lista completa
            if (SvcServer.iAmGroupLeader){
                SvcServer.spreadManager.sendMessageAsLeader();
            }
        }
        else if (message.getTypeServer() == SpreadTypeServer.LEADER){
            if (!SvcServer.iAmGroupLeader){//Se receber msg do leader mas a variavel esta como true, quer dizer que o svc recebeu msg dele mesmo.
                if (SvcServer.debugMode)
                    System.out.println("Leader message received. Qtd Process in list: "+ProcessManager.getAllProcesses().size()+ ". Qtd Servers in list "+ServerManager.getAllServers().size());

                ProcessManager.updateProcesses(message.processes);
                ServerManager.updateServers(message.servers);

                if (SvcServer.debugMode)
                    System.out.println("Leader message - After update list. Qtd Process in list: "+ProcessManager.getAllProcesses().size()+ ". Qtd Servers in list "+ServerManager.getAllServers().size());

            }
        }
        else if (message.getTypeServer() == SpreadTypeServer.WORKER){
            ProcessManager.setProcessCompleted(message.requestId, message.imageNameMarks);
            if (SvcServer.debugMode)
                System.out.println("Worker message received: "+message.requestId);

            if (SvcServer.iAmGroupLeader){
                SvcServer.spreadManager.sendMessageAsLeader();
            }
        }
    }

    @Override
    public void membershipMessageReceived(SpreadMessage spreadMessage) {
        //Recebe informações sobre membros que entraram no grupo ou sairam do grupo

        if (SvcServer.debugMode) {
            System.out.println();
            System.out.println("membershipMessageReceived SpreadMessage Received ThreadID=" + Thread.currentThread().getId() + ":");
        }

        MembershipInfo info = spreadMessage.getMembershipInfo();
        if (info.isSelfLeave()) {
            var member = info.getLeft();
            if (SvcServer.debugMode)
                System.out.println("MemberInfoLeft group:" + info.getGroup().toString()+ " "+member);

            try {
                SvcServer.spreadManager.handleDisconnectedMember(member);
            } catch (InterruptedException e) {
                System.out.println("MemberInfo An unexpected error occurred when handleDisconnectedMember");
                e.printStackTrace();
            }
        }
        else if (info.isCausedByDisconnect() || info.isCausedByLeave()){
            var member = info.getLeft();
            var member2 = info.getDisconnected();
            if (SvcServer.debugMode)
                System.out.print("MemberInfo Member disconnected " + member);

            try {
                SvcServer.spreadManager.handleDisconnectedMember(member);
            } catch (InterruptedException e) {
                System.out.println("An unexpected error occurred when handleDisconnectedMember");
                e.printStackTrace();
            }
        }
        else {
            SpreadGroup[] members = info.getMembers();

            if (members.length == 2){//Isso quer dizer que tem apenas 1 Svc, Inicio do processo e esse vai ser o leader
                ServerManager.addNewServer(SvcServer.mySpreadId,
                                            new Server(SvcServer.myIp,
                                                       SvcServer.myPort,
                                                       SvcServer.mySpreadId,
                                                    true));

                SvcServer.iAmGroupLeader = true;
                System.out.println("MemberInfo New Leader: "+SvcServer.mySpreadId);
                if (SvcServer.debugMode)
                    System.out.println("MemberInfo Leader List. Qtd Process in list: "+ProcessManager.getAllProcesses().size()+ ". Qtd Servers in list "+ServerManager.getAllServers().size());

                SvcServer.spreadManager.sendMessageAsLeader(); //Manda mensagem para o Register receber.
            }

            if (SvcServer.debugMode) {
                System.out.println("MemberInfo Members of group:" + info.getGroup().toString());
                for (int i = 0; i < members.length; ++i) {
                    System.out.print(members[i] + "; ");
                }
                System.out.println();
            }
        }


    }
}
