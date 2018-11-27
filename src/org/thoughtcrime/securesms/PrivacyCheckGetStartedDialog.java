//Devon newWarn code starts

package org.thoughtcrime.securesms;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import org.thoughtcrime.securesms.crypto.storage.TextSecureIdentityKeyStore;
import org.thoughtcrime.securesms.database.Address;
import org.thoughtcrime.securesms.database.DatabaseFactory;
import org.thoughtcrime.securesms.database.MmsDatabase;
import org.thoughtcrime.securesms.database.MmsSmsDatabase;
import org.thoughtcrime.securesms.database.PushDatabase;
import org.thoughtcrime.securesms.database.SmsDatabase;
import org.thoughtcrime.securesms.database.documents.IdentityKeyMismatch;
import org.thoughtcrime.securesms.database.model.MessageRecord;
import org.thoughtcrime.securesms.jobs.PushDecryptJob;
import org.thoughtcrime.securesms.recipients.Recipient;
import org.thoughtcrime.securesms.sms.MessageSender;
import org.thoughtcrime.securesms.util.Base64;
import org.thoughtcrime.securesms.util.VerifySpan;
import org.whispersystems.libsignal.SignalProtocolAddress;
import org.whispersystems.signalservice.api.messages.SignalServiceEnvelope;
import org.whispersystems.signalservice.internal.push.SignalServiceProtos;

import java.io.IOException;

import static org.whispersystems.libsignal.SessionCipher.SESSION_LOCK;

public class PrivacyCheckGetStartedDialog extends AlertDialog {


    @SuppressWarnings("unused")
    private static final String TAG = PrivacyCheckGetStartedDialog.class.getSimpleName();

    private DialogInterface.OnClickListener callback;

    public PrivacyCheckGetStartedDialog(Context context, IdentityKeyMismatch mismatch) {
        super(context);

        Recipient recipient = Recipient.from(context, mismatch.getAddress(), false);
        String name = recipient.toShortString();
        String dialogBody = context.getString(R.string.privacy_check_get_started_dialog_There_is_a_small_chance) + "\n\n" +
                context.getString(R.string.privacy_check_get_started_dialog_If_this_is_important_to_you) + "\n\n" +
                String.format(context.getString(R.string.privacy_check_get_started_dialog_This_will_require_a_minute_or_two_of_your_time), name);


        setTitle(context.getString(R.string.privacy_check_get_started_dialog_title));
        setIcon(R.drawable.ic_warning_light);
        setMessage(dialogBody);

        setButton(AlertDialog.BUTTON_POSITIVE, context.getString(R.string.privacy_check_get_started_dialog_Get_Started), new PrivacyCheckGetStartedDialog.AcceptListener());
        setButton(AlertDialog.BUTTON_NEGATIVE, context.getString(R.string.privacy_check_get_started_dialog_Not_Now), new PrivacyCheckGetStartedDialog.CancelListener());
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
        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (callback != null) callback.onClick(null, 0);
        }
    }

    private class CancelListener implements OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (callback != null) callback.onClick(null, 0);
        }
    }

}

//Devon code ends