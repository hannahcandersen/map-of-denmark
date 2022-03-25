package bfst22.model;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import static org.junit.jupiter.api.Assertions.*;


class KDTree2Test {

    List<OSMNode> OSMNodes = new ArrayList<>();

    public List<OSMNode> makeList(List<OSMNode> OSMNodes) {
        OSMNode o1 = new OSMNode(1, 2, 3);
        OSMNode o2 = new OSMNode(2, 1, 4);
        OSMNode o3 = new OSMNode(3, 2, 1);
        OSMNode o4 = new OSMNode(4, 3, 2);
        OSMNode o5 = new OSMNode(5, 5, 4);
        OSMNode o6 = new OSMNode(6, 3, 3);
        OSMNode o7 = new OSMNode(7, 3, 1);

        OSMNodes.add(o1);
        OSMNodes.add(o2);
        OSMNodes.add(o3);
        OSMNodes.add(o4);
        OSMNodes.add(o5);
        OSMNodes.add(o6);
        OSMNodes.add(o7);

        return OSMNodes;
    }



    @Test
    void getRoot() {

    }

    @Test
    void buildKDTree() {
        makeList(OSMNodes);
        KDTree2 kd = new KDTree2(OSMNodes);
        KDNode leaf = new Leaf(OSMNodes);



        //KDTree2 kdtest = new KDTree2();
        //KDNode test = root;

        //assertEquals(test, root);

    }


}