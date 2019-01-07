//Devon Comment: This Activity is a copy of Elham Vaziripor's version of the QR scanner made for
//user studies in the Internet Research Lab at Brigham Young University

/*
 * Copyright (C) 2016-2017 Open Whisper Systems
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.thoughtcrime.securesms;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.Toast;

import org.thoughtcrime.securesms.color.MaterialColor;
import org.thoughtcrime.securesms.components.camera.CameraView;
import org.thoughtcrime.securesms.crypto.IdentityKeyParcelable;
import org.thoughtcrime.securesms.crypto.IdentityKeyUtil;
import org.thoughtcrime.securesms.database.Address;
import org.thoughtcrime.securesms.database.DatabaseFactory;
import org.thoughtcrime.securesms.database.IdentityDatabase.VerifiedStatus;
import org.thoughtcrime.securesms.permissions.Permissions;
import org.thoughtcrime.securesms.qr.QrCode;
import org.thoughtcrime.securesms.qr.ScanListener;
import org.thoughtcrime.securesms.qr.ScanningThread;
import org.thoughtcrime.securesms.recipients.Recipient;
import org.thoughtcrime.securesms.recipients.RecipientModifiedListener;
import org.thoughtcrime.securesms.util.DynamicLanguage;
import org.thoughtcrime.securesms.util.DynamicTheme;
import org.thoughtcrime.securesms.util.TextSecurePreferences;
import org.thoughtcrime.securesms.util.Util;
import org.thoughtcrime.securesms.util.ViewUtil;
import org.whispersystems.libsignal.IdentityKey;
import org.whispersystems.libsignal.fingerprint.Fingerprint;
import org.whispersystems.libsignal.fingerprint.FingerprintParsingException;
import org.whispersystems.libsignal.fingerprint.FingerprintVersionMismatchException;
import org.whispersystems.libsignal.fingerprint.NumericFingerprintGenerator;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;


/**
 * Activity for verifying identity keys.
 *
 * @author Moxie Marlinspike
 * Edits by Devon Howard
 */
@SuppressLint("StaticFieldLeak")
public class PrivacyCheckQRScannerActivity extends PassphraseRequiredActionBarActivity implements RecipientModifiedListener, ScanListener {

    private static final String TAG = PrivacyCheckQRScannerActivity.class.getSimpleName();

    public static final String ADDRESS_EXTRA  = "address";
    public static final String IDENTITY_EXTRA = "recipient_identity";

    private final DynamicTheme    dynamicTheme    = new DynamicTheme();
    private final DynamicLanguage dynamicLanguage = new DynamicLanguage();

    //private VerifyDisplayFragment displayFragment = new VerifyDisplayFragment();
    private VerifyScanFragment    scanFragment    = new VerifyScanFragment();

    @Override
    public void onPreCreate() {
        dynamicTheme.onCreate(this);
        dynamicLanguage.onCreate(this);
    }

    @Override
    protected void onCreate(Bundle state, boolean ready) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.privacy_check_screen_header);

        Recipient recipient = Recipient.from(this, (Address)getIntent().getParcelableExtra(ADDRESS_EXTRA), true);
        recipient.addListener(this);

        setActionBarNotificationBarColor(recipient.getColor());

        // Elham code starts

        Bundle extras = new Bundle();

        extras.putParcelable(VerifyScanFragment.REMOTE_ADDRESS, getIntent().getParcelableExtra(ADDRESS_EXTRA));
        extras.putParcelable(VerifyScanFragment.REMOTE_IDENTITY, getIntent().getParcelableExtra(IDENTITY_EXTRA));
        extras.putString(VerifyScanFragment.REMOTE_NUMBER, recipient.getAddress().toPhoneString());
        extras.putParcelable(VerifyScanFragment.LOCAL_IDENTITY, new IdentityKeyParcelable(IdentityKeyUtil.getIdentityKey(this)));
        extras.putString(VerifyScanFragment.LOCAL_NUMBER, TextSecurePreferences.getLocalNumber(this));
        scanFragment.setScanListener(this);
        initFragment(android.R.id.content, scanFragment, dynamicLanguage.getCurrentLocale(), extras);

        checkCameraPermissions();
        // Elham code ends
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: finish(); return true;
        }

        return false;
    }

    @Override
    public void onModified(final Recipient recipient) {
        Util.runOnMain(() -> setActionBarNotificationBarColor(recipient.getColor()));
    }

    @Override
    public void onQrDataFound(final String data) {
        Util.runOnMain(() -> {
            ((Vibrator)getSystemService(Context.VIBRATOR_SERVICE)).vibrate(50);

            // Elham code starts
            //getSupportFragmentManager().popBackStack();
            //displayFragment.setScannedFingerprint(data);

            scanFragment.setScannedFingerprint(data);
            getSupportFragmentManager().popBackStack();

            // Elham code ends
        });
    }


    //Devon comment: I made this checkCameraPermissions function into a local function
    //and call it in the Activity's onCreate instead of on a clickListener
    private void checkCameraPermissions() {
        Permissions.with(this)
                .request(Manifest.permission.CAMERA)
                .ifNecessary()
                .withPermanentDenialDialog(getString(R.string.VerifyIdentityActivity_signal_needs_the_camera_permission_in_order_to_scan_a_qr_code_but_it_has_been_permanently_denied))
                .onAllGranted(() -> {
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.setCustomAnimations(R.anim.slide_from_top, R.anim.slide_to_bottom,
                            R.anim.slide_from_bottom, R.anim.slide_to_top);

                    transaction.replace(android.R.id.content, scanFragment)
                            .addToBackStack(null)
                            .commitAllowingStateLoss();
                })
                .onAnyDenied(() -> Toast.makeText(this, R.string.VerifyIdentityActivity_unable_to_scan_qr_code_without_camera_permission, Toast.LENGTH_LONG).show())
                .execute();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Permissions.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    private void setActionBarNotificationBarColor(MaterialColor color) {
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(color.toActionBarColor(this)));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(color.toStatusBarColor(this));
        }
    }


    public static class VerifyScanFragment extends Fragment {

        // Elham code starts

        public static final String REMOTE_ADDRESS  = "remote_address";
        public static final String REMOTE_NUMBER   = "remote_number";
        public static final String REMOTE_IDENTITY = "remote_identity";
        public static final String LOCAL_IDENTITY  = "local_identity";
        public static final String LOCAL_NUMBER    = "local_number";

        private boolean       animateSuccessOnDraw = false;
        private boolean       animateFailureOnDraw = false;

        private Recipient     recipient;
        private String        localNumber;
        private String        remoteNumber;

        private IdentityKey   localIdentity;
        private IdentityKey   remoteIdentity;

        private Fingerprint   fingerprint;

        private ImageView     qrCodeFrag;
        private ImageView     qrVerifiedFrag;

        // Elham code ends

        private View           container;
        private CameraView     cameraView;
        private ScanningThread scanningThread;
        private ScanListener   scanListener;


        // Elham code starts

        public void setScannedFingerprint(String scanned) {
            try {
                if (fingerprint.getScannableFingerprint().compareTo(scanned.getBytes("ISO-8859-1"))) {
                    // If the Qr code scanning was successful
                    this.animateSuccessOnDraw = true;

                    Log.w(TAG, "Saving identity QR code scan data: " + this.recipient.getAddress());
                    DatabaseFactory.getIdentityDatabase(getActivity())
                            .saveIdentity(this.recipient.getAddress(),
                                    remoteIdentity,
                                    VerifiedStatus.VERIFIED, false,
                                    System.currentTimeMillis(), true);

                    animateVerifiedSuccess();

                    //Devon newWarn code starts
                    //Displays the PrivacyCheckSuccessDialog upon successful scan

                    PrivacyCheckSuccessDialog successDialog = new PrivacyCheckSuccessDialog(getContext(), recipient.getName(), PrivacyCheckQRScannerActivity.class.getSimpleName());
                    successDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            onResume();
                        }
                    });
                    successDialog.show();

                    //Devon code ends

                } else {
                    // If the QR code scanning was not successful
                    this.animateFailureOnDraw = true;

                    //devon newWarn code starts
                    //saving failed status in database as veryUnverified

                    Log.w(TAG, "Saving identity QR code scan data: " + this.recipient.getAddress());
                    DatabaseFactory.getIdentityDatabase(getActivity())
                            .saveIdentity(this.recipient.getAddress(),
                                    remoteIdentity,
                                    VerifiedStatus.VERYUNVERIFIED, false,
                                    System.currentTimeMillis(), true);

                    //Devon code ends

                    animateVerifiedFailure();

                    //Devon newWarn code starts
                    //displaying the PrivacyCheckFailureDialog

                    new PrivacyCheckFailureDialog(getContext(), recipient.getName(),
                            PrivacyCheckQRScannerActivity.class.getSimpleName(),
                            new PrivacyCheckFailureDialog.PrivacyCheckFailureListener() {
                        @Override
                        public void onMatchFailedTryAgainClicked() {
                            onResume();
                        }

                        @Override
                        public void onMatchFailedImSureClicked() {
                            getActivity().finish();
                        }
                    }).show();

                    //Devon code ends

                }
            } catch (FingerprintVersionMismatchException e) {
                Log.w(TAG, e);
                if (e.getOurVersion() < e.getTheirVersion()) {
                    Toast.makeText(getActivity(), R.string.VerifyIdentityActivity_your_contact_is_running_a_newer_version_of_Signal, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getActivity(), R.string.VerifyIdentityActivity_your_contact_is_running_an_old_version_of_signal, Toast.LENGTH_LONG).show();
                }
            } catch (FingerprintParsingException e) {
                Log.w(TAG, e);
                Toast.makeText(getActivity(), R.string.VerifyIdentityActivity_the_scanned_qr_code_is_not_a_correctly_formatted_safety_number, Toast.LENGTH_LONG).show();
            } catch (UnsupportedEncodingException e) {
                throw new AssertionError(e);
            }
        }

        // Elham code ends

        public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle bundle) {
            this.container  = ViewUtil.inflate(inflater, viewGroup, R.layout.verify_scan_fragment);
            this.cameraView = ViewUtil.findById(container, R.id.scanner);

            // Elham code starts

            this.qrCodeFrag = ViewUtil.findById(container,R.id.qr_code_frag);
            this.qrVerifiedFrag = ViewUtil.findById(container, R.id.qr_verified_frag);
            Address               address                  = getArguments().getParcelable(REMOTE_ADDRESS);
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

            new AsyncTask<Void, Void, Fingerprint>() {
                @Override
                protected Fingerprint doInBackground(Void... params) {

                    //Devon code starts here
                    //Changing the remoteIdentity that is fed into the fingerprint generator
                    //to fake a new "safety number"

                    /*IsMITMAttackOn isMITMAttackOn = new IsMITMAttackOn();
                    if (isMITMAttackOn.isSafetyNumberChanged()) {
                        return new NumericFingerprintGenerator(5200).createFor(localNumber, localIdentity,
                                remoteNumber, isMITMAttackOn.getFakeKey());
                    }*/

                    //Devon code ends here


                    return new NumericFingerprintGenerator(5200).createFor(localNumber, localIdentity,
                            remoteNumber, remoteIdentity);
                }

                @Override
                protected void onPostExecute(Fingerprint fingerprint) {
                    VerifyScanFragment.this.fingerprint = fingerprint;
                    byte[] qrCodeData   = fingerprint.getScannableFingerprint().getSerialized();
                    String qrCodeString = new String(qrCodeData, Charset.forName("ISO-8859-1"));
                    Bitmap qrCodeBitmap = QrCode.create(qrCodeString);

                    VerifyScanFragment.this.qrCodeFrag.setImageBitmap(qrCodeBitmap);

                }
            }.execute();

            // Elham code ends

            return container;
        }

        @Override
        public void onResume() {
            super.onResume();
            this.scanningThread = new ScanningThread();
            this.scanningThread.setScanListener(scanListener);
            this.scanningThread.setCharacterSet("ISO-8859-1");
            this.cameraView.onResume();
            this.cameraView.setPreviewCallback(scanningThread);
            this.scanningThread.start();

            // Elham code starts

            if (animateSuccessOnDraw) {
                animateSuccessOnDraw = false;
                animateVerifiedSuccess();
            } else if (animateFailureOnDraw) {
                animateFailureOnDraw = false;
                animateVerifiedFailure();
            }

            // Elham code ends
        }

        @Override
        public void onPause() {
            super.onPause();
            this.cameraView.onPause();
            this.scanningThread.stopScanning();
        }

        @Override
        public void onConfigurationChanged(Configuration newConfiguration) {
            super.onConfigurationChanged(newConfiguration);
            this.cameraView.onPause();
            this.cameraView.onResume();
            this.cameraView.setPreviewCallback(scanningThread);
        }

        public void setScanListener(ScanListener listener) {
            if (this.scanningThread != null) scanningThread.setScanListener(listener);
            this.scanListener = listener;
        }

        // Elham code starts

        private Bitmap createVerifiedBitmap(int width, int height, @DrawableRes int id) {
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            Bitmap check  = BitmapFactory.decodeResource(getResources(), id);
            float  offset = (width - check.getWidth()) / 2;

            canvas.drawBitmap(check, offset, offset, null);

            return bitmap;
        }

        private void animateVerifiedSuccess() {

            //Devon newWarn code change: swapped white check for verified shield
            Bitmap qrBitmap  = ((BitmapDrawable) qrCodeFrag.getDrawable()).getBitmap();
            Bitmap qrSuccess = createVerifiedBitmap(qrBitmap.getWidth(), qrBitmap.getHeight(), R.mipmap.ic_devon_qr_verified); //ic_check_white_48dp);

            qrVerifiedFrag.setImageBitmap(qrSuccess);
            qrVerifiedFrag.getBackground().setColorFilter(getResources().getColor(R.color.green_500), PorterDuff.Mode.MULTIPLY);

            animateVerified();
        }

        private void animateVerifiedFailure() {

            //Devon newWarn code change: swapped white X for veryUnverified shield
            Bitmap qrBitmap  = ((BitmapDrawable) qrCodeFrag.getDrawable()).getBitmap();
            Bitmap qrSuccess = createVerifiedBitmap(qrBitmap.getWidth(), qrBitmap.getHeight(), R.mipmap.ic_devon_qr_very_unverified); //ic_close_white_48dp);

            qrVerifiedFrag.setImageBitmap(qrSuccess);
            qrVerifiedFrag.getBackground().setColorFilter(getResources().getColor(R.color.red_500), PorterDuff.Mode.MULTIPLY);

            animateVerified();
        }

        private void animateVerified() {
            ScaleAnimation scaleAnimation = new ScaleAnimation(0, 1, 0, 1,
                    ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
                    ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
            scaleAnimation.setInterpolator(new OvershootInterpolator());
            scaleAnimation.setDuration(800);
            scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}

                @Override
                public void onAnimationEnd(Animation animation) {
                    qrVerifiedFrag.postDelayed(() -> {
                        ScaleAnimation scaleAnimation1 = new ScaleAnimation(1, 0, 1, 0,
                                ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
                                ScaleAnimation.RELATIVE_TO_SELF, 0.5f);

                        scaleAnimation1.setInterpolator(new AnticipateInterpolator());
                        scaleAnimation1.setDuration(500);
                        ViewUtil.animateOut(qrVerifiedFrag, scaleAnimation1, View.GONE);
                    }, 2000);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });

            ViewUtil.animateIn(qrVerifiedFrag, scaleAnimation);
        }

        // Elham code ends

    }

}
