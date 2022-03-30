package bfst22.model;

import java.util.List;

/*
    Hvis vi til sidst har 4-10 noder, som vi ikke synes er nødvendigt at dele ud på træet,
    så kan vi til sidst tilføje en liste med de sidste noder
*/

public class Leaf extends KDNode {
        protected List<OSMNode> listOfOSMNodes;

        public Leaf(List<OSMNode> leaves){
                super();
                this.listOfOSMNodes = leaves;
        }

        public List<OSMNode> getListOfOSMNodes(){
                return listOfOSMNodes;
        }

}
