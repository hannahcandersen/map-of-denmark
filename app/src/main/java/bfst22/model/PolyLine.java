package bfst22.model;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

import bfst22.view.Drawable;
import javafx.scene.canvas.GraphicsContext;

/*
    Så snart at OSM-parseren er færdig, skal vi ikke længere gemme på OSMNode og OSMWay objekterne.
        - det tager for meget unødvendig plads
    Derfor har vi denne klasse (PolyLine) til at gemme på de koordinater der skal tegnes
        - gør koden mere kompakt
 */

public class PolyLine implements Drawable, Serializable {
    float currentMinX = 0;
    float currentMaxX = 0;
    float currentMinY = 0;
    float currentMaxY = 0;

    public static final long serialVersionUID = 134123;
    //Programmet skal kun huske en sekvens af floats koordinater
    private float[] coords;

    private ArrayList<OSMNode> nodes;


    public PolyLine(ArrayList<OSMNode> nodes) {
        this.nodes = nodes;

        //Opretter et nyt float-array.
        //Størrelsen er *2, fordi der er et y-koordinat for hvert x-koordinat
        coords = new float[nodes.size() * 2];
        int i = 0;
        //Gemmer alle lat og lon koordinater (x-,y-koordinater) fra OSMNode-listen i float-arrayet
        for (var node : nodes) {

            //Opdatere currentMaxY EVT skriver: <
            if (node.getLat() > currentMaxY){
                currentMaxY = node.getLat();
            }
            else if(node.getLat() < currentMinY){
                currentMinY = node.getLat();
            }
            else if(node.getLon() > currentMaxX){
                currentMaxX = node.getLon();
            }
            else if(node.getLon() < currentMinX){
                currentMinX = node.getLon();
            }

            coords[i++] = node.getLat();
            coords[i++] = node.getLon();
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

    //Bruges til at finde vejens midtpunkt
    @Override
    public float getMiddleX(){
        return (currentMaxY + currentMinX) / 2;
    }

    @Override
    public float getMiddleY(){
        return (currentMinY+currentMaxY)/ 2;
    }



}