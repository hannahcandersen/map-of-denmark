package bfst22.model.kdtree;

import bfst22.model.OSMNode;

import static java.awt.geom.Point2D.distanceSq;

public class Node extends KdNode {
    private KdNode left, right;

    //Enten lat/y-værdien eller lon/x-værdien
    private float split;
    private float splitDenAndenKoordinat;
    private KdTree kdTree = Model.getKDTreeNodesInGraphTEST();

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
        System.out.println("Jeg er en node");
            Node searchNodeNode = ((Node) searchKdNode);

            if(!divX) {

                if (distanceSq(x, y, searchNodeNode.getSplit(), searchNodeNode.getSplitDenAndenKoordinat()) < kdTree.getClosestDistField()) {
                    searchKdNode.getLeft().findNearestNeighbourKDNode(searchKdNode.getLeft(), closestDist, closestNode, x, y, !divX);
                }

                // !!!! HER STÅR DET SAMME SOM OVENOVER. Den skal tjekke til højre og til venstre, men nu tjekker den på den, som den står på
                // Men hvis vi siger getLeft() kan man risikere det er et leaf

                if (distanceSq(x, y, searchNodeNode.getSplit(), searchNodeNode.getSplitDenAndenKoordinat()) < kdTree.getClosestDistField()) {
                    searchKdNode.getRight().findNearestNeighbourKDNode(searchKdNode.getRight(), closestDist, closestNode, x, y, !divX);
                }}

        //Kigger på y
        else {
            if (distanceSq(x, y, searchNodeNode.getSplitDenAndenKoordinat(), searchNodeNode.getSplit()) < kdTree.getClosestDistField()) {
                searchKdNode.getLeft().findNearestNeighbourKDNode(searchKdNode.getLeft(), closestDist, closestNode, x, y, !divX);
            }

            //Hvis distancen til mit venstre barns splitpunkt er mindre end closestDist, så led til venstre
            if (distanceSq(x, y, searchNodeNode.getSplitDenAndenKoordinat(), searchNodeNode.getSplit()) < kdTree.getClosestDistField()) {
                searchKdNode.getRight().findNearestNeighbourKDNode(searchKdNode.getRight(), closestDist, closestNode, x, y, !divX);
            }}
    }
}

