//Devon newWarn code starts
//this file is copied from DisplayableFingerprint.java and slightly modified from the original so that it can return the
//remote and local fingerprints separately

/**
 * Copyright (C) 2016 Open Whisper Systems
 *
 * Licensed according to the LICENSE file in this repository.
 */

package org.thoughtcrime.securesms;

import org.whispersystems.libsignal.util.ByteUtil;

public class PrivacyCheckDisplayableFingerprint {
    private final String localFingerprintNumbers;
    private final String remoteFingerprintNumbers;

    PrivacyCheckDisplayableFingerprint(byte[] localFingerprint, byte[] remoteFingerprint)
    {
        this.localFingerprintNumbers  = getDisplayStringFor(localFingerprint);
        this.remoteFingerprintNumbers = getDisplayStringFor(remoteFingerprint);
    }

    public String getDisplayText() {
        if (localFingerprintNumbers.compareTo(remoteFingerprintNumbers) <= 0) {
            return localFingerprintNumbers + remoteFingerprintNumbers;
        } else {
            return remoteFingerprintNumbers + localFingerprintNumbers;
        }
    }

    private String getDisplayStringFor(byte[] fingerprint) {
        return getEncodedChunk(fingerprint, 0)  +
                getEncodedChunk(fingerprint, 5)  +
                getEncodedChunk(fingerprint, 10) +
                getEncodedChunk(fingerprint, 15) +
                getEncodedChunk(fingerprint, 20) +
                getEncodedChunk(fingerprint, 25);
    }

    private String getEncodedChunk(byte[] hash, int offset) {
        long chunk = ByteUtil.byteArray5ToLong(hash, offset) % 100000;
        return String.format("%05d", chunk);
    }

    public String getLocalFingerprintNumbers() {
        return localFingerprintNumbers;
    }

    public String getRemoteFingerprintNumbers() {
        return remoteFingerprintNumbers;
    }
}
