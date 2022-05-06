package bfst22.model.kdtree;

import bfst22.model.Model;
import bfst22.model.OSMNode;
import bfst22.view.Drawable;
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
        this.root = buildKDTree(drawables, true);
    }

    public KdTree(List<OSMNode> osmNodes, int nul) {
        this.root = buildKDTreeOfOSMNodes(osmNodes, true);
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
    public KdNode buildKDTreeOfOSMNodes(List<OSMNode> OSMNodes, boolean isXcoord) {
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
    public List<Drawable> rangeSearch(RangeSerachRectangle rangeSerachRectangle, KdNode root) {
        // kald den anden rangesearch, der returner en liste med inrange resultaterne
        leavesAdded = 0;
        return rangeSearch(rangeSerachRectangle, root, true, new ArrayList<>());
    }

    // RangeSearch der kun tager rectangle
    public List<Drawable> rangeSearch(RangeSerachRectangle rangeSerachRectangle) {
        // kald den anden rangesearch, der returner en liste med inrange resultaterne
        leavesAdded = 0;
        return rangeSearch(rangeSerachRectangle, root, true, new ArrayList<>());
    }

    //Privat RangeSearch metode
    private List<Drawable> rangeSearch(RangeSerachRectangle rangeSearchRectangle, KdNode splitNode, boolean isXcoord, List<Drawable> result){
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
    public boolean doRectanglesOverlap(Drawable drawable, RangeSerachRectangle rangeSerachRectangle) {

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


    public OSMNode findNearestNeighbour(float x, float y){
        closestDistField = Double.POSITIVE_INFINITY;
        closestNodeField = null;
        return findNearestNeighbour(Model.getKDTreeNodesInGraph(), x, y, true);
    }

    private OSMNode findNearestNeighbour(KdNode searchKdNode, float x, float y, boolean divX) {
        //undersøg om den skal til venstre eller højre
        //hvis venstre: kald findNearestNeighbor for .getLeft()
        //hvis højre: kald findNearest for .getRight()

        if (!divX) {

            //Hvis searchKdNode er et leaf
            if(searchKdNode instanceof Leaf) {
                findNearestNeighbourLeaf(searchKdNode, x, y, !divX);
            }
            //Hvis searchKdNode er node
            if (searchKdNode instanceof Node) {

                Node node = ((Node) searchKdNode);

                //her hvis y er mindre end splitY
                //nearest neighbour i venstre side og ellers så højre side
                if(y < ((Node) searchKdNode).getSplit()) {
                    findNearestNeighbour(searchKdNode.getLeft(), x, y, !divX);

                }else {
                    findNearestNeighbour(searchKdNode.getRight(), x, y, !divX);
                }

                //if x < searchnode.getSplit

                // hvis venstre er en node
                if (searchKdNode.getLeft() instanceof Node) {
                    Node leftNode = ((Node) node.getLeft());

                    //tjekker distance mellem punkt og venstres splitpunkt. hvis det er mindre end closestDistField, gå til venstre
                    if (distanceSq(x, y, leftNode.getSplit(), y) < this.closestDistField) {
                        findNearestNeighbour(searchKdNode.getLeft(), x, y, !divX);

                    }
                }
                //hvis venstre er et leaf
                if(searchKdNode.getLeft() instanceof Leaf){
                    //kalder metoden der løber leaf igennem
                    findNearestNeighbourLeaf(node.getLeft(), x, y, !divX);
                }

                // hvis højre er en node
                if (searchKdNode.getRight() instanceof Node) {

                    Node rightNode = ((Node) node.getRight());

                    //tjekker distance mellem punkt og venstres splitpunkt. hvis det er mindre end closestDistField, gå til venstre
                    if (distanceSq(x, y, rightNode.getSplit(), y) < this.closestDistField) {
                        findNearestNeighbour(searchKdNode.getRight(), x, y, !divX);

                    }
                }
                //hvis højre er et leaf
                if(searchKdNode.getRight() instanceof Leaf) {
                    //kalder metoden der løber leaf igennem
                    findNearestNeighbourLeaf(node.getRight(), x, y, !divX);
                }
            }
        }

        else{ //hvis divX
            //Hvis root er et leaf
            if(searchKdNode instanceof Leaf) {
                findNearestNeighbourLeaf(searchKdNode, x, y, !divX);
            }

            //Hvis searchKdNode er node
            if (searchKdNode instanceof Node) {

                Node node = ((Node) searchKdNode);

                //////her hvis x er mindre end splitX
                //////nearest neighbour i venstre side og ellers så højre side
                if(x < ((Node) searchKdNode).getSplit()) {
                    findNearestNeighbour(searchKdNode.getLeft(), x, y, !divX);
                }else {
                    findNearestNeighbour(searchKdNode.getRight(), x, y, !divX);
                }

                // hvis venstre er en node
                if (searchKdNode.getLeft() instanceof Node) {

                    Node leftNode = ((Node) node.getLeft());

                    //tjekker distance mellem punkt og venstres splitpunkt. hvis det er mindre end closestDistField, gå til venstre
                    if (distanceSq(x, y, x, leftNode.getSplit()) < this.closestDistField) {
                        findNearestNeighbour(searchKdNode.getLeft(), x, y, !divX);

                    }
                }
                //hvis venstre node er et leaf
                if (searchKdNode.getLeft() instanceof Leaf) {
                    findNearestNeighbourLeaf(node.getLeft(), x, y, !divX);
                }

                //hvis højre er en node
                if (searchKdNode.getRight() instanceof Node) {

                    Node rightNode = ((Node) node.getRight());

                    //tjekker distance mellem punkt og venstres splitpunkt. hvis det er mindre end closestDistField, gå til venstre
                    if (distanceSq(x, y, x, rightNode.getSplit()) < this.closestDistField) {
                        findNearestNeighbour(searchKdNode.getRight(), x, y, !divX);

                    }
                }
                //hvis højre er et leaf
                if (searchKdNode.getRight() instanceof Leaf){
                    //kalder metoden der løber leaf igennem
                    findNearestNeighbourLeaf(node.getRight(), x, y, !divX);
                }
            }
        }

        //System.out.println("Fra kdTree: ClosestNode: " + closestNodeField);
        //System.out.println("Fra kdTree: ClosestDist: " + closestDistField);
        //System.out.println("Final node med lat og lon: " + closestNodeField.getLat() + " " + closestNodeField.getLon());
        return closestNodeField;
    }


    public void findNearestNeighbourLeaf(KdNode searchKdNode, float x, float y, boolean divX) {
        System.out.println("Jeg er et leaf");

        Leaf leaf = ((Leaf) searchKdNode);


        for (OSMNode osmNode : leaf.getListOfOSMNodes()){
            double distanceCurrent = distanceSq(x, y, osmNode.getLon(), osmNode.getLat());

            if (this.closestDistField > distanceCurrent) {
                this.closestDistField = distanceCurrent;
                this.closestNodeField = osmNode;
            }
        }

        KdNode node = ((KdNode) leaf);
        //System.out.println("Fra leaf Champnode: " + this.closestNodeField);
        //System.out.println("Fra leaf ChampDist: " + this.closestDistField);
    }


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
}