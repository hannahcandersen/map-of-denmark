package bfst22.model;

import bfst22.model.dijkstra.DirectedEdge;
import bfst22.model.kdtree.RangeSearchRectangle;
import bfst22.view.Drawable;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import java.util.Random;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;



//Canvas = lærred. Klassen der tegner, panner og zoomer

public class MapCanvas extends Canvas {
    private Model model;
    private Affine trans = new Affine();
    //TODO hvad er det her? *Paint color
    private Paint color = WayType.UNKNOWN.getFillColor();
    private int zoomLevel;
    private OSMNode drawPunkt, drawPunktStartOfRoute, drawPunktEndOfRoute;
    private boolean isPartySelected, isGraphSelected, isDebugSelected;
    private boolean goByCar, goByBike, goByFoot;
    private Iterable<DirectedEdge> dijkstraShortestPath;

    private Point2D screenMin, screenMax;
    private Point2D realscalebarPoint1, realscalebarPoint2;

    //Tilfældige farver
    private int r, g, b;

    private double ration;
    private int scalebarincreaseX = 0;
    private int scalebarincreaseY = 0;



    public void init(Model model) {
        this.model = model;
        //mouseToModel()  -  så får vi hvor den
        pan(-model.getMinLon(), -model.getMinLat());
        zoom(getWidth() / (model.getMaxlon() - model.getMinLon()), 0, 0);
        model.addObserver(this::repaint);
        //repaint();
        //repainter hver gang størrelsen på skærmen ændres
        widthProperty().addListener((observable, oldValue, newValue) -> {
            repaint();

        });
        heightProperty().addListener((observable, oldValue, newValue) -> {
            repaint();
            scalebarincreaseY +=2;
            //System.out.println(scalebarincreaseY);
        });
    }


    //Repaint gør at billedet ikke tegner oveni det forrige billede, således at canvaset hele tiden opdateres.
    public void repaint() { //throws NullPointerException
        //Får punkterne i vinduet og laver dem om til punkter i kortet
        if(isDebugSelected) {
             screenMin = new Point2D(100, 100);
             screenMax = new Point2D(getWidth() - 200, getHeight() - 200);

        } else{
             screenMin = new Point2D(0, 0 );
             screenMax = new Point2D(getWidth(), getHeight());}

        Point2D realMin = mouseToModel(screenMin);
        Point2D realMax = mouseToModel(screenMax);


        //Opretter en Searchrectangle med de koordinater der er inden for rectangle
        RangeSearchRectangle rangeSearchRectangle = new RangeSearchRectangle(realMin.getY(), realMax.getY(), realMin.getX(), realMax.getX());
        //System.out.println(rangeSearchRectangle);




        //  TODO KAN SLETTES?????????
        //Point2D scalebarPoint1 = new Point2D((getWidth()/2) + 170,getHeight()/2 +120 + scalebarincreaseY);
        //Point2D scalebarPoint2 = new Point2D((getWidth()/2)+250, getHeight()/2 +120 + scalebarincreaseY);

        //Point2D scalebarPoint1 = new Point2D((getWidth()/2)  + (getWidth()/4) ,                (getHeight()/2)  + (getHeight()/4));
        //Point2D scalebarPoint2 = new Point2D((getWidth()/2)  + (getWidth()/4) +(getWidth()/6) , (getHeight()/2) + (getHeight()/4));
        ////////////////////////////////////


        //Sætter positionen for hvor vores scalebar skal stå
        Point2D scalebarPoint1 = new Point2D(getWidth()-60, getHeight()-100 );
        Point2D scalebarPoint2 = new Point2D(getWidth()-150 , getHeight()-100);

        realscalebarPoint1 = mouseToModel(scalebarPoint1);
        //System.out.println("Point 1: " + realscalebarPoint1.getX() + realscalebarPoint1.getY());
        realscalebarPoint2 = mouseToModel(scalebarPoint2);
        //System.out.println("Point 2: " + realscalebarPoint2.getX() + realscalebarPoint2.getY());


        //Udregner rationen for hvad en meter (så lang linjen er???) på kortet
        ration = calculateRation(realscalebarPoint1.getX(),realscalebarPoint2.getX());
        //System.out.println("ration: " + ration);


        //Udregnet zoomLevel
        zoomLevel = getZoomLevel((realMax.getY() - realMin.getY()) / 2);
        //System.out.println("Currentzoomlevel: " + zoomLevel);



        //Sætter de generelle indstillinger for GraphicsContext til at vi kan tegne
        var gc = getGraphicsContext2D();
        gc.setTransform(new Affine());



        //Laver baggrunden
        gc.setFill(Color.LIGHTBLUE);
        gc.fillRect(0, 0, getWidth(), getHeight());
        gc.setTransform(trans);
        gc.setLineWidth(1 / Math.sqrt(trans.determinant()));

        //Tegner kystlinjer
        gc.setLineWidth(1 / Math.sqrt(trans.determinant()));
        gc.setFill(Color.rgb(randomR(), randomB(), randomG()));
        for (Drawable drawable : model.getListOfCoastlines()) {
            if (zoomLevel <= 30) {
                if (isPartySelected) {
                    drawable.drawWayType(gc, WayType.COASTLINE);
                    drawable.fill(gc);
                } else {
                    drawable.drawWayType(gc, WayType.COASTLINE);
                    gc.setFill(Color.rgb(238, 240, 216));
                    drawable.fill(gc);
                }
            }
            if (zoomLevel >= 31) {
                if (isPartySelected) {
                    drawable.drawWayType(gc, WayType.COASTLINE);
                    drawable.fill(gc);
                } else {
                    drawable.drawWayType(gc, WayType.COASTLINE);
                    drawable.fillWayType(gc, WayType.COASTLINE);
                }
            }
        }








        //////////////////// TEGNER OG FARVELÆGGER KORTET ////////////////////
        //Tegner alt UNKNOWN
        /*if (Model.getKDTreeUNKNOWN() != null && KdTree.rangeSearch(rangeSearchRectangle, Model.getKDTreeUNKNOWN()) != null) {
            for (Drawable drawable : KdTree.rangeSearch(rangeSearchRectangle, Model.getKDTreeUNKNOWN())) {
                drawable.drawWayType(gc, WayType.UNKNOWN);}}*/



        //////////////////// TEGNER ALTID MULTIPOLYGON_FOREST ////////////////////
        if( model.lines.get(WayType.FOREST) != null){
            for (Drawable drawable: model.lines.get(WayType.FOREST)) {
                if (drawable instanceof MultiPolygon) drawable.fillWayType(gc, WayType.FOREST);
            }
        }

        //////////////////// TEGNER FOREST ////////////////////
        if (model.getKDTreeFOREST() != null && model.getKDTreeFOREST().rangeSearch(rangeSearchRectangle, model.getKDTreeFOREST().getRoot()) != null) {
            gc.setFill(Color.rgb(randomR(), randomB(), randomG()));
            for (Drawable drawable : model.getKDTreeFOREST().rangeSearch(rangeSearchRectangle, model.getKDTreeFOREST().getRoot())) {
                if (isPartySelected) {
                    drawable.fill(gc);
                } else {
                    drawable.fillWayType(gc, WayType.FOREST);
                }
            }
        }


        //////////////////// ZOOM UNDER 26 ////////////////////
        if(zoomLevel <= 26){
        if (model.getKDTreeTERRITORIAl() != null && model.getKDTreeTERRITORIAl().rangeSearch(rangeSearchRectangle, model.getKDTreeTERRITORIAl().getRoot()) != null) {
            gc.setFill(Color.rgb(randomR(), randomB(), randomG()));
            for (Drawable drawable : model.getKDTreeTERRITORIAl().rangeSearch(rangeSearchRectangle, model.getKDTreeTERRITORIAl().getRoot())) {
                if (isPartySelected) {
                    drawable.draw(gc);
                } else {
                    drawable.drawWayType(gc, WayType.TERRITORIAl);
                }
            }
        }}



        ///////////////////// ZOOM OVER 29 ////////////////////
        if (zoomLevel >= 29) {
            if (model.getKDTreeFARMLAND() != null && model.getKDTreeFARMLAND().rangeSearch(rangeSearchRectangle, model.getKDTreeFARMLAND().getRoot()) != null) {
                gc.setFill(Color.rgb(randomR(), randomB(), randomG()));
                for (Drawable drawable : model.getKDTreeFARMLAND().rangeSearch(rangeSearchRectangle, model.getKDTreeFARMLAND().getRoot())) {
                    if (isPartySelected) {
                        drawable.fill(gc);
                    } else {
                        drawable.fillWayType(gc, WayType.FARMLAND);
                    }
                }
            }

            if (model.getKDTreeGRASS() != null && model.getKDTreeGRASS().rangeSearch(rangeSearchRectangle, model.getKDTreeGRASS().getRoot()) != null) {
                gc.setFill(Color.rgb(randomR(), randomB(), randomG()));
                for (Drawable drawable : model.getKDTreeGRASS().rangeSearch(rangeSearchRectangle, model.getKDTreeGRASS().getRoot())) {
                    if (isPartySelected) {
                        drawable.fill(gc);
                    } else {
                        drawable.fillWayType(gc, WayType.GRASS);
                    }
                }
            }

            if (model.getKDTreeBEACH() != null && model.getKDTreeBEACH().rangeSearch(rangeSearchRectangle, model.getKDTreeBEACH().getRoot()) != null) {
                gc.setFill(Color.rgb(randomR(), randomB(), randomG()));
                for (Drawable drawable : model.getKDTreeBEACH().rangeSearch(rangeSearchRectangle, model.getKDTreeBEACH().getRoot())) {
                    if (isPartySelected) {
                        drawable.fill(gc);
                    } else {
                        drawable.fillWayType(gc, WayType.BEACH);
                    }
                }
            }

            if (model.getKDTreeRAILWAY() != null && model.getKDTreeRAILWAY().rangeSearch(rangeSearchRectangle, model.getKDTreeRAILWAY().getRoot()) != null) {
                gc.setFill(Color.rgb(randomR(), randomB(), randomG()));
                for (Drawable drawable : model.getKDTreeRAILWAY().rangeSearch(rangeSearchRectangle, model.getKDTreeRAILWAY().getRoot())) {
                    if (isPartySelected) {
                        drawable.fill(gc);
                    } else {
                        drawable.fillWayType(gc, WayType.RAILWAY);
                    }
                }
            }

            if (model.getKDTreeWATER() != null && model.getKDTreeWATER().rangeSearch(rangeSearchRectangle, model.getKDTreeWATER().getRoot()) != null) {
                gc.setFill(Color.rgb(randomR(), randomB(), randomG()));
                for (Drawable drawable : model.getKDTreeWATER().rangeSearch(rangeSearchRectangle, model.getKDTreeWATER().getRoot())) {
                    if (isPartySelected) {
                        drawable.fill(gc);
                    } else {
                        drawable.fillWayType(gc, WayType.WATER);
                    }
                }
            }

            if (model.getKDTreeRIVER_ANDMORE() != null && model.getKDTreeRIVER_ANDMORE().rangeSearch(rangeSearchRectangle, model.getKDTreeRIVER_ANDMORE().getRoot()) != null) {
                gc.setFill(Color.rgb(randomR(), randomB(), randomG()));
                for (Drawable drawable : model.getKDTreeRIVER_ANDMORE().rangeSearch(rangeSearchRectangle, model.getKDTreeRIVER_ANDMORE().getRoot())) {
                    if (isPartySelected) {
                        drawable.fill(gc);
                    } else {
                        drawable.drawWayType(gc, WayType.RIVER_ANDMORE);
                    }
                }
            }

            if (model.getKDTreeFERRY() != null && model.getKDTreeFERRY().rangeSearch(rangeSearchRectangle, model.getKDTreeFERRY().getRoot()) != null) {
                for (Drawable drawable : model.getKDTreeFERRY().rangeSearch(rangeSearchRectangle, model.getKDTreeFERRY().getRoot())) {
                    gc.setLineWidth(1 / Math.sqrt(trans.determinant()));
                    drawable.drawWayType(gc, WayType.FERRY);
                }
            }

            if (model.getKDTreeSECONDARY() != null && model.getKDTreeSECONDARY().rangeSearch(rangeSearchRectangle, model.getKDTreeSECONDARY().getRoot()) != null) {
                for (Drawable drawable : model.getKDTreeSECONDARY().rangeSearch(rangeSearchRectangle, model.getKDTreeSECONDARY().getRoot())) {
                    gc.setLineWidth(WayType.SECONDARY.getWidth() / Math.sqrt(trans.determinant()));
                    drawable.drawWayType(gc, WayType.SECONDARY);
                }
            }
        }


        //////////////////// ZOOM == 30 ELLER 31 ////////////////////
        if(zoomLevel == 30 || zoomLevel==31) {
            if (model.getKDTreeRESIDENTIAL() != null && model.getKDTreeRESIDENTIAL().rangeSearch(rangeSearchRectangle, model.getKDTreeRESIDENTIAL().getRoot()) != null) {
                gc.setFill(Color.rgb(randomR(), randomB(), randomG()));
                for (Drawable drawable : model.getKDTreeRESIDENTIAL().rangeSearch(rangeSearchRectangle, model.getKDTreeRESIDENTIAL().getRoot())) {
                    if (isPartySelected) {
                        drawable.fill(gc);
                    } else {
                        drawable.fillWayType(gc, WayType.RESIDENTIAL);
                    }
                }
            }

            if (model.getKDTreeMEADOW() != null && model.getKDTreeMEADOW().rangeSearch(rangeSearchRectangle, model.getKDTreeMEADOW().getRoot()) != null) {
                gc.setFill(Color.rgb(randomR(), randomB(), randomG()));
                for (Drawable drawable : model.getKDTreeMEADOW().rangeSearch(rangeSearchRectangle, model.getKDTreeMEADOW().getRoot())) {
                    if (isPartySelected) {
                        drawable.fill(gc);
                    } else {
                        drawable.fillWayType(gc, WayType.MEADOW);
                    }
                }
            }

            if (model.getKDTreeGREENHOUSE_HORTICULTURE() != null && model.getKDTreeGREENHOUSE_HORTICULTURE().rangeSearch(rangeSearchRectangle, model.getKDTreeGREENHOUSE_HORTICULTURE().getRoot()) != null) {
                gc.setFill(Color.rgb(randomR(), randomB(), randomG()));
                for (Drawable drawable : model.getKDTreeGREENHOUSE_HORTICULTURE().rangeSearch(rangeSearchRectangle, model.getKDTreeGREENHOUSE_HORTICULTURE().getRoot())) {
                    if (isPartySelected) {
                        drawable.fill(gc);
                    } else {
                        drawable.fillWayType(gc, WayType.GREENHOUSE_HORTICULTURE);
                    }
                }
            }

            if (model.getKDTreeRELIGIOUS() != null && model.getKDTreeRELIGIOUS().rangeSearch(rangeSearchRectangle, model.getKDTreeRELIGIOUS().getRoot()) != null) {
                gc.setFill(Color.rgb(randomR(), randomB(), randomG()));
                for (Drawable drawable : model.getKDTreeRELIGIOUS().rangeSearch(rangeSearchRectangle, model.getKDTreeRELIGIOUS().getRoot())) {
                    if (isPartySelected) {
                        drawable.fill(gc);
                    } else {
                        drawable.fillWayType(gc, WayType.RELIGIOUS);
                    }
                }
            }

            if (model.getKDTreeSECONDARY() != null && model.getKDTreeSECONDARY().rangeSearch(rangeSearchRectangle, model.getKDTreeSECONDARY().getRoot()) != null) {
                for (Drawable drawable : model.getKDTreeSECONDARY().rangeSearch(rangeSearchRectangle, model.getKDTreeSECONDARY().getRoot())) {
                    gc.setLineWidth(WayType.SECONDARY.getWidth() / Math.sqrt(trans.determinant()));
                    drawable.drawWayType(gc, WayType.SECONDARY);
                }
            }
        }

        //////////////////// ZOOM OVER 31 ////////////////////
        if (zoomLevel >= 31){
            if(model.getKDTreeRESIDENTIAL() != null && model.getKDTreeRESIDENTIAL().rangeSearch(rangeSearchRectangle, model.getKDTreeRESIDENTIAL().getRoot()) != null) {
                gc.setFill(Color.rgb(randomR(), randomB(), randomG()));
                for (Drawable drawable : model.getKDTreeRESIDENTIAL().rangeSearch(rangeSearchRectangle, model.getKDTreeRESIDENTIAL().getRoot())) {
                    if (isPartySelected) {
                        drawable.fill(gc);
                    } else {
                        drawable.fillWayType(gc, WayType.RESIDENTIAL);
                    }
                }
            }

            if(model.getKDTreeINDUSTRIAL() != null && model.getKDTreeINDUSTRIAL().rangeSearch(rangeSearchRectangle, model.getKDTreeINDUSTRIAL().getRoot()) != null) {
                gc.setFill(Color.rgb(randomR(), randomB(), randomG()));
                for (Drawable drawable : model.getKDTreeINDUSTRIAL().rangeSearch(rangeSearchRectangle, model.getKDTreeINDUSTRIAL().getRoot())) {
                    if (isPartySelected) {
                        drawable.fill(gc);
                    } else {
                        drawable.fillWayType(gc, WayType.INDUSTRIAL);
                    }
                }
            }

            if(model.getKDTreeLAKE() != null && model.getKDTreeLAKE().rangeSearch(rangeSearchRectangle, model.getKDTreeLAKE().getRoot()) != null) {
                gc.setFill(Color.rgb(randomR(), randomB(), randomG()));
                for (Drawable drawable : model.getKDTreeLAKE().rangeSearch(rangeSearchRectangle, model.getKDTreeLAKE().getRoot())) {
                    if (isPartySelected) {
                        drawable.fill(gc);
                    } else {
                        drawable.fillWayType(gc, WayType.LAKE);
                    }
                }
            }

            if(model.getKDTreeMEADOW() != null && model.getKDTreeMEADOW().rangeSearch(rangeSearchRectangle, model.getKDTreeMEADOW().getRoot()) != null) {
                gc.setFill(Color.rgb(randomR(), randomB(), randomG()));
                for (Drawable drawable : model.getKDTreeMEADOW().rangeSearch(rangeSearchRectangle, model.getKDTreeMEADOW().getRoot())) {
                    if (isPartySelected) {
                        drawable.fill(gc);
                    } else {
                        drawable.fillWayType(gc, WayType.MEADOW);
                    }
                }
            }

            if(model.getKDTreeCEMETERY() != null && model.getKDTreeCEMETERY().rangeSearch(rangeSearchRectangle, model.getKDTreeCEMETERY().getRoot()) != null) {
                gc.setFill(Color.rgb(randomR(), randomB(), randomG()));
                for (Drawable drawable : model.getKDTreeCEMETERY().rangeSearch(rangeSearchRectangle, model.getKDTreeCEMETERY().getRoot())) {
                    if (isPartySelected) {
                        drawable.fill(gc);
                    } else {
                        drawable.fillWayType(gc, WayType.CEMETERY);
                    }
                }
            }

            if(model.getKDTreeALLOTMENTS() != null && model.getKDTreeALLOTMENTS().rangeSearch(rangeSearchRectangle, model.getKDTreeALLOTMENTS().getRoot()) != null) {
                gc.setFill(Color.rgb(randomR(), randomB(), randomG()));
                for (Drawable drawable : model.getKDTreeALLOTMENTS().rangeSearch(rangeSearchRectangle, model.getKDTreeALLOTMENTS().getRoot())) {
                    if (isPartySelected) {
                        drawable.fill(gc);
                    } else {
                        drawable.fillWayType(gc, WayType.ALLOTMENTS);
                    }
                }
            }

            if(model.getKDTreeCOMMERCIAL() != null && model.getKDTreeCOMMERCIAL().rangeSearch(rangeSearchRectangle, model.getKDTreeCOMMERCIAL().getRoot()) != null) {
                gc.setFill(Color.rgb(randomR(), randomB(), randomG()));
                for (Drawable drawable : model.getKDTreeCOMMERCIAL().rangeSearch(rangeSearchRectangle, model.getKDTreeCOMMERCIAL().getRoot())) {
                    if (isPartySelected) {
                        drawable.fill(gc);
                    } else {
                        drawable.fillWayType(gc, WayType.COMMERCIAL);
                    }
                }
            }

            if(model.getKDTreeCONSTRUCTION() != null && model.getKDTreeCONSTRUCTION().rangeSearch(rangeSearchRectangle, model.getKDTreeCONSTRUCTION().getRoot()) != null) {
                gc.setFill(Color.rgb(randomR(), randomB(), randomG()));
                for (Drawable drawable : model.getKDTreeCONSTRUCTION().rangeSearch(rangeSearchRectangle, model.getKDTreeCONSTRUCTION().getRoot())) {
                    if (isPartySelected) {
                        drawable.fill(gc);
                    } else {
                        drawable.fillWayType(gc, WayType.CONSTRUCTION);
                    }
                }
            }

            if(model.getKDTreeRETAIL() != null && model.getKDTreeRETAIL().rangeSearch(rangeSearchRectangle, model.getKDTreeRETAIL().getRoot()) != null) {
                gc.setFill(Color.rgb(randomR(), randomB(), randomG()));
                for (Drawable drawable : model.getKDTreeRETAIL().rangeSearch(rangeSearchRectangle, model.getKDTreeRETAIL().getRoot())) {
                    if (isPartySelected) {
                        drawable.fill(gc);
                    } else {
                        drawable.fillWayType(gc, WayType.RETAIL);
                    }
                }
            }

            if (model.getKDTreeRELIGIOUS() != null && model.getKDTreeRELIGIOUS().rangeSearch(rangeSearchRectangle, model.getKDTreeRELIGIOUS().getRoot()) != null) {
                gc.setFill(Color.rgb(randomR(), randomB(), randomG()));
                for (Drawable drawable : model.getKDTreeRELIGIOUS().rangeSearch(rangeSearchRectangle, model.getKDTreeRELIGIOUS().getRoot())) {
                    if (isPartySelected) {
                        drawable.fill(gc);
                    } else {
                        drawable.fillWayType(gc, WayType.RELIGIOUS);
                    }
                }
            }

            if (model.getKDTreePARK() != null && model.getKDTreePARK().rangeSearch(rangeSearchRectangle, model.getKDTreePARK().getRoot()) != null) {
                gc.setFill(Color.rgb(randomR(), randomB(), randomG()));
                for (Drawable drawable : model.getKDTreePARK().rangeSearch(rangeSearchRectangle, model.getKDTreePARK().getRoot())) {
                    if (isPartySelected) {
                        drawable.fill(gc);
                    } else {
                        drawable.fillWayType(gc, WayType.PARK);
                    }
                }
            }

            if (model.getKDTreeSCHOOL() != null && model.getKDTreeSCHOOL().rangeSearch(rangeSearchRectangle, model.getKDTreeSCHOOL().getRoot()) != null) {
                gc.setFill(Color.rgb(randomR(), randomB(), randomG()));
                for (Drawable drawable : model.getKDTreeSCHOOL().rangeSearch(rangeSearchRectangle, model.getKDTreeSCHOOL().getRoot())) {
                    if (isPartySelected) {
                        drawable.fill(gc);
                    } else {
                        drawable.fillWayType(gc, WayType.SCHOOL);
                    }
                }
            }

            if (model.getKDTreeSAND() != null && model.getKDTreeSAND().rangeSearch(rangeSearchRectangle, model.getKDTreeSAND().getRoot()) != null) {
                gc.setFill(Color.rgb(randomR(), randomB(), randomG()));
                for (Drawable drawable : model.getKDTreeSAND().rangeSearch(rangeSearchRectangle, model.getKDTreeSAND().getRoot())) {
                    if (isPartySelected) {
                        drawable.fill(gc);
                    } else {
                        drawable.fillWayType(gc, WayType.SAND);
                    }
                }
            }

            if (model.getKDTreeHOSPITAL() != null && model.getKDTreeHOSPITAL().rangeSearch(rangeSearchRectangle, model.getKDTreeHOSPITAL().getRoot()) != null) {
                gc.setFill(Color.rgb(randomR(), randomB(), randomG()));
                for (Drawable drawable : model.getKDTreeHOSPITAL().rangeSearch(rangeSearchRectangle, model.getKDTreeHOSPITAL().getRoot())) {
                    if (isPartySelected) {
                        drawable.fill(gc);
                    } else {
                        drawable.fillWayType(gc, WayType.HOSPITAL);
                    }
                }
            }

            if (model.getKDTreeRECREATION_GROUND() != null && model.getKDTreeRECREATION_GROUND().rangeSearch(rangeSearchRectangle, model.getKDTreeRECREATION_GROUND().getRoot()) != null) {
                gc.setFill(Color.rgb(randomR(), randomB(), randomG()));
                for (Drawable drawable : model.getKDTreeRECREATION_GROUND().rangeSearch(rangeSearchRectangle, model.getKDTreeRECREATION_GROUND().getRoot())) {
                    if (isPartySelected) {
                        drawable.fill(gc);
                    } else {
                        drawable.fillWayType(gc, WayType.RECREATION_GROUND);
                    }
                }
            }

            if (model.getKDTreePITCH() != null && model.getKDTreePITCH().rangeSearch(rangeSearchRectangle, model.getKDTreePITCH().getRoot()) != null) {
                gc.setFill(Color.rgb(randomR(), randomB(), randomG()));
                for (Drawable drawable : model.getKDTreePITCH().rangeSearch(rangeSearchRectangle, model.getKDTreePITCH().getRoot())) {
                    if (isPartySelected) {
                        drawable.fill(gc);
                    } else {
                        drawable.fillWayType(gc, WayType.PITCH);
                    }
                }
            }

            if (model.getKDTreeBUILDING() != null && model.getKDTreeBUILDING().rangeSearch(rangeSearchRectangle, model.getKDTreeBUILDING().getRoot()) != null) {
                gc.setFill(Color.rgb(randomR(), randomB(), randomG()));
                for (Drawable drawable : model.getKDTreeBUILDING().rangeSearch(rangeSearchRectangle, model.getKDTreeBUILDING().getRoot())) {
                    gc.setLineWidth(WayType.BUILDING.getWidth() / Math.sqrt(trans.determinant()));
                    if (isPartySelected) {
                        drawable.fill(gc);
                        gc.setLineWidth(WayType.BUILDING.getWidth() / Math.sqrt(trans.determinant()));
                        drawable.drawWayType(gc, WayType.BUILDING);
                    } else {
                        drawable.fillWayType(gc, WayType.BUILDING);
                        gc.setLineWidth(WayType.BUILDING.getWidth() / Math.sqrt(trans.determinant()));
                        drawable.drawWayType(gc, WayType.BUILDING);
                    }
                }
            }


            if (model.getKDTreeBRIDLEWAY() != null && model.getKDTreeBRIDLEWAY().rangeSearch(rangeSearchRectangle, model.getKDTreeBRIDLEWAY().getRoot()) != null) {
                for (Drawable drawable : model.getKDTreeBRIDLEWAY().rangeSearch(rangeSearchRectangle, model.getKDTreeBRIDLEWAY().getRoot())) {
                    gc.setLineDashes(1 / Math.sqrt(trans.determinant()) * WayType.BRIDLEWAY.getDashes());
                    gc.setLineWidth(WayType.BRIDLEWAY.getWidth() / Math.sqrt(trans.determinant()));
                    drawable.drawWayType(gc, WayType.BRIDLEWAY);
                }
            }

            if (model.getKDTreeFOOTWAY_ANDMORE() != null && model.getKDTreeFOOTWAY_ANDMORE().rangeSearch(rangeSearchRectangle, model.getKDTreeFOOTWAY_ANDMORE().getRoot()) != null) {
                for (Drawable drawable : model.getKDTreeFOOTWAY_ANDMORE().rangeSearch(rangeSearchRectangle, model.getKDTreeFOOTWAY_ANDMORE().getRoot())) {
                    gc.setLineDashes(1 / Math.sqrt(trans.determinant()) * WayType.FOOTWAY_ANDMORE.getDashes());
                    gc.setLineWidth(WayType.FOOTWAY_ANDMORE.getWidth() / Math.sqrt(trans.determinant()));
                    drawable.drawWayType(gc, WayType.FOOTWAY_ANDMORE);
                }
                gc.setLineDashes(null);
            }

            if (model.getKDTreeCYCLEWAY() != null && model.getKDTreeCYCLEWAY().rangeSearch(rangeSearchRectangle, model.getKDTreeCYCLEWAY().getRoot()) != null) {
                for (Drawable drawable : model.getKDTreeCYCLEWAY().rangeSearch(rangeSearchRectangle, model.getKDTreeCYCLEWAY().getRoot())) {
                    gc.setLineDashes(1 / Math.sqrt(trans.determinant()) * WayType.CYCLEWAY.getDashes());
                    gc.setLineWidth(WayType.CYCLEWAY.getWidth() / Math.sqrt(trans.determinant()));
                    drawable.drawWayType(gc, WayType.CYCLEWAY);
                }
                gc.setLineDashes(null);
            }

            if (model.getKDTreePEDESTRIAN() != null && model.getKDTreePEDESTRIAN().rangeSearch(rangeSearchRectangle, model.getKDTreePEDESTRIAN().getRoot()) != null) {
                for (Drawable drawable : model.getKDTreePEDESTRIAN().rangeSearch(rangeSearchRectangle, model.getKDTreePEDESTRIAN().getRoot())) {
                    gc.setLineWidth(WayType.PEDESTRIAN.getWidth() / Math.sqrt(trans.determinant()));
                    drawable.drawWayType(gc, WayType.PEDESTRIAN);
                }
            }

            if (model.getKDTreeSERVICE() != null && model.getKDTreeSERVICE().rangeSearch(rangeSearchRectangle, model.getKDTreeSERVICE().getRoot()) != null) {
                for (Drawable drawable : model.getKDTreeSERVICE().rangeSearch(rangeSearchRectangle, model.getKDTreeSERVICE().getRoot())) {
                    gc.setLineWidth(WayType.SERVICE.getWidth() / Math.sqrt(trans.determinant()));
                    drawable.drawWayType(gc, WayType.SERVICE);
                }
            }

            if (model.getKDTreeROAD() != null && model.getKDTreeROAD().rangeSearch(rangeSearchRectangle, model.getKDTreeROAD().getRoot()) != null) {
                for (Drawable drawable : model.getKDTreeROAD().rangeSearch(rangeSearchRectangle, model.getKDTreeROAD().getRoot())) {
                    gc.setLineWidth(WayType.ROAD.getWidth() / Math.sqrt(trans.determinant()));
                    drawable.drawWayType(gc, WayType.ROAD);
                }
            }

            if (model.getKDTreeLIVING_STREET() != null && model.getKDTreeLIVING_STREET().rangeSearch(rangeSearchRectangle, model.getKDTreeLIVING_STREET().getRoot()) != null) {
                for (Drawable drawable : model.getKDTreeLIVING_STREET().rangeSearch(rangeSearchRectangle, model.getKDTreeLIVING_STREET().getRoot())) {
                    gc.setLineWidth(WayType.LIVING_STREET.getWidth() / Math.sqrt(trans.determinant()));
                    drawable.drawWayType(gc, WayType.LIVING_STREET);
                }
            }

            if (model.getKDTreeRESIDENTIALWAY() != null && model.getKDTreeRESIDENTIALWAY().rangeSearch(rangeSearchRectangle, model.getKDTreeRESIDENTIALWAY().getRoot()) != null) {
                for (Drawable drawable : model.getKDTreeRESIDENTIALWAY().rangeSearch(rangeSearchRectangle, model.getKDTreeRESIDENTIALWAY().getRoot())) {
                    gc.setLineWidth(WayType.RESIDENTIALWAY.getWidth() / Math.sqrt(trans.determinant()));
                    drawable.drawWayType(gc, WayType.RESIDENTIALWAY);
                }
            }

            if (model.getKDTreeSECONDARY() != null && model.getKDTreeSECONDARY().rangeSearch(rangeSearchRectangle, model.getKDTreeSECONDARY().getRoot()) != null) {
                for (Drawable drawable : model.getKDTreeSECONDARY().rangeSearch(rangeSearchRectangle, model.getKDTreeSECONDARY().getRoot())) {
                    gc.setLineWidth(WayType.SECONDARY.getWidth() / Math.sqrt(trans.determinant()));
                    drawable.drawWayType(gc, WayType.SECONDARY);
                }
            }

            if (model.getKDTreeTERTIARY() != null && model.getKDTreeTERTIARY().rangeSearch(rangeSearchRectangle, model.getKDTreeTERTIARY().getRoot()) != null) {
                for (Drawable drawable : model.getKDTreeTERTIARY().rangeSearch(rangeSearchRectangle, model.getKDTreeTERTIARY().getRoot())) {
                    gc.setLineWidth(WayType.TERTIARY.getWidth() / Math.sqrt(trans.determinant()));
                    drawable.drawWayType(gc, WayType.TERTIARY);
                }
            }

            if (model.getKDTreeGRAVEYARD() != null && model.getKDTreeGRAVEYARD().rangeSearch(rangeSearchRectangle, model.getKDTreeGRAVEYARD().getRoot()) != null) {
                gc.setFill(Color.rgb(randomR(), randomB(), randomG()));
                for (Drawable drawable : model.getKDTreeGRAVEYARD().rangeSearch(rangeSearchRectangle, model.getKDTreeGRAVEYARD().getRoot())) {
                    if (isPartySelected) {
                        drawable.fill(gc);
                    } else {
                        drawable.fillWayType(gc, WayType.GRAVEYARD);
                    }
                }
            }

            //////// HER VAR BYGNINGER FØR /////////////

        } //If zoomlevel slutter


        //////////////////// Tegner motorway - tegnes altid øverst ////////////////////
        if (model.getKDTreeMOTORWAY() != null && model.getKDTreeMOTORWAY().rangeSearch(rangeSearchRectangle, model.getKDTreeMOTORWAY().getRoot()) != null) {
            for (Drawable drawable : model.getKDTreeMOTORWAY().rangeSearch(rangeSearchRectangle, model.getKDTreeMOTORWAY().getRoot())) {
                gc.setLineWidth(WayType.MOTORWAY.getWidth() / Math.sqrt(trans.determinant()));
                drawable.drawWayType(gc, WayType.MOTORWAY);
            }
        }

        //////////////////// Tegner hovedveje - tegnes altid øverst ////////////////////
        if (model.getKDTreePRIMARY() != null && model.getKDTreePRIMARY().rangeSearch(rangeSearchRectangle, model.getKDTreePRIMARY().getRoot()) != null) {
            for (Drawable drawable : model.getKDTreePRIMARY().rangeSearch(rangeSearchRectangle, model.getKDTreePRIMARY().getRoot())) {
                gc.setLineWidth(WayType.PRIMARY.getWidth() / Math.sqrt(trans.determinant()));
                drawable.drawWayType(gc, WayType.PRIMARY);
            }
        }


        //////////////////// TEGNER GRAFEN - TIL DEBUG MODE ////////////////////
        if(isGraphSelected) {
            Color graphColor = new Color(0.2, 0.2, 0.6, 0.2);
            for (Drawable drawable : model.getListOfAllEdgesInWaysAllWays()) {
                gc.setLineWidth(2 / Math.sqrt(trans.determinant()));
                gc.setStroke(graphColor);
                drawable.draw(gc);
            }
        }

        //////////////////// TEGNER VORES DEBUG FIRKANT - TIL DEBUG MODE ////////////////////
        if(isDebugSelected) {
            Color rectangleColor = new Color(0.6, 0.2, 0.1, 0.2);
            gc.setFill(rectangleColor);
            gc.fillRect(realMin.getX(), realMin.getY(), realMax.getX() - realMin.getX(), realMax.getY() - realMin.getY());
        }

        //////////////////// TEGNER NEAREST NODE NÅR MAN DOUBLE KLIKKER ////////////////////
        if(drawPunkt != null){
            gc.setFill(Color.RED);
            gc.fillRect(drawPunkt.getLat(), drawPunkt.getLon(), 0.0002, 0.0002);
            gc.fillRect(drawPunkt.getLon(), drawPunkt.getLat(), 0.0002, 0.0002);}


        //////////////////// TEGNER SHORTEST PATH ////////////////////
        if(goByCar || goByBike || goByFoot) {
            if (dijkstraShortestPath != null) {
                for (Drawable drawable : dijkstraShortestPath) {
                    gc.setLineWidth(2 / Math.sqrt(trans.determinant()));
                    gc.setStroke(Color.RED);
                    drawable.draw(gc);
                }
                //System.out.println("Jeg har tegnet Dijkstra");
            }}



        //////////////////// TODO KAN SLETTES??
       /* if(scalebarPoint1 != null){
            gc.setFill(Color.HOTPINK);
            gc.fillOval(realscalebarPoint1.getX(), realscalebarPoint1.getY(), 10/Math.sqrt(trans.determinant()), 10/Math.sqrt(trans.determinant()));
        }*/


        //////////////////// TEGNER LINJEN OG CIRKLERNE PÅ GRAPHICAL SCALEBAR ////////////////////
        drawCenteredCircle(gc,realscalebarPoint1.getX(), realscalebarPoint1.getY(), 5/Math.sqrt(trans.determinant()/2) );
        drawCenteredCircle(gc,realscalebarPoint2.getX(), realscalebarPoint2.getY(), 5/Math.sqrt(trans.determinant()/2) );

        Line ScalebarLine = new Line(realscalebarPoint1, realscalebarPoint2);
        gc.setStroke(Color.HOTPINK);
        gc.setLineWidth(1/Math.sqrt(trans.determinant()));
        ScalebarLine.draw(gc);


        ///////////////////// TEGNER START OG END NODE AF EN RUTE ////////////////////
        if(drawPunktStartOfRoute != null){
            gc.setFill(Color.RED);
            gc.fillRect(drawPunktStartOfRoute.getLat(), drawPunktStartOfRoute.getLon(), 0.00002, 0.00002);
            gc.fillRect(drawPunktStartOfRoute.getLon(), drawPunktStartOfRoute.getLat(), 0.00002, 0.00002);
            //System.out.println("Tegner drawPunktStartOfRoute");
            }

        if(drawPunktEndOfRoute != null){
            gc.setFill(Color.BLUE);
            gc.fillRect(drawPunktEndOfRoute.getLat(), drawPunktEndOfRoute.getLon(), 0.00002, 0.00002);
            gc.fillRect(drawPunktEndOfRoute.getLon(), drawPunktEndOfRoute.getLat(), 0.00002, 0.00002);
            //System.out.println("Tegner drawPunktEndOfRoute");
            }


        //////////////////// TODO KAN SLETTES??
        /*gc.setFill(Color.YELLOW);
        gc.fillRect(realscalebarPoint1.getX(), realscalebarPoint1.getY(), 0.02, 0.02);
        gc.fillRect(realscalebarPoint2.getX(), realscalebarPoint2.getY(), 0.02, 0.02);*/
    }

    //////////////////// TODO SKRIV KOMMENTAR (KATRINE?)
    public double calculateRation(double x1, double x2){
                double distX = x2-x1;
                return 10/distX; //EVT bytte om
    }

    //////////////////// TODO SKRIV KOMMENTAR (KATRINE?)
    public void drawCenteredCircle(GraphicsContext g, double x, double y, double r) {
        x = x-(r/2);
        y = y-(r/2);
        g.setFill(Color.HOTPINK);
        g.fillOval(x,y,r,r);
    }

    //Kilde for formel at regne distance: https://www.geeksforgeeks.org/program-distance-two-points-earth/
    // The math module contains a function named toRadians which converts from degrees to radians.
   public double calculateDistBetween2Points(Point2D punkt1, Point2D punkt2) {
       double lon1 = Math.toRadians(punkt1.getY());
       double lon2 = Math.toRadians(punkt2.getY());
       double lat1 = Math.toRadians(punkt1.getX());
       double lat2 = Math.toRadians(punkt2.getX());
       //System.out.println("lon1: " + punkt1.getLon() + " lat1: " + punkt1.getLat());
       //System.out.println("lon2: " + punkt2.getLon() + " lat2: " + punkt2.getLat());
       //System.out.println("lon1RAD: " + lon1 + " lat1RAD: " + lat1);

       // Haversine formula
       double dlon = lon2 - lon1;
       double dlat = lat2 - lat1;
       double a = Math.pow(Math.sin(dlat / 2), 2)
               + Math.cos(lat1) * Math.cos(lat2)
               * Math.pow(Math.sin(dlon / 2), 2);

       double c = 2 * Math.asin(Math.sqrt(a));

       // Radius of earth in kilometers. Use 3956 for miles
       double r = 6371;

       // calculate the result
       return (c * r)*1000;
   }




    //////////////////// GENERELLE METODER SOM PAN OG ZOOM FX. ////////////////////
    public void pan(double dx, double dy) {
        trans.prependTranslation(dx, dy);
            repaint();
    }

    // Zoomer med en given faktor
    public void zoom(double factor, double x, double y) {
            trans.prependTranslation(-x, -y);
            trans.prependScale(factor, factor);
            trans.prependTranslation(x, y);
            //ration = calculateRation();
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

    //Sætter random farver
    public int randomR(){
        //Random c
        Random rand = new Random();
        int upperbound = 255;
        // Java 'Color' class takes 3 floats, from 0 to 1.
        this.r = Math.abs(rand.nextInt(upperbound));
        return r;
    }

    public int randomG(){
        //Random c
        Random rand = new Random();
        int upperbound = 255;
        // Java 'Color' class takes 3 floats, from 0 to 1.
        this.g = Math.abs(rand.nextInt(upperbound));
        return g;
    }

    public int randomB(){
        //Random c
        Random rand = new Random();
        int upperbound = 255;
        // Java 'Color' class takes 3 floats, from 0 to 1.
        this.b = Math.abs(rand.nextInt(upperbound));
        return b;
    }



    //////////////////// GETTERS ////////////////////
    public Model getModel() {
        return model;
    }
    private int getZoomLevel(double r) {
        if (r != 0){
            double scale = r / 500;
            zoomLevel =(int) (16 - Math.log(scale) / Math.log(2));
        }
        return zoomLevel;
    }
    public Iterable<DirectedEdge> getDijkstraShortestPath() {
        return dijkstraShortestPath;
    }
    public String getRationToString(){
        return String.valueOf(this.ration);
    }
    public String getDistToString(){
        //System.out.println("Dist: " + String.valueOf(calculateDistBetween2Points(realscalebarPoint1, realscalebarPoint2)));
        return String.valueOf(calculateDistBetween2Points(realscalebarPoint1, realscalebarPoint2));
    }
    public Point2D getRealscalebarPoint1() {
        return realscalebarPoint1;
    }
    public Point2D getRealscalebarPoint2() {
        return realscalebarPoint2;
    }



    //////////////////// SETTERS ////////////////////
    public void setDijkstraShortestPath(Iterable<DirectedEdge> dijkstraShortestPath) {this.dijkstraShortestPath = dijkstraShortestPath;}
    public void setDrawPunkt(OSMNode drawPunkt) {
        this.drawPunkt = drawPunkt;
    }
    public void setDrawPunktStartOfRoute(OSMNode drawPunkt) {
        this.drawPunktStartOfRoute = drawPunkt;
    }
    public void setDrawPunktEndOfRoute(OSMNode drawPunkt) {
        this.drawPunktEndOfRoute = drawPunkt;
    }
    public void setPartySelected(boolean partySelected) {
        isPartySelected = partySelected;
    }
    public void setGraphSelected(boolean graphSelected) {
        isGraphSelected = graphSelected;
    }
    public void setDebugSelected(boolean debugSelected) {
        isDebugSelected = debugSelected;
    }
    public void setGoByCar(boolean goByCar) {
        this.goByCar = goByCar;
    }
    public void setGoByBike(boolean goByBike) {
        this.goByBike = goByBike;
    }
    public void setGoByFoot(boolean goByFoot) {
        this.goByFoot = goByFoot;
    }
}
