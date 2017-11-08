package com.google.android.gms.samples.vision.barcodereader;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;

/**
 * Created by yoanmartin on 02.11.17.
 */

public class MainActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button register = (Button) findViewById(R.id.button_register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String result = "";
                InputStream key = null;
                try {
                    key = getAssets().open("PUBLICKEYMOBILE.bks");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Registration register = new Registration(key);
                try {
                    result = register.execute().get();
                    if(result.equals("")) {
                        createDialog("Something went wrong, you are not registered");
                    } else {
                        createDialog("You are correctly registered");
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });

        Button scan = (Button) findViewById(R.id.button_scan);
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent launchScan = new Intent(MainActivity.this, QRCodeReader.class);
                startActivity(launchScan);
            }
        });
    }

    private void createDialog(String message){
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("Registration status");
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();

    }
}
