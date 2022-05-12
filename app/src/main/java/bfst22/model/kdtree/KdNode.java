package bfst22.model.kdtree;

// Abstrakt superklasse til Node og Leaf

public abstract class KdNode {

    public KdNode() {}

    public abstract KdNode getLeft();
    public abstract KdNode getRight();
}

