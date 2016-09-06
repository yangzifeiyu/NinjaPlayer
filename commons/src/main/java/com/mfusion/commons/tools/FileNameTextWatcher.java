package com.mfusion.commons.tools;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.util.ArrayList;

/**
 * Created by ThinkPad on 2016/9/2.
 */
public class FileNameTextWatcher implements TextWatcher{

    EditText editText;

    String old_value;

    int max_length=100;

    String special_chars="<\">?/\\|";

    public FileNameTextWatcher(EditText editText){
        this.editText=editText;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        old_value=s.toString();
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

        try{
            String new_char=s.subSequence(start,start+count).toString();

            if(!new_char.isEmpty()&&(s.length()>max_length||special_chars.contains(new_char))){
                this.editText.setText(old_value);
                int selectIndex=this.editText.getSelectionStart();
                this.editText.setSelection(start);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
