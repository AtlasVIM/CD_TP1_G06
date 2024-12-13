package appregisterserver;

import spread.*;

public class SpreadAdvancedMessageListener implements AdvancedMessageListener {
    private final SpreadConnection connection;

    public SpreadAdvancedMessageListener(SpreadConnection connection) {
        this.connection = connection;
    }

    @Override
    public void regularMessageReceived(SpreadMessage spreadMessage) {
        if (AppRegisterServer.debugMode)
            System.out.println("Regular SpreadMessage Received ThreadID="+Thread.currentThread().getId()+":");
    }

    @Override
    public void membershipMessageReceived(SpreadMessage spreadMessage) {

        MembershipInfo member = spreadMessage.getMembershipInfo();
        member.
        if (member.isCausedByJoin()){
            var member2 = spreadMessage.getSender();

        }
        long leavingMemberId = member.getLeavingMemberId();
    }
}
