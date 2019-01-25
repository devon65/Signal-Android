//Cyrus new-warning code start
//Edits by Devon
package org.thoughtcrime.securesms;

import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.thoughtcrime.securesms.crypto.IdentityKeyParcelable;
import org.thoughtcrime.securesms.database.Address;
import org.thoughtcrime.securesms.recipients.Recipient;
import org.thoughtcrime.securesms.util.ViewUtil;
import org.whispersystems.libsignal.IdentityKey;

@SuppressLint("StaticFieldLeak")
public class PhoneCallPrivacyCheckFragment extends Fragment
{

    public static final String REMOTE_ADDRESS  = "remote_address";
    public static final String REMOTE_NUMBER   = "remote_number";
    public static final String REMOTE_IDENTITY = "remote_identity";
    public static final String LOCAL_IDENTITY  = "local_identity";
    public static final String LOCAL_NUMBER    = "local_number";

    private Recipient    recipient;
    private String       recipientName;
    private String       localNumber;
    private String       remoteNumber;

    private IdentityKey localIdentity;
    private IdentityKey remoteIdentity;

    private PrivacyCheckDisplayableFingerprint fingerprint;

    private View                 container;
    private View                 myNumbersContainer;
    private View                 thyNumbersContainer;

    private TextView[] myCodes              = new TextView[10];
    private TextView[] thyCodes             = new TextView[10];
    private boolean    animateSuccessOnDraw = false;
    private boolean    animateFailureOnDraw = false;

    private PhoneCallFragmentListener mListener;

    public PhoneCallPrivacyCheckFragment() {}



    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Address address                                = getArguments().getParcelable(REMOTE_ADDRESS);
        IdentityKeyParcelable localIdentityParcelable  = getArguments().getParcelable(LOCAL_IDENTITY);
        IdentityKeyParcelable remoteIdentityParcelable = getArguments().getParcelable(REMOTE_IDENTITY);

        if (address == null)                  throw new AssertionError("Address required");
        if (localIdentityParcelable == null)  throw new AssertionError("local identity required");
        if (remoteIdentityParcelable == null) throw new AssertionError("remote identity required");

        this.localNumber    = getArguments().getString(LOCAL_NUMBER);
        this.localIdentity  = localIdentityParcelable.get();
        this.remoteNumber   = getArguments().getString(REMOTE_NUMBER);
        this.recipient      = Recipient.from(getActivity(), address, true);
        this.remoteIdentity = remoteIdentityParcelable.get();
        this.recipientName  = recipient.getName();

        //this.recipient.addListener(this);

        new AsyncTask<Void, Void, PrivacyCheckDisplayableFingerprint>() {
            @Override
            protected PrivacyCheckDisplayableFingerprint doInBackground(Void... params) {

                //Changing the remoteIdentity that is fed into the fingerprint generator
                //to fake a new "safety number"

                IsMITMAttackOn isMITMAttackOn = IsMITMAttackOn.getInstance();
                if (isMITMAttackOn.isSafetyNumberChanged() && !isMITMAttackOn.isTesting()) {
                    return new PrivacyCheckNumericFingerprintGenerator(5200).createFor(localNumber, localIdentity,
                            remoteNumber, isMITMAttackOn.getFakeKey());
                }

                //Devon code ends here

                return new PrivacyCheckNumericFingerprintGenerator(5200).createFor(localNumber, localIdentity,
                        remoteNumber, remoteIdentity);
            }

            @Override
            protected void onPostExecute(PrivacyCheckDisplayableFingerprint fingerprint) {
                PhoneCallPrivacyCheckFragment.this.fingerprint = fingerprint;
                setFingerprintViews(fingerprint, true);
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup,
                             Bundle savedInstanceState)
    {
        this.container        = ViewUtil.inflate(inflater, viewGroup, R.layout.fragment_phone_call_privacy_check);
        this.myNumbersContainer = ViewUtil.findById(container, R.id.my_number_table);
        this.myCodes[0]         = ViewUtil.findById(container, R.id.my_code_first);
        this.myCodes[1]         = ViewUtil.findById(container, R.id.my_code_second);
        this.myCodes[2]         = ViewUtil.findById(container, R.id.my_code_third);
        this.myCodes[3]         = ViewUtil.findById(container, R.id.my_code_fourth);
        this.myCodes[4]         = ViewUtil.findById(container, R.id.my_code_fifth);
        this.myCodes[5]         = ViewUtil.findById(container, R.id.my_code_sixth);
        this.myCodes[6]         = ViewUtil.findById(container, R.id.my_code_seventh);
        this.myCodes[7]         = ViewUtil.findById(container, R.id.my_code_eighth);
        this.myCodes[8]         = ViewUtil.findById(container, R.id.my_code_ninth);
        this.myCodes[9]         = ViewUtil.findById(container, R.id.my_code_tenth);

        this.thyCodes[0]         = ViewUtil.findById(container, R.id.thy_code_first);
        this.thyCodes[1]         = ViewUtil.findById(container, R.id.thy_code_second);
        this.thyCodes[2]         = ViewUtil.findById(container, R.id.thy_code_third);
        this.thyCodes[3]         = ViewUtil.findById(container, R.id.thy_code_fourth);
        this.thyCodes[4]         = ViewUtil.findById(container, R.id.thy_code_fifth);
        this.thyCodes[5]         = ViewUtil.findById(container, R.id.thy_code_sixth);
        this.thyCodes[6]         = ViewUtil.findById(container, R.id.thy_code_seventh);
        this.thyCodes[7]         = ViewUtil.findById(container, R.id.thy_code_eighth);
        this.thyCodes[8]         = ViewUtil.findById(container, R.id.thy_code_ninth);
        this.thyCodes[9]         = ViewUtil.findById(container, R.id.thy_code_tenth);

        TextView theseAreTheyText   = ViewUtil.findById(container, R.id.phone_call_these_are_the_device_identifiers);
        TextView haveFriendReadText = ViewUtil.findById(container, R.id.phone_call_have_friend_read_their_identifier);
        TextView friendIdTitle      = ViewUtil.findById(container, R.id.phone_call_friend_device_identifier);
        ImageView matchButton       = ViewUtil.findById(container, R.id.phone_call_match_button);
        ImageView noMatchButton     = ViewUtil.findById(container, R.id.phone_call_no_match_button);

        theseAreTheyText.setText(String.format(getContext().getString(R.string.phone_call_screen_These_are_the_device_identifiers_for_you_and_s), recipientName));
        haveFriendReadText.setText(String.format(getContext().getString(R.string.phone_call_screen_Have_s_read_their_identifier_to_you), recipientName, recipientName));
        friendIdTitle.setText(String.format(getContext().getString(R.string.phone_call_screen_s_device_identifier), recipientName));

        matchButton.setOnClickListener((View v) -> {
            mListener.onMatchButtonClicked();
        });

        noMatchButton.setOnClickListener((View v) -> {
            mListener.onNoMatchButtonClicked();
        });

        //this.registerForContextMenu(myNumbersContainer);
        //this.registerForContextMenu(thyNumbersContainer);

        return container;
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
    public void onAttach(Context context)
    {
        super.onAttach(context);
        if (context instanceof PhoneCallFragmentListener)
        {
            mListener = (PhoneCallFragmentListener) context;
        } else
        {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        mListener = null;
    }


    public interface PhoneCallFragmentListener
    {
        void onMatchButtonClicked();
        void onNoMatchButtonClicked();
    }
}
