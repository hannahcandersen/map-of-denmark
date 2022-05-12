package bfst22.model.kdtree;

import bfst22.model.OSMNode;
import bfst22.view.Drawable;

import java.util.List;

// Subklasse til KdNode
// Holder hver på op til 100 elementer

public class Leaf extends KdNode {
        protected List<Drawable> listOfDrawables;
        protected List<OSMNode> listOfOSMNodes;

        public Leaf(List<Drawable> leaves){
                super();
                this.listOfDrawables = leaves;
        }
        public Leaf(List<OSMNode> osmNodes, int nul){ //nul tilføjes, da den ellers ikke kan kende forskel på konstruktørerne
                super();
                this.listOfOSMNodes = osmNodes;
        }

        public List<Drawable> getListOfDrawables() {
                return listOfDrawables;
        }
        public List<OSMNode> getListOfOSMNodes(){
                return listOfOSMNodes;
        }

        // Blade vil ikke have nogen left og right børn, da de er sidst i træet
        public KdNode getLeft(){return null;}
        public KdNode getRight(){return null;}


}
