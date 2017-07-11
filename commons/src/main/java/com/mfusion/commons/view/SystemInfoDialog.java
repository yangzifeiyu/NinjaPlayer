package com.mfusion.commons.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mfusion.commons.tools.WindowsDecorHelper;
import com.mfusion.commontools.R;

/**
 * Created by ThinkPad on 2016/9/13.
 */
public class SystemInfoDialog extends Dialog {

    public SystemInfoDialog(Context context) {
        super(context);
    }

    public SystemInfoDialog(Context context, int theme) {
        super(context, theme);
    }

    public static class Builder {
        private Context context;
        private int themeId;
        private int iconDrawableId;
        private String title;
        private String message;
        private int message_gravity=Gravity.CENTER_HORIZONTAL;
        private String positiveButtonText;
        private int positiveButtonImage=R.drawable.mf_save;
        private String negativeButtonText;
        private int negativeButtonImage=R.drawable.mf_trash;
        private String closeButtonText;
        private View contentView;
        private int content_view_gravity=Gravity.NO_GRAVITY;
        private LinearLayout.LayoutParams content_view_layout;
        private DialogInterface.OnClickListener positiveButtonClickListener;
        private DialogInterface.OnClickListener negativeButtonClickListener;
        private DialogInterface.OnClickListener closeButtonClickListener;

        private SystemInfoDialog dialog;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder(Context context,int themeId) {
            this.context = context;
            this.themeId=themeId;
        }

        /**
         * Set the Dialog title from resource
         *
         * @param iconId
         * @return
         */
        public Builder setIcon(int iconId) {
            this.iconDrawableId = iconId;
            return this;
        }

        /**
         * Set the Dialog title from resource
         *
         * @param title
         * @return
         */
        public Builder setTitle(int title) {
            this.title = (String) context.getText(title);
            return this;
        }

        /**
         * Set the Dialog title from String
         *
         * @param title
         * @return
         */

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        /**
         * Set the Dialog message from resource
         *
         * @param message
         * @return
         */
        public Builder setMessage(int message) {
            this.message = (String) context.getText(message);
            return this;
        }

        public Builder setMessage(String message, int gravity) {
            this.message = message;
            this.message_gravity=gravity;
            return this;
        }

        /**
         * Set the Dialog message from resource
         *
         * @param message
         * @return
         */
        public Builder setMessage(int message, int gravity) {
            this.message = (String) context.getText(message);
            this.message_gravity=gravity;
            return this;
        }

        public Builder setContentView(View v) {
            this.contentView = v;
            return this;
        }

        public Builder setContentView(View v, int gravity) {
            this.contentView = v;
            this.content_view_gravity=gravity;
            return this;
        }

        public Builder setContentView(View v, LinearLayout.LayoutParams layoutParams) {
            this.contentView = v;
            this.content_view_layout=layoutParams;
            return this;
        }

        /**
         * Set the positive button resource and it's listener
         *
         * @param positiveButtonText
         * @return
         */
        public Builder setPositiveButton(int positiveButtonText,
                                         DialogInterface.OnClickListener listener) {
            this.positiveButtonText = (String) context
                    .getText(positiveButtonText);
            this.positiveButtonClickListener = listener;
            return this;
        }

        public Builder setPositiveButton(String positiveButtonText,
                                         DialogInterface.OnClickListener listener) {
            this.positiveButtonText = positiveButtonText;
            this.positiveButtonClickListener = listener;
            return this;
        }

        public Builder setPositiveButtonImage(int positiveButtonImage){
            this.positiveButtonImage=positiveButtonImage;
            return this;
        }

        public Builder setNegativeButton(int negativeButtonText,
                                         DialogInterface.OnClickListener listener) {
            this.negativeButtonText = (String) context
                    .getText(negativeButtonText);
            this.negativeButtonClickListener = listener;
            return this;
        }

        public Builder setNegativeButton(String negativeButtonText,
                                         DialogInterface.OnClickListener listener) {
            this.negativeButtonText = negativeButtonText;
            this.negativeButtonClickListener = listener;
            return this;
        }

        public Builder setNegativeButtonImage(int negativeButtonImage){
            this.negativeButtonImage=negativeButtonImage;
            return this;
        }

        public Builder setCloseButton(int closeButtonText,
                                         DialogInterface.OnClickListener listener) {
            this.closeButtonText = (String) context
                    .getText(closeButtonText);
            this.closeButtonClickListener = listener;
            return this;
        }

        public Builder setCloseButton(DialogInterface.OnClickListener listener) {
            this.closeButtonClickListener = listener;
            return this;
        }

        public SystemInfoDialog create() {
            if(dialog!=null)
                return dialog;

            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // instantiate the dialog with the custom Theme
            dialog = new SystemInfoDialog(context,themeId);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

            View layout = inflater.inflate(R.layout.view_dialog_sample, null);
            dialog.addContentView(layout, new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            final LinearLayout contentLayout = (LinearLayout) layout.findViewById(R.id.system_dialog_content_layout);
            //
            if(iconDrawableId>0)
                ((ImageView)layout.findViewById(R.id.system_dialog_icon)).setImageDrawable(context.getResources().getDrawable(iconDrawableId));
            // set the dialog title
            ((TextView) layout.findViewById(R.id.system_dialog_title)).setText(title);
            // set the confirm button
            ImageTextHorizontalView positiveBtn=(ImageTextHorizontalView)layout.findViewById(R.id.system_dialog_positive);
            if (positiveButtonText != null) {
                positiveBtn.setText(positiveButtonText);
                positiveBtn.setImage(positiveButtonImage);
                if (positiveButtonClickListener != null) {
                    positiveBtn.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    clearDialogContent();
                                    positiveButtonClickListener.onClick(dialog,
                                            DialogInterface.BUTTON_POSITIVE);
                                }
                            });
                }
            } else {
                // if no confirm button just set the visibility to GONE
                positiveBtn.setVisibility(View.INVISIBLE);
            }
            // set the cancel button
            ImageTextHorizontalView negativeBtn=(ImageTextHorizontalView)layout.findViewById(R.id.system_dialog_negative);
            if (negativeButtonText != null) {
                negativeBtn.setText(negativeButtonText);
                negativeBtn.setImage(negativeButtonImage);
                if (negativeButtonClickListener != null) {
                    negativeBtn.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    clearDialogContent();
                                    negativeButtonClickListener.onClick(dialog,
                                            DialogInterface.BUTTON_NEGATIVE);
                                }
                            });
                }
                negativeBtn.requestFocus();
                negativeBtn.setFocusable(true);
            } else {
                // if no confirm button just set the visibility to GONE
                negativeBtn.setVisibility(View.INVISIBLE);
            }

            if(positiveButtonText==null&&negativeButtonText==null){
                ((RelativeLayout)layout.findViewById(R.id.system_dialog_foot)).setVisibility(View.GONE);
            }
            //set dialog close button
            ((Button)layout.findViewById(R.id.system_dialog_close)).setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    clearDialogContent();
                    if(closeButtonClickListener!=null)
                        closeButtonClickListener.onClick(dialog,
                                DialogInterface.BUTTON_NEGATIVE);
                    else if(negativeButtonClickListener!=null)
                        negativeButtonClickListener.onClick(dialog,
                                DialogInterface.BUTTON_NEGATIVE);
                    dialog.dismiss();
                }
            });

            // set the content message
            TextView messageView=((TextView) layout.findViewById(R.id.system_dialog_message));

            LinearLayout.LayoutParams content_layout=(LinearLayout.LayoutParams)contentLayout.getLayoutParams();
            if (message != null) {
                messageView.setText(message);
                content_layout.gravity=message_gravity;
            } else if (contentView != null) {
                // if no message set, add the contentView to the dialog body
                messageView.setVisibility(View.GONE);
                contentLayout.removeAllViews();
                content_layout.gravity=content_view_gravity;
                contentLayout.addView(contentView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT));
            }
            if(content_view_layout!=null){
                content_layout=content_view_layout;
            }
            contentLayout.setLayoutParams(content_layout);

            dialog.setContentView(layout);

            return dialog;
        }

        public void clearDialogContent(){
            dialog.hide();
            /*if(contentView!=null){
                InputMethodManager imm = (InputMethodManager) ((Activity)context).getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(contentView.getWindowToken(),0);
            }*/
        }
    }
}
