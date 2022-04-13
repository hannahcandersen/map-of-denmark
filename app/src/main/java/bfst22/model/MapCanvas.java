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
    private Paint color = WayType.UNKNOWN.getFillColor();

    public void init(Model model) {
        this.model = model;
        pan(-model.getMinLon(), -model.getMinLat());
        zoom(640 / (model.getMaxlon() - model.getMinLon()), 0, 0);
        model.addObserver(this::repaint);
        //try {
            repaint();
        /*} catch (NullPointerException e) {
            System.out.println(e.getMessage());
        }*/
    }

    //Repaint gør at billedet ikke tegner oveni det forrige billede, således at canvaset hele tiden opdateres.
    public void repaint() { //throws NullPointerException

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
        var resultBROWNFIELD = KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeBROWNFIELD());
        var resultGARAGES = KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeGARAGES());
        var resultLANDFILL = KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeLANDFILL());
        var resultRAILWAY = KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeRAILWAY());
        var resultRELIGIOUS = KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeRELIGIOUS());
        var resultCONSTRUCTIONWAY = KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeCONSTRUCTIONWAY());
        var resultBRIDLEWAY = KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeBRIDLEWAY());
        var resultSTEPS = KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeSTEPS());
        var resultFOOTWAY_ANDMORE = KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeFOOTWAY_ANDMORE());
        var resultCYCLEWAY= KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeCYCLEWAY());
        var resultRACEWAY= KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeRACEWAY());
        var resultTRACK= KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeTRACK());
        var resultPEDESTRIAN= KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreePEDESTRIAN());
        var resultSERVICE= KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeSERVICE());
        var resultROAD= KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeROAD());
        var resultLIVING_STREET= KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeLIVING_STREET());
        var resultRESIDENTIALWAY= KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeRESIDENTIALWAY());
        var resultUNCLASSIFIED= KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeUNCLASSIFIED());
        var resultTERTIARY= KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeTERTIARY());
        var resultSECONDARY= KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeSECONDARY());
        var resultPRIMARY= KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreePRIMARY());
        var resultTRUNK= KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeTRUNK());
        var resultMOTORWAY = KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeMOTORWAY());
        var resultBUILDING = KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeBUILDING());


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
                drawable.drawWayType(gc, WayType.UNKNOWN);
            }


            //////////////////////////////////////tegner LANDUSE//////////////////////////////////////
            for (Drawable drawable : resultRESIDENTIAL) {
                drawable.fillWayType(gc, WayType.RESIDENTIAL);
            }
            for (Drawable drawable : resultFOREST) {
                drawable.fillWayType(gc, WayType.FOREST);
            }
            for (Drawable drawable : resultMEADOW) {
                drawable.fillWayType(gc, WayType.MEADOW);
            }
            /*gc.setFill(WayType.CEMETERY.getFillColor());
            for (Drawable drawable : resultCEMETERY) {
                drawable.fill(gc);
            }
            gc.setFill(WayType.ALLOTMENTS.getFillColor());
            for (Drawable drawable : resultALLOTMENTS) {
                drawable.fill(gc);
            }
            gc.setFill(WayType.FARMYARD.getFillColor());
            for (Drawable drawable : resultFARMYARD) {
                drawable.fill(gc);
            }
            gc.setFill(WayType.COMMERCIAL.getFillColor());
            for (Drawable drawable : resultCOMMERCIAL) {
                drawable.fill(gc);
            }
            gc.setFill(WayType.CONSTRUCTION.getFillColor());
            for (Drawable drawable : resultCONSTRUCTION) {
                drawable.fill(gc);
            }
            gc.setFill(WayType.RETAIL.getFillColor());
            for (Drawable drawable : resultRETAIL) {
                drawable.fill(gc);
            }*/
            for (Drawable drawable : resultINDUSTRIAL) {
                drawable.fillWayType(gc, WayType.INDUSTRIAL);
            }
            /*gc.setFill(WayType.FARMLAND.getFillColor());
            for (Drawable drawable : resultFARMLAND) {
                drawable.fill(gc);
            }
            gc.setFill(WayType.ORCHARD.getFillColor());
            for (Drawable drawable : resultORCHARD) {
                drawable.fill(gc);
            }
            gc.setFill(WayType.VINEYARD.getFillColor());
            for (Drawable drawable : resultVINEYARD) {
                drawable.fill(gc);
            }
            gc.setFill(WayType.BASIN.getFillColor());
            for (Drawable drawable : resultBASIN) {
                drawable.fill(gc);
            }
            gc.setFill(WayType.GRASS.getFillColor());
            for (Drawable drawable : resultGRASS) {
                drawable.fill(gc);
            }
            gc.setFill(WayType.GREENFIELD.getFillColor());
            for (Drawable drawable : resultGREENFIELD) {
                drawable.fill(gc);
            }
            gc.setFill(WayType.GREENHOUSE_HORTICULTURE.getFillColor());
            for (Drawable drawable : resultGREENHOUSE_HORTICULTURE) {
                drawable.fill(gc);
            }
            gc.setFill(WayType.MILITARY.getFillColor());
            for (Drawable drawable : resultMILITARY) {
                drawable.fill(gc);
            }
            gc.setFill(WayType.PLANT_NURSERY.getFillColor());
            for (Drawable drawable : resultPLANT_NURSERY) {
                drawable.fill(gc);
            }
            gc.setFill(WayType.QUARRY.getFillColor());
            for (Drawable drawable : resultQUARRY) {
                drawable.fill(gc);
            }
            gc.setFill(WayType.RECREATION_GROUND.getFillColor());
            for (Drawable drawable : resultRECREATION_GROUND) {
                drawable.fill(gc);
            }
            gc.setFill(WayType.VILLAGE_GREEN.getFillColor());
            for (Drawable drawable : resultVILLAGE_GREEN) {
                drawable.fill(gc);
            }
            ///Er ikke på Bornholm///
        /*gc.setFill(WayType.BROWNFIELD.getFillColor());
        for (Drawable drawable : resultBROWNFIELD) {
            drawable.fill(gc);
        }
        gc.setFill(WayType.GARAGES.getFillColor());
        for (Drawable drawable : resultGARAGES) {
            drawable.fill(gc);
        }
        gc.setFill(WayType.LANDFILL.getFillColor());
        for (Drawable drawable : resultLANDFILL) {
            drawable.fill(gc);
        }
        gc.setFill(WayType.RAILWAY.getFillColor());
        for (Drawable drawable : resultRAILWAY) {
            drawable.fill(gc);
        }
        gc.setFill(WayType.RELIGIOUS.getFillColor());
        for (Drawable drawable : resultRELIGIOUS) {
            drawable.fill(gc);
        }*/


            //////////////////////////////////////tegner HIGHWAY//////////////////////////////////////
            /*gc.setStroke(WayType.CONSTRUCTIONWAY.getFillColor());
            gc.setLineWidth(WayType.CONSTRUCTIONWAY.getWidth() / Math.sqrt(trans.determinant()));
            for (Drawable drawable : resultCONSTRUCTIONWAY) {
                drawable.draw(gc);
            }
            gc.setStroke(WayType.BRIDLEWAY.getFillColor());
            gc.setLineDashes(1 / Math.sqrt(trans.determinant()) * WayType.BRIDLEWAY.getDashes());
            gc.setLineWidth(WayType.BRIDLEWAY.getWidth() / Math.sqrt(trans.determinant()));
            for (Drawable drawable : resultBRIDLEWAY) {
                drawable.draw(gc);
            }
            gc.setStroke(WayType.STEPS.getFillColor());
            gc.setLineDashes(1 / Math.sqrt(trans.determinant()) * WayType.STEPS.getDashes());
            gc.setLineWidth(WayType.STEPS.getWidth() / Math.sqrt(trans.determinant()));
            for (Drawable drawable : resultSTEPS) {
                drawable.draw(gc);
            }
            gc.setStroke(WayType.FOOTWAY_ANDMORE.getFillColor());
            gc.setLineDashes(1 / Math.sqrt(trans.determinant()) * WayType.FOOTWAY_ANDMORE.getDashes());
            gc.setLineWidth(WayType.FOOTWAY_ANDMORE.getWidth() / Math.sqrt(trans.determinant()));
            for (Drawable drawable : resultFOOTWAY_ANDMORE) {
                drawable.draw(gc);
            }
            gc.setStroke(WayType.CYCLEWAY.getFillColor());
            gc.setLineDashes(1 / Math.sqrt(trans.determinant()) * WayType.CYCLEWAY.getDashes());
            gc.setLineWidth(WayType.CYCLEWAY.getWidth() / Math.sqrt(trans.determinant()));
            for (Drawable drawable : resultCYCLEWAY) {
                drawable.draw(gc);
            }
            gc.setLineDashes(null);
            gc.setStroke(WayType.RACEWAY.getFillColor());
            gc.setLineWidth(WayType.RACEWAY.getWidth() / Math.sqrt(trans.determinant()));
            for (Drawable drawable : resultRACEWAY) {
                drawable.draw(gc);
            }
            gc.setStroke(WayType.TRACK.getFillColor());
            gc.setLineDashes(1 / Math.sqrt(trans.determinant()) * WayType.TRACK.getDashes());
            gc.setLineWidth(WayType.TRACK.getWidth() / Math.sqrt(trans.determinant()));
            for (Drawable drawable : resultTRACK) {
                drawable.draw(gc);
            }
            gc.setLineDashes(null);
            gc.setStroke(WayType.PEDESTRIAN.getFillColor());
            gc.setLineWidth(WayType.PEDESTRIAN.getWidth() / Math.sqrt(trans.determinant()));
            for (Drawable drawable : resultPEDESTRIAN) {
                drawable.draw(gc);
            }
            gc.setStroke(WayType.SERVICE.getFillColor());
            gc.setLineWidth(WayType.SERVICE.getWidth() / Math.sqrt(trans.determinant()));
            for (Drawable drawable : resultSERVICE) {
                drawable.draw(gc);
            }
            ///Er ikke på Bornholm///
        /*gc.setStroke(WayType.ROAD.getFillColor());
        gc.setLineWidth(WayType.ROAD.getWidth()/Math.sqrt(trans.determinant()));
        for (Drawable drawable : resultROAD) {
            drawable.draw(gc);
        }*/
            /*gc.setStroke(WayType.LIVING_STREET.getFillColor());
            gc.setLineWidth(WayType.LIVING_STREET.getWidth() / Math.sqrt(trans.determinant()));
            for (Drawable drawable : resultLIVING_STREET) {
                drawable.draw(gc);
            }
            gc.setStroke(WayType.RESIDENTIALWAY.getFillColor());
            gc.setLineWidth(WayType.RESIDENTIALWAY.getWidth() / Math.sqrt(trans.determinant()));
            for (Drawable drawable : resultRESIDENTIALWAY) {
                drawable.draw(gc);
            }
            gc.setStroke(WayType.UNCLASSIFIED.getFillColor());
            gc.setLineWidth(WayType.UNCLASSIFIED.getWidth() / Math.sqrt(trans.determinant()));
            for (Drawable drawable : resultUNCLASSIFIED) {
                drawable.draw(gc);
            }
            gc.setStroke(WayType.TERTIARY.getFillColor());
            gc.setLineWidth(WayType.TERTIARY.getWidth() / Math.sqrt(trans.determinant()));
            for (Drawable drawable : resultTERTIARY) {
                drawable.draw(gc);
            }
            gc.setStroke(WayType.SECONDARY.getFillColor());
            gc.setLineWidth(WayType.SECONDARY.getWidth() / Math.sqrt(trans.determinant()));
            for (Drawable drawable : resultSECONDARY) {
                drawable.draw(gc);
            }
            gc.setStroke(WayType.PRIMARY.getFillColor());
            gc.setLineWidth(WayType.PRIMARY.getWidth() / Math.sqrt(trans.determinant()));
            for (Drawable drawable : resultPRIMARY) {
                drawable.draw(gc);
            }*/

            ///Er ikke på Bornholm///
        /*gc.setStroke(WayType.TRUNK.getFillColor());
        gc.setLineWidth(WayType.TRUNK.getWidth()/Math.sqrt(trans.determinant()));
        for (Drawable drawable : resultTRUNK) {
            drawable.draw(gc);
        }
            gc.setStroke(WayType.MOTORWAY.getFillColor());
            gc.setLineWidth(WayType.MOTORWAY.getWidth()/Math.sqrt(trans.determinant()));
        for (Drawable drawable : resultMOTORWAY) {
            drawable.draw(gc);
        }*/


            //////////////////////////////////////tegner BUILDING//////////////////////////////////////
            //gc.setFill(WayType.BUILDING.getFillColor());
            //gc.setStroke(Color.valueOf("C3B8AE"));
            gc.setLineWidth(WayType.BUILDING.getWidth() / Math.sqrt(trans.determinant()));
            for (Drawable drawable : resultBUILDING) {
                drawable.fillWayType(gc, WayType.BUILDING);
                drawable.drawWayType(gc, WayType.BUILDING);
            }




        //Laver rectangle - virker ikke
        Color rectangleColor = new Color(0.6, 0.2, 0.1, 0.2);
        gc.setFill(rectangleColor);
        gc.setStroke(Color.BLACK);
        gc.fillRect(realMin.getY(), realMax.getY(), realMin.getX(), realMax.getX());
        gc.stroke();
    }

    public void pan(double dx, double dy) {
        trans.prependTranslation(dx, dy);
        //try {
            repaint();
        /*} catch (NullPointerException e) {
            System.out.println(e.getMessage());
        }*/
    }

    // Zoomer med en given faktor
    public void zoom(double factor, double x, double y) {
        trans.prependTranslation(-x, -y);
        trans.prependScale(factor, factor);
        trans.prependTranslation(x, y);
        //try {
            repaint();
        /*} catch (NullPointerException e) {
            System.out.println(e.getMessage());
        }*/
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