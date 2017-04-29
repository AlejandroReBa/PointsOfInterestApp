package xyz.alejandoreba.pointsofinterestapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;

//task 8
public class PoiListActivity extends Activity implements POIsInterface {

    private ArrayList<PointOfInterest> POIsList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getResources().getConfiguration().orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE)
        {
            finish();
            return;
        }

        setContentView(R.layout.activity_poi_list);

        Intent intent = this.getIntent();
        if(intent!=null)
        {
            Bundle bundle = intent.getExtras();
            //get all places, send in any way to this, or to the fragment? and then the fragment
            //display the list. or to obtain directly from the main activity
            //this.pointsOfInterestList = bundle.getParcelableArrayList(); //implements parcelable in PointOfInterest, and then get it here
            //after that the fragment in onActivityCreated can calla method from this class (getpois whatever) and create the own list.
            //the name of the method should be the same in both of them... but maybe won't work, in that case use a interface just for
            //implementing that method. Therefore you can refer to that interface.

            this.POIsList = bundle.getParcelableArrayList("poiList");

            /*
            Double latitude = intent.getDoubleExtra("latitude", 50.0);
            Double longitude = intent.getDoubleExtra("longitude", -0.5);
            if(latitude != null && longitude != null){
                MapFragment mapFragment = (MapFragment)getFragmentManager().findFragmentById(R.id.mapFragment2);
                mapFragment.centerMap(latitude, longitude);
            }
            */

        }
    }

    //task 8
    public ArrayList<PointOfInterest> getPOIsList(){
        return this.POIsList;
    }

    @Override
    public void receiveLocation(double latitude, double longitude) {

        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putDouble("latitude", latitude);
        bundle.putDouble("longitude", longitude);

        intent.putExtras(bundle);

        setResult(RESULT_OK,intent);
        finish();

    }
}
