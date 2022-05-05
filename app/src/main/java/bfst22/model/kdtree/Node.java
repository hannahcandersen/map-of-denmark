package bfst22.model.kdtree;

import bfst22.model.OSMNode;

import static java.awt.geom.Point2D.distanceSq;

public class Node extends KdNode {
    private KdNode left, right;

    //Enten lat/y-værdien eller lon/x-værdien
    private float split;
    private float splitDenAndenKoordinat;

    public Node() {
        super();
    }

    public KdNode getLeft() {return left;}
    public KdNode getRight() {return right;}
    public float  getSplit(){return split;}
    public float  getSplitDenAndenKoordinat(){return splitDenAndenKoordinat;}

    public void setLeft(KdNode left) {
        this.left = left;
    }
    public void setRight(KdNode right) {
        this.right = right;
    }
    public void setSplit(float split) {
        this.split = split;
    }

    public void setSplitDenAndenKoordinat(float split) {
        this.splitDenAndenKoordinat = split;
    }


    @Override
    public void findNearestNeighbourKDNode(KdNode searchKdNode, double closestDist, OSMNode closestNode, float x, float y, boolean divX) {
            //Hvis distancen til mit venstre barns splitpunkt er mindre end closestDist, så led til venstre
        //System.out.println("Jeg er en node");
            Node searchNodeNode = ((Node) searchKdNode);
            if(!divX) {

                if (distanceSq(x, y, searchNodeNode.getSplit(), searchNodeNode.getSplitDenAndenKoordinat()) < closestDist) {
                    searchKdNode.getLeft().findNearestNeighbourKDNode(searchKdNode.getLeft(), closestDist, closestNode, x, y, !divX); //Måske kommer vi til at gå 2 gange til venstre
                }

                //Hvis distancen til mit højre barns splitpunkt er mindre end closestDist, så led til højre
                if (distanceSq(x, y, searchNodeNode.getSplit(), searchNodeNode.getSplitDenAndenKoordinat()) < closestDist) {
                    searchKdNode.getRight().findNearestNeighbourKDNode(searchKdNode.getRight(), closestDist, closestNode, x, y, !divX); //Måske kommer vi til at gå 2 gange til venstre
                }}

        //Kigger på y
        else {
            if (distanceSq(x, y, searchNodeNode.getSplitDenAndenKoordinat(), searchNodeNode.getSplit()) < closestDist) {
                searchKdNode.getLeft().findNearestNeighbourKDNode(searchKdNode.getLeft(), closestDist, closestNode, x, y, !divX); //Måske kommer vi til at gå 2 gange til venstre
            }

            //Hvis distancen til mit venstre barns splitpunkt er mindre end closestDist, så led til venstre
            if (distanceSq(x, y, searchNodeNode.getSplitDenAndenKoordinat(), searchNodeNode.getSplit()) < closestDist) {
                searchKdNode.getRight().findNearestNeighbourKDNode(searchKdNode.getRight(), closestDist, closestNode, x, y, !divX); //Måske kommer vi til at gå 2 gange til venstre
            }}
    }
}

