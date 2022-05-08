package bfst22.model;

import bfst22.model.kdtree.RangeSerachRectangle;
import bfst22.view.Drawable;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import java.util.Random;

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
    public OSMNode TestdrawPunkt;
    private OSMNode drawPunkt;
    public boolean isPartySelected;
    public boolean isPinkSelected;

    //Randomme farver
    int r;
    int g;
    int b;




    public void init(Model model) {
        this.model = model;
        pan(-model.getMinLon(), -model.getMinLat());
        //tallet 640 bestemmer hvor tæt eller hvor langt fra vi er på kortet, når programmet starter
        zoom(640 / (model.getMaxlon() - model.getMinLon()), 0, 0);
        model.addObserver(this::repaint);
        repaint();
        //repainter hver gang størrelsen på skærmen ændres
        widthProperty().addListener((observable, oldValue, newValue) -> {
            repaint();
        });
        heightProperty().addListener((observable, oldValue, newValue) -> {
            repaint();
        });
    }

    //Repaint gør at billedet ikke tegner oveni det forrige billede, således at canvaset hele tiden opdateres.
    public void repaint() { //throws NullPointerException
        //Får punkterne i vinduet og laver dem om til punkter i kortet
        Point2D screenMin = new Point2D(100, 100);
        Point2D screenMax = new Point2D(getWidth() - 100, getHeight() - 100);
        Point2D realMin = mouseToModel(screenMin);
        Point2D realMax = mouseToModel(screenMax);

        //Opretter en Searchrectangle med de koordinater der er inden for rectangle
        RangeSerachRectangle rangeSearchRectangle = new RangeSerachRectangle(realMin.getY(), realMax.getY(), realMin.getX(), realMax.getX());
        //System.out.println(rangeSearchRectangle);
        //System.out.println(rangeSearchRectangle);

        //Udregnet zoomLevel
        zoomLevel = getZoomLevel((realMax.getY() - realMin.getY()) / 2);
        //System.out.println("Currentzoomlevel: " + zoomLevel);


        //Result listen, med de drawables der er inRange
        //var resultUNKNOWN = model.getKDTreeUNKNOWN().rangeSearch(rangeSearchRectangle, model.getKDTreeUNKNOWN().getRoot());

        //Sætter de generelle indstillinger for MapCanvas
        var gc = getGraphicsContext2D();
        gc.setTransform(new Affine());

        //Laver baggrunden
        gc.setFill(Color.LIGHTBLUE);
        gc.fillRect(0, 0, getWidth(), getHeight());
        gc.setTransform(trans);
        gc.setLineWidth(1 / Math.sqrt(trans.determinant()));

        //Tegner kystlinjer
        gc.setLineWidth(1 / Math.sqrt(trans.determinant()));
        //{
        gc.setFill(Color.rgb(randomR(), randomB(), randomG()));
        for (Drawable drawable : model.getListOfCoastlines()) {
            if (zoomLevel <= 29) {
                if (isPartySelected) {
                    drawable.drawWayType(gc, WayType.COASTLINE);
                    drawable.fill(gc);
                } else {
                    drawable.drawWayType(gc, WayType.COASTLINE);
                    gc.setFill(Color.rgb(238, 240, 216));
                    drawable.fill(gc);
                }
            }
            if (zoomLevel >= 30) {
                if (isPartySelected) {
                    drawable.drawWayType(gc, WayType.COASTLINE);
                    drawable.fill(gc);
                } else {
                    drawable.drawWayType(gc, WayType.COASTLINE);
                    drawable.fillWayType(gc, WayType.COASTLINE);
                }
            }
        }
        // }


        // Tegner og farvelægger kortet
        //Tegner alt UNKNOWN
        /*if (model.getKDTreeUNKNOWN() != null && model.getKDTreeUNKNOWN().rangeSearch(rangeSearchRectangle, model.getKDTreeUNKNOWN().getRoot()) != null) {
            for (Drawable drawable : model.getKDTreeUNKNOWN().rangeSearch(rangeSearchRectangle, model.getKDTreeUNKNOWN().getRoot())) {
                drawable.drawWayType(gc, WayType.UNKNOWN);}}*/

        //Tegner forest
        if (model.getKDTreeFOREST() != null && model.getKDTreeFOREST().rangeSearch(rangeSearchRectangle, model.getKDTreeFOREST().getRoot()) != null) {
            gc.setFill(Color.rgb(randomR(), randomB(), randomG()));
            for (Drawable drawable : model.getKDTreeFOREST().rangeSearch(rangeSearchRectangle, model.getKDTreeFOREST().getRoot())) {
                if (isPartySelected) {
                    drawable.fill(gc);
                } else {
                    //if (drawable instanceof MultiPolygon) System.out.println("Multipolygon forest");
                    drawable.fillWayType(gc, WayType.FOREST);
                }
            }
        }

        // Alt der skal tegnes ved zoom level 29
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

        // Alt der skal tegnes ved zoom level 30
        if(zoomLevel == 30) {
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
        }

        // Alt der skal tegnes ved zoom level 31 og højere
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

            if (model.getKDTreeBUILDING() != null && model.getKDTreeBUILDING().rangeSearch(rangeSearchRectangle, model.getKDTreeBUILDING().getRoot()) != null) {
                gc.setFill(Color.rgb(randomR(), randomB(), randomG()));
                for (Drawable drawable : model.getKDTreeBUILDING().rangeSearch(rangeSearchRectangle, model.getKDTreeBUILDING().getRoot())) {
                    gc.setLineWidth(WayType.BUILDING.getWidth() / Math.sqrt(trans.determinant()));
                    if (isPartySelected) {
                        drawable.fill(gc);
                        drawable.drawWayType(gc, WayType.BUILDING);
                    } else {
                        drawable.fillWayType(gc, WayType.BUILDING);
                        drawable.drawWayType(gc, WayType.BUILDING);
                    }
                }
            }
        }

        //Tegner motorway - tegnes altid øverst
        if (model.getKDTreeMOTORWAY() != null && model.getKDTreeMOTORWAY().rangeSearch(rangeSearchRectangle, model.getKDTreeMOTORWAY().getRoot()) != null) {
            for (Drawable drawable : model.getKDTreeMOTORWAY().rangeSearch(rangeSearchRectangle, model.getKDTreeMOTORWAY().getRoot())) {
                gc.setLineWidth(WayType.MOTORWAY.getWidth() / Math.sqrt(trans.determinant()));
                drawable.fillWayType(gc, WayType.MOTORWAY);
            }
        }

        // Tegner hovedveje - tegnes altid øverst
        if (model.getKDTreePRIMARY() != null && model.getKDTreePRIMARY().rangeSearch(rangeSearchRectangle, model.getKDTreePRIMARY().getRoot()) != null) {
            for (Drawable drawable : model.getKDTreePRIMARY().rangeSearch(rangeSearchRectangle, model.getKDTreePRIMARY().getRoot())) {
                gc.setLineWidth(WayType.PRIMARY.getWidth() / Math.sqrt(trans.determinant()));
                drawable.drawWayType(gc, WayType.PRIMARY);
            }
        }





        //// TEST AF DIJKSTRA //////
/*
        // Tager tilfældige nodes fra grafen - TIL TEST - VIRKER IKKE
        OSMNode startOSM = Model.getListOfAllEdgesInWays().get(0).getStart();
        int start = Model.getMapOfOSMNodesToIndex().get(startOSM);
        OSMNode endOSM = Model.getListOfAllEdgesInWays().get(10).getEnd();
        int end = Model.getMapOfOSMNodesToIndex().get(endOSM);

        // Det her kald der fucker
        Dijkstra dijkstra = new Dijkstra(Model.getGraph(), start, end);


        //HER SKER FEJLEN, FORDI VI HAR VALGT EN ENDNODE DER IKKE FINDES EN ROUTE TIL
        for (Drawable drawable : dijkstra.pathTo(Model.getMapOfOSMNodesToIndex().get(endOSM))) {
            gc.setStroke(Color.RED);
            drawable.draw(gc);
        }*/





        //Tegner vores debug firkant
        Color rectangleColor = new Color(0.6, 0.2, 0.1, 0.2);
        gc.setFill(rectangleColor);
        gc.fillRect(realMin.getX(), realMin.getY(), realMax.getX() - realMin.getX(), realMax.getY() - realMin.getY());



        if(drawPunkt != null){
            gc.setFill(Color.RED);
            gc.fillRect(drawPunkt.getLat(), drawPunkt.getLon(), 0.0002, 0.0002);
            gc.fillRect(drawPunkt.getLon(), drawPunkt.getLat(), 0.0002, 0.0002);}

    }

    public Model getModel() {
        return model;
    }

    public void setDrawPunkt(OSMNode drawPunkt) {
        this.drawPunkt = drawPunkt;
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

}
