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

            this.POIsList = bundle.getParcelableArrayList("poiList");
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
