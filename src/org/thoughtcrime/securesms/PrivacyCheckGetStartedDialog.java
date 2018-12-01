//Devon newWarn code starts
//This file was copied from "ConfirmIdentityDialog.java" and adapted

package org.thoughtcrime.securesms;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import org.thoughtcrime.securesms.crypto.IdentityKeyParcelable;
import org.thoughtcrime.securesms.database.Address;
import org.thoughtcrime.securesms.database.documents.IdentityKeyMismatch;
import org.thoughtcrime.securesms.database.model.MessageRecord;
import org.thoughtcrime.securesms.recipients.Recipient;

public class PrivacyCheckGetStartedDialog extends AlertDialog {


    @SuppressWarnings("unused")
    private static final String TAG = PrivacyCheckGetStartedDialog.class.getSimpleName();

    private DialogInterface.OnClickListener callback;

    public PrivacyCheckGetStartedDialog(Context context, MessageRecord messageRecord, IdentityKeyMismatch mismatch) {
        super(context);

        Recipient recipient = Recipient.from(context, mismatch.getAddress(), false);
        String name = recipient.toShortString();
        String dialogBody = context.getString(R.string.privacy_check_get_started_dialog_There_is_a_small_chance) + "\n\n" +
                context.getString(R.string.privacy_check_get_started_dialog_If_this_is_important_to_you) + "\n\n" +
                String.format(context.getString(R.string.privacy_check_get_started_dialog_This_will_require_a_minute_or_two_of_your_time), name);


        setTitle(context.getString(R.string.privacy_check_get_started_dialog_title));
        setIcon(R.drawable.ic_warning_light);
        setMessage(dialogBody);

        setButton(AlertDialog.BUTTON_POSITIVE, context.getString(R.string.privacy_check_get_started_dialog_Get_Started),
                new PrivacyCheckGetStartedDialog.AcceptListener(mismatch, messageRecord.isIdentityVerified()));
        setButton(AlertDialog.BUTTON_NEGATIVE, context.getString(R.string.privacy_check_get_started_dialog_Not_Now),
                new PrivacyCheckGetStartedDialog.CancelListener(context, messageRecord, mismatch));
    }

    @Override
    public void show() {
        super.show();
        ((TextView) this.findViewById(android.R.id.message))
                .setMovementMethod(LinkMovementMethod.getInstance());
    }

    public void setCallback(DialogInterface.OnClickListener callback) {
        this.callback = callback;
    }

    private class AcceptListener implements OnClickListener {

        private final IdentityKeyMismatch   mismatch;
        private final Boolean               isVerified;

        private AcceptListener(IdentityKeyMismatch mismatch, Boolean isVerified){
            this.mismatch   = mismatch;
            this.isVerified = isVerified;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {

            Intent intent = new Intent(getContext(), PrivacyCheckActivity.class);
            intent.putExtra(PrivacyCheckActivity.ADDRESS_EXTRA, mismatch.getAddress());
            intent.putExtra(PrivacyCheckActivity.IDENTITY_EXTRA, new IdentityKeyParcelable(mismatch.getIdentityKey()));
            intent.putExtra(PrivacyCheckActivity.VERIFIED_EXTRA, isVerified);
            getContext().startActivity(intent);

            if (callback != null) callback.onClick(null, 0);
        }
    }

    private class CancelListener implements OnClickListener {

        private final Context              context;
        private final IdentityKeyMismatch  mismatch;
        private final MessageRecord        messageRecord;

        private CancelListener(Context context, MessageRecord messageRecord, IdentityKeyMismatch mismatch) {
            this.context       = context;
            this.mismatch      = mismatch;
            this.messageRecord = messageRecord;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {

            new PrivacyCheckRemindLaterDialog(context, messageRecord, mismatch).show();

            if (callback != null) callback.onClick(null, 0);
        }
    }

}

//Devon code ends