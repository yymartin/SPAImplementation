/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.gms.samples.vision.barcodereader;

import android.content.Intent;
import android.content.SyncStatusObserver;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.samples.vision.barcodereader.cryptography.Hash;
import com.google.android.gms.vision.barcode.Barcode;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.Random;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Main activity demonstrating how to pass extra parameters to an activity that
 * reads barcodes.
 */
public class QRCodeReader extends Activity {

    // use a compound button so either checkbox or switch widgets work.
    private TextView statusMessage;
    private TextView barcodeValue;

    private byte[] KAsBytes;
    private byte[] password;

    private SecretKey K;

    private static final int RC_BARCODE_CAPTURE = 9001;
    private static final String TAG = "BarcodeMain";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qrcode_reader_activity);

        Intent intentLauncher = getIntent();
        byte[] ctext = intentLauncher.getByteArrayExtra("ctext");
        password = intentLauncher.getByteArrayExtra("password");
        
        KAsBytes = Hash.decryptOneTimePadding(ctext, password);

        K = new SecretKeySpec(KAsBytes, 0, KAsBytes.length, "HmacSHA256");

        statusMessage = (TextView)findViewById(R.id.status_message);
        barcodeValue = (TextView)findViewById(R.id.barcode_value);

        Intent intent = new Intent(this, BarcodeCaptureActivity.class);
        startActivityForResult(intent, RC_BARCODE_CAPTURE);
    }

    /**
     * Called when an activity you launched exits, giving you the requestCode
     * you started it with, the resultCode it returned, and any additional
     * data from it.  The <var>resultCode</var> will be
     * {@link #RESULT_CANCELED} if the activity explicitly returned that,
     * didn't return any result, or crashed during its operation.
     * <p/>
     * <p>You will receive this call immediately before onResume() when your
     * activity is re-starting.
     * <p/>
     *
     * @param requestCode The integer request code originally supplied to
     *                    startActivityForResult(), allowing you to identify who this
     *                    result came from.
     * @param resultCode  The integer result code returned by the child activity
     *                    through its setResult().
     * @param data        An Intent, which can return result data to the caller
     *                    (various data can be attached to Intent "extras").
     * @see #startActivityForResult
     * @see #createPendingResult
     * @see #setResult(int)
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_BARCODE_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    statusMessage.setText(R.string.barcode_success);
                    String valueOfQRCode = barcode.displayValue;
                    BigInteger challenge = new BigInteger(valueOfQRCode);
                    BigInteger response = Hash.generateHMac(challenge, K);
                    String responseTrimmed = trim(response);
                    barcodeValue.setText(String.valueOf(responseTrimmed));
                } else {
                    statusMessage.setText(R.string.barcode_failure);
                }
            } else {
                statusMessage.setText(String.format(getString(R.string.barcode_error),
                        CommonStatusCodes.getStatusCodeString(resultCode)));
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private String trim(BigInteger data) {
        Random rand = new Random(data.longValue());
        String result = "";
        for(int i = 0; i < 7; i++) {
            result = result + Integer.toString(rand.nextInt(10));
        }

        return result;
    }
}
