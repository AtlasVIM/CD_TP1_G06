package svcserver;

import spread.AdvancedMessageListener;
import spread.SpreadConnection;
import spread.SpreadMessage;

public class SpreadAdvancedMessageListener implements AdvancedMessageListener {
    private final SpreadConnection connection;

    public SpreadAdvancedMessageListener(SpreadConnection connection) {
        this.connection = connection;
    }

    @Override
    public void regularMessageReceived(SpreadMessage spreadMessage) {
        if (SvcServer.debugMode)
            System.out.println("Regular SpreadMessage Received ThreadID="+Thread.currentThread().getId()+":");
    }

    @Override
    public void membershipMessageReceived(SpreadMessage spreadMessage) {

        var member = spreadMessage.getMembershipInfo();
        member.
        if (member.isCausedByJoin()){
            var member2 = spreadMessage.getSender();

        }
        long leavingMemberId = info.getLeavingMemberId();
    }
}
