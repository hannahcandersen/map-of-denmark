package bfst22.model.kdtree;

import bfst22.view.Drawable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.transform.Affine;

public class RangeSerachRectangle extends Canvas {
    private double minLon, maxLon, minLat, maxLat;

    public RangeSerachRectangle(double minLon, double maxLon, double minLat, double maxLat) {
        this.minLon = minLon;
        this.maxLon = maxLon;
        this.minLat = minLat;
        this.maxLat = maxLat;
    }

    public double getMinLon() {
        return minLon;
    }

    public double getMinLat() {
        return minLat;
    }

    public double getMaxLon() {
        return maxLon;
    }

    public double getMaxLat() {
        return maxLat;
    }

    public void setMinLon(float minLon) {
        this.minLon = minLon;
    }

    public void setMinLat(float minLat) {
        this.minLat = minLat;
    }

    public void setMaxLon(float maxLon) {
        this.maxLon = maxLon;
    }

    public void setMaxLat(float maxLat) {
        this.maxLat = maxLat;
    }


    //En metode til at tegne firkantens omkreds

    //Skal først finde skærm koordinaterne, så kan vi omdanne dem til kortest koordinater
    //trans.inverseTransform(point)  ---- kig i klassen: MapCanvas


    @Override
    public String toString() {
        return "Rectangle{" +
                "minLon=" + minLon +
                ", maxLon=" + maxLon +
                ", minLat=" + minLat +
                ", maxLat=" + maxLat +
                '}';
    }
}
