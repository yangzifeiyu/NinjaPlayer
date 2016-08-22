package com.mfusion.player.common.Floatwindows;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class IPTextWatcher implements TextWatcher {
	 //����ı���ı���  
    private EditText editText;  

      
    public IPTextWatcher(EditText editText){  
        this.editText = editText;  
    }  
    
	@Override
	public void afterTextChanged(Editable arg0) {
		// TODO Auto-generated method stub
		String editable = this.editText.getText().toString();
		String str = stringFilter(editable);
		if (!editable.equals(str)) {
			this.editText.setText(str);
			this.editText.setSelection(++changedIndex);
		}
	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
			int arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		/* String editable = editText.getText().toString();  
         String str = stringFilter(editable.toString());
         if(!editable.equals(str)){
             editText.setText(str);
             //�����µĹ������λ��  
             editText.setSelection(str.length());
         }*/
	}
	int changedIndex;
	private String stringFilter(String str)throws PatternSyntaxException{     
	      // ֻ������ĸ������       
		String regEx = "[/\\:#*?<>|\"\n\ta-zA-Z]";
		String tmp = null;
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		if (m.find()) {
			tmp = m.replaceAll(".");
			for (int i = 0; i < tmp.length(); i++) {
				if (tmp.charAt(i) != str.charAt(i)) {
					changedIndex = i;
					break;
				}
			}
		} else {
			return str;
		}
		return tmp;
		
	      /*String   regEx  = "[^a-zA-Z0-9]";// "/^(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])$/";                     
	      Pattern   p   =   Pattern.compile(regEx);     
	      Matcher   m   =   p.matcher(str);
	      
	      return   m.replaceAll("").trim();   */
	      
	  }
}
