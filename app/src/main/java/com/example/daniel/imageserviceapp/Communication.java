package com.example.daniel.imageserviceapp;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class Communication {
    File file;

    public Communication(File file) {
        this.file = file;
    }

    public void startCommunication() throws Exception {
        try {
            InetAddress serverAddress = InetAddress.getByName("10.0.2.2");
            try {
                Socket socket = new Socket(serverAddress, 7000);
                OutputStream output = socket.getOutputStream();
                InputStream input = socket.getInputStream();
                //write to server
                output.write(file.getName().getBytes());
                //confirm
                byte[] confirm = new byte[1];
                if (input.read(confirm) == 1) {
                    //write to server
                    output.write(getBytes(file));
                }
                output.flush();
            } catch (Exception e) {
                Log.e(this.getClass().getSimpleName(), e.getMessage());
            }
        } catch (Exception e) {
            Log.e(this.getClass().getSimpleName(), e.getMessage());
        }
    }

    private static byte[] getBytes(File file) throws Exception {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        FileInputStream fis = new FileInputStream(file);
        try {
            int i;
            while ((i = fis.read(buffer)) != -1) {
                stream.write(buffer, 0, i);
            }
        } catch (Exception e) {
            Log.e("getBytes function", e.getMessage());
        }
        return stream.toByteArray();
    }
}