package bfst22.model;

import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;

//Canvas = lærred

public class MapCanvas extends Canvas {
    private Model model;
    private Affine trans = new Affine();

    public void init(Model model) {
        this.model = model;
        pan(-model.getMinLon(), -model.getMinLat());
        zoom(640 / (model.getMaxlon() - model.getMinLon()), 0, 0);
        model.addObserver(this::repaint);
        repaint();
    }

    //Repaint gør at billedet ikke tegner oveni det forrige billede, således at canvaset hele tiden opdateres.
    public void repaint() {
        var gc = getGraphicsContext2D();
        gc.setTransform(new Affine());
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, getWidth(), getHeight());
        gc.setTransform(trans);
        gc.setFill(Color.LIGHTBLUE);
        for (var line : model.iterable(WayType.LAKE)) {
            line.fill(gc);
        }
        gc.setLineWidth(1/Math.sqrt(trans.determinant()));
        for (var line : model.iterable(WayType.UNKNOWN)) {
            line.draw(gc);
        }
        //InRange
        //Punkter i vinduet
        /*Point2D screenMin = new Point2D(0, 0);
        Point2D screenMax = new Point2D(getWidth(), getHeight());

        //Punkter i vinduet lavet om til coords
        Point2D realMin = mouseToModel(screenMin);
        Point2D realMax = mouseToModel(screenMax);

        //Opretter en rectangle med de koordinater der er inden for rectangle
        Rectangle searchRectangle = new Rectangle(realMin.getX(), realMax.getX(), realMax.getY(), realMin.getY());

        ///////////////// MANGLER AT FÅ FAT PÅ id2node LISTEN
        KDTree2 kdTree = new KDTree2();
        var result = kdTree.rangeSearch(searchRectangle); */

        // løb results igennem og tegn dem

    }

    public void pan(double dx, double dy) {
        trans.prependTranslation(dx, dy);
        repaint();
    }

    public void zoom(double factor, double x, double y) {
        trans.prependTranslation(-x, -y);
        trans.prependScale(factor, factor);
        trans.prependTranslation(x, y);
        repaint();
    }

    public Point2D mouseToModel(Point2D point) {
        try {
            return trans.inverseTransform(point);
        } catch (NonInvertibleTransformException e) {
            throw new RuntimeException(e);
        }
    }
}