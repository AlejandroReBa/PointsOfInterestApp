package xyz.alejandoreba.pointsofinterestapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

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

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements LocationListener {

    private MapView mv;
    private ItemizedIconOverlay<OverlayItem> items;
    private List<PointOfInterest> POIsList;
    private MyLocationNewOverlay mLocationOverlay;
    private static final String POIsListFileName = "pois.csv";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // This line sets the user agent, a requirement to download OSM maps
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));

        //set map, zoom and center
        setContentView(R.layout.activity_main);
        mv = (MapView) findViewById(R.id.map1);

        //initialize the list of Points of Interest
        this.POIsList = new ArrayList<>();

        //initialize the list of OverlayItems (POIs) over the map
         this.items = new ItemizedIconOverlay<OverlayItem>(this, new ArrayList<OverlayItem>(), null);

        //add the MyLocation Overlay to track the position of the user
        this.mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this), mv);
        this.mLocationOverlay.enableMyLocation();
        mv.getOverlays().add(this.mLocationOverlay);

        //listener for tap clicks on the map
        //when you click you intend the new activity to add a POI
        MapEventsReceiver eventsReceiver = new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                /*
                Toast.makeText (MainActivity.this, "singleTapConfirmedHelper go go go -> lat:" + p.getLatitude() + " , long: " + p.getLongitude(), Toast.LENGTH_LONG).show();
                return false;
                */
                /*
                Intent intent = new Intent(MainActivity.this, addPOI.class);
                Bundle bundle = new Bundle();
                bundle.putDouble("poilat", p.getLatitude());
                bundle.putDouble("poilon", p.getLongitude());
                intent.putExtras(bundle);
                startActivityForResult(intent, 0);
                */
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

        //center and zoom the map
        mv.getController().setZoom(14);
        mv.setBuiltInZoomControls(true);
        mv.getController().setCenter(new GeoPoint(50.9319, -1.4011));

        //check GPS service is on
        LocationManager mgr=(LocationManager)getSystemService(Context.LOCATION_SERVICE);
        try {
            mgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }catch(SecurityException ex){
            new AlertDialog.Builder(this).setMessage("ERROR: we need permission for track your position").
                    setPositiveButton("OK", null).show();
        }
    }

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
            //System.exit(0);
            Intent intent = new Intent (this, addPOI.class);
            Bundle bundle = new Bundle();
            //add a POI where the map is centered
            //bundle.putDouble("poilat", this.mv.getMapCenter().getLatitude());
            //bundle.putDouble("poilon", this.mv.getMapCenter().getLongitude());
            //add a POI where the gps track the user is.
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
        }else if (item.getItemId() == R.id.savepois){
            savePOIs(POIsListFileName, this.POIsList);
            return true;
            //task 4
        }else if (item.getItemId() == R.id.prefsactivity){
            Intent intent = new Intent (this, MyPrefsActivity.class);
            startActivityForResult(intent, 1);
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


                Toast.makeText(this, "POI virtually added -> name: " + name + ", type: " + type +
                        ", lat: " + lat + ", lon: " + lon + " description: " + description,
                        Toast.LENGTH_LONG).show();

                //task 4
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                String uploadToWeb = prefs.getString("uploadWebOptions","NO");
                if (uploadToWeb == "YES"){
                    //To do, code to upload the new POI to the web
                }

                //task 2
                this.POIsList.add(new PointOfInterest(name,type,description,lat,lon));
                OverlayItem newItem = new OverlayItem(name,type + description, new GeoPoint(lat,lon));
                items.addItem(newItem);
                mv.getOverlays().add(items);
                //refresh the map in order to show the new POI immediately
                mv.invalidate();

            }

        }
    }

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
            Toast.makeText(this, "POIs has been saved into the file " + fileName,
                    Toast.LENGTH_LONG).show();
        }
        catch(IOException e)
        {
            new AlertDialog.Builder(this).setMessage("ERROR: seems to have been a problem saving the POIs").
                    setPositiveButton("OK", null).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        savePOIs(POIsListFileName, POIsList);
    }

}

