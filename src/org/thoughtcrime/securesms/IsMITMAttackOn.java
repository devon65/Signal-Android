//Devon code starts here

package org.thoughtcrime.securesms;

import android.content.Context;
import android.content.SharedPreferences;

import org.thoughtcrime.securesms.backup.BackupProtos;
import org.whispersystems.libsignal.IdentityKey;
import org.whispersystems.libsignal.ecc.Curve;
import org.whispersystems.libsignal.ecc.ECKeyPair;

public class IsMITMAttackOn {

    private static final String attackBoolString = "attackBoolean";
    private static final String safetyNumberBoolString = "safetyNumberBoolean";
    private static final String welcomeScreenBoolString = "welcomeScreenBoolean";

    private static IsMITMAttackOn isMITMAttackOn = null;
    private static boolean attackOn = false;
    private static boolean safetyNumberChanged = false;
    private static boolean welcomeScreenAlreadyShown = false;
    private static boolean testing = false;

    private static IdentityKey fakeKey = null;


    public static IsMITMAttackOn getInstance(){
        if (isMITMAttackOn == null){
            isMITMAttackOn = new IsMITMAttackOn();
        }
        return isMITMAttackOn;
    }

    private IsMITMAttackOn(){
        if (this.fakeKey == null) {
            ECKeyPair ourKeyPair = Curve.generateKeyPair();
            this.fakeKey = new IdentityKey(ourKeyPair.getPublicKey());
        }
    }


    public void initializeBooleans(Context context){

        SharedPreferences sharedPref = context.getSharedPreferences(
                "preferencesMITM", Context.MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = sharedPref.edit();

        this.attackOn = sharedPref.getBoolean(attackBoolString, false);
        this.safetyNumberChanged = sharedPref.getBoolean(safetyNumberBoolString, false);
        this.welcomeScreenAlreadyShown = sharedPref.getBoolean(welcomeScreenBoolString, false);
    }



    //getters

    public boolean isAttackOn() {
        return attackOn;
    }

    public static boolean isSafetyNumberChanged() {
        return safetyNumberChanged;
    }

    public static boolean isWelcomeScreenAlreadyShown() {
        return welcomeScreenAlreadyShown;
    }

    public static boolean isTesting(){
        return testing;
    }

    public static IdentityKey getFakeKey() {
        return fakeKey;
    }

    //setters

    public static void setIsAttackOn(boolean isAttackOn, Context context){

        SharedPreferences sharedPref = context.getSharedPreferences(
                "preferencesMITM", Context.MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = sharedPref.edit();

        IsMITMAttackOn.attackOn = isAttackOn;

        prefEditor.putBoolean(attackBoolString, attackOn);
        prefEditor.apply();
    }

    public static void setIsSafetyNumberChanged(boolean isSafetyNumberChanged, Context context){

        SharedPreferences sharedPref = context.getSharedPreferences(
                "preferencesMITM", Context.MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = sharedPref.edit();

        IsMITMAttackOn.safetyNumberChanged = isSafetyNumberChanged;

        prefEditor.putBoolean(safetyNumberBoolString, isSafetyNumberChanged);
        prefEditor.apply();
    }

    public static void setIsWelcomeScreenAlreadyShown(boolean isWelcomeScreenAlreadyShown, Context context){

        SharedPreferences sharedPref = context.getSharedPreferences(
                "preferencesMITM", Context.MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = sharedPref.edit();

        IsMITMAttackOn.welcomeScreenAlreadyShown = isWelcomeScreenAlreadyShown;

        prefEditor.putBoolean(welcomeScreenBoolString, isWelcomeScreenAlreadyShown);
        prefEditor.apply();
    }
}

//Devon code ends here
