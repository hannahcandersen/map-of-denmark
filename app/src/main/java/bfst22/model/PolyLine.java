package bfst22.model;

import java.io.Serializable;
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
    public static final long serialVersionUID = 134123;
    private float currentMinX, currentMaxX, currentMinY, currentMaxY;
    private WayType wayType;

    //Gemmer en sekvens af floats koordinater
    private float[] coords;
    private ArrayList<OSMNode> nodes;

     public PolyLine(ArrayList<OSMNode> nodes, WayType wayType){
         int i = 0;
         this.nodes = new ArrayList<>(nodes);
         this.wayType = wayType;

         // Opretter et nyt float-array. Størrelsen er *2, fordi der er et y-koordinat for hvert x-koordinat
         coords = new float[nodes.size() * 2];
         currentMinX = nodes.get(0).getLon();
         currentMaxX = nodes.get(0).getLon();
         currentMinY = nodes.get(0).getLat();
         currentMaxY = nodes.get(0).getLat();

         //Gemmer alle lat og lon koordinater (x-,y-koordinater) fra OSMNode-listen i float-arrayet
         for (var node : nodes) {
             //Løber alle coords igennem og opdaterer currentMax og currentMin, hvis den finder noget der er større end currentmax eller mindre end current min
             if (node.getLat() > currentMaxY) {currentMaxY = node.getLat();}
             if (node.getLat() < currentMinY) {currentMinY = node.getLat();}
             if (node.getLon() > currentMaxX) {currentMaxX = node.getLon();}
             if (node.getLon() < currentMinX) {currentMinX = node.getLon();}
             coords[i++] = node.getLat();
             coords[i++] = node.getLon();}
     }

    public PolyLine(ArrayList<OSMNode> nodes) {
        int i = 0;
        this.nodes = new ArrayList<>(nodes);

        //Opretter et nyt float-array. Størrelsen er *2, fordi der er et y-koordinat for hvert x-koordinat
        coords = new float[nodes.size() * 2];
        currentMinX = nodes.get(0).getLon();
        currentMaxX = nodes.get(0).getLon();
        currentMinY = nodes.get(0).getLat();
        currentMaxY = nodes.get(0).getLat();
        //Gemmer alle lat og lon koordinater (x-,y-koordinater) fra OSMNode-listen i float-arrayet
        for (var node : nodes) {
            //Opdatere currentMax og currentmin
            if (node.getLat() > currentMaxY) {currentMaxY = node.getLat();}
            if (node.getLat() < currentMinY) {currentMinY = node.getLat();}
            if (node.getLon() > currentMaxX) {currentMaxX = node.getLon();}
            if (node.getLon() < currentMinX) {currentMinX = node.getLon();}
            coords[i++] = node.getLat();
            coords[i++] = node.getLon();}
    }

    @Override
    public void trace(GraphicsContext gc) {
        gc.moveTo(coords[0], coords[1]);
        for (var i = 2; i < coords.length; i += 2) {
            gc.lineTo(coords[i], coords[i + 1]);}}



    ///////////////// GETTER METODER /////////////////

    @Override
    public float getCurrentMinX() {
        return currentMinX;
    }
    @Override
    public float getCurrentMaxX() {
        return currentMaxX;
    }
    @Override
    public float getCurrentMinY() {
        return currentMinY;
    }
    @Override
    public float getCurrentMaxY() {
        return currentMaxY;
    }
    @Override
    public ArrayList<OSMNode> getNodes(){
        return nodes;
    }
    @Override
    public WayType getType(){
        return this.wayType;
    }
    //Returner vejens midtpunkt
    @Override
    public float getMiddleX() {
        return (currentMaxX + currentMinX) / 2;
    }
    @Override
    public float getMiddleY() {
        return (currentMinY + currentMaxY) / 2;
    }

}