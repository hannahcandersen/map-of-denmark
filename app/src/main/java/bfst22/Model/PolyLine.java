package bfst22.Model;

import java.io.Serializable;
import java.util.List;

import bfst22.view.Drawable;
import javafx.scene.canvas.GraphicsContext;

/*
    Så snart at OSM-parseren er færdig, skal vi ikke længere gemme på OSMNode og OSMWay objekterne.
        - det tager for meget unødvendig plads
    Derfor har vi denne klasse (PolyLine) til at gemme på de koordinater der skal tegnes
        - gør koden mere kompakt
 */

public class PolyLine implements Drawable, Serializable {
    public static final long serialVersionUID = 134123;
    //Programmet skal kun huske en sekvens af floats koordinater
    float[] coords;

    public PolyLine(List<OSMNode> nodes) {
        //Opretter et nyt float-array.
        //Størrelsen er *2, fordi der er et y-koordinat for hvert x-koordinat
        coords = new float[nodes.size() * 2];
        int i = 0;
        //Gemmer alle lat og lon koordinater (x-,y-koordinater) fra OSMNode-listen i float-arrayet
        for (var node : nodes) {
            coords[i++] = node.lat;
            coords[i++] = node.lon;
        }
    }

    @Override
    public void trace(GraphicsContext gc) {
        //Finder start-punktet (x og y koordinaterne)
        gc.moveTo(coords[0], coords[1]);
        //Tegner alle OSMNodes
        for (var i = 2 ; i < coords.length ; i += 2) {
            gc.lineTo(coords[i], coords[i+1]);
        }
    }
}