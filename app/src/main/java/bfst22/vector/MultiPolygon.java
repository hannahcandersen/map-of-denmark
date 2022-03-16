package bfst22.vector;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.canvas.GraphicsContext;

public class MultiPolygon implements Drawable, Serializable {
    /*
        SerialVersionUID is a unique identifier for each class, JVM uses it to compare the versions of the
        class ensuring that the same class was used during Serialization is loaded during Deserialization.
        Specifying one gives more control, though JVM does generate one if you don't specify.
     */
    public static final long serialVersionUID = 1325234;
    List<Drawable> parts = new ArrayList<>();

    public MultiPolygon(ArrayList<OSMWay> rel) {
        for (var way : rel) {
            parts.add(new PolyLine(way.nodes));
        }
    }

    @Override
    public void trace(GraphicsContext gc) {
        for (var part : parts) part.trace(gc);
    }
}
