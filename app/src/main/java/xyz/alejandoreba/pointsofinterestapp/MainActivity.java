package xyz.alejandoreba.pointsofinterestapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements POIsInterface {

    //private MapView mv;
    //private ItemizedIconOverlay<OverlayItem> items;
    private ArrayList<PointOfInterest> POIsList;
    private double lastLatitude;
    private double lastLongitude;
    private int lastZoom;
    //private MyLocationNewOverlay mLocationOverlay;
    private static final String POIsListFileName = "pois.csv";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // This line sets the user agent, a requirement to download OSM maps
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));

        //set map, zoom and center
        setContentView(R.layout.activity_main);
        //mv = (MapView) findViewById(R.id.map1);

        //initialize the list of Points of Interest
        if (savedInstanceState != null && !savedInstanceState.isEmpty()){
            Toast.makeText(this, "poisList LOADED FROM BUNDLE ",
                    Toast.LENGTH_LONG).show();
            this.POIsList = savedInstanceState.getParcelableArrayList("poisList");
            this.lastLatitude = savedInstanceState.getDouble("lat");
            this.lastLongitude = savedInstanceState.getDouble("lon");
            this.lastZoom = savedInstanceState.getInt("zoomLevel");
        }else{
            this.POIsList = new ArrayList<>();
            this.lastLatitude = 50.9319;
            this.lastLongitude = -1.4011;
            this.lastZoom = 15;
        }


        //initialize the list of OverlayItems (POIs) over the map
         //this.items = new ItemizedIconOverlay<OverlayItem>(this, new ArrayList<OverlayItem>(), null);

        //add the MyLocation Overlay to track the position of the user
       // this.mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this), mv);
       // this.mLocationOverlay.enableMyLocation();
       // mv.getOverlays().add(this.mLocationOverlay);

        //listener for tap clicks on the map
        //when you click you intend the new activity to add a POI
        /*
        MapEventsReceiver eventsReceiver = new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                return false;
            }

            @Override
            public boolean longPressHelper(GeoPoint p) {
                //Toast.makeText (MainActivity.this, "longPressHelper go go go", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(MainActivity.this, addPOI.class);
                Bundle bundle = new Bundle();
                bundle.putDouble("poilat", p.getLatitude());
                bundle.putDouble("poilon", p.getLongitude());
                intent.putExtras(bundle);
                startActivityForResult(intent, 0);

                return true;
            }
        };

        Overlay eventsOnMap = new MapEventsOverlay(eventsReceiver);
        mv.getOverlays().add(eventsOnMap);
        //
        */

        /*
        //center and zoom the map
        mv.getController().setZoom(14);
        mv.setBuiltInZoomControls(true);
        mv.getController().setCenter(new GeoPoint(50.9319, -1.4011));
        */

        /*
        //check GPS service is on
        LocationManager mgr=(LocationManager)getSystemService(Context.LOCATION_SERVICE);
        try {
            mgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }catch(SecurityException ex){
            new AlertDialog.Builder(this).setMessage("ERROR: we need permission for track your position").
                    setPositiveButton("OK", null).show();
        }
        */
    }

    /*
    @Override
    public void onLocationChanged(Location newLoc) {
        Toast.makeText (this, "Location=" +
                newLoc.getLatitude()+ " " +
                newLoc.getLongitude() , Toast.LENGTH_LONG).show();
        mv.getController().setCenter(new GeoPoint(newLoc.getLatitude(), newLoc.getLongitude()));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Toast.makeText(this, "Status changed: " + status,
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(this, "Provider " + provider +
                " enabled", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(this, "Provider " + provider +
                " disabled", Toast.LENGTH_LONG).show();
    }
    */

    //methods to inflate the menu and manage the start
    //of the distinct activities displayed in the menu

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == R.id.addpoi)
        {
            Intent intent = new Intent (this, addPOI.class);
            Bundle bundle = new Bundle();
            //add a POI where the map is centered
            //bundle.putDouble("poilat", this.mv.getMapCenter().getLatitude());
            //bundle.putDouble("poilon", this.mv.getMapCenter().getLongitude());
            //changed at task8 because use of fragments
            MapFragment mapFragment = (MapFragment)getFragmentManager().findFragmentById(R.id.mapFragment);
            bundle.putDouble("poilat", mapFragment.getLatitude());
            bundle.putDouble("poilon", mapFragment.getLongitude());
            intent.putExtras(bundle);
            startActivityForResult(intent, 0);
            return true;
            //add a POI where the gps track the user is.
            /*
            if(this.mLocationOverlay.getMyLocation() != null) {
                bundle.putDouble("poilat", this.mLocationOverlay.getMyLocation().getLatitude());
                bundle.putDouble("poilon", this.mLocationOverlay.getMyLocation().getLongitude());
                intent.putExtras(bundle);
                startActivityForResult(intent, 0);
                return true;
            }else{
                Toast.makeText(this, "It's not possible to add a new POI because we couldn't find your location",
                        Toast.LENGTH_LONG).show();
                return false;
            }
            */
        }else if (item.getItemId() == R.id.savepois){
            savePOIs(POIsListFileName, this.POIsList);
            return true;
            //task 4
        }else if (item.getItemId() == R.id.prefsactivity){
            Intent intent = new Intent (this, MyPrefsActivity.class);
            startActivityForResult(intent, 1);
            return true;
            //task 5
        }else if (item.getItemId() == R.id.loadpois) {
            loadPOIs(POIsListFileName, this.POIsList);
            return true;
            //task 6
        }else if (item.getItemId() == R.id.loadpoisfromweb) {
            MyTask task = new MyTask();
            task.execute("load");
            return true;
            //task 8
        }else if (item.getItemId() == R.id.listofpois){
            //depends on the poiListFragment and mapFragment being in the same activity
            Intent intent = new Intent (this, PoiListActivity.class);
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("poiList", this.POIsList);
            intent.putExtras(bundle);
            startActivityForResult(intent, 2);
            return true;
        }
        return false;
    }

    //method to manage results of activities.
    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent intent)
    {
        if(requestCode==0)
        {

            if (resultCode==RESULT_OK)
            {
                Bundle extras=intent.getExtras();
                String name = extras.getString("poiname");
                String type = extras.getString("poitype");
                String description = extras.getString("poidescription");
                double lat = extras.getDouble("poilat");
                double lon = extras.getDouble("poilon");

                //task 2 --> part of create overlayItem, mv.getOverlays().add(items) and mv.invalidate migrated to MapFragment
                this.POIsList.add(new PointOfInterest(name,type,description,lat,lon));
                MapFragment mapFragment = (MapFragment)getFragmentManager().findFragmentById(R.id.mapFragment);
                mapFragment.addItem(name,type,description,lat,lon);
                /*
                OverlayItem newItem = new OverlayItem(name,type + description, new GeoPoint(lat,lon));
                items.addItem(newItem);
                mv.getOverlays().add(items);
                //refresh the map in order to show the new POI immediately
                mv.invalidate();

                Toast.makeText(this, "POI added -> name: " + name + ", type: " + type +
                                ", lat: " + lat + ", lon: " + lon + " description: " + description,
                        Toast.LENGTH_LONG).show();
                */
                //task 8
                if (getResources().getConfiguration().orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE)
                {
                    PoiListFragment poiListFragment = (PoiListFragment) getFragmentManager().findFragmentById(R.id.poiListFragment);
                    poiListFragment.createEntriesAndAdapter();
                }

                //task 4
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                String uploadToWeb = prefs.getString("uploadweb","NO");
                if (uploadToWeb.equals("YES")){
                    //task 7
                    MyTask task = new MyTask();
                    task.execute("add", name, type, description, String.valueOf(lat), String.valueOf(lon));
                }
            }
        //requestCode == 1 is not being used at the moment
        }else if (requestCode==2){
            if (resultCode==RESULT_OK) {
                Bundle extras = intent.getExtras();
                receiveLocation(extras.getDouble("latitude", 50.0), extras.getDouble("longitude", -0.5));
            }
        }
    }

    //task 3
    private void savePOIs(String fileName, List<PointOfInterest> list){
        String savedText = "";
        for (PointOfInterest poi : list) {
            savedText += poi.getName() + "," + poi.getType() + ",\"" + poi.getDescription() + "\"," + poi.getLat() + "," + poi.getLon() + "\n";
        }
        try
        {
            PrintWriter pw =
                    new PrintWriter(new FileWriter(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + fileName));
            pw.println(savedText);
            pw.close(); // close the file to ensure data is flushed to file
            Toast.makeText(this, "POIs have been saved into file " + fileName,
                    Toast.LENGTH_LONG).show();
        }
        catch(IOException e)
        {
            new AlertDialog.Builder(this).setMessage("ERROR: seems to have been a problem saving the POIs").
                    setPositiveButton("OK", null).show();
        }
    }

    //task 5
    private void loadPOIs(String fileName, List<PointOfInterest> list){
        try
        {
            FileReader fr = new FileReader(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + fileName);
            BufferedReader reader = new BufferedReader(fr);
            //clean the list of POIs
            list.clear();
            String line = "";
            while((line = reader.readLine()) != null)
            {
                String[] components = line.split(",");
                if(components.length == 5)
                {
                    PointOfInterest currentPOI = new PointOfInterest (components[0], components[1], components[2], Double.parseDouble(components[3]), Double.parseDouble(components[4]));
                    list.add(currentPOI);
                }
            }
            reader.close();
            displayPOIs(list);
            Toast.makeText(this, "POIs have been loaded from file " + POIsListFileName,
                    Toast.LENGTH_LONG).show();
        }
        catch(IOException e)
        {
            new AlertDialog.Builder(this).setMessage("ERROR: seems to have been a problem reading in the POIs from file " + fileName).
                    setPositiveButton("OK", null).show();

        }
    }

    //task 8, to allow the mapfragment update the list of items when MainActivity is re-created
    public void displayPOIs() {
        displayPOIs(this.POIsList);
    }

    //task 5
    public void displayPOIs(List<PointOfInterest> list){
        /*
        this.items.removeAllItems();
        for (PointOfInterest poi : list){
            OverlayItem newItem = new OverlayItem(poi.getName(), poi.getType() + poi.getDescription(), new GeoPoint(poi.getLat(),poi.getLon()));
            //setMarker(newItem,type);
            items.addItem(newItem);
        }
        mv.getOverlays().add(items);
        mv.invalidate();
        */
        MapFragment mapFragment = (MapFragment)getFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.removeItems();
        for (PointOfInterest poi : list){
            mapFragment.addItem(poi.getName(), poi.getType(), poi.getDescription(), poi.getLat(),poi.getLon());
        }


        //task 8
        if (getResources().getConfiguration().orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE)
        {
            PoiListFragment poiListFragment = (PoiListFragment) getFragmentManager().findFragmentById(R.id.poiListFragment);
            poiListFragment.createEntriesAndAdapter();
        }

    }
/*
    //task 3
    @Override
    protected void onDestroy() {
        super.onDestroy();
        savePOIs(POIsListFileName, POIsList);
    }
*/

    //task 3
    @Override
    protected void onPause() {
        super.onPause();
        savePOIs(POIsListFileName, POIsList);
    }

    //to keep the poisList when screen rotates
    @Override
    public void onSaveInstanceState (Bundle savedInstanceState)
    {
        Toast.makeText(this, "poisList SAVED IN BUNDLE ",
                Toast.LENGTH_LONG).show();
        savedInstanceState.putParcelableArrayList("poisList", this.POIsList);
        MapFragment mapFragment = (MapFragment)getFragmentManager().findFragmentById(R.id.mapFragment);
        savedInstanceState.putDouble("lat", mapFragment.getLatitude());
        savedInstanceState.putDouble("lon", mapFragment.getLongitude());
        savedInstanceState.putInt("zoomLevel", mapFragment.getZoomLevel());

    }



    //task 6, task 7
    //class MyTask extends AsyncTask<Void,Void,String>
    private class MyTask extends AsyncTask<String,Void,String>
    {
        //public String doInBackground(Void... unused)
        public String doInBackground(String... params)
            {
            HttpURLConnection conn = null;
            try
            {   //task 6
                if (params[0] != null && params[0].equals("load")) {
                    String urlstring = "http://www.free-map.org.uk/course/mad/ws/get.php?year=17&username=user002&format=json";
                    URL url = new URL(urlstring);

                    conn = (HttpURLConnection) url.openConnection();
                    InputStream in = conn.getInputStream();
                    if (conn.getResponseCode() == 200) {
                        BufferedReader br = new BufferedReader(new InputStreamReader(in));
                        String result = "", line;
                        while ((line = br.readLine()) != null)
                            result += line;

                        JSONArray jsonArr = new JSONArray(result);
                        String name, type, description;
                        Double lat, lon;

                        POIsList.clear();
                        for (int i = 0; i < jsonArr.length(); i++) {
                            JSONObject currentObject = jsonArr.getJSONObject(i);
                            name = currentObject.getString("name");
                            type = currentObject.getString("type");
                            description = currentObject.getString("description");
                            lat = currentObject.getDouble("lat");
                            lon = currentObject.getDouble("lon");

                            POIsList.add(new PointOfInterest(name, type, description, lat, lon));
                        }

                        return "POIs have been loaded from the web successfully";
                    } else {
                        return "HTTP ERROR: " + conn.getResponseCode();
                    }
                }else{ //task 7
                    String urlstring = "http://www.free-map.org.uk/course/mad/ws/add.php";
                    URL url = new URL(urlstring);
                    String postData = "username=user002&name=" + params[1] + "&type=" + params[2] + "&description=" + params[3]
                            + "&lat=" + params[4] + "&lon=" + params[5] + "&year=17";
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setDoOutput(true);
                    conn.setFixedLengthStreamingMode(postData.length());
                    OutputStream out = null;
                    out = conn.getOutputStream();
                    out.write(postData.getBytes());
                    InputStream in = conn.getInputStream();
                    if (conn.getResponseCode() == 200) {
                        return "POI has been added to the web successfully";
                    } else {
                        return "HTTP ERROR: " + conn.getResponseCode();
                    }
                }
            }
            catch(IOException e)
            {
                return e.toString();
            }
            catch (JSONException e){
                return e.toString();
            }
            finally
            {
                if(conn!=null)
                    conn.disconnect();
            }
        }

        public void onPostExecute(String result)
        {
            displayPOIs(POIsList);
            Toast.makeText(MainActivity.this, result,
                    Toast.LENGTH_LONG).show();
        }
    }

    //task 8
    public ArrayList<PointOfInterest> getPOIsList(){
        return this.POIsList;
    }

    //task 8
    public void receiveLocation (double latitude, double longitude)
    {
        MapFragment mapFragment = (MapFragment)getFragmentManager().findFragmentById(R.id.mapFragment);
        // set its contents
        mapFragment.centerMap(latitude, longitude);
    }

    //task 8 --> to allow center of the map is not changed when screen is rotated
    public void centerMapZoom(){
        MapFragment mapFragment = (MapFragment)getFragmentManager().findFragmentById(R.id.mapFragment);
        // set its contents
        mapFragment.centerMap(this.lastLatitude, this.lastLongitude);
        mapFragment.changeZoom(this.lastZoom);
    }



    //when long click add a new POI within MapFragment-->this method is used to launch addPOI activity from here
    public void startActivityUsingIntent(Intent intent, int code){
        startActivityForResult(intent, code);
    }

}

