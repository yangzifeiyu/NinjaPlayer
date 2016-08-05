package com.mfusion.ninjaplayer.New;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.mfusion.ninjaplayer.R;

public class Home extends Activity {


    Button go,ins;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        go = (Button) findViewById(R.id.btnGo);//proceed button
        ins = (Button) findViewById(R.id.btnInt);//instruction button


        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intObj = new Intent(Home.this,MainActivity.class);//intent from home class to mainActivity class
                startActivity(intObj);


            }
        });//go to mainActivity
        
        ins.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(Home.this, "You click instructions", Toast.LENGTH_SHORT).show();//display when clicking ins(instruction) button
                AlertDialog alertDialog = new AlertDialog.Builder(Home.this).create();
                alertDialog.setTitle("Instruction");//set Title
                alertDialog.setMessage("Step 1: Click Mfusion" + "\n"+ "\n" + "Step 2: Configure"+ "\n"+ "\n" + "Step 3: Choose templates"
                        + "\n"+ "\n" + "Step 4: Adjust Templates"+ "\n"+ "\n" + "Step 5: Create PBU" + " " + "Step 6: Schedule"
                        + "\n"+ "\n" + "Step 7: RUN and Display"+ "\n"+ "\n" + "Step 8: Finish");//set dialog message (instruction)
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();//dismiss dialog
                            }
                        });
                alertDialog.show();//show dialog


            }
        });
        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
        }


    }
}

