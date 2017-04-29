package xyz.alejandoreba.pointsofinterestapp;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.CharArrayReader;
import java.util.ArrayList;


public class PoiListFragment extends ListFragment {

    String[] entries;
    String[] entryValues;// = { "51.51, -0.1", "48.85, 2.34",
                    //"40.75, -74.0"};
    String[] locations;//= { "51.51, -0.1", "48.85, 2.34",
           // "40.75, -74.0"};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        createEntriesAndAdapter();

    }

    //task 8: each entry in the list should show not only the point of interest name, but also its type, on a separate line in smaller font.
    class SelectPOIAdapter extends ArrayAdapter<String>
    {
        public SelectPOIAdapter() {
            super(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1, entries);
        }


        @Override
        public View getView(int index, View convertView, ViewGroup parent){

            View view = convertView;
            if (view == null)
            {
                LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(getActivity().getApplicationContext().LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.fragment_poi_list, parent, false);
            }

            TextView titleTextView = (TextView) view.findViewById(R.id.poi_title);
            TextView descriptionTextView = (TextView) view.findViewById(R.id.poi_type);

            titleTextView.setText(entries[index]);
            descriptionTextView.setText(entryValues[index]);

            return view;
        }
    }

    public void onListItemClick(ListView lv, View v, int index, long id)
    {
        String[] loc = locations[index].split(",");
        //use of the interface because depends on the orientation this fragment refers to mainActivity or PoiListActivity
        POIsInterface activity = (POIsInterface) getActivity();
        activity.receiveLocation(Double.parseDouble(loc[0]), Double.parseDouble(loc[1]));
    }

    public void createEntriesAndAdapter(){
        //use of the interface because depends on the orientation this fragment refers to mainActivity or PoiListActivity
        POIsInterface activity = (POIsInterface) getActivity();
        ArrayList<PointOfInterest> POIsList = activity.getPOIsList();
        int size = POIsList.size();

        if (size == 0){
            this.entries = new String[1];
            this.entryValues = new String[1];
            this.locations = new String[1];

            this.entries[0] = "NO PLACES AVAILABLES";
            this.entryValues[0] = "NO TYPE";
            this.locations[0] = "50.9097, -1.4044";

        }else{
            this.entries = new String[size];
            this.entryValues = new String[size];
            this.locations = new String[size];
        }

        for (int i = 0; i < size; i++){
            PointOfInterest currentPOI = POIsList.get(i);
            this.entries[i] = currentPOI.getName();
            this.entryValues[i] = currentPOI.getType();
            this.locations[i] = currentPOI.getLat() + ", " + currentPOI.getLon();
        }
        Toast.makeText(getActivity(), "SIZE OF POIS LIST=" + entries.length, Toast.LENGTH_LONG).show();


        SelectPOIAdapter selectPOIAdapter = new SelectPOIAdapter();
        setListAdapter(selectPOIAdapter);

        /*
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_list_item_1, entries);
        setListAdapter(adapter);
        */

    }
}
