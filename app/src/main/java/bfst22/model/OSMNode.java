package bfst22.model;

import java.io.Serializable;

/*
    OSMNode er alle koordinaterne på kortet
    Den har et id og lat og lon
 */

public class OSMNode implements Serializable {
    public static final long serialVersionUID = 9082413;
    private long id;
    private float lat, lon;

    public OSMNode(long id, float lat, float lon) {
        this.id = id;
        this.lat = lat;
        this.lon = lon;
    }

    public OSMNode(float lat, float lon) {
        this.lat = lat;
        this.lon = lon;
    }


    ///////////////// GETTER METODER /////////////////

    public long getId(){
        //System.out.println("id: " + id);
        return id;}
    public float getLat(){
        //System.out.println("lat: " + lat);
        return lat;
    }
    public float getLon(){
        //System.out.println("lon: " + lon);
        return lon;}



}