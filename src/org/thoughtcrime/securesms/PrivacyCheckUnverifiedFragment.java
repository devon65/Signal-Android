//Devon newWarn code starts
package org.thoughtcrime.securesms;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.thoughtcrime.securesms.database.IdentityDatabase;


public class PrivacyCheckUnverifiedFragment extends Fragment {

    public static final String REMOTE_ADDRESS  = "remote_address";
    public static final String REMOTE_NUMBER   = "remote_number";
    public static final String REMOTE_IDENTITY = "remote_identity";
    public static final String RECIPIENT_NAME  = "recipient_name";
    public static final String LOCAL_IDENTITY  = "local_identity";
    public static final String LOCAL_NUMBER    = "local_number";

    private TextView notNowButton;
    private TextView inPersonButton;
    private TextView phoneCallButton;
    private TextView youAndFriendText;
    private TextView ifTheyMatchText;
    private TextView toCompareIdentifiersText;

    private String recipientName;

    private OnUnverifiedInteractionListener mListener;

    public PrivacyCheckUnverifiedFragment() {}



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            recipientName  = getArguments().getString(RECIPIENT_NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_privacy_check_unverified, container, false);

        notNowButton = view.findViewById(R.id.privacy_check_unverified_not_now);
        notNowButton.setOnClickListener(view1 -> notNowClicked());

        inPersonButton = view.findViewById(R.id.privacy_check_unverified_in_person);
        inPersonButton.setOnClickListener(view12 -> inPersonClicked());

        phoneCallButton = view.findViewById(R.id.privacy_check_unverified_phone_call);
        phoneCallButton.setOnClickListener(view13 -> phoneCallClicked());

        youAndFriendText = view.findViewById(R.id.privacy_check_unverified_you_and_friend_text);
        youAndFriendText.setText(String.format(getContext().getString(R.string.privacy_check_unverified_screen_You_and_s_can_compare_your_copy_of_each_others_identifiers), recipientName));

        ifTheyMatchText = view.findViewById(R.id.privacy_check_unverified_if_they_match_text);
        ifTheyMatchText.setText(String.format(getContext().getString(R.string.privacy_check_unverified_screen_If_they_match), recipientName));

        toCompareIdentifiersText = view.findViewById(R.id.privacy_check_unverified_to_compare_identifiers_text);
        toCompareIdentifiersText.setText(String.format(getContext().getString(R.string.privacy_check_unverified_screen_To_compare_your_identifier_with_s), recipientName));

        return view;
    }

    private void notNowClicked(){
        mListener.onNotNowClicked();
    }

    private void inPersonClicked(){
        //Toast.makeText(getContext(), "So Sexy!", Toast.LENGTH_LONG).show();
        if (mListener.getVerifiedStatus() == IdentityDatabase.VerifiedStatus.UNVERIFIED.toInt()) {
            mListener.changeVerifiedStatus(IdentityDatabase.VerifiedStatus.DEFAULT, false);
        }
        mListener.onInPersonAuthentication();
    }

    private void phoneCallClicked(){
        //Toast.makeText(getContext(), "Smashing!", Toast.LENGTH_LONG).show();
        if (mListener.getVerifiedStatus() == IdentityDatabase.VerifiedStatus.UNVERIFIED.toInt()) {
            mListener.changeVerifiedStatus(IdentityDatabase.VerifiedStatus.DEFAULT, false);
        }
        mListener.onPhoneCallAuthentication();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnUnverifiedInteractionListener) {
            mListener = (OnUnverifiedInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnUnverifiedInteractionListener {
        int getVerifiedStatus();
        void onNotNowClicked();
        void changeVerifiedStatus(IdentityDatabase.VerifiedStatus updatedVerifiedStatus, boolean markVerifiedStatusChange);
        void onInPersonAuthentication();
        void onPhoneCallAuthentication();
    }
}

//Devon code ends
