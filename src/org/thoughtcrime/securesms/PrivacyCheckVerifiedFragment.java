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


public class PrivacyCheckVerifiedFragment extends Fragment {

    public static final String REMOTE_ADDRESS  = "remote_address";
    public static final String REMOTE_NUMBER   = "remote_number";
    public static final String REMOTE_IDENTITY = "remote_identity";
    public static final String RECIPIENT_NAME  = "recipient_name";
    public static final String LOCAL_IDENTITY  = "local_identity";
    public static final String LOCAL_NUMBER    = "local_number";


    private TextView viewQRCodeButton;
    private TextView viewIdentifiersButton;
    private TextView markContactAsUnverifiedButton;
    private TextView screenTitle;

    private String recipientName;

    //private OnFragmentInteractionListener mListener;

    public PrivacyCheckVerifiedFragment() {}


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
        View view = inflater.inflate(R.layout.fragment_privacy_check_verified, container, false);

        viewQRCodeButton = (TextView) view.findViewById(R.id.privacy_check_verified_view_qr_code);
        viewQRCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewQRCodeClicked();
            }
        });

        viewIdentifiersButton = (TextView) view.findViewById(R.id.privacy_check_verified_view_identifiers);
        viewIdentifiersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewIdentifiersClicked();
            }
        });

        markContactAsUnverifiedButton = (TextView) view.findViewById(R.id.privacy_check_verified_mark_contact_as_unverified);
        markContactAsUnverifiedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                markContactAsVerifiedClicked();
            }
        });

        screenTitle = (TextView) view.findViewById(R.id.privacy_check_verified_title_text);
        screenTitle.setText(String.format(getContext().getString(R.string.privacy_check_verified_screen_The_privacy_of_your_conversation_with_s_is_guaranteed), recipientName));

        return view;
    }

    private void viewQRCodeClicked(){
        Toast.makeText(getContext(), "Ah Yeah!", Toast.LENGTH_LONG).show();
    }

    private void viewIdentifiersClicked(){
        Toast.makeText(getContext(), "Oh Baby!", Toast.LENGTH_LONG).show();
    }

    private void markContactAsVerifiedClicked(){
        Toast.makeText(getContext(), "Que Suave!", Toast.LENGTH_LONG).show();
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