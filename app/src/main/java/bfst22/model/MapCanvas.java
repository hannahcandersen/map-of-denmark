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
    private int zoomLevel;



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

        //Får punkterne i vinduet og laver dem om til punkter i kortet
        Point2D screenMin = new Point2D(200, 200);
        Point2D screenMax = new Point2D(getWidth()-200, getHeight()-200);
        Point2D realMin = mouseToModel(screenMin);
        Point2D realMax = mouseToModel(screenMax);

        //Opretter en Searchrectangle med de koordinater der er inden for rectangle
        RangeSerachRectangle searchRangeSerachRectangle = new RangeSerachRectangle(realMin.getY(), realMax.getY(), realMin.getX(), realMax.getX());
        System.out.println(searchRangeSerachRectangle);
        
        //Udregnet zoomLevel
        //VI HAR 3 ZOOM UDREGNERE, Currentzoomlevel, ZoomLevel (ens værdi) og zoom.
        //currentZoomLevel = getZoomLevel((realMax.getY() - realMin.getY())/2);
        zoomLevel = getZoomLevel((realMax.getY() - realMin.getY())/2);
        System.out.println("Currentzoomlevel: " + zoomLevel);
        

        //Result listen, med de drawables der er inRange
        var resultUNKNOWN = KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeUNKNOWN());
        //System.out.println("leaves added in resultUNKNOWN: " + KdTree.getLeavesAdded());
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
        gc.setLineWidth(1 / Math.sqrt(trans.determinant()));

        // Tegner og farvelægger kortet
        //////////////////////////////////////tegner LANDUSE//////////////////////////////////////

        if(Model.getKDTreeUNKNOWN() != null) {for (Drawable drawable : resultUNKNOWN) {   drawable.drawWayType(gc, WayType.UNKNOWN);}}
        if(Model.getKDTreeRESIDENTIAL() != null){for (Drawable drawable : resultRESIDENTIAL) {drawable.fillWayType(gc, WayType.RESIDENTIAL);}}
        if(Model.getKDTreeFOREST() != null){for (Drawable drawable : resultFOREST) {drawable.fillWayType(gc, WayType.FOREST);}}
        if(Model.getKDTreeMEADOW() != null){for (Drawable drawable : resultMEADOW) {drawable.fillWayType(gc, WayType.MEADOW);}}
        if(Model.getKDTreeCEMETERY() != null){for (Drawable drawable : resultCEMETERY) {drawable.fillWayType(gc, WayType.CEMETERY);}}
        if(Model.getKDTreeFARMYARD() != null){for (Drawable drawable : resultFARMYARD) {drawable.fillWayType(gc, WayType.FARMYARD);}}
        if(Model.getKDTreeCOMMERCIAL() != null){for (Drawable drawable : resultCOMMERCIAL) {drawable.fillWayType(gc, WayType.COMMERCIAL);}}
        if(Model.getKDTreeCONSTRUCTION() != null){for (Drawable drawable : resultCONSTRUCTION) {drawable.fillWayType(gc, WayType.CONSTRUCTION);}}
        if(Model.getKDTreeRETAIL() != null){for (Drawable drawable : resultRETAIL) {drawable.fillWayType(gc, WayType.RETAIL);}}
        if(Model.getKDTreeINDUSTRIAL() != null){for (Drawable drawable : resultINDUSTRIAL) {drawable.fillWayType(gc, WayType.INDUSTRIAL);}}
        if(Model.getKDTreeFARMLAND() != null){for (Drawable drawable : resultFARMLAND) {drawable.fillWayType(gc, WayType.FARMLAND);}}
        if(Model.getKDTreeGRASS() != null){for (Drawable drawable : resultGRASS) {drawable.fillWayType(gc, WayType.GRASS);}}
        if(Model.getKDTreeGREENHOUSE_HORTICULTURE() != null){for (Drawable drawable : resultGREENHOUSE_HORTICULTURE) {drawable.fillWayType(gc, WayType.GREENHOUSE_HORTICULTURE);}}
        if(Model.getKDTreeLANDFILL() != null){for (Drawable drawable : resultLANDFILL) {drawable.fillWayType(gc, WayType.LANDFILL);}}
        if(Model.getKDTreeRAILWAY() != null){for (Drawable drawable : resultRAILWAY) {drawable.fillWayType(gc, WayType.RAILWAY);}}
        if(Model.getKDTreeRELIGIOUS() != null){for (Drawable drawable : resultRELIGIOUS) {drawable.fillWayType(gc, WayType.RELIGIOUS);}}
        if(Model.getKDTreeALLOTMENTS() != null){for (Drawable drawable : resultALLOTMENTS) {drawable.fillWayType(gc, WayType.ALLOTMENTS);}}

        gc.setLineWidth(WayType.BUILDING.getWidth() / Math.sqrt(trans.determinant()));
        if(Model.getKDTreeBUILDING() != null){
            for (Drawable drawable : resultBUILDING) {
                drawable.fillWayType(gc, WayType.BUILDING);
                drawable.drawWayType(gc, WayType.BUILDING);
        }}

        ////////////////Ting vi kunne overveje IKKE at farvelægge ////////////
        /*
        if(Model.getKDTreeVINEYARD() != null){for (Drawable drawable : resultVINEYARD) {drawable.fillWayType(gc, WayType.VINEYARD);}}
        if(Model.getKDTreeBASIN() != null){for (Drawable drawable : resultBASIN) {drawable.fillWayType(gc, WayType.BASIN);}}
        if(Model.getKDTreeORCHARD() != null){for (Drawable drawable : resultORCHARD) {drawable.fillWayType(gc, WayType.ORCHARD);}}
        if(Model.getKDTreeMILITARY() != null){for (Drawable drawable : resultMILITARY) {drawable.fillWayType(gc, WayType.MILITARY);}}
        if(Model.getKDTreePLANT_NURSERY() != null){for (Drawable drawable : resultPLANT_NURSERY) {drawable.fillWayType(gc, WayType.PLANT_NURSERY);}}
        if(Model.getKDTreeQUARRY() != null){for (Drawable drawable : resultQUARRY) {drawable.fillWayType(gc, WayType.QUARRY);}} //Stenbrud
        if(Model.getKDTreeRECREATION_GROUND() != null){for (Drawable drawable : resultRECREATION_GROUND) {drawable.fillWayType(gc, WayType.RECREATION_GROUND);}}//an area of public land that is used for sports and outdoor games.
        if(Model.getKDTreeVILLAGE_GREEN() != null){for (Drawable drawable : resultVILLAGE_GREEN) {drawable.fillWayType(gc, WayType.VILLAGE_GREEN);}}//A village green is a common open area within a village or other settlement
        if(Model.getKDTreeBROWNFIELD() != null){for (Drawable drawable : resultBROWNFIELD) {drawable.fillWayType(gc, WayType.BROWNFIELD);}} //Findes ikke på bornholm
        if(Model.getKDTreeGARAGES() != null){for (Drawable drawable : resultGARAGES) {drawable.fillWayType(gc, WayType.GARAGES);}}
        if(Model.getKDTreeGREENFIELD() != null){for (Drawable drawable : resultGREENFIELD) {drawable.fillWayType(gc, WayType.GREENFIELD);}}
*/


        //////////////////////////////////////tegner HIGHWAY//////////////////////////////////////
        //Her er ét stort if-statement der kun tegner MOTORWAY hvis zoomLevel er over 29.
        if(zoomLevel > 29) {
            gc.setLineWidth(WayType.CONSTRUCTIONWAY.getWidth() / Math.sqrt(trans.determinant()));
            if (Model.getKDTreeCONSTRUCTIONWAY() != null) {
                for (Drawable drawable : resultCONSTRUCTIONWAY) {
                    drawable.drawWayType(gc, WayType.CONSTRUCTIONWAY);
                }
            }

            gc.setLineDashes(1 / Math.sqrt(trans.determinant()) * WayType.BRIDLEWAY.getDashes());
            gc.setLineWidth(WayType.BRIDLEWAY.getWidth() / Math.sqrt(trans.determinant()));
            if (Model.getKDTreeBRIDLEWAY() != null) {
                for (Drawable drawable : resultBRIDLEWAY) {
                    drawable.drawWayType(gc, WayType.BRIDLEWAY);
                }
            }

            gc.setLineDashes(1 / Math.sqrt(trans.determinant()) * WayType.STEPS.getDashes());
            gc.setLineWidth(WayType.STEPS.getWidth() / Math.sqrt(trans.determinant()));
            if (Model.getKDTreeSTEPS() != null) {
                for (Drawable drawable : resultSTEPS) {
                    drawable.drawWayType(gc, WayType.STEPS);
                }
            }

            gc.setLineDashes(1 / Math.sqrt(trans.determinant()) * WayType.FOOTWAY_ANDMORE.getDashes());
            gc.setLineWidth(WayType.FOOTWAY_ANDMORE.getWidth() / Math.sqrt(trans.determinant()));
            if (Model.getKDTreeFOOTWAY_ANDMORE() != null) {
                for (Drawable drawable : resultFOOTWAY_ANDMORE) {
                    drawable.drawWayType(gc, WayType.FOOTWAY_ANDMORE);
                }
            }

            gc.setLineDashes(1 / Math.sqrt(trans.determinant()) * WayType.CYCLEWAY.getDashes());
            gc.setLineWidth(WayType.CYCLEWAY.getWidth() / Math.sqrt(trans.determinant()));
            if (Model.getKDTreeCYCLEWAY() != null) {
                for (Drawable drawable : resultCYCLEWAY) {
                    drawable.drawWayType(gc, WayType.CYCLEWAY);
                }
            }

            gc.setLineDashes(null);
            gc.setLineWidth(WayType.RACEWAY.getWidth() / Math.sqrt(trans.determinant()));
            if (Model.getKDTreeRACEWAY() != null) {
                for (Drawable drawable : resultRACEWAY) {
                    drawable.drawWayType(gc, WayType.RACEWAY);
                }
            }

            gc.setLineDashes(1 / Math.sqrt(trans.determinant()) * WayType.TRACK.getDashes());
            gc.setLineWidth(WayType.TRACK.getWidth() / Math.sqrt(trans.determinant()));
            if (Model.getKDTreeTRACK() != null) {
                for (Drawable drawable : resultTRACK) {
                    drawable.drawWayType(gc, WayType.TRACK);
                }
            }

            gc.setLineDashes(null);
            gc.setLineWidth(WayType.PEDESTRIAN.getWidth() / Math.sqrt(trans.determinant()));
            if (Model.getKDTreePEDESTRIAN() != null) {
                for (Drawable drawable : resultPEDESTRIAN) {
                    drawable.drawWayType(gc, WayType.PEDESTRIAN);
                }
            }

            gc.setLineWidth(WayType.SERVICE.getWidth() / Math.sqrt(trans.determinant()));
            if (Model.getKDTreeSERVICE() != null) {
                for (Drawable drawable : resultSERVICE) {
                    drawable.drawWayType(gc, WayType.SERVICE);
                }
            }

            gc.setLineWidth(WayType.ROAD.getWidth() / Math.sqrt(trans.determinant()));
            if (Model.getKDTreeROAD() != null) {
                for (Drawable drawable : resultROAD) {
                    drawable.drawWayType(gc, WayType.ROAD);
                }
            }

            gc.setLineWidth(WayType.LIVING_STREET.getWidth() / Math.sqrt(trans.determinant()));
            if (Model.getKDTreeLIVING_STREET() != null) {
                for (Drawable drawable : resultLIVING_STREET) {
                    drawable.drawWayType(gc, WayType.LIVING_STREET);
                }
            }

            gc.setLineWidth(WayType.RESIDENTIALWAY.getWidth() / Math.sqrt(trans.determinant()));
            if (Model.getKDTreeRESIDENTIALWAY() != null) {
                for (Drawable drawable : resultRESIDENTIALWAY) {
                    drawable.drawWayType(gc, WayType.RESIDENTIALWAY);
                }
            }

            gc.setLineWidth(WayType.UNCLASSIFIED.getWidth() / Math.sqrt(trans.determinant()));
            if (Model.getKDTreeUNCLASSIFIED() != null) {
                for (Drawable drawable : resultUNCLASSIFIED) {
                    drawable.drawWayType(gc, WayType.UNCLASSIFIED);
                }
            }

            gc.setLineWidth(WayType.TERTIARY.getWidth() / Math.sqrt(trans.determinant()));
            if (Model.getKDTreeTERTIARY() != null) {
                for (Drawable drawable : resultTERTIARY) {
                    drawable.drawWayType(gc, WayType.TERTIARY);
                }
            }

            gc.setLineWidth(WayType.SECONDARY.getWidth() / Math.sqrt(trans.determinant()));
            if (Model.getKDTreeSECONDARY() != null) {
                for (Drawable drawable : resultSECONDARY) {
                    drawable.drawWayType(gc, WayType.SECONDARY);
                }
            }
        }
            gc.setLineWidth(WayType.PRIMARY.getWidth() / Math.sqrt(trans.determinant()));
            if (Model.getKDTreePRIMARY() != null) {
                for (Drawable drawable : resultPRIMARY) {
                    drawable.drawWayType(gc, WayType.PRIMARY);
                }
            }

            gc.setLineWidth(WayType.TRUNK.getWidth() / Math.sqrt(trans.determinant()));
            if (Model.getKDTreeTRUNK() != null) {
                for (Drawable drawable : resultTRUNK) {
                    drawable.drawWayType(gc, WayType.TRUNK);
                }
            }

        gc.setLineWidth(WayType.MOTORWAY.getWidth()/Math.sqrt(trans.determinant()));
        if(Model.getKDTreeMOTORWAY() != null) {for (Drawable drawable : resultMOTORWAY) {drawable.drawWayType(gc, WayType.MOTORWAY);}}

        //Tegner vores debug firkant
        Color rectangleColor = new Color(0.6, 0.2, 0.1, 0.2);
        gc.setFill(rectangleColor);
        gc.setStroke(Color.BLACK);
        gc.fillRect(realMin.getX(), realMin.getY(), realMax.getX() - realMin.getX(), realMax.getY() - realMin.getY());
        gc.stroke();
    }

    private int getZoomLevel(double r) {
        if (r != 0){
            double scale = r / 500;
            zoomLevel =(int) (16 - Math.log(scale) / Math.log(2));
        }
        return zoomLevel;
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
            repaint();

        //try {
        //  repaint();
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
