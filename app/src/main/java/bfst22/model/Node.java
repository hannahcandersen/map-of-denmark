package bfst22.model;

public class Node extends KDNode {
    private KDNode root, left, right;
    private float root2;

    public Node() {
        super();
    }

    public Node(float root) {
        super();
        this.root2 = root;
    }

    public KDNode getRoot() {return root;}
    public KDNode getLeft() {return left;}
    public KDNode getRight() {return right;}

    public void setLeft(KDNode left) {
        this.left = left;
    }

    public void setRight(KDNode right) {
        this.right = right;
    }
}
