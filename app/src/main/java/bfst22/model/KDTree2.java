package bfst22.model;

import java.util.Comparator;
import java.util.List;

public class KDTree2 {

    //Erklærer roden.
    private Node root;

    public KDTree2(List<OSMNode> OSMNodes) {
        buildKDTree(OSMNodes, false);
    }

            // noget if der tjekker om der skal oprettes KDNote eller Leaf (tjekker size)

            // sorter på den rigtige akse (x/y)

            // split listen i 2 halvdele

            // skift boolean

            // left = new KDNode(den ene halvdel af listen)
            // right = new KDNode(den anden halvdel af listen)

            //root = buildKDTree(OSMNodes, true)


/*
    //Sammenligner X-koordinater
    private final Comparator<OSMNode> compareByXCoord = new Comparator<OSMNode>() {
        @Override
        public int compare(OSMNode n1, OSMNode n2) {
            return Double.compare(n1.getLon(), n2.getLon());
        }
    };
    //Sammenligner Y-koordinater
    private final Comparator<OSMNode> compareByYCoord = new Comparator<OSMNode>() {
        @Override
        public int compare(OSMNode n1, OSMNode n2) {
            return Double.compare(n1.getLat(), n2.getLat());
        }
    };
 */


    //Metode til at bygge kdTree
    public KDNode buildKDTree(List<OSMNode> OSMNodes, boolean isXcoord) {
        //If statement der tjekker om der findes et træ
        if(OSMNodes == null || OSMNodes.size() == 0) {
            System.out.println("OSMNode list is empty!");
            return null;
        }

        // Laver leaves
        KDNode kdNode;
        if(OSMNodes.size() < 100){
            // find ud af hvordan vi laver leaves
            kdNode = new Leaf(OSMNodes);
        }
        else{
            //sorter listen
            if(isXcoord){
                //Vi sorterer listen OSMNodes ved at bruge sort, som tager parametren Comparator.comparing.
                //Den fortæller at vi skal hive fat i OSMNode-klassen og kalde getLon-metoden. Sorter på x.
                OSMNodes.sort(Comparator.comparing(OSMNode::getLon));
            }
            else{
                //sorter på y
                OSMNodes.sort(Comparator.comparing(OSMNode::getLat));
            }

            //finder midterste element i den sorterede liste og laver det til en root
            Node node = new Node();
            int mid = OSMNodes.size()/2;

            // laver left og right root for root
            node.setLeft(buildKDTree(OSMNodes.subList(0, mid), !isXcoord));
            node.setRight(buildKDTree(OSMNodes.subList(mid, OSMNodes.size()), !isXcoord));

            kdNode = node;

        }
        return kdNode;
    }

}
