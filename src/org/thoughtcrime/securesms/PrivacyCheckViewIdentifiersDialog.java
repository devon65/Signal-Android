//Devon newWarn code starts

package org.thoughtcrime.securesms;

import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import org.thoughtcrime.securesms.database.documents.IdentityKeyMismatch;
import org.thoughtcrime.securesms.database.model.MessageRecord;
import org.thoughtcrime.securesms.recipients.Recipient;
import org.thoughtcrime.securesms.util.TextSecurePreferences;
import org.whispersystems.libsignal.IdentityKey;

@SuppressLint("StaticFieldLeak")
public class PrivacyCheckViewIdentifiersDialog extends AlertDialog {
    private static final String TAG = PrivacyCheckViewIdentifiersDialog.class.getSimpleName();

    private String       recipientName;
    private String       localNumber;
    private String       remoteNumber;

    private IdentityKey localIdentity;
    private IdentityKey remoteIdentity;

    private PrivacyCheckDisplayableFingerprint fingerprint;

    private TextView[] myCodes              = new TextView[10];
    private TextView[] thyCodes             = new TextView[10];

    public PrivacyCheckViewIdentifiersDialog(Context context, Recipient recipient, IdentityKey localIdentity, IdentityKey remoteIdentity)
    {
        super(context);

        this.recipientName      = recipient.toShortString();
        this.localNumber        = TextSecurePreferences.getLocalNumber(context);
        this.remoteNumber       = recipient.getAddress().toPhoneString();
        this.localIdentity      = localIdentity;
        this.remoteIdentity     = remoteIdentity;

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_privacy_check_view_identifiers, null);
        TextView friendIdentifierTitle = view.findViewById(R.id.phone_call_friend_device_identifier_dialog);
        friendIdentifierTitle.setText(String.format(getContext().getString(R.string.phone_call_screen_s_device_identifier), recipientName));

        view = setIdentifiersView(view);
        setView(view);
        setTitle(R.string.privacy_check_verified_screen_view_identifiers_dialog_title);

        setButton(AlertDialog.BUTTON_POSITIVE, context.getString(R.string.privacy_check_verified_screen_view_identifiers_dialog_Done), (dialogInterface, i) -> dismiss());
    }

    private View setIdentifiersView(View view){
        this.myCodes[0]         = view.findViewById(R.id.my_code_first_dialog);
        this.myCodes[1]         = view.findViewById(R.id.my_code_second_dialog);
        this.myCodes[2]         = view.findViewById(R.id.my_code_third_dialog);
        this.myCodes[3]         = view.findViewById(R.id.my_code_fourth_dialog);
        this.myCodes[4]         = view.findViewById(R.id.my_code_fifth_dialog);
        this.myCodes[5]         = view.findViewById(R.id.my_code_sixth_dialog);
        this.myCodes[6]         = view.findViewById(R.id.my_code_seventh_dialog);
        this.myCodes[7]         = view.findViewById(R.id.my_code_eighth_dialog);
        this.myCodes[8]         = view.findViewById(R.id.my_code_ninth_dialog);
        this.myCodes[9]         = view.findViewById(R.id.my_code_tenth_dialog);

        this.thyCodes[0]         = view.findViewById(R.id.thy_code_first_dialog);
        this.thyCodes[1]         = view.findViewById(R.id.thy_code_second_dialog);
        this.thyCodes[2]         = view.findViewById(R.id.thy_code_third_dialog);
        this.thyCodes[3]         = view.findViewById(R.id.thy_code_fourth_dialog);
        this.thyCodes[4]         = view.findViewById(R.id.thy_code_fifth_dialog);
        this.thyCodes[5]         = view.findViewById(R.id.thy_code_sixth_dialog);
        this.thyCodes[6]         = view.findViewById(R.id.thy_code_seventh_dialog);
        this.thyCodes[7]         = view.findViewById(R.id.thy_code_eighth_dialog);
        this.thyCodes[8]         = view.findViewById(R.id.thy_code_ninth_dialog);
        this.thyCodes[9]         = view.findViewById(R.id.thy_code_tenth_dialog);

        new AsyncTask<Void, Void, PrivacyCheckDisplayableFingerprint>() {
            @Override
            protected PrivacyCheckDisplayableFingerprint doInBackground(Void... params) {

                //Changing the remoteIdentity that is fed into the fingerprint generator
                //to fake a new "safety number"

                /*IsMITMAttackOn isMITMAttackOn = new IsMITMAttackOn();
                if (isMITMAttackOn.isSafetyNumberChanged()) {
                    return new PrivacyCheckNumericFingerprintGenerator(5200).createFor(localNumber, localIdentity,
                            remoteNumber, isMITMAttackOn.getFakeKey());
                }*/

                return new PrivacyCheckNumericFingerprintGenerator(5200).createFor(localNumber, localIdentity,
                        remoteNumber, remoteIdentity);
            }

            @Override
            protected void onPostExecute(PrivacyCheckDisplayableFingerprint fingerprint) {
                PrivacyCheckViewIdentifiersDialog.this.fingerprint = fingerprint;
                setFingerprintViews(fingerprint, true);
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        return view;
    }

    private void setFingerprintViews(PrivacyCheckDisplayableFingerprint fingerprint, boolean animate) {
        String[] myNumSegments  = getSegments(fingerprint.getLocalFingerprintNumbers(), myCodes.length);
        String[] thyNumSegments = getSegments(fingerprint.getRemoteFingerprintNumbers(), thyCodes.length);

        for (int i = 0; i < myCodes.length; i++) {
            if (animate) setCodeSegment(myCodes[i], myNumSegments[i]);
            else myCodes[i].setText(myNumSegments[i]);
        }

        for (int i = 0; i < thyCodes.length; i++) {
            if (animate) setCodeSegment(thyCodes[i], thyNumSegments[i]);
            else thyCodes[i].setText(thyNumSegments[i]);
        }
    }

    private void setCodeSegment(final TextView codeView, String segment) {
        if (Build.VERSION.SDK_INT >= 11) {
            ValueAnimator valueAnimator = new ValueAnimator();
            valueAnimator.setObjectValues(0, Integer.parseInt(segment));

            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int value = (int) animation.getAnimatedValue();
                    codeView.setText(String.format("%03d", value));
                }
            });

            valueAnimator.setEvaluator(new TypeEvaluator<Integer>() {
                public Integer evaluate(float fraction, Integer startValue, Integer endValue) {
                    return Math.round(startValue + (endValue - startValue) * fraction);
                }
            });

            valueAnimator.setDuration(1000);
            valueAnimator.start();
        } else {
            codeView.setText(segment);
        }
    }

    private String[] getSegments(String digits, int segmentCount) {
        String[] segments = new String[segmentCount];
        int      partSize = digits.length() / segmentCount;

        for (int i=0;i<segmentCount;i++) {
            segments[i] = digits.substring(i * partSize, (i * partSize) + partSize);
        }

        return segments;
    }

    @Override
    public void show() {
        super.show();
        ((TextView)this.findViewById(android.R.id.message))
                .setMovementMethod(LinkMovementMethod.getInstance());
    }
}

//Devon code ends