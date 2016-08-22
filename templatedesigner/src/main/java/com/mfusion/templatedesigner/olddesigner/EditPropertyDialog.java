package com.mfusion.templatedesigner.olddesigner;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.mfusion.commons.entity.template.ComponentEntity;
import com.mfusion.commons.tools.ColorConverter;

import org.w3c.dom.Element;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;


public class EditPropertyDialog extends DialogFragment {
    private static final String TAG = "EditPropertyDialog";
    private ComponentEntity entity;
    private HashMap<String,EditText> propertyEtMap;
    private HashMap<String,EditText> paramsMap;

    private static final String[] PARAMS={"left","top","width","height","backColor"};




    public static EditPropertyDialog newInstance(ComponentEntity entity){
        EditPropertyDialog instance=new EditPropertyDialog();
        instance.entity=entity;

        return instance;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        propertyEtMap=new HashMap<>();
        paramsMap=new HashMap<>();
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        LinearLayout dialogLayout=new LinearLayout(getActivity());
        ScrollView scrollView=new ScrollView(getActivity());
        scrollView.addView(dialogLayout);

        dialogLayout.setOrientation(LinearLayout.VERTICAL);
        ArrayList<Element> properties=entity.property;

        for(String currentParam:PARAMS){
            LinearLayout paramLayout=new LinearLayout(getActivity());
            paramLayout.setWeightSum(4);
            paramLayout.setOrientation(LinearLayout.HORIZONTAL);
            TextView tvPropertyName=new TextView(getActivity());

            tvPropertyName.setText(currentParam);
            tvPropertyName.setGravity(Gravity.CENTER);

            EditText etParam=new EditText(getActivity());
            int currentFieldValue=0;
            try {
                Field currentField=ComponentEntity.class.getDeclaredField(currentParam);
                currentFieldValue=currentField.getInt(entity);


            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            if(currentParam.toLowerCase().contains("color"))
            {
                String colorStr=ColorConverter.convertIntColorToARGBStr(currentFieldValue);
                etParam.setText(colorStr);
            }
            else{
                etParam.setText(String.valueOf(currentFieldValue));
            }

            paramsMap.put(currentParam,etParam);

            paramLayout.addView(tvPropertyName,new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT,1));
            paramLayout.addView(etParam,new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT,3));
            dialogLayout.addView(paramLayout);
        }

        for(Element current:properties){
            LinearLayout paramLayout=new LinearLayout(getActivity());
            paramLayout.setWeightSum(4);
            paramLayout.setOrientation(LinearLayout.HORIZONTAL);
            TextView tvPropertyName=new TextView(getActivity());

            String currentAttributeName=current.getAttribute("name");
            //create text view for property names
            tvPropertyName.setText(currentAttributeName);
            tvPropertyName.setGravity(Gravity.CENTER);
            //create edit text to display current setting and allow user to edit
            EditText etProperty=new EditText(getActivity());
            etProperty.setText(current.getTextContent());

            propertyEtMap.put(currentAttributeName,etProperty);


            paramLayout.addView(tvPropertyName,new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT,1));
            paramLayout.addView(etProperty,new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT,3));
            dialogLayout.addView(paramLayout);
        }

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveInfoToEntity();
                try {
                    EventDispatcher.dispatchRefreshEvent();
                } catch (Exception e) {
                    Log.e(TAG, "onClick: event cannot be dispatched",e );
                }
                dismiss();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
            }
        });

        builder.setView(scrollView);
        return builder.create();
    }


    private void saveInfoToEntity(){
        Set<String> keySet = propertyEtMap.keySet();
        for(String currentKey:keySet){
            entity.setComponentProperty(currentKey,propertyEtMap.get(currentKey).getText().toString());
        }
        for(String currentParam:PARAMS){
            try {
                Field currentField=ComponentEntity.class.getDeclaredField(currentParam);
                if(currentParam.toLowerCase().contains("color"))
                {
                    String colorStr=paramsMap.get(currentParam).getText().toString();
                    currentField.set(entity,ColorConverter.convertARGBStrToColorInt(colorStr));
                }
                else{
                    currentField.set(entity,Integer.valueOf(paramsMap.get(currentParam).getText().toString()));
                }

            } catch (NoSuchFieldException e) {
                Log.e(TAG, "saveInfoToEntity: ",e );
            } catch (IllegalAccessException e) {
                Log.e(TAG, "saveInfoToEntity: ",e );
            }
        }

    }

}
