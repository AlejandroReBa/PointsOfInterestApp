package xyz.alejandoreba.pointsofinterestapp;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by AlejandroReBa on 22/04/2017.
 */

public class PointOfInterest implements Parcelable{
    private String name;
    private String type;
    private String description;
    private Double lat;
    private Double lon;

    public PointOfInterest(String nameIn, String typeIn, String descriptionIn, Double latIn, Double lonIn){
        this.name = nameIn;
        this.type = typeIn;
        this.description = descriptionIn;
        this.lat = latIn;
        this.lon = lonIn;
    }

    public String getName(){
        return this.name;
    }

    public String getType(){
        return this.type;
    }

    public String getDescription(){
        return this.description;
    }

    public Double getLat(){
        return this.lat;
    }

    public Double getLon(){
        return this.lon;
    }


    //task 8
    public PointOfInterest(Parcel in) {
        super();
        readFromParcel(in);
    }

    public static final Parcelable.Creator<PointOfInterest> CREATOR = new Parcelable.Creator<PointOfInterest>() {
        public PointOfInterest createFromParcel(Parcel in) {
            return new PointOfInterest(in);
        }

        public PointOfInterest[] newArray(int size) {

            return new PointOfInterest[size];
        }

    };


    public void readFromParcel(Parcel in) {
        this.name = in.readString();
        this.type = in.readString();
        this.description = in.readString();
        this.lat = in.readDouble();
        this.lon = in.readDouble();

    }

    @Override
    public int describeContents() {
        return 0;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.type);
        dest.writeString(this.description);
        dest.writeDouble(this.lat);
        dest.writeDouble(this.lon);
    }
}
