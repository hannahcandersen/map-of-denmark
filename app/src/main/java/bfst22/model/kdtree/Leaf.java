package bfst22.model.kdtree;

import bfst22.model.OSMNode;
import bfst22.view.Drawable;

import java.util.ArrayList;
import java.util.List;

import static java.awt.geom.Point2D.distanceSq;

/*
    Hvis vi til sidst har 4-10 noder, som vi ikke synes er nødvendigt at dele ud på træet,
    så kan vi til sidst tilføje en liste med de sidste noder
*/

public class Leaf extends KdNode {
        protected List<Drawable> listOfDrawables;
        protected List<OSMNode> listOfOSMNodes;
        private KdTree kdTree = new KdTree();

        public Leaf(List<Drawable> leaves){
                super();
                this.listOfDrawables = leaves;
        }

        public Leaf(List<OSMNode> osmNodes, int nul){
                super();
                this.listOfOSMNodes = osmNodes;
        }

        public List<Drawable> getListOfDrawables() {
                return listOfDrawables;
        }

        public List<OSMNode> getListOfOSMNodes(){
                return listOfOSMNodes;
        }
        public KdNode getLeft(){return null;}
        public KdNode getRight(){return null;}


}
