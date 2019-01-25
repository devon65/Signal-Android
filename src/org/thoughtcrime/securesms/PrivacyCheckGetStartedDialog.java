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
    public static boolean isDialogShowingAlready = false;



    public PrivacyCheckGetStartedDialog(Context context, IdentityKeyMismatch mismatch) {
        super(context);

        Recipient recipient = Recipient.from(context, mismatch.getAddress(), false);
        String name = recipient.toShortString();
        String dialogBody = context.getString(R.string.privacy_check_get_started_dialog_There_is_a_small_chance) + "\n\n" +
                context.getString(R.string.privacy_check_get_started_dialog_If_this_is_important_to_you) + "\n\n" +
                String.format(context.getString(R.string.privacy_check_get_started_dialog_This_will_require_a_minute_or_two_of_your_time), name);


        setTitle(context.getString(R.string.privacy_check_get_started_dialog_title));
        setIcon(R.drawable.ic_devon_privacy_check_very_unverified_dialog_shield);
        setMessage(dialogBody);

        setButton(AlertDialog.BUTTON_POSITIVE, context.getString(R.string.privacy_check_get_started_dialog_Get_Started),
                new PrivacyCheckGetStartedDialog.AcceptListener(mismatch));
        setButton(AlertDialog.BUTTON_NEGATIVE, context.getString(R.string.privacy_check_get_started_dialog_Not_Now),
                new PrivacyCheckGetStartedDialog.CancelListener(context, mismatch));
    }

    @Override
    public void show() {
        super.show();
        ((TextView) this.findViewById(android.R.id.message))
                .setMovementMethod(LinkMovementMethod.getInstance());
    }

    private class AcceptListener implements OnClickListener {

        private final IdentityKeyMismatch   mismatch;

        private AcceptListener(IdentityKeyMismatch mismatch){
            this.mismatch   = mismatch;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {

            Intent intent = new Intent(getContext(), PrivacyCheckActivity.class);
            intent.putExtra(PrivacyCheckActivity.ADDRESS_EXTRA, mismatch.getAddress());
            getContext().startActivity(intent);
        }
    }

    private class CancelListener implements OnClickListener {

        private final Context              context;
        private final IdentityKeyMismatch  mismatch;

        private CancelListener(Context context, IdentityKeyMismatch mismatch) {
            this.context       = context;
            this.mismatch      = mismatch;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {

            new PrivacyCheckRemindLaterDialog(context, mismatch).show();

        }
    }

}

//Devon code ends