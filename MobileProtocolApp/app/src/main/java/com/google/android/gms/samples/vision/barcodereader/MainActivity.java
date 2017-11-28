package com.google.android.gms.samples.vision.barcodereader;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.concurrent.ExecutionException;

/**
 * Created by yoanmartin on 02.11.17.
 */

public class MainActivity extends Activity{

    private byte[] password = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent launchIntent = getIntent();
        password = launchIntent.getByteArrayExtra("password");

        Button register = (Button) findViewById(R.id.button_register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText ipAddressFromUser = (EditText) findViewById(R.id.ipAddress);
                String ipAddress = ipAddressFromUser.getText().toString();

                if (ipAddress.equals("")){
                    createDialog("Bad login, you are not registered");
                    return;
                }

                byte[] result = null;
                InputStream key = null;
                try {
                    key = getAssets().open("PUBLICKEYMOBILE.bks");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Registration register = new Registration(key, ipAddress);
                try {
                    result = register.execute().get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

                if(result == null || result.equals("")) {
                    createDialog("Something went wrong, you are not registered");
                } else {

                    File internalStorageDir = getFilesDir();
                    File myfile = new File(internalStorageDir, "myfile.txt");
                    FileOutputStream fos;
                    try {
                        fos = new FileOutputStream(myfile);
                        fos.write(result);
                        fos.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    createDialog("You are correctly registered");
                }
            }
        });

        Button scan = (Button) findViewById(R.id.button_scan);
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File internalStorageDir = getFilesDir();
                File myfile = new File(internalStorageDir, "myfile.txt");
                byte[] ctext = new byte[(int) myfile.length()];
                try {
                    FileInputStream inputStream = new FileInputStream(myfile);
                    inputStream.read(ctext);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Intent launchScan = new Intent(MainActivity.this, QRCodeReader.class);
                launchScan.putExtra("ctext", ctext);
                launchScan.putExtra("password", password);
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
