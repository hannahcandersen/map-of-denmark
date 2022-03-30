package bfst22.model;

public class Rectangle {
    private float minLon, maxLon, minLat, maxLat;

    public Rectangle(float minLon, float maxLon, float minLat, float maxLat){
        this.minLon = minLon;
        this.maxLon = maxLon;
        this.minLat = minLat;
        this.maxLat = maxLat;
    }

    public float getMinLon() {return minLon;}
    public float getMinLat() {return minLat;}
    public float getMaxLon() {return maxLon;}
    public float getMaxLat() {return maxLat;}

    public void setMinLon(float minLon) {this.minLon = minLon;}
    public void setMinLat(float minLat) {this.minLat = minLat;}
    public void setMaxLon(float maxLon) {this.maxLon = maxLon;}
    public void setMaxLat(float maxLat) {this.maxLat = maxLat;}

    //En metode til at tegne firkantens omkreds


}
