package bfst22.run;

import bfst22.model.Model;
import bfst22.view.View;
import javafx.application.Application;
import javafx.stage.Stage;

//Starter rammen/programmet med Danmarkskortet
public class App extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        // var filename = getParameters().getRaw().get(0);
        var model = new Model("data/bornholm.osm");
        new View(model, primaryStage);
    }
}
