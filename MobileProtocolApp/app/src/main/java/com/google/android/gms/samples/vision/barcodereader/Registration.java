package com.google.android.gms.samples.vision.barcodereader;

import android.content.Context;
import android.content.SyncStatusObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.samples.vision.barcodereader.SSLUtility.SSLClientUtility;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;

import javax.net.ssl.SSLSocket;

/**
 * Created by yoanmartin on 07.11.17.
 */

public class Registration extends AsyncTask<Void,Void,byte[]> {
    DataInputStream in;
    InputStream key;

    public Registration(InputStream key){
        this.key = key;
    }

    @Override
    protected byte[] doInBackground(Void... voids) {
        SSLSocket socket;

        String address = "172.22.22.58";
        int port = 1234;

        byte[] result = null;

        try {
            socket = SSLClientUtility.getSocketWithCert(InetAddress.getByName(address), port, key, "8rXbM7twa)E96xtFZmWq6/J^");
	        in = new DataInputStream(socket.getInputStream());

            result = getData();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
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