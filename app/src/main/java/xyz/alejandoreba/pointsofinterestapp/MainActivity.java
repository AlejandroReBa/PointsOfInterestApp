package xyz.alejandoreba.pointsofinterestapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

import java.util.ArrayList;

public class MainActivity extends Activity implements LocationListener {

    MapView mv;
    ItemizedIconOverlay<OverlayItem> items;
    MyLocationNewOverlay mLocationOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // This line sets the user agent, a requirement to download OSM maps
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));

        //set map, zoom and center
        setContentView(R.layout.activity_main);
        mv = (MapView) findViewById(R.id.map1);


        //initialize the list of items (POIs) over the map
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


                OverlayItem newItem = new OverlayItem(name,type,description, new GeoPoint(lat,lon));
                items.addItem(newItem);
                mv.getOverlays().add(items);
                //refresh the map in order to show the new POI immediately
                mv.invalidate();

            }

        }
    }
}

