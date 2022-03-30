package bfst22.model;

public class Node extends KDNode {
    private KDNode left, right;

    //Enten lat/y-værdien eller lon/x-værdien
    private float split;

    public Node() {
        super();
    }

    public KDNode getLeft() {return left;}
    public KDNode getRight() {return right;}
    public float  getSplit(){return split;}

    public void setLeft(KDNode left) {
        this.left = left;
    }
    public void setRight(KDNode right) {
        this.right = right;
    }
    public void setSplit(float split) {
        this.split = split;
    }
}
