package com.mfusion.commons.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mfusion.commons.tools.FileNameTextWatcher;
import com.mfusion.commons.tools.OperateCallbackBundle;
import com.mfusion.commons.tools.WindowsDecorHelper;
import com.mfusion.commontools.R;

/**
 * Created by ThinkPad on 2016/11/2.
 */
public class FileNameEditor {
    public static Dialog createDialog(final Context context, String title, String nameHint, final OperateCallbackBundle callbackBundle){
        return createDialog(context,title,nameHint,"",callbackBundle);
    }
    public static Dialog createDialog(final Context context, String title, String nameHint,String inputText, final OperateCallbackBundle callbackBundle){
        LinearLayout dialogContent=(LinearLayout) ((Activity)context).getLayoutInflater().inflate(R.layout.name_edit_view, null);
        final EditText etName=(EditText)dialogContent.findViewById(R.id.input_name);
        etName.setHint(nameHint);
        etName.setText(inputText);
        etName.addTextChangedListener(new FileNameTextWatcher(etName));

        WindowsDecorHelper.hideSoftInputInEditText(etName);

        SystemInfoDialog.Builder builder =new SystemInfoDialog.Builder(context)
                .setTitle(title).setIcon(R.drawable.mf_edit)
                .setContentView(dialogContent)
                .setPositiveButton("Apply", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name=etName.getText().toString();
                        System.out.println("Name :"+name);
                        if(name.isEmpty()) {
                            return;
                        }
                        if(callbackBundle!=null)
                            callbackBundle.onConfim(name);
                        dialog.dismiss();
                    }
                }).setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(callbackBundle!=null)
                            callbackBundle.onCancel("");
                        dialog.dismiss();
                    }
                });

        Dialog dialog=builder.create();
        dialog.setCancelable(false);
        dialog.show();

        return dialog;
    }
}
