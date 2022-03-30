package bfst22.model;

import java.util.*;

public class KDTree2 {

    //Erklærer roden.
    private KDNode root;

    public KDTree2() {}

    public KDTree2(List<OSMNode> OSMNodes) {
        // buildKDTree(OSMNodes, false);
        this.root = buildKDTree(OSMNodes, true);
    }

    public KDNode getRoot() {
        return root;
    }

    //////////////////// BULDING KDTREE ///////////////////////
    public static KDNode buildKDTree(List<OSMNode> OSMNodes, boolean isXcoord) {
        //If statement der tjekker om der findes et træ
        if(OSMNodes == null || OSMNodes.size() == 0) {
            System.out.println("OSMNode list is empty!");
            return null;
        }

        //Erklærer
        KDNode kdNode;

        // Laver leaves
        if(OSMNodes.size() < 1000){
            kdNode = new Leaf(OSMNodes);
            System.out.println("Laver et blad");
            for(int i = 0; i < OSMNodes.size(); i++) {
                System.out.print("(" + OSMNodes.get(i).getLon());
                System.out.println(" , " + OSMNodes.get(i).getLat() + ")");

                //System.out.println(((Leaf) kdNode).getListOfOSMNodes());
            }
        }
        else{
            //Laver en ny node
            Node node = new Node();
            System.out.println("Laver en node");

            //sorter listen på x
            if(isXcoord){
                //Vi sorterer listen OSMNodes ved at bruge sort, som tager parametren Comparator.comparing.
                //Den fortæller at vi skal hive fat i OSMNode-klassen og kalde getLon-metoden. Sorter på x.
                OSMNodes.sort(Comparator.comparing(OSMNode::getLon));
            }
            else{
                //sorter listen på y
                OSMNodes.sort(Comparator.comparing(OSMNode::getLat));
            }


            //Finder midterste indeks af listen
            int mid = OSMNodes.size()/2;
            System.out.println("Dette er midten: " + mid);

            //Sætter splitværdien
            if(isXcoord){
                node.setSplit(OSMNodes.get(mid).getLon());
                System.out.println("SplitX: " + node.getSplit());
            }
            else{ //!isXcoord
                node.setSplit(OSMNodes.get(mid).getLat());
                System.out.println("SplitY: " + node.getSplit());
            }

            //Laver left og right nodes
            node.setLeft(buildKDTree(OSMNodes.subList(0, mid), !isXcoord));
            node.setRight(buildKDTree(OSMNodes.subList(mid, OSMNodes.size()), !isXcoord));

            kdNode = node;

        }

        return kdNode;
    }

    //////////////////// RANGE SEARCH ///////////////////////

    //Public den der kaldt i MapCanvas
    public List<OSMNode> rangeSearch(Rectangle rectangle) {
        // kald den anden rangesearch, der returner en lsite med inrange resultaterne
        return rangeSearch(rectangle, root, true, new ArrayList<>());
    }

    //Metode der returner en liste
    private List<OSMNode> rangeSearch(Rectangle rectangle, KDNode splitNode, boolean isXcoord, List<OSMNode> result){
       if (splitNode == null){
           System.out.println("splitnode er tom!!!");
           return null;
       }

        //Hvis vores splitnode er en leaf, tilføj leaf(s) til result listen
        if (splitNode instanceof Leaf){
            //Caster KDnode splitNode om til et leaf.
            result.add((OSMNode) ((Leaf) splitNode).getListOfOSMNodes());
        }

        //Ellers er vores splitnode en splitnode, og vi skal kalde rekursivt ned gennem træet
        else{
            //Gør det på X (isXcoord)
            if(isXcoord){

                //InRange? - mellem minLon og MaxLon
                if(((Node) splitNode).getSplit() > rectangle.getMinLon()
                        && ((Node) splitNode).getSplit() < rectangle.getMaxLon()) {

                    //Gå begge veje
                    rangeSearch(rectangle, ((Node) splitNode).getLeft(), !isXcoord, result);
                    rangeSearch(rectangle, ((Node) splitNode).getRight(), !isXcoord, result);

                }
                // Over? - større end maxLon (((rectangle.getMaxLon())))
                if(((Node) splitNode).getSplit() > rectangle.getMaxLon()) {
                    //Gå til venstre
                    rangeSearch(rectangle, ((Node) splitNode).getLeft(), !isXcoord, result);
                }
                //Under? - mindre end minLon
                if(((Node) splitNode).getSplit() < rectangle.getMinLon()) {
                    //Gå til højre
                    rangeSearch(rectangle, ((Node) splitNode).getRight(), !isXcoord, result);
                }
            }

            //Gør det på Y (!isXcoord) Måske skal lat ændres til minus
            else {
                //InRange? - mellem minLon og MaxLon
                if(((Node) splitNode).getSplit() > rectangle.getMinLat()
                        && ((Node) splitNode).getSplit() < rectangle.getMaxLat()) {

                    //Gå begge veje
                    rangeSearch(rectangle, ((Node) splitNode).getLeft(), !isXcoord, result);
                    rangeSearch(rectangle, ((Node) splitNode).getRight(), !isXcoord, result);

                }
                // Over? - større end maxLon (((rectangle.getMaxLon())))
                if(((Node) splitNode).getSplit() > rectangle.getMaxLat()) {
                    //Gå til venstre
                    rangeSearch(rectangle, ((Node) splitNode).getLeft(), !isXcoord, result);
                }
                //Under? - mindre end minLon
                if(((Node) splitNode).getSplit() < rectangle.getMinLat()) {
                    //Gå til højre
                    rangeSearch(rectangle, ((Node) splitNode).getRight(), !isXcoord, result);
                }
            }
        }
      return result;
    }


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

}