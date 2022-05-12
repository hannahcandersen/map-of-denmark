package bfst22.model;

import java.io.Serializable;

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

    public long getId(){
        return id;
    }
    public float getLat(){return lat;}
    public float getLon(){return lon;}



}