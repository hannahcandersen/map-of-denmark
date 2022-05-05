package bfst22.model.kdtree;

import bfst22.model.OSMNode;

public abstract class KdNode {

    protected OSMNode closestNode;
    protected double closestDist;

    public KdNode(){
    }

    public abstract void findNearestNeighbourKDNode(KdNode searchKdNode, double closestDist, OSMNode closestNode, float x, float y, boolean divX);

    public OSMNode getClosestNode() {
        return closestNode;
    }

    public double getClosestDist() {
        return closestDist;
    }

    public abstract KdNode getLeft();
    public abstract KdNode getRight();


    public void setClosestNode(OSMNode closestNode) {
        this.closestNode = closestNode;
    }

    public void setClosestDist(double closestDist) {
        this.closestDist = closestDist;
    }
}