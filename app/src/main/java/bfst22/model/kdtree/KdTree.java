package bfst22.model.kdtree;

import bfst22.view.Drawable;
import java.util.*;

public class KdTree {


    // Har vi faktisk lavet et RTree??



    //Erklærer roden.
    private static KdNode root;
    static int leavesAdded = 0;


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
            kdNode = new Leaf(drawables);
            //System.out.println("Laver et blad");

            //Test - printer hvert blad
            /*for(int i = 0; i < drawables.size(); i++) {
                System.out.print("(" + drawables.get(i).getLon());
                System.out.println(" , " + drawables.get(i).getLat() + ")"); */

            //System.out.println(((Leaf) kdNode).getListOfOSMNodes());
            //}

        } else {
            //Laver en ny node
            Node node = new Node();
            //System.out.println("Laver en node");

            //sorter listen på x
            if (isXcoord) {
                //Vi sorterer listen drawables ved at bruge sort, som tager parametren Comparator.comparing.
                //Den fortæller at vi skal hive fat i OSMNode-klassen og kalde getLon-metoden. Sorter på x.
                drawables.sort(Comparator.comparing(Drawable::getMiddleX));
            } else {
                //sorter listen på y
                drawables.sort(Comparator.comparing(Drawable::getMiddleY));
            }

            //Finder midterste indeks af listen
            int mid = drawables.size() / 2;
            //System.out.println("Dette er midten: " + mid);

            //Sætter splitværdien
            if (isXcoord) {
                node.setSplit(drawables.get(mid).getMiddleX());
                //System.out.println("SplitX: " + node.getSplit());
            } else { //!isXcoord
                node.setSplit(drawables.get(mid).getMiddleY());
                //System.out.println("SplitY: " + node.getSplit());
            }

            //Laver left og right nodes
            node.setLeft(buildKDTree(drawables.subList(0, mid), !isXcoord));
            node.setRight(buildKDTree(drawables.subList(mid, drawables.size()), !isXcoord));

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
            //System.out.println("splitnode er tom!!!");
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
    public static void main(String[] args) {
        List<OSMNode> OSMNodesTEST = new ArrayList<>();

        OSMNode o1 = new OSMNode(1, 2, 3);
        OSMNode o2 = new OSMNode(2, 1, 4);
        OSMNode o3 = new OSMNode(3, 2, 1);
        OSMNode o4 = new OSMNode(4, 3, 2);
        OSMNode o5 = new OSMNode(5, 5, 4);
        OSMNode o6 = new OSMNode(6, 3, 3);
        OSMNode o7 = new OSMNode(7, 3, 1);

        OSMNodesTEST.add(o1);
        OSMNodesTEST.add(o2);
        OSMNodesTEST.add(o3);
        OSMNodesTEST.add(o4);
        OSMNodesTEST.add(o5);
        OSMNodesTEST.add(o6);
        OSMNodesTEST.add(o7);

        KDTree2 test = new KDTree2(OSMNodesTEST);
    }

     */
}