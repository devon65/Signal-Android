//Devon newWarn code starts
//bits of this file were copied from VerifyIdentityActivity

package org.thoughtcrime.securesms;

import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import org.thoughtcrime.securesms.color.MaterialColor;
import org.thoughtcrime.securesms.crypto.IdentityKeyParcelable;
import org.thoughtcrime.securesms.crypto.IdentityKeyUtil;
import org.thoughtcrime.securesms.database.Address;
import org.thoughtcrime.securesms.recipients.Recipient;
import org.thoughtcrime.securesms.util.TextSecurePreferences;

public class PrivacyCheckActivity extends AppCompatActivity {

    private static final String TAG = PrivacyCheckActivity.class.getSimpleName();

    public static final String ADDRESS_EXTRA  = "address";
    public static final String IDENTITY_EXTRA = "recipient_identity";
    public static final String VERIFIED_EXTRA = "verified_state";

    private Recipient recipient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.privacy_check);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.privacy_check_screen_header);

        this.recipient = Recipient.from(this, (Address)getIntent().getParcelableExtra(ADDRESS_EXTRA), true);
        setActionBarNotificationBarColor(recipient.getColor());

        /*Fragment displayFrag;
        Boolean isVerified = getIntent().getBooleanExtra(VERIFIED_EXTRA, false);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (isVerified){
            displayFrag = initializePrivacyCheckVerifiedFragment(recipient);
        }
        else{
            displayFrag = initializePrivacyCheckUnverifiedFragment(recipient);
        }

        transaction.replace(R.id.privacy_check_fragment_container, displayFrag);
        transaction.commit();*/

    }

    @Override
    protected void onResume() {
        super.onResume();
        Fragment displayFrag;
        Boolean isVerified = getIntent().getBooleanExtra(VERIFIED_EXTRA, false);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (isVerified){
            displayFrag = initializePrivacyCheckVerifiedFragment();
        }
        else{
            displayFrag = initializePrivacyCheckUnverifiedFragment();
        }

        transaction.replace(R.id.privacy_check_fragment_container, displayFrag);
        transaction.commit();
    }

    private Fragment initializePrivacyCheckVerifiedFragment(){
        Fragment verifiedFrag = new PrivacyCheckVerifiedFragment();
        Bundle extras = new Bundle();

        extras.putParcelable(PrivacyCheckVerifiedFragment.REMOTE_ADDRESS, getIntent().getParcelableExtra(ADDRESS_EXTRA));
        extras.putParcelable(PrivacyCheckVerifiedFragment.REMOTE_IDENTITY, getIntent().getParcelableExtra(IDENTITY_EXTRA));
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

        extras.putParcelable(PrivacyCheckUnverifiedFragment.REMOTE_ADDRESS, getIntent().getParcelableExtra(ADDRESS_EXTRA));
        extras.putParcelable(PrivacyCheckUnverifiedFragment.REMOTE_IDENTITY, getIntent().getParcelableExtra(IDENTITY_EXTRA));
        extras.putString(PrivacyCheckUnverifiedFragment.REMOTE_NUMBER, recipient.getAddress().toPhoneString());
        extras.putString(PrivacyCheckUnverifiedFragment.RECIPIENT_NAME, recipient.toShortString());
        extras.putParcelable(PrivacyCheckUnverifiedFragment.LOCAL_IDENTITY, new IdentityKeyParcelable(IdentityKeyUtil.getIdentityKey(this)));
        extras.putString(PrivacyCheckUnverifiedFragment.LOCAL_NUMBER, TextSecurePreferences.getLocalNumber(this));
        unverifiedFrag.setArguments(extras);

        return unverifiedFrag;
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



}

//Devon code ends