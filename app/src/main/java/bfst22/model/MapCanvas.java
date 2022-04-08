package bfst22.model;

import bfst22.model.kdtree.KdTree;
import bfst22.model.kdtree.RangeSerachRectangle;
import bfst22.view.Drawable;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;

//Canvas = lærred. Klassen der tegner, panner og zoomer

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

        //Får punkterne i vinduet
        //PLUS 20 OG MINUS 20 SKAL FJERNES I DEN ENDELIGE LØSNING
        Point2D screenMin = new Point2D(200, 200);
        Point2D screenMax = new Point2D(getWidth()-200, getHeight()-200);

        //Punkter i vinduet lavet om til coords
        Point2D realMin = mouseToModel(screenMin);
        Point2D realMax = mouseToModel(screenMax);

        //Opretter en rectangle med de koordinater der er inden for rectangle
        RangeSerachRectangle searchRangeSerachRectangle = new RangeSerachRectangle(realMin.getY(),
                realMax.getY(), realMin.getX(), realMax.getX());
        System.out.println(searchRangeSerachRectangle);

        //Result listen, med de drawables der er inRange
        var result = KdTree.rangeSearch(searchRangeSerachRectangle);
        System.out.println("leaves added: " + KdTree.getLeavesAdded());

        //Sætter de generelle indstillinger for MapCanvas
        var gc = getGraphicsContext2D();
        gc.setTransform(new Affine());

        //Laver baggrunden
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, getWidth(), getHeight());
        gc.setTransform(trans);

        //Løber results (liste af drawables returneret af rangeSearch) igennem og tegner dem
        gc.setLineWidth(1 / Math.sqrt(trans.determinant()));
        for (Drawable drawable : result) {
            drawable.draw(gc);
        }

        //Laver rectangle - virker ikke
        Color rectangleColor = new Color(0.6, 0.2, 0.1, 0.2);
        gc.setFill(rectangleColor);
        gc.fillRect(realMin.getY(), realMax.getY(), realMin.getX(), realMax.getX());
        gc.setStroke(Color.BLACK);
        gc.stroke();
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