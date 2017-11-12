package com.google.android.gms.samples.vision.barcodereader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.samples.vision.barcodereader.cryptography.Hash;

import java.math.BigInteger;

/**
 * Created by yoanmartin on 08.11.17.
 */

public class LoginActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button submitButton = (Button) findViewById(R.id.btnSubmit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText passwordEditText = (EditText) findViewById(R.id.txtPassword);
                byte[] passwordAsBytes = passwordEditText.getText().toString().getBytes();
                BigInteger password = Hash.generateSHA256Hash(passwordAsBytes);
                Intent launchMain = new Intent(LoginActivity.this, MainActivity.class);
                launchMain.putExtra("password", password.toByteArray());
                startActivity(launchMain);
            }
        });
    }
}
