//Devon newWarn code starts

package org.thoughtcrime.securesms;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;


public class PrivacyCheckSuccessDialog extends AlertDialog{

    @SuppressWarnings("unused")
    private static final String TAG = PrivacyCheckSuccessDialog.class.getSimpleName();

    private OnClickListener callback;

    public PrivacyCheckSuccessDialog(Context context, String name, String whichAuthCeremony)
    {
        super(context);

        String congratulations = String.format(context.getString(R.string.privacy_check_privacy_verified_dialog_Congratulations), name);
        String markedAsVerified = String.format(context.getString(R.string.privacy_check_privacy_verified_dialog_S_has_been_marked_as_verified), name);

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_privacy_check_success, null);

        TextView congratsView = view.findViewById(R.id.privacy_check_success_congratulations);
        congratsView.setText(congratulations);

        TextView markedAsVerifiedView = view.findViewById(R.id.privacy_check_success_marked_as_verified);
        markedAsVerifiedView.setText(markedAsVerified);


        setTitle(R.string.privacy_check_privacy_verified_dialog_title);
        setView(view);


        if (whichAuthCeremony.equals(PrivacyCheckQRScannerActivity.class.getSimpleName())) {
            setButton(AlertDialog.BUTTON_POSITIVE, context.getString(R.string.privacy_check_privacy_verified_dialog_Back_to_QR_code), new PrivacyCheckSuccessDialog.AcceptListener());
        }

        else {
            setButton(AlertDialog.BUTTON_POSITIVE, context.getString(R.string.privacy_check_privacy_verified_dialog_Back_to_phone_call), new PrivacyCheckSuccessDialog.AcceptListener());
        }

    }

        private class AcceptListener implements OnClickListener {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (callback != null) callback.onClick(null, 0);
            }
        }

    @Override
    public void show() {
        super.show();
        ((TextView)this.findViewById(android.R.id.message))
                .setMovementMethod(LinkMovementMethod.getInstance());
    }
}

//Devon code ends