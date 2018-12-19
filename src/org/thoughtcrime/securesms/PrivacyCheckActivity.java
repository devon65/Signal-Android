//Devon newWarn code starts
//bits of this file were copied from VerifyIdentityActivity

package org.thoughtcrime.securesms;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import org.thoughtcrime.securesms.color.MaterialColor;
import org.thoughtcrime.securesms.crypto.IdentityKeyParcelable;
import org.thoughtcrime.securesms.crypto.IdentityKeyUtil;
import org.thoughtcrime.securesms.database.Address;
import org.thoughtcrime.securesms.database.IdentityDatabase;
import org.thoughtcrime.securesms.recipients.Recipient;
import org.thoughtcrime.securesms.util.CommunicationActions;
import org.thoughtcrime.securesms.util.IdentityUtil;
import org.thoughtcrime.securesms.util.TextSecurePreferences;
import org.thoughtcrime.securesms.util.concurrent.ListenableFuture;
import org.whispersystems.libsignal.IdentityKey;
import org.whispersystems.libsignal.util.guava.Optional;

import java.util.concurrent.ExecutionException;

public class PrivacyCheckActivity extends AppCompatActivity
        implements PrivacyCheckVerifiedFragment.OnMarkContactAsUnverifiedListener,
        PrivacyCheckUnverifiedFragment.OnUnverifiedInteractionListener,
        PrivacyCheckVeryUnverifiedFragment.OnVeryUnverifiedInteractionListener{

    private static final String TAG = PrivacyCheckActivity.class.getSimpleName();

    public static final String ADDRESS_EXTRA  = "address";

    private Recipient   recipient;
    private IdentityKey remoteIdentityKey;
    private int         verifiedStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.privacy_check);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.privacy_check_screen_header);

        this.recipient = Recipient.from(this, (Address)getIntent().getParcelableExtra(ADDRESS_EXTRA), true);
        setActionBarNotificationBarColor(recipient.getColor());


    }

    @Override
    protected void onResume() {
        super.onResume();

        IdentityUtil.getRemoteIdentityKey(this, this.recipient).addListener(new ListenableFuture.Listener<Optional<IdentityDatabase.IdentityRecord>>() {
            @Override
            public void onSuccess(Optional<IdentityDatabase.IdentityRecord> result) {
                IdentityDatabase.IdentityRecord remoteIdentity = result.get();
                remoteIdentityKey   = remoteIdentity.getIdentityKey();
                verifiedStatus      = remoteIdentity.getVerifiedStatus().toInt();

                Fragment displayFrag;
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();


                if (verifiedStatus == IdentityDatabase.VerifiedStatus.VERIFIED.toInt()){
                    displayFrag = initializePrivacyCheckVerifiedFragment();
                }
                else if (verifiedStatus == IdentityDatabase.VerifiedStatus.VERYUNVERIFIED.toInt()){
                    displayFrag = initializePrivacyCheckVeryUnverifiedFragment();
                }
                else {
                    displayFrag = initializePrivacyCheckUnverifiedFragment();
                }

                transaction.replace(R.id.privacy_check_fragment_container, displayFrag);
                transaction.commit();
            }

            @Override
            public void onFailure(ExecutionException e) {
                Toast.makeText(getApplicationContext(), R.string.toast_you_have_broken_the_study_key_not_found, Toast.LENGTH_LONG).show();
            }
        });

    }


    private Fragment initializePrivacyCheckVerifiedFragment(){
        Fragment verifiedFrag = new PrivacyCheckVerifiedFragment();
        Bundle extras = new Bundle();

        extras.putParcelable(PrivacyCheckVerifiedFragment.REMOTE_ADDRESS, recipient.getAddress());
        extras.putParcelable(PrivacyCheckVerifiedFragment.REMOTE_IDENTITY, new IdentityKeyParcelable(remoteIdentityKey));
        extras.putString(PrivacyCheckVerifiedFragment.REMOTE_NUMBER, recipient.getAddress().toPhoneString());
        extras.putString(PrivacyCheckVerifiedFragment.RECIPIENT_NAME, recipient.toShortString());
        extras.putParcelable(PrivacyCheckVerifiedFragment.LOCAL_IDENTITY, new IdentityKeyParcelable(IdentityKeyUtil.getIdentityKey(this)));
        extras.putString(PrivacyCheckVerifiedFragment.LOCAL_NUMBER, TextSecurePreferences.getLocalNumber(this));
        verifiedFrag.setArguments(extras);

        return verifiedFrag;
    }

    private Fragment initializePrivacyCheckUnverifiedFragment(){
        Fragment unverifiedFrag = new PrivacyCheckUnverifiedFragment();
        Bundle extras = new Bundle();

        extras.putParcelable(PrivacyCheckUnverifiedFragment.REMOTE_ADDRESS, recipient.getAddress());
        extras.putParcelable(PrivacyCheckUnverifiedFragment.REMOTE_IDENTITY, new IdentityKeyParcelable(remoteIdentityKey));
        extras.putString(PrivacyCheckUnverifiedFragment.REMOTE_NUMBER, recipient.getAddress().toPhoneString());
        extras.putString(PrivacyCheckUnverifiedFragment.RECIPIENT_NAME, recipient.toShortString());
        extras.putParcelable(PrivacyCheckUnverifiedFragment.LOCAL_IDENTITY, new IdentityKeyParcelable(IdentityKeyUtil.getIdentityKey(this)));
        extras.putString(PrivacyCheckUnverifiedFragment.LOCAL_NUMBER, TextSecurePreferences.getLocalNumber(this));
        unverifiedFrag.setArguments(extras);

        return unverifiedFrag;
    }

    private Fragment initializePrivacyCheckVeryUnverifiedFragment(){
        Fragment veryUnverifiedFrag = new PrivacyCheckVeryUnverifiedFragment();
        Bundle extras = new Bundle();

        extras.putParcelable(PrivacyCheckVeryUnverifiedFragment.REMOTE_ADDRESS, recipient.getAddress());
        extras.putParcelable(PrivacyCheckVeryUnverifiedFragment.REMOTE_IDENTITY, new IdentityKeyParcelable(remoteIdentityKey));
        extras.putString(PrivacyCheckVeryUnverifiedFragment.REMOTE_NUMBER, recipient.getAddress().toPhoneString());
        extras.putString(PrivacyCheckVeryUnverifiedFragment.RECIPIENT_NAME, recipient.toShortString());
        extras.putParcelable(PrivacyCheckVeryUnverifiedFragment.LOCAL_IDENTITY, new IdentityKeyParcelable(IdentityKeyUtil.getIdentityKey(this)));
        extras.putString(PrivacyCheckVeryUnverifiedFragment.LOCAL_NUMBER, TextSecurePreferences.getLocalNumber(this));
        veryUnverifiedFrag.setArguments(extras);

        return veryUnverifiedFrag;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: finish(); return true;
        }

        return false;
    }


    private void setActionBarNotificationBarColor(MaterialColor color) {
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(color.toActionBarColor(this)));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(color.toStatusBarColor(this));
        }
    }


    @Override
    public void switchToUnverifiedFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.privacy_check_fragment_container, initializePrivacyCheckUnverifiedFragment());
        transaction.commit();
    }

    @Override
    public void onInPersonAuthentication() {
        Intent intent = new Intent(getApplicationContext(), PrivacyCheckQRScannerActivity.class);
        intent.putExtra(PrivacyCheckQRScannerActivity.ADDRESS_EXTRA, recipient.getAddress());
        intent.putExtra(PrivacyCheckQRScannerActivity.IDENTITY_EXTRA, new IdentityKeyParcelable(remoteIdentityKey));
        getApplicationContext().startActivity(intent);
    }

    @Override
    public void onViewIdentifiers() {
        new PrivacyCheckViewIdentifiersDialog(this, recipient, IdentityKeyUtil.getIdentityKey(this), remoteIdentityKey).show();
    }

    @Override
    public void onPhoneCallAuthentication() {
        CommunicationActions.startVoiceCall(this, this.recipient);
    }
}

//Devon code ends