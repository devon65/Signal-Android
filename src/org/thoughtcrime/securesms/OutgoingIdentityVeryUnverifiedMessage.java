//Devon newWarn code starts
//this class creates the VeryUnverified in-text message

package org.thoughtcrime.securesms;

import org.thoughtcrime.securesms.recipients.Recipient;
import org.thoughtcrime.securesms.sms.OutgoingTextMessage;

public class OutgoingIdentityVeryUnverifiedMessage extends OutgoingTextMessage {

    public OutgoingIdentityVeryUnverifiedMessage(Recipient recipient) {
        this(recipient, "");
    }

    private OutgoingIdentityVeryUnverifiedMessage(Recipient recipient, String body) {
        super(recipient, body, -1);
    }

    @Override
    public boolean isIdentityVeryUnverified(){
        return true;
    }

    public OutgoingTextMessage withBody(String body){
        return new OutgoingIdentityVeryUnverifiedMessage(getRecipient());
    }
}

//Devon code ends