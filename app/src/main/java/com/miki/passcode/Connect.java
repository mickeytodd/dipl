package com.miki.passcode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

/**
 * Created by Miki
 */
public class Connect extends Thread{

    public static final String SERVERIP = "192.168.0.170";
    public static final int SERVERPORT = 2000;
    public static Socket socket;
    public PrintStream output;
    public BufferedReader input;

    private static Connect instance;

    public static Connect getInstance(){
        return (instance == null) ? instance = new Connect() : instance;
    }

    public Connect(){
    }


    @Override
    public void run() {
        try {
            socket = new Socket(SERVERIP, SERVERPORT);
            output = new PrintStream( socket.getOutputStream());
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }


    public void sendMessage (String s) {
        output.println(s);
    }

    public String receiveMessage () throws IOException {
        return input.readLine();
    }

}