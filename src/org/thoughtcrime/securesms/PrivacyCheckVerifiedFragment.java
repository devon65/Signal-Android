//Devon newWarn code starts

package org.thoughtcrime.securesms;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.thoughtcrime.securesms.crypto.IdentityKeyParcelable;
import org.thoughtcrime.securesms.database.Address;
import org.thoughtcrime.securesms.database.DatabaseFactory;
import org.thoughtcrime.securesms.database.IdentityDatabase;
import org.thoughtcrime.securesms.jobs.MultiDeviceVerifiedUpdateJob;
import org.thoughtcrime.securesms.logging.Log;
import org.thoughtcrime.securesms.recipients.Recipient;
import org.thoughtcrime.securesms.util.IdentityUtil;
import org.whispersystems.libsignal.IdentityKey;

import static org.whispersystems.libsignal.SessionCipher.SESSION_LOCK;


public class PrivacyCheckVerifiedFragment extends Fragment {

    public static final String REMOTE_ADDRESS  = "remote_address";
    public static final String REMOTE_NUMBER   = "remote_number";
    public static final String REMOTE_IDENTITY = "remote_identity";
    public static final String RECIPIENT_NAME  = "recipient_name";
    public static final String LOCAL_IDENTITY  = "local_identity";
    public static final String LOCAL_NUMBER    = "local_number";


    private TextView viewQRCodeButton;
    private TextView viewIdentifiersButton;
    private TextView resetPrivacyStatus;
    private TextView screenTitle;

    private String      recipientName;
    private IdentityKey remoteIdentityKey;
    private Address     remoteAddress;
    private Recipient   recipient;


    private OnMarkContactAsUnverifiedListener mListener;

    public PrivacyCheckVerifiedFragment() {}


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            recipientName                   = getArguments().getString(RECIPIENT_NAME);
            IdentityKeyParcelable remoteKey = getArguments().getParcelable(REMOTE_IDENTITY);
            remoteIdentityKey               = remoteKey.get();
            remoteAddress                   = getArguments().getParcelable(REMOTE_ADDRESS);
            recipient                       = Recipient.from(getActivity(), remoteAddress, true);
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

        resetPrivacyStatus = (TextView) view.findViewById(R.id.privacy_check_verified_mark_contact_as_unverified);
        resetPrivacyStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.changeVerifiedStatus(IdentityDatabase.VerifiedStatus.DEFAULT, false);
                mListener.switchToUnverifiedFragment();
            }
        });

        screenTitle = (TextView) view.findViewById(R.id.privacy_check_verified_title_text);
        screenTitle.setText(String.format(getContext().getString(R.string.privacy_check_verified_screen_The_privacy_of_your_conversation_with_s_is_guaranteed), recipientName));

        return view;
    }

    private void viewQRCodeClicked(){
        //Toast.makeText(getContext(), "Ah Yeah!", Toast.LENGTH_LONG).show();
        mListener.onInPersonAuthentication();
    }

    private void viewIdentifiersClicked(){
        //Toast.makeText(getContext(), "Oh Baby!", Toast.LENGTH_LONG).show();
        mListener.onViewIdentifiers();
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnMarkContactAsUnverifiedListener) {
            mListener = (OnMarkContactAsUnverifiedListener) context;
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


    public interface OnMarkContactAsUnverifiedListener {
        void changeVerifiedStatus(IdentityDatabase.VerifiedStatus updatedVerifiedStatus, boolean markVerifiedStatusChange);
        void switchToUnverifiedFragment();
        void onInPersonAuthentication();
        void onViewIdentifiers();
    }
}

//Devon code ends