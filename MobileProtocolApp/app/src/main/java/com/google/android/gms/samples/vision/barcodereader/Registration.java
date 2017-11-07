package com.google.android.gms.samples.vision.barcodereader;

import android.content.SyncStatusObserver;
import android.os.AsyncTask;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

/**
 * Created by yoanmartin on 07.11.17.
 */

public class Registration extends AsyncTask<Void,Void,Void> {
    DataInputStream in;

    @Override
    protected Void doInBackground(Void... voids) {
        Socket socket;

        String address = "172.22.22.58";
        int port = 1234;

        try {
            socket = new Socket(address, port);
	        in = new DataInputStream(socket.getInputStream());

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private byte[] getData() {
        byte[] result = null;
        int length;
        try {
            length = in.readInt();
            if(length > 0) {
                result = new byte[length];
                in.readFully(result, 0, result.length);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }
}