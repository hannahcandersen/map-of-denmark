package bfst22.model.kdtree;

// Subklasse til KdNode
// Indeholder ingen elementer, men en splitværdi og en reference til højre og venstre barn

import java.io.Serializable;

public class Node extends KdNode implements Serializable {

    public static final long serialVersionUID = 1325236;

    private KdNode left, right;
    private float split; // Koordinat for den node, der var split i KdTræet. Er skiftevis x og y

    public Node() {super();}

    public KdNode getLeft() {return left;}
    public KdNode getRight() {return right;}
    public float  getSplit(){return split;}
    public void setLeft(KdNode left) {
        this.left = left;
    }
    public void setRight(KdNode right) {
        this.right = right;
    }
    public void setSplit(float split) {
        this.split = split;
    }
}

