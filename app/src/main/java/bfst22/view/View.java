package bfst22.view;

import java.io.IOException;

import bfst22.vector.Controller;
import bfst22.vector.Model;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

public class View {
    public View(Model model, Stage primaryStage) throws IOException {
        //gør således, at man kan se noget på skærmen
        primaryStage.show();
        //finder vores FXML fil og åbner den
        var loader = new FXMLLoader(View.class.getResource("View.fxml"));
        //sætter vores FXML til at blive vist på skærmen
        primaryStage.setScene(loader.load());
        //opretter et controller objekt
        Controller controller = loader.getController();
        //kalder på init metoden i controller klassen med model som parameter
        controller.init(model);
        //sætter titlen på vores window
        primaryStage.setTitle("Totalt mega fed vector grafik!!!");
    }
}