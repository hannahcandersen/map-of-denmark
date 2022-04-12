package bfst22.model;

import bfst22.model.kdtree.KdTree;
import bfst22.model.kdtree.RangeSerachRectangle;
import bfst22.view.Drawable;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;

//Canvas = lærred. Klassen der tegner, panner og zoomer

public class MapCanvas extends Canvas {
    private Model model;
    private Affine trans = new Affine();
    private Paint color = WayType.UNKNOWN.getColorValue();

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
        var resultUNKNOW = KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeUNKNOWN());
        //System.out.println("leaves added in resultUNKNOW: " + KdTree.getLeavesAdded());
        var resultRESIDENTIAL = KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeRESIDENTIAL());
        var resultFOREST = KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeFOREST());
        var resultMEADOW = KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeMEADOW());
        var resultCEMETERY = KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeCEMETERY());
        var resultALLOTMENTS = KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeALLOTMENTS());
        var resultFARMYARD = KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeFARMYARD());
        var resultCOMMERCIAL = KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeCOMMERCIAL());
        var resultCONSTRUCTION = KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeCONSTRUCTION());
        var resultRETAIL = KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeRETAIL());
        var resultINDUSTRIAL = KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeINDUSTRIAL());
        var resultFARMLAND = KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeFARMLAND());
        var resultORCHARD = KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeORCHARD());
        var resultVINEYARD = KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeVINEYARD());
        var resultBASIN = KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeBASIN());
        var resultGRASS = KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeGRASS());
        var resultGREENFIELD = KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeGREENFIELD());
        var resultGREENHOUSE_HORTICULTURE = KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeGREENHOUSE_HORTICULTURE());
        var resultMILITARY = KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeMILITARY());
        var resultPLANT_NURSERY = KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreePLANT_NURSERY());
        var resultQUARRY = KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeQUARRY());
        var resultRECREATION_GROUND = KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeRECREATION_GROUND());
        var resultVILLAGE_GREEN = KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeVILLAGE_GREEN());
        var resultSALT_POND = KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeSALT_POND());
        var resultBROWNFIELD = KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeBROWNFIELD());
        var resultGARAGES = KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeGARAGES());
        var resultLANDFILL = KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeLANDFILL());
        var resultRAILWAY = KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeRAILWAY());
        var resultRELIGIOUS = KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeRELIGIOUS());






        //Sætter de generelle indstillinger for MapCanvas
        var gc = getGraphicsContext2D();
        gc.setTransform(new Affine());

        //Laver baggrunden
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, getWidth(), getHeight());
        gc.setTransform(trans);

        //Løber results (liste af drawables returneret af rangeSearch) igennem og tegner dem
        gc.setLineWidth(1 / Math.sqrt(trans.determinant()));


        //////////////////////////////////////tegner UNKNOWN//////////////////////////////////////
        for (Drawable drawable : resultUNKNOW) {
            drawable.draw(gc);
        }

        //////////////////////////////////////tegner LANDUSE//////////////////////////////////////
        for (Drawable drawable : resultRESIDENTIAL) {
            gc.setFill(WayType.RESIDENTIAL.getColorValue());
            drawable.fill(gc);
        }
        /*for (Drawable drawable : resultFOREST) {
            gc.setFill(Color.valueOf("B4D0A3"));
            drawable.fill(gc);
        }
        for (Drawable drawable : resultMEADOW) {
            gc.setFill(Color.valueOf("D4EAB6"));
            drawable.fill(gc);
        }
        for (Drawable drawable : resultCEMETERY) {
            gc.setFill(Color.valueOf("B0CAB2"));
            drawable.fill(gc);
        }
        for (Drawable drawable : resultALLOTMENTS) {
            gc.setFill(Color.valueOf("CFE0C1"));
            drawable.fill(gc);
        }
        for (Drawable drawable : resultFARMYARD) {
            gc.setFill(Color.valueOf("F1DDBE"));
            drawable.fill(gc);
        }
        for (Drawable drawable : resultCOMMERCIAL) {
            gc.setFill(Color.valueOf("EEDAD9"));
            drawable.fill(gc);
        }
        for (Drawable drawable : resultCONSTRUCTION) {
            gc.setFill(Color.valueOf("C7C7B6"));
            drawable.fill(gc);
        }
        for (Drawable drawable : resultRETAIL) {
            gc.setFill(Color.valueOf("F8D6D1"));
            drawable.fill(gc);
        }*/
        for (Drawable drawable : resultINDUSTRIAL) {
            gc.setFill(Color.valueOf("E8DCE7"));
            drawable.fill(gc);
        }
        /*for (Drawable drawable : resultFARMLAND) {
            gc.setFill(Color.valueOf("EEF0D8"));
            drawable.fill(gc);
        }
        for (Drawable drawable : resultORCHARD) {
            gc.setFill(Color.valueOf("B7DFA7"));
            drawable.fill(gc);
        }
        for (Drawable drawable : resultVINEYARD) {
            gc.setFill(Color.valueOf("B7DFA7"));
            drawable.fill(gc);
        }
        for (Drawable drawable : resultBASIN) {
            gc.setFill(Color.valueOf("B2D2DD"));
            drawable.fill(gc);
        }
        for (Drawable drawable : resultGRASS) {
            gc.setFill(Color.valueOf("D4E9B6"));
            drawable.fill(gc);
        }
        for (Drawable drawable : resultGREENFIELD) {
            gc.setFill(Color.valueOf("F1EEE9"));
            drawable.fill(gc);
        }
        for (Drawable drawable : resultGREENHOUSE_HORTICULTURE) {
            gc.setFill(Color.valueOf("EEF0D8"));
            drawable.fill(gc);
        }
        for (Drawable drawable : resultMILITARY) {
            gc.setFill(Color.valueOf("F0E4DF"));
            drawable.fill(gc);
        }
        for (Drawable drawable : resultPLANT_NURSERY) {
            gc.setFill(Color.valueOf("B8DEA8"));
            drawable.fill(gc);
        }
        for (Drawable drawable : resultQUARRY) {
            gc.setFill(Color.valueOf("C3C2C2"));
            drawable.fill(gc);
        }
        for (Drawable drawable : resultRECREATION_GROUND) {
            gc.setFill(Color.valueOf("E5FBE4"));
            drawable.fill(gc);
        }
        for (Drawable drawable : resultVILLAGE_GREEN) {
            gc.setFill(Color.valueOf("D3EAB6"));
            drawable.fill(gc);
        }
        /*for (Drawable drawable : resultSALT_POND) {
            gc.setFill(Color.valueOf("B2D2DD"));
            drawable.fill(gc);
        }
        for (Drawable drawable : resultBROWNFIELD) {
            gc.setFill(Color.valueOf("C7C7B6"));
            drawable.fill(gc);
        }
        for (Drawable drawable : resultGARAGES) {
            gc.setFill(Color.valueOf("DEDDCE"));
            drawable.fill(gc);
        }
        for (Drawable drawable : resultLANDFILL) {
            gc.setFill(Color.valueOf("B6B694"));
            drawable.fill(gc);
        }
        for (Drawable drawable : resultRAILWAY) {
            gc.setFill(Color.valueOf("E9DBE8"));
            drawable.fill(gc);
        }
        for (Drawable drawable : resultRELIGIOUS) {
            gc.setFill(Color.valueOf("CDCCC9"));
            drawable.fill(gc);
        }*/





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

    // Zoomer med en given faktor
    public void zoom(double factor, double x, double y) {
        trans.prependTranslation(-x, -y);
        trans.prependScale(factor, factor);
        trans.prependTranslation(x, y);
        repaint();
    }

    //oversætter window koordinater til model koordinater
    public Point2D mouseToModel(Point2D point) {
        try {
            return trans.inverseTransform(point);
        } catch (NonInvertibleTransformException e) {
            //ikke alle lineære transformer kan transverteres, derfor kaster den denne exception
            //men Troels er så sikker på, at dette ikke vil ske, så hvis det sker skal programmet bare crashe
            //dette er en Java funktion, som udkommenterer "this can not happen"
            throw new RuntimeException(e);
        }
    }
}