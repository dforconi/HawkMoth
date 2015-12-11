package com.teamvictory.map;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class SettingsActivity extends MapsActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        //
        TextView currentLatLng = (TextView)findViewById(R.id.currentLatLng);
        currentLatLng.setText(extras.getString("currentPosition"));
        //check if there is a current
        //intent.hasExtra(string name)
        if(intent.hasExtra("startPosition")){
        TextView startPosition = (TextView)findViewById(R.id.startPosPlaceholder);
                       //change a textView to the string of the starting position
            startPosition.setText(extras.getString("startPosition"));
        }
        if(intent.hasExtra("endPosition")){
        TextView endPosition = (TextView)findViewById(R.id.endPositionPlaceholder);
            endPosition.setText(extras.getString("endPosition"));
        }


//get starting pos
        //get final pos
        //get distance traveled by user

    }

}
