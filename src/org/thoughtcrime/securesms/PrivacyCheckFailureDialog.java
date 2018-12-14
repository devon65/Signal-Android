//Devon newWarn code starts

package org.thoughtcrime.securesms;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class PrivacyCheckFailureDialog extends AlertDialog {

    @SuppressWarnings("unused")
    private static final String TAG = PrivacyCheckFailureDialog.class.getSimpleName();

    public PrivacyCheckFailureDialog(Context context, String name, String whichAuthCeremony, PrivacyCheckFailureListener failureListener)
    {
        super(context);

        String identifiersDoNotMatch;
        String ifYoureSure = String.format(context.getString(R.string.privacy_check_failure_dialog_If_youre_sure_you_compared_s_identifier), name);

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_privacy_check_failure, null);

        if (whichAuthCeremony.equals(PrivacyCheckQRScannerActivity.class.getSimpleName())) {
            identifiersDoNotMatch = String.format(context.getString(R.string.privacy_check_failure_dialog_The_identifiers_do_not_match), name);
        }

        else {
            identifiersDoNotMatch = String.format(context.getString(R.string.privacy_check_failure_dialog_You_have_found_that_your_identifiers_with_s_do_not_match), name);
        }


        TextView identifiersNoMatchView = view.findViewById(R.id.privacy_check_failure_identifiers_do_not_match);
        identifiersNoMatchView.setText(identifiersDoNotMatch);

        TextView ifYourSureView = view.findViewById(R.id.privacy_check_failure_if_youre_sure);
        ifYourSureView.setText(ifYoureSure);


        setTitle(R.string.privacy_check_failure_dialog_title);
        setIcon(R.drawable.ic_warning_light);
        setView(view);

        setButton(AlertDialog.BUTTON_POSITIVE, context.getString(R.string.privacy_check_failure_dialog_Im_sure), new PrivacyCheckFailureDialog.AcceptListener(failureListener));
        setButton(AlertDialog.BUTTON_NEGATIVE, context.getString(R.string.privacy_check_failure_dialog_Try_Again), new PrivacyCheckFailureDialog.CancelListener(failureListener));

    }

    private class AcceptListener implements OnClickListener {

        private PrivacyCheckFailureListener failureListener;

        public AcceptListener(PrivacyCheckFailureListener failureListener){
            this.failureListener = failureListener;
        }


        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            failureListener.onMatchFailedImSureClicked();
        }
    }

    private class CancelListener implements OnClickListener {
        private PrivacyCheckFailureListener failureListener;

        public CancelListener(PrivacyCheckFailureListener failureListener){
            this.failureListener = failureListener;
        }


        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            failureListener.onMatchFailedTryAgainClicked();
        }
    }

    public interface PrivacyCheckFailureListener{
        void onMatchFailedTryAgainClicked();
        void onMatchFailedImSureClicked();
    }
    @Override
    public void show() {
        super.show();
        ((TextView)this.findViewById(android.R.id.message))
                .setMovementMethod(LinkMovementMethod.getInstance());
    }
}

//Devon code ends