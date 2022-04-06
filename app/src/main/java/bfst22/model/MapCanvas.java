package bfst22.model;

import com.sun.scenario.effect.impl.prism.ps.PPSBlend_BLUEPeer;
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
        //Point2D screenMin = new Point2D(20, -20);
        //Point2D screenMax = new Point2D(getWidth()+20, getHeight()-20);

        //Punkter i vinduet lavet om til coords
        //Point2D realMin = mouseToModel(screenMin);
        //Point2D realMax = mouseToModel(screenMax);

        //Opretter en rectangle med de koordinater der er inden for rectangle
        //Rectangle searchRectangle = new Rectangle(realMin.getX(), realMax.getX(), realMax.getY(), realMin.getY());

        //Result listen, med de punkter der er inRange
        //var result = KDTree2.rangeSearch(searchRectangle);

        //Sætter de generelle indstillinger for MapCanvas
        var gc = getGraphicsContext2D();
        gc.setTransform(new Affine());
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, getWidth(), getHeight());

        //gc.strokeLine(realMin.getX(), realMin.getY(), realMax.getX(), realMax.getY());

        gc.setTransform(trans); //
        gc.setFill(Color.LIGHTBLUE);


        //
        //Tegner lake
        for (var line : model.iterable(WayType.LAKE)) {
            line.fill(gc);
        }

        //Tegner alle linjer
        gc.setLineWidth(1/Math.sqrt(trans.determinant()));
        for (var line : model.iterable(WayType.UNKNOWN)) {
            line.draw(gc);
        }



        ///////////////// MANGLER AT FÅ FAT PÅ id2node LISTEN
        //KDTree2 kdTree = new KDTree2();
        //

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
            throw new RuntimeException("her er fejlen" + e);
        }
    }
}