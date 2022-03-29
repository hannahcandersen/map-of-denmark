package bfst22.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class KdTree<T> {

    //Sammenligner x og y koordinatorerne
    private final Comparator<KdNode<T>> cmpX = new Comparator<KdNode<T>>() {

        @Override
        public int compare(KdNode<T> p1, KdNode<T> p2) {
            return Double.compare(p1.getX(), p2.getY());
        }
    };

    //Sammenligner y-koordinatorerne
    private final Comparator<KdNode<T>> cmpY = new Comparator<KdNode<T>>() {

        @Override
        public int compare(KdNode<T> p1, KdNode<T> p2) {
            return Double.compare(p1.getY(), p2.getY());
        }
    };

    //Erklærer en sammenlignelige rod af Kd-træet
    private KdNode<T> root;

    //Tom constructor
    public KdTree() {}

    //Constructor der tager i mod en liste af sammenlignelige KdNodes
    // Kalder på funktionen buildTree(kdNodes)
    public KdTree(List<KdNode<T>> kdNodes) {
        buildTree(kdNodes);
    }

    //Bliver kaldt i Constructoren
    //Metode til at initialiserer et Kd-Tree
    public void buildTree(List<KdNode<T>> kdNodes) {

        //Initialiserer roden
        root = new Object() {

            KdNode<T> buildTree(boolean divX, List<KdNode<T>> kdNodes) {

                //Tjekker for om træet er tomt
                if (kdNodes == null || kdNodes.isEmpty())
                    return null;

                //Sorteret liste. Hvis divX=true cmpX, hvis divX=false cmpY.
                //Bliver sorteret på enten x eller y
                Collections.sort(kdNodes, divX ? cmpX : cmpY);

                //Finder midten ved at dividere midten med 2. Finder midtens index
                int mid = kdNodes.size() >> 1;

                //Erklærer ny kdNode
                KdNode<T> kdNode = new KdNode<>();

                //Sætter kdNode's coords til midten
                kdNode.coords = kdNodes.get(mid).coords;

                //Sætter kdNode til et objects
                kdNode.object = kdNodes.get(mid).object;

                //Bygger træet i venstre side, From 0 (inklusiv) to mid (eksklusiv)
                kdNode.left = buildTree(!divX, kdNodes.subList(0, mid));

                //Bygger træet i højre side, hvis det ikke er tomt.
                if (mid + 1 <= kdNodes.size() - 1)
                    kdNode.right = buildTree(!divX, kdNodes.subList(mid+1, kdNodes.size()));

                //return roden
                return kdNode;

            } //Slutter buildTree

        } //Slutter Root = new object

        //Bygger træet igen, men nu med den anden koordinat, OBS VED IKKE HELT
        .buildTree(true, kdNodes);

    } //BuildTree slutter
















    //Andet shit vi ikke skal bruge nu.

    public T findNearestNeighbour(double x, double y) {
        return findNearestNeighbour(root, x, y, true);
    }

    private T findNearestNeighbour(KdNode<T> fromKdNode, final double x, final double y, boolean divX) {
        return new Object() {

            double closestDist = Double.POSITIVE_INFINITY;

            T closestNode = null;

            T findNearestNeighbour(KdNode<T> kdNode, boolean divX) {
                if (kdNode == null)
                    return null;
                double d = Point2D.distanceSq(kdNode.coords.x, kdNode.coords.y, x, y);
                if (closestDist > d) {
                    closestDist = d;
                    closestNode = kdNode.object;
                }
                double delta = divX ? x - kdNode.coords.x : y - kdNode.coords.y;
                double delta2 = delta * delta;
                KdNode<T> kdNode1 = delta < 0 ? kdNode.left : kdNode.right;
                KdNode<T> kdNode2 = delta < 0 ? kdNode.right : kdNode.left;
                findNearestNeighbour(kdNode1, !divX);
                if (delta2 < closestDist) {
                    findNearestNeighbour(kdNode2, !divX);
                }
                return closestNode;
            }
        }.findNearestNeighbour(fromKdNode, divX);
    }


    public List<T> rangeSearch(final double x, final double y, final double dist) {
        return new Object() {
            List<T> result = new LinkedList<>();
            double radius = dist * dist;
            List<T> rangeSearch(KdNode<T> kdNode, boolean divX) {
                if (kdNode == null)
                    return result;
                double d = Point2D.distanceSq(kdNode.coords.x, kdNode.coords.y, x, y);

                if (radius >= d)
                    result.add(kdNode.object);

                double delta = divX ? x - kdNode.coords.x : y - kdNode.coords.y;
                double delta2 = delta * delta;
                KdNode<T> kdNode1 = delta < 0 ? kdNode.left : kdNode.right;
                KdNode<T> kdNode2 = delta < 0 ? kdNode.right : kdNode.left;
                rangeSearch(kdNode1, !divX);
                if (delta2 < radius) {
                    rangeSearch(kdNode2, !divX);
                }
                return result;
            }
        }.rangeSearch(root, true);
    }

    public List<T> rangeSearchKilometers(final double x, final double y, final double dist) {
        return new Object() {
            List<T> result = new LinkedList<>();
            List<T> rangeSearch(KdNode<T> kdNode, boolean divX) {
                if (kdNode == null)
                    return result;
                double d = EarthDistance.calculate(kdNode.coords.x, kdNode.coords.y, x, y);
                if (dist >= d)
                    result.add(kdNode.object);
                double delta = divX ? x - kdNode.coords.x : y - kdNode.coords.y;
                double delta2 = divX ? EarthDistance.calculate(x, 0, kdNode.coords.x, 0)
                        : EarthDistance.calculate(0, y, 0, kdNode.coords.y);
                KdNode<T> kdNode1 = delta < 0 ? kdNode.left : kdNode.right;
                KdNode<T> kdNode2 = delta < 0 ? kdNode.right : kdNode.left;
                rangeSearch(kdNode1,!divX);
                if (delta2 < dist) {
                    rangeSearch(kdNode2, !divX);
                }
                return result;
            }
        }.rangeSearch(root, true);
    }


    public List<T> findNearestNeighbours(final double x, final double y, final int limit) {
        final List<T> result = new LinkedList<>();
        final List<KdNode<T>> closestKdNodes = new ArrayList<>();
        final Comparator<KdNode<T>> cmp = new Comparator<KdNode<T>>() {
            @Override
            public int compare(KdNode<T> node1, KdNode<T> node2) {
                Double p1 = Point2D.distanceSq(node1.coords.x, node1.coords.y, x, y);
                Double p2 = Point2D.distanceSq(node2.coords.x, node2.coords.y, x, y);
                return Double.compare(p1, p2);
            }
        };

        new Object() {
            double closestDist = Double.POSITIVE_INFINITY;
            int size = 0;

            void findNearestNeighbours(KdNode<T> kdNode, boolean divX) {
                if (kdNode == null)
                    return ;
                double d = Point2D.distanceSq(kdNode.coords.x, kdNode.coords.y, x, y);
                if (closestDist > d) {
                    closestDist = d;
                }
                closestKdNodes.add(kdNode);
                size++;
                double delta = divX ? x - kdNode.coords.x : y - kdNode.coords.y;
                double delta2 = delta * delta;
                KdNode<T> kdNode1 = delta < 0 ? kdNode.left : kdNode.right;
                KdNode<T> kdNode2 = delta < 0 ? kdNode.right : kdNode.left;
                findNearestNeighbours(kdNode1, !divX);
                if (delta2 < closestDist || size < limit) {
                    findNearestNeighbours(kdNode2, !divX);
                }
            }

        }.findNearestNeighbours(root, true);

        Collections.sort(closestKdNodes, cmp);

        int cnt = 0;
        for(KdNode<T> kdNode : closestKdNodes) {
            if (cnt >= limit)
                break;
            result.add(kdNode.object);
            cnt++;
        }

        return result;
    }


    /*public void insert(T object, double x, double y) {
        KdNode<T> kdNode = new KdNode<>(object, x, y);
        if (root == null)
            root = kdNode;
        else
            insert(kdNode, root, true);
    }

    private void insert(KdNode<T> kdNode, KdNode<T> currentKdNode, boolean divX) {
        if (kdNode == null)
            return;
        int cmpResult = (divX ? cmpX : cmpY).compare(kdNode, currentKdNode);
        if (cmpResult == -1)
            if(currentKdNode.left == null)
                currentKdNode.left = kdNode;
            else
                insert(kdNode, currentKdNode.left, !divX);
        else
        if(currentKdNode.right == null)
            currentKdNode.right = kdNode;
        else
            insert(kdNode, currentKdNode.right, !divX);
    }

     */

    public T findMin() {
        KdNode<T> kdNode = findMin(root, true);
        if(kdNode != null)
            return kdNode.object;
        return null;
    }


    private KdNode<T> findMin(KdNode<T> kdNode, boolean divX) {
        if (kdNode == null)
            return null;
        if (divX) {
            if (kdNode.left == null)
                return kdNode;
            else
                return findMin(kdNode.left, false);
        }
        else {
            List<KdNode<T>> list = new LinkedList<>();
            list.add(findMin(kdNode, true));

            if(kdNode.left != null)
                list.add(findMin(kdNode.left, true));

            if(kdNode.right != null)
                list.add(findMin(kdNode.right, true));

            Collections.sort(list, cmpY);

            return list.get(0);

        }
    }

   /* public KdNode<T> fastDelete(final T target, double x, double y) {
        root = new Object() {
            KdNode<T> delete(T targetObject, double x, double y, KdNode<T> kdNode, boolean divX) {
                if(kdNode == null)
                    return null;
                if (targetObject.equals(kdNode.object)) {
                    if(kdNode.right != null) {
                        KdNode<T> minKdNode = findMin(kdNode.right, !divX);
                        kdNode.object = minKdNode.object;
                        kdNode.coords = minKdNode.coords;
                        kdNode.right = delete(kdNode.object, kdNode.getX(), kdNode.getY(), kdNode.right, !divX);
                    }
                    else if (kdNode.left != null) {
                        KdNode<T> minKdNode = findMin(kdNode.left, !divX);
                        kdNode.object = minKdNode.object;
                        kdNode.coords = minKdNode.coords;
                        kdNode.left = delete(kdNode.object, kdNode.getX(), kdNode.getY(), kdNode.left, !divX);
                    }
                    else
                        kdNode = null;
                }
                else {
                    double delta = divX ? x - kdNode.coords.x : y - kdNode.coords.y;
                    if(delta < 0)
                        kdNode.left = delete(targetObject, x, y, kdNode.left, !divX);
                    else
                        kdNode.right = delete(targetObject, x, y, kdNode.right, !divX);
                }
                return kdNode;
            }
        }.delete(target, x, y, root, true);

        return root;
    } */

   /* public KdNode<T> delete(final T target) {
        root = new Object() {
            KdNode<T> delete(T targetObject, KdNode<T> kdNode, boolean divX) {
                if(kdNode == null)
                    return null;
                if (targetObject.equals(kdNode.object)) {
                    if(kdNode.right != null) {
                        KdNode<T> minKdNode = findMin(kdNode.right, !divX);
                        kdNode.object = minKdNode.object;
                        kdNode.coords = minKdNode.coords;
                        kdNode.right = delete(kdNode.object, kdNode.right, !divX);
                    }
                    else if (kdNode.left != null) {
                        KdNode<T> minKdNode = findMin(kdNode.left, !divX);
                        kdNode.object = minKdNode.object;
                        kdNode.coords = minKdNode.coords;
                        kdNode.left = delete(kdNode.object, kdNode.left, !divX);
                    }
                    else
                        kdNode = null;
                }
                else {
                    kdNode.left = delete(targetObject, kdNode.left, !divX);
                    kdNode.right = delete(targetObject, kdNode.right, !divX);
                }
                return kdNode;
            }
        }.delete(target, root, true);

        return root;
    } */


    /*public void insertOrUpdate(T target, double x, double y) {
        delete(target);
        insert(target, x, y);
    }

     */

    public int getSize() {
        return new Object() {
            int cnt = 0;
            int getSize(KdNode<T> kdNode) {
                if (kdNode == null)
                    return cnt - 1;
                if (kdNode.left != null) {
                    cnt++;
                    getSize(kdNode.left);
                }
                if (kdNode.right != null) {
                    cnt++;
                    getSize(kdNode.right);
                }
                return cnt;
            }
        }.getSize(root) + 1;
    }

    public List<T> getAll() {
        final List<T> result = new LinkedList<>();
        new Object() {
            void fillList(KdNode<T> kdNode) {
                if(kdNode == null)
                    return;
                result.add(kdNode.getObject());
                if(kdNode.left != null)
                    fillList(kdNode.left);
                if(kdNode.right != null)
                    fillList(kdNode.right);
            }
        }.fillList(root);
        return result;
    }

}
