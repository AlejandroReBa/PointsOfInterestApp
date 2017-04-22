package xyz.alejandoreba.pointsofinterestapp;

/**
 * Created by AlejandroReBa on 22/04/2017.
 */

public class PointOfInterest {
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
}
