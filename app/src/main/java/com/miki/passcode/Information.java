package com.miki.passcode;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;

public class Information extends AppCompatActivity {

    public static String info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);

        TextView textInfo = (TextView) findViewById(R.id.information);

        if (textInfo != null) {
            textInfo.setText(info);
        }

        Button exitButton = (Button) findViewById(R.id.exit);

        if (exitButton != null) {
            exitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Connect.getInstance().socket.close();
                        Connect.getInstance().output.close();
                        Connect.getInstance().input.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    finish();
                }
            });
        }
    }
}
