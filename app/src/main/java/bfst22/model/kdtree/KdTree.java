package bfst22.model.kdtree;

import bfst22.model.MapCanvas;
import bfst22.model.Model;
import bfst22.model.OSMNode;
import bfst22.model.OSMWay;
import bfst22.view.Drawable;

import java.awt.*;
import java.awt.geom.Line2D;
import java.util.*;
import java.util.List;

import static java.awt.geom.Point2D.distanceSq;

public class KdTree {


    //Erklærer roden.
    private static KdNode root;
    static int leavesAdded = 0;

    private OSMNode closestNodeField = null;
    private double closestDistField = Double.POSITIVE_INFINITY;



    public KdTree() {
    }

    public KdTree(List<Drawable> drawables) {
        // buildKDTree(OSMNodes, false);
        this.root = buildKDTree(drawables, true);
    }

    public static int getLeavesAdded() {
        return leavesAdded;
    }

    public KdNode getRoot() {
        return root;
    }

    //////////////////// BULDING KDTREE ///////////////////////
    public static KdNode buildKDTree(List<Drawable> drawables, boolean isXcoord) {
        //If statement der tjekker om der findes et træ
        if (drawables == null || drawables.size() == 0) {
            //System.out.println("OSMNode list is empty!");
            return null;
        }

        //Erklærer
        KdNode kdNode;

        // Laver leaves
        if (drawables.size() < 100) {
            kdNode = new Leaf(drawables);}

        else {
            //Laver en ny node
            Node node = new Node();

            //sorter listen på x
            if (isXcoord) {
                //Vi sorterer listen drawables ved at bruge sort, som tager parametren Comparator.comparing.
                //Den fortæller at vi skal hive fat i OSMNode-klassen og kalde getLon-metoden. Sorter på x.
                drawables.sort(Comparator.comparing(Drawable::getMiddleX));}
            else {
                //sorter listen på y
                drawables.sort(Comparator.comparing(Drawable::getMiddleY));}

            //Finder midterste indeks af listen
            int mid = drawables.size() / 2;


            //Sætter splitværdien
            if (isXcoord) {
                node.setSplit(drawables.get(mid).getMiddleX());}
            else { //!isXcoord
                node.setSplit(drawables.get(mid).getMiddleY());}

            //Laver left og right nodes
            node.setLeft(buildKDTree(drawables.subList(0, mid), !isXcoord));
            node.setRight(buildKDTree(drawables.subList(mid, drawables.size()), !isXcoord));

            kdNode = node;
        }
        root = kdNode;
        return kdNode;
    }

    //////////////////// BULDING KDTREE OF OSM NODES///////////////////////
    public static KdNode buildKDTreeOfOSMNodes(List<OSMNode> OSMNodes, boolean isXcoord) {
        //If statement der tjekker om der findes et træ
        if (OSMNodes == null || OSMNodes.size() == 0) {
            return null;}

        //Erklærer
        KdNode kdNode;

        // Laver leaves
        if (OSMNodes.size() < 100) {
            kdNode = new Leaf(OSMNodes, 0);}

        else {
            //Laver en ny node
            Node node = new Node();

            //sorter listen på x
            if (isXcoord) {
                //Vi sorterer listen drawables ved at bruge sort, som tager parametren Comparator.comparing.
                //Den fortæller at vi skal hive fat i OSMNode-klassen og kalde getLon-metoden. Sorter på x.
                OSMNodes.sort(Comparator.comparing(OSMNode::getLon));}
            else {
                //sorter listen på y
                OSMNodes.sort(Comparator.comparing(OSMNode::getLat));}

            //Finder midterste indeks af listen
            int mid = OSMNodes.size() / 2;


            //Sætter splitværdien
            if (isXcoord) {
                node.setSplit(OSMNodes.get(mid).getLon());
                node.setSplitDenAndenKoordinat(OSMNodes.get(mid).getLat());}

            else { //!isXcoord
                node.setSplit(OSMNodes.get(mid).getLat());
                node.setSplitDenAndenKoordinat(OSMNodes.get(mid).getLon());}

            //Laver left og right nodes
            node.setLeft(buildKDTreeOfOSMNodes(OSMNodes.subList(0, mid), !isXcoord));
            node.setRight(buildKDTreeOfOSMNodes(OSMNodes.subList(mid, OSMNodes.size()), !isXcoord));

            kdNode = node;
        }
        root = kdNode;
        return kdNode;
    }


    //////////////////// RANGE SEARCH ///////////////////////

    //Public den der kaldt i MapCanvas
    //RangeSearch der tager rectangle og root
    public static List<Drawable> rangeSearch(RangeSerachRectangle rangeSerachRectangle, KdNode root) {
        // kald den anden rangesearch, der returner en liste med inrange resultaterne
        leavesAdded = 0;
        return rangeSearch(rangeSerachRectangle, root, true, new ArrayList<>());
    }

    // RangeSearch der kun tager rectangle
    public static List<Drawable> rangeSearch(RangeSerachRectangle rangeSerachRectangle) {
        // kald den anden rangesearch, der returner en liste med inrange resultaterne
        leavesAdded = 0;
        return rangeSearch(rangeSerachRectangle, root, true, new ArrayList<>());
    }

    //Privat RangeSearch metode
    private static List<Drawable> rangeSearch(RangeSerachRectangle rangeSearchRectangle, KdNode splitNode, boolean isXcoord, List<Drawable> result){
        if (splitNode == null){
            return null;
        }

        //Hvis vores splitnode er en leaf, tilføj leaf(s) til result listen
        if (splitNode instanceof Leaf) {
            // For loop der caster splitnode til Leaf of løber alle drawables i leaf igennem
            // Tilføjer kun drawable til result hvis drawable og searchRectangle overlapper
            for (Drawable d : ((Leaf) splitNode).getListOfDrawables()) {
                if (doRectanglesOverlap(d, rangeSearchRectangle)) {
                    result.add(d);
                }
                leavesAdded++;}}

        //Ellers er vores splitnode en splitnode, og vi skal kalde rekursivt ned gennem træet
        else{
            //Gør det på X (isXcoord)
            if(isXcoord){

                //InRange? - mellem minLon og MaxLon
                if(((Node) splitNode).getSplit() > rangeSearchRectangle.getMinLon()
                        && ((Node) splitNode).getSplit() < rangeSearchRectangle.getMaxLon()) {

                    //Gå begge veje
                    rangeSearch(rangeSearchRectangle, ((Node) splitNode).getLeft(), !isXcoord, result);
                    rangeSearch(rangeSearchRectangle, ((Node) splitNode).getRight(), !isXcoord, result);

                }
                // Over? - større end maxLon (((rectangle.getMaxLon())))
                else if (((Node) splitNode).getSplit() > rangeSearchRectangle.getMaxLon()) {
                    //Gå til venstre
                    rangeSearch(rangeSearchRectangle, ((Node) splitNode).getLeft(), !isXcoord, result);
                }
                //Under? - mindre end minLon
                else if(((Node) splitNode).getSplit() < rangeSearchRectangle.getMinLon()) {
                    //Gå til højre
                    rangeSearch(rangeSearchRectangle, ((Node) splitNode).getRight(), !isXcoord, result);
                }
            }

            //Gør det på Y (!isXcoord) Måske skal lat ændres til minus
            else {
                //InRange? - mellem minLon og MaxLon
                if(((Node) splitNode).getSplit() > rangeSearchRectangle.getMinLat()
                        && ((Node) splitNode).getSplit() < rangeSearchRectangle.getMaxLat()) {

                    //Gå begge veje
                    rangeSearch(rangeSearchRectangle, ((Node) splitNode).getLeft(), !isXcoord, result);
                    rangeSearch(rangeSearchRectangle, ((Node) splitNode).getRight(), !isXcoord, result);

                }
                // Over? - større end maxLon (((rectangle.getMaxLon())))
                else if(((Node) splitNode).getSplit() > rangeSearchRectangle.getMaxLat()) {
                    //Gå til venstre
                    rangeSearch(rangeSearchRectangle, ((Node) splitNode).getLeft(), !isXcoord, result);
                }
                //Under? - mindre end minLon
                else if(((Node) splitNode).getSplit() < rangeSearchRectangle.getMinLat()) {
                    //Gå til højre
                    rangeSearch(rangeSearchRectangle, ((Node) splitNode).getRight(), !isXcoord, result);
                }
            }
        }
        return result;
    }


    // checker om et element af drawable overlapper med searchTriangle. https://stackoverflow.com/questions/23302698/java-check-if-two-rectangles-overlap-at-any-point
    public static boolean doRectanglesOverlap(Drawable drawable, RangeSerachRectangle rangeSerachRectangle) {

        //sandt hvis de ikke overlapper
        if (drawable.getCurrentMinX() > rangeSerachRectangle.getMaxLon()){
            return false;
        }
        else if (drawable.getCurrentMaxX() < rangeSerachRectangle.getMinLon()){
            return false;
        }
        else if (drawable.getCurrentMinY() > rangeSerachRectangle.getMaxLat()){
            return false;
        }
        else if (drawable.getCurrentMaxY() < rangeSerachRectangle.getMinLat()){
            return false;
        }

        // ellers overlapper de
            return true;
    }

    /*
    public OSMNode findNearestNeighbour(float x, float y){
       return findNearestNeighbour(Model.getKDTreeNodesInGraph(), x, y, true);
    }
    // LAV EN ABSTRAKT METODE I KDNODE
    // LAV TO FORSKELLIGE METODER - EN I NODE OG EN I LEAF
    // GIV METODEN CLOSESTDIST MED SOM PARAMETER
    private OSMNode findNearestNeighbour (KdNode searchKdNode, float x, float y, boolean divX){

        //Tjekker om vores KdNode er 0
        //if (searchKdNode == null) return null;

        // hvis vi er i et leaf: Løb leaf igennem og udregn distancen. Kald rekursivt til andre leaves
        if (searchKdNode instanceof Leaf){

                for (OSMNode osmNode : ((Leaf) searchKdNode).getListOfOSMNodes()){
                    double distanceCurrent = distanceSq(osmNode.getLon(), osmNode.getLat(), x, y);
                    if (closestDist > distanceCurrent) {
                        closestDist = distanceCurrent;
                        closestNode = osmNode;
                    }}
        }

        if (searchKdNode instanceof Node){

            // hvis divX er true: Sammenlign node.getSplitX med punktets x. Kald rekursivt til den side
            // Hvis x er mindre end nodens splitX, leder vi til venstre
            //Hvad er afstanden fra punkt til linje? Nok som en metode
            // kan mit venstre barn have et punkt, der er bedre end champion?? (er afstand fra klikpunkt til splitlinjen er kortere end closestDist?)
            // hvis ja, søg videre ned i venstre barn
            // kan mit højre barn have et punkt, der er bedre end champion??
            // hvis ja, søg videre ned i højre barn
            //Kigger på x
            if(!divX){
                //Hvis distancen til mit venstre barns splitpunkt er mindre end closestDist, så led til venstre
                if (distanceSq(x, y, ((Node) ((Node) searchKdNode).getLeft()).getSplit(), ((Node) ((Node) searchKdNode).getLeft()).getSplitDenAndenKoordinat())< closestDist){
                    findNearestNeighbour(((Node) searchKdNode).getLeft(), x, y, !divX);}

                //Hvis distancen til mit højre barns splitpunkt er mindre end closestDist, så led til højre
                if (distanceSq(x, y, ((Node) ((Node) searchKdNode).getRight()).getSplit(), ((Node) ((Node) searchKdNode).getRight()).getSplitDenAndenKoordinat()) < closestDist){
                    findNearestNeighbour(((Node) searchKdNode).getRight(), x, y, !divX);}}

            //Kigger på y
            else{
                //Hvis distancen til mit venstre barns splitpunkt er mindre end closestDist, så led til venstre
                if (distanceSq(x, y, ((Node) ((Node) searchKdNode).getLeft()).getSplitDenAndenKoordinat(), ((Node) ((Node) searchKdNode).getLeft()).getSplit()) < closestDist){
                    findNearestNeighbour(((Node) searchKdNode).getLeft(), x, y, !divX);}
                //Hvis distancen til mit højre barns splitpunkt er mindre end closestDist, så led til højre
                if (distanceSq(x, y, ((Node) ((Node) searchKdNode).getRight()).getSplitDenAndenKoordinat(), ((Node) ((Node) searchKdNode).getRight()).getSplit()) < closestDist){
                    findNearestNeighbour(((Node) searchKdNode).getRight(), x, y, !divX);}
            }  }

            if(!divX){
            if(x <= ((Node) searchKdNode).getSplit()){
                findNearestNeighbour(((Node) searchKdNode).getLeft(), x, y, !divX);
            }
            //Hvis x er større end nodens splitX, går vi til højre
            if(x > ((Node) searchKdNode).getSplit()){
                findNearestNeighbour(((Node) searchKdNode).getRight(), x, y, !divX);
            }}

            // hvis divY er false: Sammenlign node.getSplitY med punktets y. Kald rekursivt til den side
            else{
            if(y <= ((Node) searchKdNode).getSplit()){
                findNearestNeighbour(((Node) searchKdNode).getLeft(), x, y, !divX);
            }
            //Hvis x er større end nodens splitY, går vi til højre
            if(y > ((Node) searchKdNode).getSplit()){
                findNearestNeighbour(((Node) searchKdNode).getRight(), x, y, !divX);


        }}

        return closestNode;}

    */




    public OSMNode findNearestNeighbour(float x, float y){
        return findNearestNeighbour(Model.getKDTreeNodesInGraph(), Double.POSITIVE_INFINITY, null, x, y, true);
    }

    public OSMNode findNearestNeighbour(KdNode searchKdNode, double closestDist, OSMNode closestNode, float x, float y, boolean divX) {
        //undersøg om den skal til venstre eller højre
        //hvis venstre: kald findNearestNeighbor for .getLeft()
        //hvis højre: kald findNearest for .getRight()

        searchKdNode.getLeft().findNearestNeighbourKDNode(searchKdNode.getLeft(), closestDist, closestNode, x, y, false);
        searchKdNode.getRight().findNearestNeighbourKDNode(searchKdNode.getRight(), closestDist, closestNode, x, y, false);

        System.out.println("Fra kdTree: ClosestNode: " + closestNode);
        System.out.println("Fra kdTree: ClosestDist: " + closestDist);
        return closestNode;

    }

    //Det der er galt: Den opdaterer ik closestDist i ovenstående metode. Den finder en for hver blad, og giver den ikke med videre

        //returns the distance between the infinite line(x1,y1)(x2,y2) and a point(x,y)
    public float pDistance(float x, float y, float x1, float y1, float x2, float y2) {

        float A = x - x1; // position of point rel one end of line
        float B = y - y1;
        float C = x2 - x1; // vector along line
        float D = y2 - y1;
        float E = -D; // orthogonal vector
        float F = C;

        float dot = A * E + B * F;
        float len_sq = E * E + F * F;

        return (float) (Math.abs(dot) / Math.sqrt(len_sq));
    }

    /*
    public OSMNode getClosestNodeField() {
        return closestNodeField;
    }

    public void setClosestNodeField(OSMNode closestNodeField) {
        this.closestNodeField = closestNodeField;
    }

    public double getClosestDistField() {
        return closestDistField;
    }

    public void setClosestDistField(double closestDistField) {
        this.closestDistField = closestDistField;
    }

     */


}