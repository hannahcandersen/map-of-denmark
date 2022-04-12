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
        var resultCONSTRUCTIONWAY = KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeCONSTRUCTIONWAY());


        var resultUNCLASSIFIED= KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeUNCLASSIFIED());
        var resultTERTIARY= KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeTERTIARY());
        var resultSECONDARY= KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeSECONDARY());
        var resultPRIMARY= KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreePRIMARY());
        var resultTRUNK= KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeTRUNK());
        var resultMOTORWAY = KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeMOTORWAY());


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
        for (Drawable drawable : resultFOREST) {
            gc.setFill(WayType.FOREST.getColorValue());
            drawable.fill(gc);
        }
        for (Drawable drawable : resultMEADOW) {
            gc.setFill(WayType.MEADOW.getColorValue());
            drawable.fill(gc);
        }
        for (Drawable drawable : resultCEMETERY) {
            gc.setFill(WayType.CEMETERY.getColorValue());
            drawable.fill(gc);
        }
        for (Drawable drawable : resultALLOTMENTS) {
            gc.setFill(WayType.ALLOTMENTS.getColorValue());
            drawable.fill(gc);
        }
        for (Drawable drawable : resultFARMYARD) {
            gc.setFill(WayType.FARMYARD.getColorValue());
            drawable.fill(gc);
        }
        for (Drawable drawable : resultCOMMERCIAL) {
            gc.setFill(WayType.COMMERCIAL.getColorValue());
            drawable.fill(gc);
        }
        for (Drawable drawable : resultCONSTRUCTION) {
            gc.setFill(WayType.CONSTRUCTION.getColorValue());
            drawable.fill(gc);
        }
        for (Drawable drawable : resultRETAIL) {
            gc.setFill(WayType.RETAIL.getColorValue());
            drawable.fill(gc);
        }
        for (Drawable drawable : resultINDUSTRIAL) {
            gc.setFill(WayType.INDUSTRIAL.getColorValue());
            drawable.fill(gc);
        }
        for (Drawable drawable : resultFARMLAND) {
            gc.setFill(WayType.FARMLAND.getColorValue());
            drawable.fill(gc);
        }
        for (Drawable drawable : resultORCHARD) {
            gc.setFill(WayType.ORCHARD.getColorValue());
            drawable.fill(gc);
        }
        for (Drawable drawable : resultVINEYARD) {
            gc.setFill(WayType.VINEYARD.getColorValue());
            drawable.fill(gc);
        }
        for (Drawable drawable : resultBASIN) {
            gc.setFill(WayType.BASIN.getColorValue());
            drawable.fill(gc);
        }
        for (Drawable drawable : resultGRASS) {
            gc.setFill(WayType.GRASS.getColorValue());
            drawable.fill(gc);
        }
        for (Drawable drawable : resultGREENFIELD) {
            gc.setFill(WayType.GREENFIELD.getColorValue());
            drawable.fill(gc);
        }
        for (Drawable drawable : resultGREENHOUSE_HORTICULTURE) {
            gc.setFill(WayType.GREENHOUSE_HORTICULTURE.getColorValue());
            drawable.fill(gc);
        }
        for (Drawable drawable : resultMILITARY) {
            gc.setFill(WayType.MILITARY.getColorValue());
            drawable.fill(gc);
        }
        for (Drawable drawable : resultPLANT_NURSERY) {
            gc.setFill(WayType.PLANT_NURSERY.getColorValue());
            drawable.fill(gc);
        }
        for (Drawable drawable : resultQUARRY) {
            gc.setFill(WayType.QUARRY.getColorValue());
            drawable.fill(gc);
        }
        for (Drawable drawable : resultRECREATION_GROUND) {
            gc.setFill(WayType.RECREATION_GROUND.getColorValue());
            drawable.fill(gc);
        }
        for (Drawable drawable : resultVILLAGE_GREEN) {
            gc.setFill(WayType.VILLAGE_GREEN.getColorValue());
            drawable.fill(gc);
        }
        ///Er ikke på Bornholm///
        /*for (Drawable drawable : resultSALT_POND) {
            gc.setFill(WayType.SALT_POND.getColorValue());
            drawable.fill(gc);
        }
        for (Drawable drawable : resultBROWNFIELD) {
            gc.setFill(WayType.BROWNFIELD.getColorValue());
            drawable.fill(gc);
        }
        for (Drawable drawable : resultGARAGES) {
            gc.setFill(WayType.GARAGES.getColorValue());
            drawable.fill(gc);
        }
        for (Drawable drawable : resultLANDFILL) {
            gc.setFill(WayType.LANDFILL.getColorValue());
            drawable.fill(gc);
        }
        for (Drawable drawable : resultRAILWAY) {
            gc.setFill(WayType.RAILWAY.getColorValue());
            drawable.fill(gc);
        }
        for (Drawable drawable : resultRELIGIOUS) {
            gc.setFill(WayType.RELIGIOUS.getColorValue());
            drawable.fill(gc);
        }*/


        //////////////////////////////////////tegner HIGHWAY//////////////////////////////////////
        for (Drawable drawable : resultCONSTRUCTIONWAY) {
            gc.setStroke(WayType.CONSTRUCTIONWAY.getColorValue());
            gc.setLineWidth(2/Math.sqrt(trans.determinant()));
            drawable.fill(gc);
        }



        for (Drawable drawable : resultUNCLASSIFIED) {
            gc.setStroke(WayType.UNCLASSIFIED.getColorValue());
            gc.setLineWidth(5/Math.sqrt(trans.determinant()));
            drawable.fill(gc);
        }
        for (Drawable drawable : resultTERTIARY) {
            gc.setStroke(WayType.TERTIARY.getColorValue());
            gc.setLineWidth(6/Math.sqrt(trans.determinant()));
            drawable.fill(gc);
        }
        for (Drawable drawable : resultSECONDARY) {
            gc.setStroke(WayType.SECONDARY.getColorValue());
            gc.setLineWidth(7/Math.sqrt(trans.determinant()));
            drawable.fill(gc);
        }
        for (Drawable drawable : resultPRIMARY) {
            gc.setStroke(WayType.PRIMARY.getColorValue());
            gc.setLineWidth(9/Math.sqrt(trans.determinant()));
            drawable.fill(gc);
        }

        ///Er ikke på Bornholm///
        /*for (Drawable drawable : resultTRUNK) {
            gc.setStroke(WayType.TRUNK.getColorValue());
            gc.setLineWidth(9/Math.sqrt(trans.determinant()));
            drawable.fill(gc);
        }
        for (Drawable drawable : resultMOTORWAY) {
            gc.setStroke(WayType.MOTORWAY.getColorValue());
            gc.setLineWidth(10/Math.sqrt(trans.determinant()));
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