//Devon newWarn code starts
//This file was copied from "ConfirmIdentityDialog.java" and adapted

package org.thoughtcrime.securesms;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import org.thoughtcrime.securesms.crypto.storage.TextSecureIdentityKeyStore;
import org.thoughtcrime.securesms.database.Address;
import org.thoughtcrime.securesms.database.DatabaseFactory;
import org.thoughtcrime.securesms.database.MmsDatabase;
import org.thoughtcrime.securesms.database.MmsSmsDatabase;
import org.thoughtcrime.securesms.database.PushDatabase;
import org.thoughtcrime.securesms.database.SmsDatabase;
import org.thoughtcrime.securesms.database.documents.IdentityKeyMismatch;
import org.thoughtcrime.securesms.database.model.MessageRecord;
import org.thoughtcrime.securesms.jobs.PushDecryptJob;
import org.thoughtcrime.securesms.recipients.Recipient;
import org.thoughtcrime.securesms.sms.MessageSender;
import org.thoughtcrime.securesms.util.Base64;
import org.whispersystems.libsignal.SignalProtocolAddress;
import org.whispersystems.signalservice.api.messages.SignalServiceEnvelope;
import org.whispersystems.signalservice.internal.push.SignalServiceProtos;

import java.io.IOException;

import static org.whispersystems.libsignal.SessionCipher.SESSION_LOCK;

public class PrivacyCheckRemindLaterDialog extends AlertDialog {

    @SuppressWarnings("unused")
    private static final String TAG = PrivacyCheckRemindLaterDialog.class.getSimpleName();

    private OnClickListener callback;

    public PrivacyCheckRemindLaterDialog(Context context,
                                 MessageRecord messageRecord,
                                 IdentityKeyMismatch mismatch)
    {
        super(context);

        Recipient       recipient       = Recipient.from(context, mismatch.getAddress(), false);
        String          name            = recipient.toShortString();
        String          recommendation  = String.format(context.getString(R.string.privacy_check_remind_later_dialog_you_can_keep_sending_messages), name);

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_privacy_check_remind_later, null);
        TextView recommendationView = view.findViewById(R.id.privacy_check_remind_later_recommendation);
        recommendationView.setText(recommendation);


        setTitle(R.string.privacy_check_remind_later_dialog_title);
        setIcon(R.drawable.ic_info_outline_light);
        setView(view);

        setButton(AlertDialog.BUTTON_POSITIVE, context.getString(R.string.privacy_check_remind_later_dialog_Resend_failed_messages), new PrivacyCheckRemindLaterDialog.AcceptListener(messageRecord, mismatch, recipient.getAddress()));
        setButton(AlertDialog.BUTTON_NEGATIVE, context.getString(R.string.privacy_check_remind_later_dialog_Dont_resend),               new PrivacyCheckRemindLaterDialog.CancelListener());
    }

    @Override
    public void show() {
        super.show();
        ((TextView)this.findViewById(android.R.id.message))
                .setMovementMethod(LinkMovementMethod.getInstance());
    }

    public void setCallback(OnClickListener callback) {
        this.callback = callback;
    }

    private class AcceptListener implements OnClickListener {

        private final MessageRecord       messageRecord;
        private final IdentityKeyMismatch mismatch;
        private final Address address;

        private AcceptListener(MessageRecord messageRecord, IdentityKeyMismatch mismatch, Address address) {
            this.messageRecord = messageRecord;
            this.mismatch      = mismatch;
            this.address       = address;
        }

        @SuppressLint("StaticFieldLeak")
        @Override
        public void onClick(DialogInterface dialog, int which) {
            new AsyncTask<Void, Void, Void>()
            {
                @Override
                protected Void doInBackground(Void... params) {
                    synchronized (SESSION_LOCK) {
                        SignalProtocolAddress mismatchAddress  = new SignalProtocolAddress(address.toPhoneString(), 1);
                        TextSecureIdentityKeyStore identityKeyStore = new TextSecureIdentityKeyStore(getContext());

                        identityKeyStore.saveIdentity(mismatchAddress, mismatch.getIdentityKey(), true);
                    }

                    processMessageRecord(messageRecord);
                    processPendingMessageRecords(messageRecord.getThreadId(), mismatch);

                    return null;
                }

                private void processMessageRecord(MessageRecord messageRecord) {
                    if (messageRecord.isOutgoing()) processOutgoingMessageRecord(messageRecord);
                    else                            processIncomingMessageRecord(messageRecord);
                }

                private void processPendingMessageRecords(long threadId, IdentityKeyMismatch mismatch) {
                    MmsSmsDatabase mmsSmsDatabase = DatabaseFactory.getMmsSmsDatabase(getContext());
                    Cursor cursor         = mmsSmsDatabase.getIdentityConflictMessagesForThread(threadId);
                    MmsSmsDatabase.Reader reader         = mmsSmsDatabase.readerFor(cursor);
                    MessageRecord         record;

                    try {
                        while ((record = reader.getNext()) != null) {
                            for (IdentityKeyMismatch recordMismatch : record.getIdentityKeyMismatches()) {
                                if (mismatch.equals(recordMismatch)) {
                                    processMessageRecord(record);
                                }
                            }
                        }
                    } finally {
                        if (reader != null)
                            reader.close();
                    }
                }

                private void processOutgoingMessageRecord(MessageRecord messageRecord) {
                    SmsDatabase smsDatabase        = DatabaseFactory.getSmsDatabase(getContext());
                    MmsDatabase mmsDatabase        = DatabaseFactory.getMmsDatabase(getContext());

                    if (messageRecord.isMms()) {
                        mmsDatabase.removeMismatchedIdentity(messageRecord.getId(),
                                mismatch.getAddress(),
                                mismatch.getIdentityKey());

                        if (messageRecord.getRecipient().isPushGroupRecipient()) {
                            MessageSender.resendGroupMessage(getContext(), messageRecord, mismatch.getAddress());
                        } else {
                            MessageSender.resend(getContext(), messageRecord);
                        }
                    } else {
                        smsDatabase.removeMismatchedIdentity(messageRecord.getId(),
                                mismatch.getAddress(),
                                mismatch.getIdentityKey());

                        MessageSender.resend(getContext(), messageRecord);
                    }
                }

                private void processIncomingMessageRecord(MessageRecord messageRecord) {
                    try {
                        PushDatabase pushDatabase = DatabaseFactory.getPushDatabase(getContext());
                        SmsDatabase  smsDatabase  = DatabaseFactory.getSmsDatabase(getContext());

                        smsDatabase.removeMismatchedIdentity(messageRecord.getId(),
                                mismatch.getAddress(),
                                mismatch.getIdentityKey());

                        boolean legacy = !messageRecord.isContentBundleKeyExchange();

                        SignalServiceEnvelope envelope = new SignalServiceEnvelope(SignalServiceProtos.Envelope.Type.PREKEY_BUNDLE_VALUE,
                                messageRecord.getIndividualRecipient().getAddress().toPhoneString(),
                                messageRecord.getRecipientDeviceId(), "",
                                messageRecord.getDateSent(),
                                legacy ? Base64.decode(messageRecord.getBody()) : null,
                                !legacy ? Base64.decode(messageRecord.getBody()) : null);

                        long pushId = pushDatabase.insert(envelope);

                        ApplicationContext.getInstance(getContext())
                                .getJobManager()
                                .add(new PushDecryptJob(getContext(), pushId, messageRecord.getId()));
                    } catch (IOException e) {
                        throw new AssertionError(e);
                    }
                }

            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

            if (callback != null) callback.onClick(null, 0);
        }
    }

    private class CancelListener implements OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (callback != null) callback.onClick(null, 0);
        }
    }

}

//Devon code ends