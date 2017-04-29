package xyz.alejandoreba.pointsofinterestapp;

import java.util.ArrayList;

/**
 * Created by Alejandro Reyes (AlejandroReBa) on 29/04/2017.
 */

//task 8
public interface POIsInterface {

    ArrayList<PointOfInterest> getPOIsList();
    void receiveLocation (double latitude, double longitude);
}
