//Devon newWarn code starts

package org.thoughtcrime.securesms;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class WelcomeScreenDialog extends AlertDialog {
        private static final String TAG = WelcomeScreenDialog.class.getSimpleName();

    public WelcomeScreenDialog(Context context)
        {
            super(context);
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.dialog_welcome_screen, null);
            TextView startMessaging = view.findViewById(R.id.welcome_screen_start_messaging);
            startMessaging.setOnClickListener(view1 -> dismiss());

            setView(view);
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        @Override
        public void show() {
            super.show();
            ((TextView)this.findViewById(android.R.id.message))
                    .setMovementMethod(LinkMovementMethod.getInstance());
        }
}

//Devon code ends