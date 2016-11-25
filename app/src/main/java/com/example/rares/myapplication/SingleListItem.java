package com.example.rares.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import com.example.rares.myapplication.R;
import com.example.rares.myapplication.Vehicle;

public class SingleListItem extends Activity {

    int position;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_list_item);

        TextView make = (EditText) findViewById(R.id.vehicle_make);
        TextView model = (EditText) findViewById(R.id.vehicle_model);

        TextView capacity = (EditText) findViewById(R.id.vehicle_capacity);


        Intent i = getIntent();
        // getting attached intent data
        Vehicle vehicle =(Vehicle) i.getSerializableExtra("post");
        position=(int)i.getExtras().getSerializable("position");
        // displaying selected product name
        int cc=vehicle.getCapacity();

        make.setText(vehicle.getMake().toString());
        model.setText(vehicle.getModel().toString());
        capacity.setText(String.valueOf(cc));



    }

    @Override
    public void onBackPressed() {


        final EditText makeField = (EditText) findViewById(R.id.vehicle_make);
        String make = makeField.getText().toString();

        final EditText modelField = (EditText) findViewById(R.id.vehicle_model);
        String model = modelField.getText().toString();

        final EditText capacityField = (EditText) findViewById(R.id.vehicle_capacity);
        int cc = Integer.parseInt(capacityField.getText().toString());


//
        Vehicle v=new Vehicle(make,model,cc);


        Intent resultIntent = new Intent();
        resultIntent.putExtra("newveh", v);
        resultIntent.putExtra("position", position);


        setResult(RESULT_OK, resultIntent);

        super.onBackPressed();

    }
}
