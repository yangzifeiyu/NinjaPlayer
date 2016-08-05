package com.mfusion.templatedesigner;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.mfusion.commons.entity.template.TemplateEntity;

public class DesignerActivity extends AppCompatActivity {

    private TemplateDesigner designer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_designer);

        TemplateEntity selectedEntity=(TemplateEntity) getIntent().getSerializableExtra("selected");

        designer=(TemplateDesigner)findViewById(R.id.designer_activity_designer_view);
        if(selectedEntity!=null)
            designer.openTemplate(selectedEntity);
    }
}
