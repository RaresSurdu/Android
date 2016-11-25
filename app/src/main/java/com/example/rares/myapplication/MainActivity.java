package com.example.rares.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.rares.myapplication.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Button button_send = (Button) findViewById(R.id.button_sendFeedback);
        button_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final EditText nameField = (EditText) findViewById(R.id.textedit_name);
                String name = nameField.getText().toString();

                String email = "raressurdu95@gmail.com";

                final EditText feedbackField = (EditText) findViewById(R.id.textedit_feed);
                String feedback = feedbackField.getText().toString();


                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL  , new String[]{email});
                i.putExtra(Intent.EXTRA_SUBJECT, "hello");
                i.putExtra(Intent.EXTRA_TEXT   , feedback+"\n"+"\n"+name+"\n"+"\n"+"\nSent from "+android.os.Build.MODEL);
                try {
                    startActivity(i);
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(MainActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });


        Button vehicles=(Button) findViewById(R.id.button_vehicles);
        vehicles.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View vehicles) {

                Intent intent = new Intent(MainActivity.this,DisplayVehiclesActivity.class);
                startActivity(intent);
            }
        });



    }

}
