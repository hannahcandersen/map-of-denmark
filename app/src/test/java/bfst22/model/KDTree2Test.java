package bfst22.model;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;


class KDTree2Test {

    List<OSMNode> OSMNodes = new ArrayList<>();
    OSMNode o1, o2, o3, o4, o5, o6, o7;


    public void makeList(List<OSMNode> OSMNodes) {
        o1 = new OSMNode(1, 2, 3);
        o2 = new OSMNode(2, 1, 4);
        o3 = new OSMNode(3, 2, 1);
        o4 = new OSMNode(4, 3, 2);
        o5 = new OSMNode(5, 5, 4);
        o6 = new OSMNode(6, 3, 3);
        o7 = new OSMNode(7, 3, 1);

        OSMNodes.add(o1);
        OSMNodes.add(o2);
        OSMNodes.add(o3);
        OSMNodes.add(o4);
        OSMNodes.add(o5);
        OSMNodes.add(o6);
        OSMNodes.add(o7);

        //return OSMNodes;
    }



    @Test
    void getRoot() {

    }

    @Test
    void buildKDTree() {
        makeList(OSMNodes);

        //assertEquals(o7,o1.left);

        //AE(b,a.left)
        //(e,a.left.right)




        //KDTree2 kdtest = new KDTree2();
        //KDNode test = root;

        //assertEquals(test, root);

    }


}