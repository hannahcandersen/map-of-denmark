package bfst22.model;

import bfst22.view.Drawable;
import java.util.List;

public class KDTree2 {
    private KDNode root;
    private Drawable item; //OSMWay

    public KDTree2(List<OSMNode> list, boolean x) {
        root = new KDNode(list, x);
    }

    private class KDNode {
        KDNode right, left;

        KDNode(List<OSMNode> list, boolean x) {
            // noget if der tjekker om der skal oprettes KDNote eller Leaf (tjekker size)

            // sorter på den rigtige akse (x/y)

            // split listen i 2 halvdele

            // skift boolean

            // left = new KDNode(den ene halvdel af listen)
            // right = new KDNode(den anden halvdel af listen)

        }
    }

    public KDNode createKDTree(List<OSMNode> drawableList, boolean x) {
        // if ....
        int lo = 0;
        int hi = drawableList.size();


        if (x){
            //sorter på x
            // drawableList.sort()
        }
        if(!x){
            //sorter på y
        }

        //finder midterste element i den sorterede liste
        //root = drawableList.get(drawableList.size()/2);
        root.left = createKDTree(drawableList, !x);
        root.right = createKDTree(drawableList, !x);


        return root;
    }//

    /*
    Private class Leaf:
        Hvis vi til sidst har 4-10 noder, som vi ikke synes er nødvendigt at dele ud på træet,
        så kan vi til sidst tilføje en liste med de sidste noder
     */
    private class Leaf extends KDNode {
        List<OSMNode> leaves;
        public Leaf(List<OSMNode> drawableList, boolean x){
            super(drawableList, x);
        }

    }


}
