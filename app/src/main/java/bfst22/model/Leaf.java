package bfst22.model;

import bfst22.view.Drawable;

import java.util.List;

/*
    Hvis vi til sidst har 4-10 noder, som vi ikke synes er nødvendigt at dele ud på træet,
    så kan vi til sidst tilføje en liste med de sidste noder
*/

public class Leaf extends KDNode {
        protected List<Drawable> listOfDrawables;

        public Leaf(List<Drawable> leaves){
                super();
                this.listOfDrawables = leaves;
        }

        public List<Drawable> getListOfDrawables() {
                return listOfDrawables;

        }
}
