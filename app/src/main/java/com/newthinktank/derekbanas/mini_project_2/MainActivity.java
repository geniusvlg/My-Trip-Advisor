package com.newthinktank.derekbanas.mini_project_2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.newthinktank.derekbanas.mini_project_2.Attractions.Attraction;
import com.newthinktank.derekbanas.mini_project_2.view.viewgroup.FlyOutContainer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.widget.AdapterView.*;

public class MainActivity extends FragmentActivity {
    String name, description, phone, address, closing, opening, image, website;
    int rating;
    boolean isFavorite;

    ArrayList<Attraction> attractionList = new ArrayList<Attraction>();
    private int createNewLocation = 0;
    GPSTracker gps;
    Marker currentMarker;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    FlyOutContainer root;
    LatLng newMarker = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.root = (FlyOutContainer) this.getLayoutInflater().inflate(R.layout.activity_main, null);
        this.setContentView(root);
        try {
            setUpMapIfNeeded();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        HandleSpinner();
        try {
            initialize();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mMap.setMyLocationEnabled(true);

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                root.closeMenu();
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                currentMarker = marker;
                displayInformation();
                root.toggleMenu();
                createNewLocation = 0;
                return false;
            }
        });

        ToggleButton toggleButton = (ToggleButton) findViewById(R.id.toggleButton);
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                for (int i=0; i < attractionList.size(); ++i) {
                    String[] latlong = attractionList.get(i).position.split(",");
                    double latitude = Double.parseDouble(latlong[0]);
                    double longitude = Double.parseDouble(latlong[1]);
                    LatLng location = new LatLng(latitude, longitude);
                    if (currentMarker.getPosition().equals(location)) {
                        if (isChecked) {
                            attractionList.get(i).setFavorite(true);

                        } else {

                            attractionList.get(i).setFavorite(false);
                        }
                        return;
                    }
                }


            }
        });


    }

    public void loadAttractionInFavoriteList() {
        mMap.clear();
        String attractionName;
        String[] attractionPosition;
        String attractionIcon;
        int resID;
        for (int i = 0; i < attractionList.size(); ++i) {
            if (attractionList.get(i).isFavorite == true) {
                attractionName = attractionList.get(i).name;
                attractionPosition = attractionList.get(i).position.split(",");
                attractionIcon = attractionList.get(i).icon;
                if (!attractionIcon.equals("null")) {
                    resID = getResources().getIdentifier(attractionIcon, "drawable", "com.newthinktank.derekbanas.mini_project_2");
                    mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(attractionPosition[0]),
                            Double.parseDouble(attractionPosition[1]))).title(attractionName).icon(BitmapDescriptorFactory.fromResource(resID)));
                }else{
                    mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(attractionPosition[0]),
                            Double.parseDouble(attractionPosition[1]))).title(attractionName));
                }
            }
        }
    }


    private void displayInformation() {
        for (int i=0; i < attractionList.size(); ++i) {
            String[] latlong = attractionList.get(i).position.split(",");
            double latitude = Double.parseDouble(latlong[0]);
            double longitude = Double.parseDouble(latlong[1]);
            LatLng location = new LatLng(latitude, longitude);
            if (currentMarker.getPosition().equals(location)) {
                name = attractionList.get(i).name;
                description = attractionList.get(i).description;
                phone = attractionList.get(i).phone;
                address = attractionList.get(i).address;
                closing = attractionList.get(i).closing;
                opening = attractionList.get(i).opening;
                image = attractionList.get(i).image;
                website = attractionList.get(i).website;
                rating = attractionList.get(i).rating;
                isFavorite = attractionList.get(i).isFavorite;
                setView(name, description, phone, address, closing, opening, image, website, rating, isFavorite);
                return;
            }
        }

    }

    private void setView(String name, String description, String phone, String address, String closing, String opening, String image, String website, int rating, boolean isFavorite) {
        ImageView imageView = (ImageView) findViewById(R.id.image);
        imageView.setImageResource(getResources().getIdentifier(
                image, "drawable", "com.newthinktank.derekbanas.mini_project_2"));

        TextView descriptionView = (TextView)findViewById(R.id.description);
        descriptionView.setText(description);

        TextView nameView = (TextView) findViewById(R.id.name);
        nameView.setText(name);

        TextView phoneView = (TextView) findViewById(R.id.phone);
        phoneView.setText("Tel: " + phone);

        RatingBar barView = (RatingBar) findViewById(R.id.ratingBar);
        barView.setRating(rating);

        TextView webView = (TextView) findViewById(R.id.website);
        webView.setText("Web: " + website);

        ToggleButton toggleButton = (ToggleButton) findViewById(R.id.toggleButton);
        toggleButton.setChecked(isFavorite);

    }

    private void setUpMapIfNeeded() throws JSONException, IOException {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        String name = data.getStringExtra("name");
        String description = data.getStringExtra("description");
        String address = data.getStringExtra("address");
        String phone = data.getStringExtra("phoneNumber");
        String website = data.getStringExtra("website");
        int rating = data.getIntExtra("rating", -1);
        String openingTime = data.getStringExtra("opening");
        String closingTime = data.getStringExtra("closing");
        String image = "ic_launcher";
        String icon = "null";
        boolean isFavorite = false;

        if (name != null) {
            mMap.addMarker(new MarkerOptions().position(newMarker).title(name));
            String position = newMarker.latitude + "," + newMarker.longitude;
           Attraction newAttraction = new Attraction(isFavorite, address, name, phone, website, position, rating, openingTime, closingTime,
                    icon, image, description);
            attractionList.add(newAttraction);
            JSONArray newJA = new JSONArray();
            for (int i = 0; i < attractionList.size(); i++)
                try {
                    newJA.put(new JSONObject(attractionList.get(i).toJSON()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            writeDataToFile(newJA.toString());
        }
    }


    private void setUpMap() throws JSONException, IOException {
        initialize();
    }

    public void findRoute(View view) {
        mMap.clear();
        loadAttraction();
        gps = new GPSTracker(MainActivity.this);
        LatLng origin = new LatLng(gps.getLatitude(), gps.getLongitude());
        LatLng dest = new LatLng(currentMarker.getPosition().latitude, currentMarker.getPosition().longitude);
        String url = getDirectionsUrl(origin, dest);
        DownloadTask downloadTask = new DownloadTask();
        downloadTask.execute(url);
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;


        return url;
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception while downloading url", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }


    public void HandleSpinner() {
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.attractiontype, android.R.layout.simple_dropdown_item_1line);
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    loadAttraction();
                } else if (position == 1) {
                    root.closeMenu();
                    createNewLocation = 1;
                    Toast.makeText(getBaseContext(), "Chose a position on the map", Toast.LENGTH_LONG).show();
                    mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                        @Override
                        public void onMapClick(LatLng position) {
                            if (createNewLocation == 1) {
                                newMarker = position;
                                Intent getPopupIntent = new Intent(getBaseContext(), PopupWindow.class);
                                final int result = 1;
                                startActivityForResult(getPopupIntent, result);
                                createNewLocation = 0;
                            } else {
                                root.closeMenu();
                            }
                        }
                    });
                } else if (position == 2) {
                    mMap.clear();
                    loadAttractionInFavoriteList();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    public void goToWebsite(View view) {
        TextView web = (TextView) findViewById(R.id.website);
        Intent NameOfTheIntent = new Intent(Intent.ACTION_VIEW, Uri.parse((String) web.getText()));
        startActivity(NameOfTheIntent);
    }

    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);

        }
    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(2);
                lineOptions.color(Color.RED);

            }

            // Drawing polyline in the Google Map for the i-th route
            mMap.addPolyline(lineOptions);
        }
    }

    public void showMessage(String title, String Message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(Message);
        builder.show();
    }

    public void writeDataToFile(String storedData) {
        BufferedWriter bufferedWriter = null;
        try {
            FileOutputStream fileOutputStream = openFileOutput("Data", Context.MODE_PRIVATE);
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(fileOutputStream));

            bufferedWriter.write(storedData);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bufferedWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void loadAttraction() {
        String attractionName;
        String[] attractionPosition;
        String attractionIcon;
        int resID;
        for (int i = 0; i < attractionList.size(); ++i) {
            attractionName = attractionList.get(i).name;
            attractionPosition = attractionList.get(i).position.split(",");
            attractionIcon = attractionList.get(i).icon;
            if (!attractionIcon.equals("null")) {
                resID = getResources().getIdentifier(attractionIcon, "drawable", "com.newthinktank.derekbanas.mini_project_2");
                mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(attractionPosition[0]),
                        Double.parseDouble(attractionPosition[1]))).title(attractionName).icon(BitmapDescriptorFactory.fromResource(resID)));
            }else
            {
                mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(attractionPosition[0]),
                        Double.parseDouble(attractionPosition[1]))).title(attractionName));
            }
        }
    }


    public void initialize() throws JSONException, IOException {
            FileOutputStream fileOutputStream = openFileOutput("Data", Context.MODE_APPEND);
            String result = "";
            BufferedReader bufferedReader = null;
            FileInputStream fileInputStream = openFileInput("Data");
            bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
            String line = bufferedReader.readLine();
            if(line == null)
            {
                InputStream inputStream = getResources().openRawResource(R.raw.data);
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                try {
                    while ((line = bufferedReader.readLine()) !=null)
                    {
                        result = result + line;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else{

                while (line != null) {
                    result = result + line;
                    line = bufferedReader.readLine();
                }
            }

        JSONArray jsonArray = new JSONArray(result);
       for(int i=0; i <jsonArray.length();++i)
            attractionList.add(new Attraction(jsonArray.getString(i)));
    }
}
