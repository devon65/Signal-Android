package org.thoughtcrime.securesms.components.identity;


import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import org.thoughtcrime.securesms.logging.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.thoughtcrime.securesms.R;
import org.thoughtcrime.securesms.database.IdentityDatabase.IdentityRecord;
import org.thoughtcrime.securesms.util.ViewUtil;

import java.util.List;

public class UnverifiedBannerView extends LinearLayout {

  private static final String TAG = UnverifiedBannerView.class.getSimpleName();

  private View      container;
  private TextView  text;
  //Devon newWarn code starts:
  //commenting out "X" closing button
  //private ImageView closeButton;
  //Devon code ends

  public UnverifiedBannerView(Context context) {
    super(context);
    initialize();
  }

  public UnverifiedBannerView(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    initialize();
  }

  @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
  public UnverifiedBannerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    initialize();
  }

  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  public UnverifiedBannerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    initialize();
  }

  private void initialize() {
    LayoutInflater.from(getContext()).inflate(R.layout.unverified_banner_view, this, true);
    this.container   = ViewUtil.findById(this, R.id.container);
    this.text        = ViewUtil.findById(this, R.id.unverified_text);

    //Devon newWarn code starts:
    //commenting out "X" closing button
    //this.closeButton = ViewUtil.findById(this, R.id.cancel);
    //Devon code ends
  }

  public void display(@NonNull final String text,
                      @NonNull final List<IdentityRecord> unverifiedIdentities,
                      @NonNull final ClickListener clickListener,
                      @NonNull final DismissListener dismissListener)
  {

    //Devon newWarn code starts
    //commenting out setting text because it is not necessary in our new version
    //this.text.setText(text);
    //Devon code ends
    setVisibility(View.VISIBLE);

    this.container.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        Log.i(TAG, "onClick()");
        clickListener.onClicked(unverifiedIdentities);
      }
    });

    //Devon newWarn code starts:
    //Commenting out closeButton for unverified blue banner
    /*this.closeButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        hide();
        dismissListener.onDismissed(unverifiedIdentities);
      }
    });*/
    //Devon code ends
  }

  public void hide() {
    setVisibility(View.GONE);
  }

  public interface DismissListener {
    public void onDismissed(List<IdentityRecord> unverifiedIdentities);
  }

  public interface ClickListener {
    public void onClicked(List<IdentityRecord> unverifiedIdentities);
  }

}
