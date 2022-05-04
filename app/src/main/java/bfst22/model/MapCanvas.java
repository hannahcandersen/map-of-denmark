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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
        repaint();
    }

    //Repaint gør at billedet ikke tegner oveni det forrige billede, således at canvaset hele tiden opdateres.
    public void repaint() { //throws NullPointerException
        //Får punkterne i vinduet og laver dem om til punkter i kortet
        Point2D screenMin = new Point2D(100, 100);
        Point2D screenMax = new Point2D(getWidth() - 100, getHeight() - 100);
        Point2D realMin = mouseToModel(screenMin);
        Point2D realMax = mouseToModel(screenMax);

        //Opretter en Searchrectangle med de koordinater der er inden for rectangle
        RangeSerachRectangle searchRangeSerachRectangle = new RangeSerachRectangle(realMin.getY(), realMax.getY(), realMin.getX(), realMax.getX());
       // System.out.println(searchRangeSerachRectangle);

        //currentZoomLevel = getZoomLevel((realMax.getY() - realMin.getY())/2);
        zoomLevel = getZoomLevel((realMax.getY() - realMin.getY()) / 2);
        System.out.println("Currentzoomlevel: " + zoomLevel);

        //Result listen, med de drawables der er inRange
        var resultUNKNOWN = KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeUNKNOWN());

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
        {
            for (Drawable drawable : Model.getList()) {
                if (zoomLevel<=29){
                    drawable.drawWayType(gc, WayType.COASTLINE);
                    gc.setFill(Color.rgb(238, 240, 216));
                    drawable.fill(gc);}
                if (zoomLevel >= 30) {
                    drawable.drawWayType(gc, WayType.COASTLINE);
                    drawable.fillWayType(gc, WayType.COASTLINE);}}}


        // Tegner og farvelægger kortet
        //Tegner alt UNKNOWN
        /*if (Model.getKDTreeUNKNOWN() != null && KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeUNKNOWN()) != null) {
            for (Drawable drawable : KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeUNKNOWN())) {
                if (drawable.getType() != WayType.UNKNOWN) System.out.println("BUG: " + drawable.getType().toString());
                drawable.drawWayType(gc, WayType.UNKNOWN);}}*/


        //Tegner forest
        if (Model.getKDTreeFOREST() != null && KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeFOREST()) != null) {
            for (Drawable drawable : KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeFOREST())) {
               // if (drawable instanceof MultiPolygon) System.out.println("Multipolygon forest");
                drawable.fillWayType(gc, WayType.FOREST);}}


        // Alt der skal tegnes ved zoom level 29
        if (zoomLevel >= 29){
            if (Model.getKDTreeFARMLAND() != null  && KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeFARMLAND()) != null) {
                for (Drawable drawable : KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeFARMLAND())) {
                    drawable.fillWayType(gc, WayType.FARMLAND);}}

            if (Model.getKDTreeGRASS() != null && KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeGRASS()) != null) {
                for (Drawable drawable : KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeGRASS())) {
                    drawable.fillWayType(gc, WayType.GRASS);}}

            if (Model.getKDTreeRAILWAY() != null && KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeRAILWAY()) != null) {
                for (Drawable drawable : KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeRAILWAY())) {
                    drawable.fillWayType(gc, WayType.RAILWAY);}}

            if (Model.getKDTreeWATER() != null && KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeWATER()) != null) {
                for (Drawable drawable : KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeWATER())) {
                    drawable.fillWayType(gc, WayType.WATER);}}

            if (Model.getKDTreeRIVER_ANDMORE() != null && KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeRIVER_ANDMORE()) != null) {
                for (Drawable drawable : KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeRIVER_ANDMORE())) {
                    drawable.drawWayType(gc, WayType.RIVER_ANDMORE);}}}

        if (Model.getKDTreeFERRY() != null && KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeFERRY()) != null) {
            for (Drawable drawable : KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeFERRY())) {
                gc.setLineWidth(1 / Math.sqrt(trans.determinant()));
                drawable.drawWayType(gc, WayType.FERRY);}}

        if (Model.getKDTreeSECONDARY() != null && KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeSECONDARY()) != null) {
            for (Drawable drawable : KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeSECONDARY())) {
                gc.setLineWidth(WayType.SECONDARY.getWidth() / Math.sqrt(trans.determinant()));
                drawable.drawWayType(gc, WayType.SECONDARY);}}

        // Alt der skal tegnes ved zoom level 30
        if(zoomLevel == 30){
            if(Model.getKDTreeRESIDENTIAL() != null && KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeRESIDENTIAL()) != null)
            {for (Drawable drawable : KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeRESIDENTIAL()))
            {drawable.fillWayType(gc, WayType.RESIDENTIAL);}}

            if(Model.getKDTreeMEADOW() != null && KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeMEADOW()) != null)
            {for (Drawable drawable : KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeMEADOW()))
            {drawable.fillWayType(gc, WayType.MEADOW);}}

            if (Model.getKDTreeGREENHOUSE_HORTICULTURE() != null && KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeGREENHOUSE_HORTICULTURE()) != null) {
                for (Drawable drawable : KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeGREENHOUSE_HORTICULTURE())) {
                    drawable.fillWayType(gc, WayType.GREENHOUSE_HORTICULTURE);}}

            if (Model.getKDTreeRELIGIOUS() != null && KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeRELIGIOUS()) != null) {
                for (Drawable drawable : KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeRELIGIOUS())) {
                    drawable.fillWayType(gc, WayType.RELIGIOUS);}}

            if (Model.getKDTreeGRASS() != null && KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeGRASS()) != null) {
                for (Drawable drawable : KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeGRASS())) {
                    drawable.fillWayType(gc, WayType.GRASS);}}}

        // Alt der skal tegnes ved zoom level 31 og højere
        if (zoomLevel >= 31){
            if(Model.getKDTreeRESIDENTIAL() != null && KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeRESIDENTIAL()) != null)
            {for (Drawable drawable : KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeRESIDENTIAL()))
            {drawable.fillWayType(gc, WayType.RESIDENTIAL);}}

            if(Model.getKDTreeINDUSTRIAL() != null && KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeINDUSTRIAL()) != null)
            {for (Drawable drawable : KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeINDUSTRIAL()))
            {drawable.fillWayType(gc, WayType.INDUSTRIAL);}}

            if(Model.getKDTreeLAKE() != null && KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeLAKE()) != null)
            {for (Drawable drawable : KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeLAKE()))
            {drawable.fillWayType(gc, WayType.LAKE);}}

            if(Model.getKDTreeMEADOW() != null && KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeMEADOW()) != null)
            {for (Drawable drawable : KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeMEADOW()))
            {drawable.fillWayType(gc, WayType.MEADOW);}}

            if(Model.getKDTreeCEMETERY() != null && KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeCEMETERY()) != null)
            {for (Drawable drawable : KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeCEMETERY()))
            {drawable.fillWayType(gc, WayType.CEMETERY);}}

            if(Model.getKDTreeALLOTMENTS() != null && KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeALLOTMENTS()) != null)
            {for (Drawable drawable : KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeALLOTMENTS()))
            {drawable.fillWayType(gc, WayType.ALLOTMENTS);}}

            if(Model.getKDTreeCOMMERCIAL() != null && KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeCOMMERCIAL()) != null)
            {for (Drawable drawable : KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeCOMMERCIAL()))
            {drawable.fillWayType(gc, WayType.COMMERCIAL);}}

            if(Model.getKDTreeCONSTRUCTION() != null && KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeCONSTRUCTION()) != null)
            {for (Drawable drawable : KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeCONSTRUCTION()))
            {drawable.fillWayType(gc, WayType.CONSTRUCTION);}}

            if(Model.getKDTreeRETAIL() != null && KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeRETAIL()) != null)
            {for (Drawable drawable : KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeRETAIL()))
            {drawable.fillWayType(gc, WayType.RETAIL);}}

            if (Model.getKDTreeRELIGIOUS() != null && KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeRELIGIOUS()) != null)
            {for (Drawable drawable : KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeRELIGIOUS()))
            {drawable.fillWayType(gc, WayType.RELIGIOUS);}}

            if (Model.getKDTreePARK() != null && KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreePARK()) != null)
            {for (Drawable drawable : KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreePARK()))
            {drawable.fillWayType(gc, WayType.PARK);}}

            if (Model.getKDTreeSCHOOL() != null && KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeSCHOOL()) != null)
            {for (Drawable drawable : KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeSCHOOL()))
            {drawable.fillWayType(gc, WayType.SCHOOL);}}

            if (Model.getKDTreeBEACH() != null && KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeBEACH()) != null)
            {for (Drawable drawable : KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeBEACH()))
            {drawable.fillWayType(gc, WayType.BEACH);}}

            if (Model.getKDTreeSAND() != null && KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeSAND()) != null)
            {for (Drawable drawable : KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeSAND()))
            {drawable.fillWayType(gc, WayType.SAND);}}


            if (Model.getKDTreeHOSPITAL() != null && KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeHOSPITAL()) != null)
            {for (Drawable drawable : KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeHOSPITAL()))
            {drawable.fillWayType(gc, WayType.HOSPITAL);}}

            if (Model.getKDTreeRECREATION_GROUND() != null && KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeRECREATION_GROUND()) != null)
            {for (Drawable drawable : KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeRECREATION_GROUND()))
            {drawable.fillWayType(gc, WayType.RECREATION_GROUND);}}

            if (Model.getKDTreePITCH() != null && KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreePITCH()) != null)
            {for (Drawable drawable : KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreePITCH()))
            {drawable.fillWayType(gc, WayType.PITCH);}}

            if(Model.getKDTreeLAKE() != null && KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeLAKE()) != null)
            {for (Drawable drawable : KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeLAKE()))
            {drawable.fillWayType(gc, WayType.LAKE);}}

            if (Model.getKDTreeBRIDLEWAY() != null && KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeBRIDLEWAY()) != null) {
                for (Drawable drawable : KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeBRIDLEWAY())) {
                    gc.setLineDashes(1 / Math.sqrt(trans.determinant()) * WayType.BRIDLEWAY.getDashes());
                    gc.setLineWidth(WayType.BRIDLEWAY.getWidth() / Math.sqrt(trans.determinant()));
                    drawable.drawWayType(gc, WayType.BRIDLEWAY);}}

            if (Model.getKDTreeFOOTWAY_ANDMORE() != null && KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeFOOTWAY_ANDMORE()) != null) {
                for (Drawable drawable : KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeFOOTWAY_ANDMORE())) {
                    gc.setLineDashes(1 / Math.sqrt(trans.determinant()) * WayType.FOOTWAY_ANDMORE.getDashes());
                    gc.setLineWidth(WayType.FOOTWAY_ANDMORE.getWidth() / Math.sqrt(trans.determinant()));
                    drawable.drawWayType(gc, WayType.FOOTWAY_ANDMORE);}}

            if (Model.getKDTreeCYCLEWAY() != null && KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeCYCLEWAY()) != null) {
                for (Drawable drawable : KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeCYCLEWAY())) {
                    gc.setLineDashes(1 / Math.sqrt(trans.determinant()) * WayType.CYCLEWAY.getDashes());
                    gc.setLineWidth(WayType.CYCLEWAY.getWidth() / Math.sqrt(trans.determinant()));
                    drawable.drawWayType(gc, WayType.CYCLEWAY);}}

            if (Model.getKDTreeTRACK() != null && KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeTRACK()) != null) {
                for (Drawable drawable : KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeTRACK())) {
                    gc.setLineDashes(1 / Math.sqrt(trans.determinant()) * WayType.TRACK.getDashes());
                    gc.setLineWidth(WayType.TRACK.getWidth() / Math.sqrt(trans.determinant()));
                    drawable.drawWayType(gc, WayType.TRACK);}}

            if (Model.getKDTreePEDESTRIAN() != null && KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreePEDESTRIAN()) != null) {
                for (Drawable drawable : KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreePEDESTRIAN())) {
                    gc.setLineDashes(null);
                    gc.setLineWidth(WayType.PEDESTRIAN.getWidth() / Math.sqrt(trans.determinant()));
                    drawable.drawWayType(gc, WayType.PEDESTRIAN);}}

            if (Model.getKDTreeSERVICE() != null && KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeSERVICE()) != null) {
                for (Drawable drawable : KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeSERVICE())) {
                    gc.setLineWidth(WayType.SERVICE.getWidth() / Math.sqrt(trans.determinant()));
                    drawable.drawWayType(gc, WayType.SERVICE);}}


            if (Model.getKDTreeROAD() != null && KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeROAD()) != null) {
                for (Drawable drawable : KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeROAD())) {
                    gc.setLineWidth(WayType.ROAD.getWidth() / Math.sqrt(trans.determinant()));
                    drawable.drawWayType(gc, WayType.ROAD);}}

            if (Model.getKDTreeLIVING_STREET() != null && KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeLIVING_STREET()) != null) {
                for (Drawable drawable : KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeLIVING_STREET())) {
                    gc.setLineWidth(WayType.LIVING_STREET.getWidth() / Math.sqrt(trans.determinant()));
                    drawable.drawWayType(gc, WayType.LIVING_STREET);}}

            if (Model.getKDTreeRESIDENTIALWAY() != null && KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeRESIDENTIALWAY()) != null) {
                for (Drawable drawable : KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeRESIDENTIALWAY())) {
                    gc.setLineWidth(WayType.RESIDENTIALWAY.getWidth() / Math.sqrt(trans.determinant()));
                    drawable.drawWayType(gc, WayType.RESIDENTIALWAY);}}

            /*if (Model.getKDTreeSECONDARY() != null && KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeSECONDARY()) != null) {
                for (Drawable drawable : KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeSECONDARY())) {
                    gc.setLineWidth(WayType.SECONDARY.getWidth() / Math.sqrt(trans.determinant()));
                    drawable.drawWayType(gc, WayType.SECONDARY);}}*/

            if (Model.getKDTreeTERTIARY() != null) {
                for (Drawable drawable : KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeTERTIARY())) {
                    gc.setLineWidth(WayType.TERTIARY.getWidth() / Math.sqrt(trans.determinant()));
                    drawable.drawWayType(gc, WayType.TERTIARY);}}

            if (Model.getKDTreeGRASS() != null && KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeGRASS()) != null)
            {for (Drawable drawable : KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeGRASS()))
            {drawable.fillWayType(gc, WayType.GRASS);}}

            if(Model.getKDTreeBUILDING() != null && KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeBUILDING()) != null){
                for (Drawable drawable : KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeBUILDING())) {
                    gc.setLineWidth(WayType.BUILDING.getWidth() / Math.sqrt(trans.determinant()));
                    drawable.fillWayType(gc, WayType.BUILDING);
                    drawable.drawWayType(gc, WayType.BUILDING);
                }}}

        //Tegner motorway
        if(Model.getKDTreeMOTORWAY() != null && KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeMOTORWAY()) != null) {
            for (Drawable drawable :  KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreeMOTORWAY()))
            {gc.setLineWidth(WayType.MOTORWAY.getWidth()/Math.sqrt(trans.determinant()));
                drawable.fillWayType(gc, WayType.MOTORWAY);}}

        // Tegner hovedveje
        if (Model.getKDTreePRIMARY() != null && KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreePRIMARY()) != null) {
            for (Drawable drawable : KdTree.rangeSearch(searchRangeSerachRectangle, Model.getKDTreePRIMARY())) {
                gc.setLineWidth(WayType.PRIMARY.getWidth() / Math.sqrt(trans.determinant()));
                drawable.drawWayType(gc, WayType.PRIMARY);
            }}

        //Tegner vores debug firkant
        Color rectangleColor = new Color(0.6, 0.2, 0.1, 0.2);
        gc.setFill(rectangleColor);
        gc.fillRect(realMin.getX(), realMin.getY(), realMax.getX() - realMin.getX(), realMax.getY() - realMin.getY());

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
