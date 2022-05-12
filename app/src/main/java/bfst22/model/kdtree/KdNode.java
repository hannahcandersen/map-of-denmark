package bfst22.model.kdtree;

// Abstrakt superklasse til Node og Leaf

import java.io.Serializable;

public abstract class KdNode implements Serializable {

    public static final long serialVersionUID = 1325239;

    public KdNode() {}

    public abstract KdNode getLeft();
    public abstract KdNode getRight();
}

