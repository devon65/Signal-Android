//Devon newWarn code starts
package org.thoughtcrime.securesms;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;


public class PrivacyCheckVeryUnverifiedFragment extends Fragment {

    public static final String REMOTE_ADDRESS  = "remote_address";
    public static final String REMOTE_NUMBER   = "remote_number";
    public static final String REMOTE_IDENTITY = "remote_identity";
    public static final String RECIPIENT_NAME  = "recipient_name";
    public static final String LOCAL_IDENTITY  = "local_identity";
    public static final String LOCAL_NUMBER    = "local_number";

    private TextView inPersonButton;
    private TextView phoneCallButton;
    private TextView titleText;
    private TextView completedPrivacyCheck;

    private String recipientName;

    private OnVeryUnverifiedInteractionListener mListener;

    public PrivacyCheckVeryUnverifiedFragment() {}


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            recipientName = getArguments().getString(RECIPIENT_NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_privacy_check_very_unverified, container, false);

        inPersonButton = (TextView) view.findViewById(R.id.privacy_check_very_unverified_in_person);
        inPersonButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inPersonClicked();
            }
        });

        phoneCallButton = (TextView) view.findViewById(R.id.privacy_check_very_unverified_phone_call);
        phoneCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                phoneCallClicked();
            }
        });

        titleText = (TextView) view.findViewById(R.id.privacy_check_very_unverified_title_text);
        titleText.setText(String.format(getContext().getString(R.string.privacy_check_very_unverified_screen_Do_not_communicate_any_sensitive_information),recipientName));

        completedPrivacyCheck = (TextView) view.findViewById(R.id.privacy_check_very_unverified_you_have_completed_the_privacy_check);
        completedPrivacyCheck.setText(String.format(getContext().getString(R.string.privacy_check_very_unverified_screen_You_have_completed_the_privacy_check), recipientName));

        return view;
    }

    private void inPersonClicked(){
        //Toast.makeText(getContext(), "Whoopee!", Toast.LENGTH_LONG).show();
        mListener.onInPersonAuthentication();
    }

    private void phoneCallClicked(){
        //Toast.makeText(getContext(), "Yeahooo!", Toast.LENGTH_LONG).show();
        mListener.onPhoneCallAuthentication();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnVeryUnverifiedInteractionListener) {
            mListener = (OnVeryUnverifiedInteractionListener) context;
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


    public interface OnVeryUnverifiedInteractionListener {
        void onInPersonAuthentication();
        void onPhoneCallAuthentication();
    }
}

//Devon code ends