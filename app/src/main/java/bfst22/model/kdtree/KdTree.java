package bfst22.model.kdtree;

import bfst22.view.Drawable;
import java.util.*;

public class KdTree {

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
            System.out.println("OSMNode list is empty!");
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
    private static List<Drawable> rangeSearch(RangeSerachRectangle rangeSerachRectangle, KdNode splitNode, boolean isXcoord, List<Drawable> result){
        if (splitNode == null){
            //System.out.println("splitnode er tom!!!");
            return null;
        }

        //Hvis vores splitnode er en leaf, tilføj leaf(s) til result listen
        if (splitNode instanceof Leaf) {


            // VERSION 1: Tilføjer drawable til result hvis der er bare én enkelt node i drawable, der er indenfor firkanten. Ved ikke om den virker??
            // For loop der caster splitnode til Leaf of løber alle drawables i leaf igennem
            /*for (Drawable d : ((Leaf) splitNode).getListOfDrawables()) {
                if (d.getCurrentMinX() >= rangeSerachRectangle.getMinLon() || d.getCurrentMaxX() <= rangeSerachRectangle.getMaxLon() || d.getCurrentMinY() >= rangeSerachRectangle.getMinLat() || d.getCurrentMaxY() <= rangeSerachRectangle.getMaxLat()) {
                    result.add(d);
                }
                //TIL TEST. Printer min og max koordinater. minX er Lon (minus) og minY er Lat (ikke minus).
                //System.out.println("MinX : " + d.getCurrentMinX() + " MaxX : " + d.getCurrentMaxX() + " MinY: " + d.getCurrentMinY() + " MaxY :" + d.getCurrentMaxY());
            }*/


            // VERSION 2: Tilføjer drawable til result hvis middleX eller middleY er indenfor firkanten
            // For loop der caster splitnode til Leaf of løber alle drawables i leaf igennem
            for(Drawable d : ((Leaf) splitNode).getListOfDrawables()) {
                // Langt if-statement der tjekker at midX for hver drawable er mellem firkantens min og maxLon. OG at midY samtidigt er mellem firkantens min og maxLat
                if ((d.getMiddleX() >= rangeSerachRectangle.getMinLon() && d.getMiddleX() <= rangeSerachRectangle.getMaxLon()) && (d.getMiddleY() >= rangeSerachRectangle.getMinLat() && d.getMiddleY() <= rangeSerachRectangle.getMaxLat())) {
                    // Tilføjer kun de Drawables, som er inde i firkanten til result
                    result.add(d);
                }

            }



            leavesAdded++;
        }



        //Ellers er vores splitnode en splitnode, og vi skal kalde rekursivt ned gennem træet
        else{
            //Gør det på X (isXcoord)
            if(isXcoord){

                //InRange? - mellem minLon og MaxLon
                if(((Node) splitNode).getSplit() > rangeSerachRectangle.getMinLon()
                        && ((Node) splitNode).getSplit() < rangeSerachRectangle.getMaxLon()) {

                    //Gå begge veje
                    rangeSearch(rangeSerachRectangle, ((Node) splitNode).getLeft(), !isXcoord, result);
                    rangeSearch(rangeSerachRectangle, ((Node) splitNode).getRight(), !isXcoord, result);

                }
                // Over? - større end maxLon (((rectangle.getMaxLon())))
                else if (((Node) splitNode).getSplit() > rangeSerachRectangle.getMaxLon()) {
                    //Gå til venstre
                    rangeSearch(rangeSerachRectangle, ((Node) splitNode).getLeft(), !isXcoord, result);
                }
                //Under? - mindre end minLon
                else if(((Node) splitNode).getSplit() < rangeSerachRectangle.getMinLon()) {
                    //Gå til højre
                    rangeSearch(rangeSerachRectangle, ((Node) splitNode).getRight(), !isXcoord, result);
                }
            }

            //Gør det på Y (!isXcoord) Måske skal lat ændres til minus
            else {
                //InRange? - mellem minLon og MaxLon
                if(((Node) splitNode).getSplit() > rangeSerachRectangle.getMinLat()
                        && ((Node) splitNode).getSplit() < rangeSerachRectangle.getMaxLat()) {

                    //Gå begge veje
                    rangeSearch(rangeSerachRectangle, ((Node) splitNode).getLeft(), !isXcoord, result);
                    rangeSearch(rangeSerachRectangle, ((Node) splitNode).getRight(), !isXcoord, result);

                }
                // Over? - større end maxLon (((rectangle.getMaxLon())))
                else if(((Node) splitNode).getSplit() > rangeSerachRectangle.getMaxLat()) {
                    //Gå til venstre
                    rangeSearch(rangeSerachRectangle, ((Node) splitNode).getLeft(), !isXcoord, result);
                }
                //Under? - mindre end minLon
                else if(((Node) splitNode).getSplit() < rangeSerachRectangle.getMinLat()) {
                    //Gå til højre
                    rangeSearch(rangeSerachRectangle, ((Node) splitNode).getRight(), !isXcoord, result);
                }
            }
        }
        return result;
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