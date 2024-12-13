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
            System.out.println("Regular SpreadMessage Received ThreadID=" + Thread.currentThread().getId() + ":");
    }

    @Override
    public void membershipMessageReceived(SpreadMessage spreadMessage) {

        MembershipInfo memberships = spreadMessage.getMembershipInfo();

        if (memberships.isSelfLeave() || memberships.isCausedByDisconnect() || memberships.isCausedByLeave()) {
            System.out.println("Left group:" + memberships.getGroup().toString());
        } else {
            SpreadGroup[] members = memberships.getMembers();
            System.out.println("members of belonging group:" + memberships.getGroup().toString());
            for (int i = 0; i < members.length; ++i) {
                System.out.print(members[i] + ":");
            }
            System.out.println();
        }
    }
}
