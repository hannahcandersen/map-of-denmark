package bfst22.model.kdtree;

import bfst22.view.Drawable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.transform.Affine;

// Den firkant vi laver range search indenfor. I det officielle program er det skærmen, i debug mode er det debug firkanten

public class RangeSearchRectangle extends Canvas {
    private double minLon, maxLon, minLat, maxLat;

    public RangeSearchRectangle(double minLon, double maxLon, double minLat, double maxLat) {
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
