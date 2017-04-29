package xyz.alejandoreba.pointsofinterestapp;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

//task 8
public class MapFragment extends Fragment implements LocationListener {

    private MapView mv;
    private MyLocationNewOverlay mLocationOverlay;
    private ItemizedIconOverlay<OverlayItem> items;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        //Toast.makeText(getActivity(), "onCreateView has finished + isVisible=" + this.isVisible(), Toast.LENGTH_LONG).show();
        return inflater.inflate(R.layout.fragment_map, parent);
    }

    ///put mv = fdsfasdf a in the method create not before. after or whatever
    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        Activity activity = getActivity();
        // This line sets the user agent, a requirement to download OSM maps
        Configuration.getInstance().load(activity, PreferenceManager.getDefaultSharedPreferences(activity));
        mv = (MapView) getView().findViewById(R.id.map1);
        mv.getController().setZoom(14);
        mv.setBuiltInZoomControls(true);
        mv.getController().setCenter(new GeoPoint(50.9319, -1.4011));

        //initialize the list of OverlayItems (POIs) over the map
        this.items = new ItemizedIconOverlay<OverlayItem>(getActivity(), new ArrayList<OverlayItem>(), null);

        //add the MyLocation Overlay to track the position of the user
        this.mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(getActivity()), mv);
        this.mLocationOverlay.enableMyLocation();
        mv.getOverlays().add(this.mLocationOverlay);

        //listener for tap clicks on the map
        //when you click you intend the new activity to add a POI
        MapEventsReceiver eventsReceiver = new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                return false;
            }

            @Override
            public boolean longPressHelper(GeoPoint p) {
                //Toast.makeText (MainActivity.this, "longPressHelper go go go", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getActivity(), addPOI.class);
                Bundle bundle = new Bundle();
                bundle.putDouble("poilat", p.getLatitude());
                bundle.putDouble("poilon", p.getLongitude());
                intent.putExtras(bundle);
                //startActivityForResult(intent, 0);
                ((MainActivity)getActivity()).startActivityUsingIntent(intent, 0);

                return true;
            }
        };

        Overlay eventsOnMap = new MapEventsOverlay(eventsReceiver);
        mv.getOverlays().add(eventsOnMap);
        //

        //check GPS service is on
        LocationManager mgr=(LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
        try {
            mgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }catch(SecurityException ex){
            new AlertDialog.Builder(getActivity()).setMessage("ERROR: we need permission for track your position").
                    setPositiveButton("OK", null).show();
        }

        //Toast.makeText(getActivity(), "onActivityCreated has finished + isVisible=" + this.isVisible(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onLocationChanged(Location newLoc) {
        Toast.makeText (getActivity(), "Location=" +
                newLoc.getLatitude()+ " " +
                newLoc.getLongitude() , Toast.LENGTH_LONG).show();
        mv.getController().setCenter(new GeoPoint(newLoc.getLatitude(), newLoc.getLongitude()));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Toast.makeText(getActivity(), "Status changed: " + status,
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(getActivity(), "Provider " + provider +
                " enabled", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(getActivity(), "Provider " + provider +
                " disabled", Toast.LENGTH_LONG).show();
    }

    public void centerMap(double latitude, double longitude){
        this.mv.getController().setCenter(new GeoPoint(latitude,longitude));
        this.mv.getController().setZoom(18);
        Toast.makeText(getActivity(), "Map setted to the new position and isVisible " + this.isVisible(), Toast.LENGTH_LONG).show();
    }

    public double getLatitude(){
        return this.mv.getMapCenter().getLatitude();
    }

    public double getLongitude(){
        return this.mv.getMapCenter().getLongitude();
    }

    public void addItem(String name, String type, String description, Double lat, Double lon){
        OverlayItem newItem = new OverlayItem(name,type + " - " + description, new GeoPoint(lat,lon));
        items.addItem(newItem);
        mv.getOverlays().add(items);
        //refresh the map in order to show the new POI immediately
        mv.invalidate();

        Toast.makeText(getActivity(), "POI added -> name: " + name + ", type: " + type +
                        ", lat: " + lat + ", lon: " + lon + " description: " + description,
                Toast.LENGTH_LONG).show();
    }

    public void removeItems(){
        this.items.removeAllItems();
    }
}