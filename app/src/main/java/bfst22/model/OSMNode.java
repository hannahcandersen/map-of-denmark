package bfst22.model;

import java.io.Serializable;


//Opretter objekter af "Nodes" fra OSM filen.


public class OSMNode implements Serializable {
    public static final long serialVersionUID = 9082413;
    long id;
    float lat, lon;

    //Tager parameterne ID, bredde- (lat) og længdegrader (lon), fra Noden i .osm filen.
    public OSMNode(long id, float lat, float lon) {
        this.id = id;
        this.lat = lat;
        this.lon = lon;
    }

}