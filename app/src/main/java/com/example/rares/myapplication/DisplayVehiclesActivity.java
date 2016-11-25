package com.example.rares.myapplication;

import android.app.ListActivity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.rares.myapplication.R;
import com.example.rares.myapplication.Vehicle;

import java.util.ArrayList;
import java.util.List;

public class DisplayVehiclesActivity extends ListActivity {


    List<Vehicle> listItems=new ArrayList<Vehicle>();
    Vehicle entry1=new Vehicle("vw","passat",1968);
    Vehicle entry2=new Vehicle("suzuki","sv",649);




    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        System.out.println("////////////////////////////////////////////////////////////CREATEEEEE");

        listItems.add(entry1);
        listItems.add(entry2);

        setContentView(R.layout.activity_display_vehicles);



        setListAdapter(new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,listItems));

        ListView lv = getListView();

        // listening to single list item on click
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // selected item
                Vehicle vehicle = (Vehicle) parent.getItemAtPosition(position);


                // Launching new Activity on selecting single List Item
                Intent i = new Intent(getApplicationContext(), SingleListItem.class);
                // sending data to new activity
                i.putExtra("post",vehicle);
                i.putExtra("position",position);
                startActivityForResult(i,1);

            }
        });


    }


    @Override
    protected void onRestart() {
        super.onResume();
        System.out.println("////////////////////////////////////////////////////////////RESUMEEEEEEEE");



    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if(resultCode == RESULT_OK){

                Vehicle nev = (Vehicle) data.getExtras().getSerializable("newveh");
                int position=data.getExtras().getInt("position");

                listItems.set(position,nev);
                setListAdapter(new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,listItems));


            }
        }
    }
}