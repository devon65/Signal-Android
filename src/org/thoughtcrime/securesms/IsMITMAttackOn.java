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

    private static boolean attackOn = false;
    private static boolean safetyNumberChanged = false;

    private static IdentityKey fakeKey = null;


    public IsMITMAttackOn(){

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
    }



    //getters

    public boolean isAttackOn() {
        return attackOn;
    }

    public static boolean isSafetyNumberChanged() {
        return safetyNumberChanged;
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
}

//Devon code ends here
