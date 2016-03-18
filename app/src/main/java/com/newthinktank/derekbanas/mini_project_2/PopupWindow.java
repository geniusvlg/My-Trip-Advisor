package com.newthinktank.derekbanas.mini_project_2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

/**
 * Created by Huy on 8/2/2015.
 */
public class PopupWindow extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_window);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        //get percentage of the screen
        getWindow().setLayout((int) (width * .6), (int) (height * .6));
    }

    public void sendDataBack(View view) {
        EditText descriptionET = (EditText) findViewById(R.id.attractionDescription);

         EditText nameET = (EditText) findViewById(R.id.attractionName);

         EditText addressET = (EditText) findViewById(R.id.attractionAddress);

         EditText phoneNumberET = (EditText) findViewById(R.id.attractionPhone);

         EditText websiteET = (EditText) findViewById(R.id.attractionWebsite);

         EditText closingET = (EditText) findViewById(R.id.attractionClosing);

         EditText openingET = (EditText)findViewById(R.id.attractionOpening);

         RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBar);

        String name = String.valueOf(nameET.getText());

        String address = String.valueOf(addressET.getText());

        String phoneNumber= String.valueOf(phoneNumberET.getText());

        String website = String.valueOf(websiteET.getText());

        int rating = ratingBar.getNumStars();

        String opening = String.valueOf(openingET.getText());

        String closing = String.valueOf(closingET.getText());

        String description = String.valueOf(descriptionET.getText());

        Intent goingBack = new Intent();
        goingBack.putExtra("name",name);
        goingBack.putExtra("opening",opening);
        goingBack.putExtra("closing",closing);
        goingBack.putExtra("address",address);
        goingBack.putExtra("phoneNumber",phoneNumber);
        goingBack.putExtra("website",website);
        goingBack.putExtra("rating",rating);
        goingBack.putExtra("description",description);
        setResult(RESULT_OK, goingBack);
        finish();

    }

    public void cancel(View view) {
        Intent goingBack = new Intent();
        setResult(RESULT_OK, goingBack);
        finish();

    }
}
