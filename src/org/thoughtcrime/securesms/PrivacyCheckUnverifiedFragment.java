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

    //private OnFragmentInteractionListener mListener;

    public PrivacyCheckUnverifiedFragment() {}



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
        View view = inflater.inflate(R.layout.fragment_privacy_check_unverified, container, false);

        notNowButton = (TextView) view.findViewById(R.id.privacy_check_unverified_not_now);
        notNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notNowClicked();
            }
        });

        inPersonButton = (TextView) view.findViewById(R.id.privacy_check_unverified_in_person);
        inPersonButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inPersonClicked();
            }
        });

        phoneCallButton = (TextView) view.findViewById(R.id.privacy_check_unverified_phone_call);
        phoneCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                phoneCallClicked();
            }
        });

        youAndFriendText = (TextView) view.findViewById(R.id.privacy_check_unverified_you_and_friend_text);
        youAndFriendText.setText(String.format(getContext().getString(R.string.privacy_check_unverified_screen_You_and_s_can_compare_your_copy_of_each_others_identifiers), recipientName));

        ifTheyMatchText = (TextView) view.findViewById(R.id.privacy_check_unverified_if_they_match_text);
        ifTheyMatchText.setText(String.format(getContext().getString(R.string.privacy_check_unverified_screen_If_they_match), recipientName));

        toCompareIdentifiersText = (TextView) view.findViewById(R.id.privacy_check_unverified_to_compare_identifiers_text);
        toCompareIdentifiersText.setText(String.format(getContext().getString(R.string.privacy_check_unverified_screen_To_compare_your_identifier_with_s), recipientName));

        return view;
    }

    private void notNowClicked(){
        Toast.makeText(getContext(), "Rockin\' it!", Toast.LENGTH_LONG).show();
    }

    private void inPersonClicked(){
        Toast.makeText(getContext(), "So Sexy!", Toast.LENGTH_LONG).show();
    }

    private void phoneCallClicked(){
        Toast.makeText(getContext(), "Smashing!", Toast.LENGTH_LONG).show();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        /*if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    /*public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }*/
}

//Devon code ends
