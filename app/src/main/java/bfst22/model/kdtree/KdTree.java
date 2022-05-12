package bfst22.model.kdtree;

import bfst22.model.OSMNode;
import bfst22.view.Drawable;

import java.util.*;
import java.util.List;

import static java.awt.geom.Point2D.distanceSq;

public class KdTree {

    /////////////// FELTER ///////////////////
    private KdNode root; // Træets rod
    private static int leavesAdded = 0;
    //Til nearest neighbour:
    private OSMNode closestNodeField = null;
    private double closestDistField = Double.POSITIVE_INFINITY;

    /////////////// KONSTRUKTØR ///////////////////
    public KdTree(List<Drawable> drawables) { // Til at bygge træ af Drawables
        this.root = buildKDTree(drawables, true);
    }
    public KdTree(ArrayList<OSMNode> osmNodes, int nul) { // Til at bygge træ af OSMNodes (til nearest neighbour)
        this.root = buildKDTreeOfOSMNodes(osmNodes, true);
    }


    /////////////// BYGGER KD TREE AF DRAWABLES ///////////////////
    public KdNode buildKDTree(List<Drawable> drawables, boolean isXcoord) {
        //If statement der tjekker om listen er over 0
        if (drawables == null || drawables.size() == 0) {
            //System.out.println("OSMNode list is empty!");
            return null;
        }

        // Erklærer roden som KdNode
        KdNode kdNode;

        // Laver leaves
        if (drawables.size() < 100) {
            kdNode = new Leaf(drawables);}
        else {
            // Laver en ny node
            Node node = new Node();

            // Sorter listen på x
            if (isXcoord) {
                // Sorterer på x værdien (for midten af drawable)
                drawables.sort(Comparator.comparing(Drawable::getMiddleX));}
            else { // Sorter listen på y
                // Sorterer på y værdien (for midten af drawable)
                drawables.sort(Comparator.comparing(Drawable::getMiddleY));}

            // Finder midterste indeks af listen
            int mid = drawables.size() / 2;

            //Sætter splitværdien
            if (isXcoord) {
                node.setSplit(drawables.get(mid).getMiddleX());}
            else { //!isXcoord
                node.setSplit(drawables.get(mid).getMiddleY());}

            //Laver left og right nodes
            node.setLeft(buildKDTree(drawables.subList(0, mid), !isXcoord));
            node.setRight(buildKDTree(drawables.subList(mid, drawables.size()), !isXcoord));

            kdNode = node; // Sætter vores egen node til kdNode
        }
        root = kdNode;
        return kdNode;
    }

    /////////////// BYGGER KD TREE AF OSMNODES ///////////////////
    public KdNode buildKDTreeOfOSMNodes(List<OSMNode> OSMNodes, boolean isXcoord) {
        //If statement der tjekker om listen er over 0
        if (OSMNodes == null || OSMNodes.size() == 0) {
            return null;}

        // Erklærer roden som KdNode
        KdNode kdNode;

        // Laver leaves
        if (OSMNodes.size() < 100) {
            kdNode = new Leaf(OSMNodes, 0);}

        else {
            //Laver en ny node
            Node node = new Node();

            // Sorter listen på x
            if (isXcoord) {
                // Sorterer på x værdien (for midten af drawable)
                OSMNodes.sort(Comparator.comparing(OSMNode::getLon));}
            else { // Sorter listen på y
                // Sorterer på y værdien (for midten af drawable)
                OSMNodes.sort(Comparator.comparing(OSMNode::getLat));}

            //Finder midterste indeks af listen
            int mid = OSMNodes.size() / 2;

            //Sætter splitværdien
            if (isXcoord) {
                node.setSplit(OSMNodes.get(mid).getLon());
            }
            else { //!isXcoord
                node.setSplit(OSMNodes.get(mid).getLat());
            }
            //Laver left og right nodes
            node.setLeft(buildKDTreeOfOSMNodes(OSMNodes.subList(0, mid), !isXcoord));
            node.setRight(buildKDTreeOfOSMNodes(OSMNodes.subList(mid, OSMNodes.size()), !isXcoord));

            kdNode = node; // Sætter vores egen node til kdNode
        }
        root = kdNode;
        return kdNode;
    }


    /////////////// RANGE SEARCH ///////////////////

    //Public - den der bliver kaldt i MapCanvas
    public List<Drawable> rangeSearch(RangeSearchRectangle rangeSearchRectangle, KdNode root) {
        // Kalder den priavte rangesearch, der returner en liste med inrange resultaterne
        leavesAdded = 0;
        return rangeSearch(rangeSearchRectangle, root, true, new ArrayList<>());
    }

    //Private metode
    private List<Drawable> rangeSearch(RangeSearchRectangle rangeSearchRectangle, KdNode splitNode, boolean isXcoord, List<Drawable> result) {
        if (splitNode == null) {
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
                leavesAdded++;
            }
        }

        //Ellers er vores splitnode en splitnode, og vi skal kalde rekursivt ned gennem træet
        else {
            //Gør det på X (isXcoord)
            if (isXcoord) {
                //InRange? - mellem minLon og MaxLon
                if (((Node) splitNode).getSplit() > rangeSearchRectangle.getMinLon()
                        && ((Node) splitNode).getSplit() < rangeSearchRectangle.getMaxLon()) {
                    //Gå begge veje
                    rangeSearch(rangeSearchRectangle, ((Node) splitNode).getLeft(), !isXcoord, result);
                    rangeSearch(rangeSearchRectangle, ((Node) splitNode).getRight(), !isXcoord, result);
                }
                // Over? - større end maxLon
                else if (((Node) splitNode).getSplit() > rangeSearchRectangle.getMaxLon()) {
                    //Gå til venstre
                    rangeSearch(rangeSearchRectangle, ((Node) splitNode).getLeft(), !isXcoord, result);
                }
                //Under? - mindre end minLon
                else if (((Node) splitNode).getSplit() < rangeSearchRectangle.getMinLon()) {
                    //Gå til højre
                    rangeSearch(rangeSearchRectangle, ((Node) splitNode).getRight(), !isXcoord, result);
                }
            }

            //Gør det på Y (!isXcoord)
            else {
                //InRange? - mellem minLon og MaxLon
                if (((Node) splitNode).getSplit() > rangeSearchRectangle.getMinLat()
                        && ((Node) splitNode).getSplit() < rangeSearchRectangle.getMaxLat()) {
                    //Gå begge veje
                    rangeSearch(rangeSearchRectangle, ((Node) splitNode).getLeft(), !isXcoord, result);
                    rangeSearch(rangeSearchRectangle, ((Node) splitNode).getRight(), !isXcoord, result);
                }
                // Over? - større end maxLon (((rectangle.getMaxLon())))
                else if (((Node) splitNode).getSplit() > rangeSearchRectangle.getMaxLat()) {
                    //Gå til venstre
                    rangeSearch(rangeSearchRectangle, ((Node) splitNode).getLeft(), !isXcoord, result);
                }
                //Under? - mindre end minLon
                else if (((Node) splitNode).getSplit() < rangeSearchRectangle.getMinLat()) {
                    //Gå til højre
                    rangeSearch(rangeSearchRectangle, ((Node) splitNode).getRight(), !isXcoord, result);
                }
            }
        }
        return result;
    }


    // Checker om drawable overlapper med searchRectangle
    public boolean doRectanglesOverlap(Drawable drawable, RangeSearchRectangle rangeSearchRectangle) {
        // Sandt hvis de ikke overlapper
        if (drawable.getCurrentMinX() > rangeSearchRectangle.getMaxLon()){
            return false;
        }
        else if (drawable.getCurrentMaxX() < rangeSearchRectangle.getMinLon()){
            return false;
        }
        else if (drawable.getCurrentMinY() > rangeSearchRectangle.getMaxLat()){
            return false;
        }
        else if (drawable.getCurrentMaxY() < rangeSearchRectangle.getMinLat()){
            return false;
        }
        // Ellers overlapper de
            return true;
    }


    /////////////// NEAREST NEIGHBOUR ///////////////////

    // Public metode
    public OSMNode findNearestNeighbour(KdNode searchKdNode, float x, float y){ // Starter med at tage en root og to koordinater på mappet
        closestDistField = Double.POSITIVE_INFINITY;
        closestNodeField = null; // Champion
        return findNearestNeighbour(searchKdNode, x, y, true); // kalder private metode
    }

    private OSMNode findNearestNeighbour(KdNode searchKdNode, float x, float y, boolean divX) {

        // Hvis det er den del af træet der er sorteret på y
        if (!divX) {

            //Hvis searchKdNode er et leaf
            if(searchKdNode instanceof Leaf) {
                findNearestNeighbourLeaf(searchKdNode, x, y, !divX); // Kalder metoden for leaf, der løber alle OSMNodes i bladet igennem
            }
            //Hvis searchKdNode er node
            if (searchKdNode instanceof Node) {

                Node node = ((Node) searchKdNode); // Caster til node

                // Starter med at gå hele vejen ned til det blad, som vi mistænker punktet er i
                //Hvis y er mindre end splitY - Gå til venstre
                if (y < ((Node) searchKdNode).getSplit()) {
                    findNearestNeighbour(searchKdNode.getLeft(), x, y, !divX);
                //Hvis y er større end splitY - Gå til højre
                }else {
                    findNearestNeighbour(searchKdNode.getRight(), x, y, !divX);
                }

                // Går op ad træet og tjekker om der kan være nogle tættere på
                // Hvis venstre er en node
                if (searchKdNode.getLeft() instanceof Node) {
                    Node leftNode = ((Node) node.getLeft());
                    //Tjekker distance mellem punkt og venstres splitpunkt. Hvis det er mindre end closestDistField, gå til venstre
                    if (distanceSq(x, y, leftNode.getSplit(), y) < this.closestDistField) {
                        findNearestNeighbour(searchKdNode.getLeft(), x, y, !divX);
                    }
                }
                // Hvis venstre er et leaf
                if(searchKdNode.getLeft() instanceof Leaf){
                    findNearestNeighbourLeaf(node.getLeft(), x, y, !divX); // Kalder metoden for leaf, der løber alle OSMNodes i bladet igennem
                }

                // Hvis højre er en node
                if (searchKdNode.getRight() instanceof Node) {
                    Node rightNode = ((Node) node.getRight());
                    // Tjekker distance mellem punkt og venstres splitpunkt. hvis det er mindre end closestDistField, gå til venstre
                    if (distanceSq(x, y, rightNode.getSplit(), y) < this.closestDistField) {
                        findNearestNeighbour(searchKdNode.getRight(), x, y, !divX);
                    }
                }
                // Hvis højre er et leaf
                if(searchKdNode.getRight() instanceof Leaf) {
                    //kalder metoden der løber leaf igennem
                    findNearestNeighbourLeaf(node.getRight(), x, y, !divX);
                }
            }
        }
        // Hvis det er den del af træet der er sorteret på x
        else{
            // Hvis root er et leaf
            if(searchKdNode instanceof Leaf) {
                findNearestNeighbourLeaf(searchKdNode, x, y, !divX);
            }

            // Hvis searchKdNode er node
            if (searchKdNode instanceof Node) {

                Node node = ((Node) searchKdNode); // Caster til node

                // Starter med at gå hele vejen ned til det blad, som vi mistænker punktet er i
                // Hvis x er mindre end splitX - Gå til venstre
                if(x < ((Node) searchKdNode).getSplit()) {
                    findNearestNeighbour(searchKdNode.getLeft(), x, y, !divX);
                //Hvis x er større end splitX - Gå til højre
                }else {
                    findNearestNeighbour(searchKdNode.getRight(), x, y, !divX);
                }

                // Hvis venstre er en node
                if (searchKdNode.getLeft() instanceof Node) {
                    Node leftNode = ((Node) node.getLeft());
                    // Tjekker distance mellem punkt og venstres splitpunkt. Hvis det er mindre end closestDistField, gå til venstre
                    if (distanceSq(x, y, x, leftNode.getSplit()) < this.closestDistField) {
                        findNearestNeighbour(searchKdNode.getLeft(), x, y, !divX);
                    }
                }
                // Hvis venstre node er et leaf
                if (searchKdNode.getLeft() instanceof Leaf) {
                    findNearestNeighbourLeaf(node.getLeft(), x, y, !divX);
                }

                // Hvis højre er en node
                if (searchKdNode.getRight() instanceof Node) {
                    Node rightNode = ((Node) node.getRight());
                    // Tjekker distance mellem punkt og venstres splitpunkt. hvis det er mindre end closestDistField, gå til venstre
                    if (distanceSq(x, y, x, rightNode.getSplit()) < this.closestDistField) {
                        findNearestNeighbour(searchKdNode.getRight(), x, y, !divX);
                    }
                }
                // Hvis højre er et leaf
                if (searchKdNode.getRight() instanceof Leaf){
                    // Kalder metoden der løber leaf igennem
                    findNearestNeighbourLeaf(node.getRight(), x, y, !divX);
                }
            }
        }
        // TIL TEST:
        //System.out.println("Fra kdTree: ClosestNode: " + closestNodeField);
        //System.out.println("Fra kdTree: ClosestDist: " + closestDistField);
        //System.out.println("Final node med lat og lon: " + closestNodeField.getLat() + " " + closestNodeField.getLon());
        return closestNodeField;
    }

    // Metode der bruges i findNearestNeighbor(), når man er nede i et leaf
    public void findNearestNeighbourLeaf(KdNode searchKdNode, float x, float y, boolean divX) {
        Leaf leaf = ((Leaf) searchKdNode); // Caster til leaf

        // Løber alle OSMNodes i bladet igennem og tjekker, om der er en kortere distance end den, vi allerede har
        for (OSMNode osmNode : leaf.getListOfOSMNodes()){
            double distanceCurrent = distanceSq(x, y, osmNode.getLon(), osmNode.getLat());
            if (this.closestDistField > distanceCurrent) {
                this.closestDistField = distanceCurrent;
                this.closestNodeField = osmNode;
            }
        }
        KdNode node = ((KdNode) leaf);
    }


    /////////////// GETTERS ///////////////////
    public KdNode getRoot() {return root;}



}