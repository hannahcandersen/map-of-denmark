package bfst22.model.kdtree;


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

}

