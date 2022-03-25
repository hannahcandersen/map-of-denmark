package bfst22.model;

import java.util.Comparator;
import java.util.List;

public class KDTree2 {

    //Erklærer roden.
    private Node root;

    public KDTree2(List<OSMNode> OSMNodes) {
        buildKDTree(OSMNodes, false);
    }

    public KDNode getRoot() {
        return root;
    }

            // noget if der tjekker om der skal oprettes KDNote eller Leaf (tjekker size) - tjek

            // sorter på den rigtige akse (x/y) - tjek

            // split listen i 2 halvdele - tjek

            // skift boolean - tjek

            // left = new KDNode(den ene halvdel af listen) - tjek
            // right = new KDNode(den anden halvdel af listen) - tjek


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

   /*
    public String toString(){
        StringBuilder sb = new StringBuilder();
        Queue<KDNode> q = new LinkedList<>();
        q.add(this.root);

        while (!q.isEmpty()) {
            int size = q.size();
            for (int i = 0; i < size; i++) {
                KDNode n = q.poll();
                if (n != null) {
                    sb.append(n.point).append(", ");
                    q.add(n.left);
                    q.add(n.right);
                } else {
                    sb.append("null, ");
                }
            }
            sb.append('\n');
        }
        return sb.toString();
    }
    */

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
