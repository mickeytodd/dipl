package com.miki.passcode;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

public class MainActivity extends AppCompatActivity {


    String contents = "";

    SecretKey key;

    String usnm = "";
    String out = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final EditText username = (EditText) findViewById(R.id.username);
        final EditText password = (EditText) findViewById(R.id.password_field);
        Button scan = (Button) findViewById(R.id.scan_button);
        Button login = (Button) findViewById(R.id.login_button);


        byte[] keyBytes = new byte[0];
        try {
            keyBytes = "mihailot".getBytes("ASCII");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        DESKeySpec keySpec;
        SecretKeyFactory factory;
        key = null;
        try {
            keySpec = new DESKeySpec(keyBytes);
            factory = SecretKeyFactory.getInstance("DES");
            key = factory.generateSecret(keySpec);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        } catch (InvalidKeySpecException e2) {
            e2.printStackTrace();
        }


        new Thread (Connect.getInstance()).start();


        if (login != null) {
            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (username != null) {
                        usnm = username.getText().toString();
                    }
                    if (password != null) {
                        String string = password.getText().toString();

                        String time = new SimpleDateFormat("yyMMddHHmm").format(new Date());

                        out = string + contents + time;

                        Cipher desCipher = null;
                        try {
                            desCipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
                        } catch (NoSuchAlgorithmException e) {
                            e.printStackTrace();
                        } catch (NoSuchPaddingException e1) {
                            e1.printStackTrace();
                        }

                        byte[] outByte = new byte[0];
                        try {
                            outByte = out.getBytes("UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }

                        if (desCipher != null)
                            try {
                                IvParameterSpec zeroIv = new IvParameterSpec(new byte[8]);
                                desCipher.init(Cipher.ENCRYPT_MODE, key, zeroIv);
                            } catch (InvalidKeyException e) {
                                e.printStackTrace();
                            } catch (InvalidAlgorithmParameterException e) {
                                e.printStackTrace();
                            }

                        byte[] textEncrypted = new byte[0];
                        try {
                            textEncrypted = desCipher.doFinal(outByte);
                        } catch (IllegalBlockSizeException e) {
                            e.printStackTrace();
                        } catch (BadPaddingException e) {
                            e.printStackTrace();
                        }

                        out = Base64.encodeToString(textEncrypted, Base64.DEFAULT);

                        attempt_login();
                    }
                }
            });
        }

        if (scan != null) {
            scan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {

                        Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                        intent.putExtra("SCAN_MODE", "QR_CODE_MODE"); // "PRODUCT_MODE for bar codes

                        startActivityForResult(intent, 0);

                    } catch (Exception e) {
                        Uri marketUri = Uri.parse("market://details?id=com.google.zxing.client.android");
                        Intent marketIntent = new Intent(Intent.ACTION_VIEW,marketUri);
                        startActivity(marketIntent);

                    }
                }
            });
        }

            }

    private void attempt_login() {

        new Thread () {
            @Override
            public void run() {
                String receive1 = "";
                String receive2 = "";
                Connect.getInstance().sendMessage(usnm);
                try {
                    receive1 = Connect.getInstance().receiveMessage();
                } catch (IOException e) {
                    Information.info = "Nothing received 1";
                    e.printStackTrace();
                }
                if (receive1.equals("OK")) {
                    Connect.getInstance().sendMessage(out);
                    try {
                        receive2 = Connect.getInstance().receiveMessage();
                    } catch (IOException e) {
                        Information.info = "Nothing received 2";
                        e.printStackTrace();
                    }
                    if (receive2.equals("Logged in")) {
                        Information.info = "Logged in";
                        Intent i = new Intent (MainActivity.this, Information.class);
                        startActivity(i);
                        finish();
                    }
                    else {
                        Information.info = "Wrong password";
                        Intent i = new Intent (MainActivity.this, Information.class);
                        startActivity(i);
                        finish();
                    }
                } else {
                    Information.info = "Username not found";
                    Intent i = new Intent (MainActivity.this, Information.class);
                    startActivity(i);
                    finish();
                }

            }
        }.start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {

            if (resultCode == RESULT_OK) {
                contents = data.getStringExtra("SCAN_RESULT");

            }
            if(resultCode == RESULT_CANCELED){
                //handle cancel
            }
        }
    }
}