package bfst22.controller;

import bfst22.model.Model;
import bfst22.model.MapCanvas;
import bfst22.model.addresses.AddressCollection;
import bfst22.model.addresses.AddressNode;
import bfst22.model.addresses.AddressObject;
import bfst22.model.addresses.TST;
import com.sun.glass.ui.CommonDialogs;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.stage.FileChooser;
import javafx.scene.control.ListView;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

//LINK//https://gist.github.com/Da9el00/cde4381b8f17299693bab886ec700a78

//Controller klassen håndterer alle kliks og scrolls eller andet som brugeren foretager sig.
//Klassen arbejder meget sammen med klassen MapCanvas som bruger koordinater til at tegne programmet.

public class Controller {
    @FXML
    private Point2D lastMouse;
    @FXML
    private TextField from;
    @FXML
    private TextField to;
    @FXML
    private ListView<AddressObject> listView;


    //Canvas er superklassen til vores MapCanvas.
    //Canvas er billede som bliver tegnet givet en højde og en bredde som afgør størrelsen af billede.
    //Den bruger pakken GraphicsContext.
    @FXML
    private MapCanvas canvas;

    //metoden init() er en predefineret metode der bruges til at initialisere et objekt efter objektet er skabt.
    public void init(Model model) {
        canvas.init(model);
    }

    //Metoden har et scroll event, som handler efter når brugeren scroller i kortet.
    //getDeltaY metoden returnerer den vertikale mængde af pixels som forstørres eller formindskes når brugeren scroller.
    @FXML
    public void onScroll(ScrollEvent e) {
        var factor = e.getDeltaY();
        canvas.zoom(Math.pow(1.05, factor), e.getX(), e.getY());
    }
    //Metoden gør det muligt at trække i kortet ved at trække musens sidste koordinater fra musens nye koordinater.
    @FXML
    public void onMouseDragged(MouseEvent e) {
        var dx = e.getX() - lastMouse.getX();
        var dy = e.getY() - lastMouse.getY();
        canvas.pan(dx, dy);
        lastMouse = new Point2D(e.getX(), e.getY());
    }
    //Gør det muligt for brugeren at klikke på kortet, når der klikkes opdateres "lastmouse" med de nye klik-koordinater.
    @FXML
    public void onMousePressed(MouseEvent e) {
        lastMouse = new Point2D(e.getX(), e.getY());
    }

    @FXML
    public void addressParse(KeyEvent keyEvent) {
        
    }

    @FXML
    public void openFile(ActionEvent event) throws XMLStreamException, IOException, ClassNotFoundException {
        FileChooser fc = new FileChooser();
        //så den kun viser pdf filer
        //fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PDF files", "*.pdf"));
        File selectedFile = fc.showOpenDialog(null);

        if(selectedFile != null) {
            Model model = new Model(selectedFile.getAbsolutePath());
        } else {
            System.out.println("File is not valid");
        }
    }

    @FXML
    public void etEllerAndet(ActionEvent event) {
        to.setText("Bye");
    }

    public void searchRoute(ActionEvent actionEvent) {

    }

    public void getSearchResult(KeyEvent keyEvent) {
        //listView setVisisble
        String input = from.getText();

        List<AddressObject> listOfSearchedAddresses = TST.get(input);
        //listView.setItems(); //lav en observablelist
    }




   /* @FXML
    void search(ActionEvent event) {
        listView.getItems().clear();
        listView.getItems().addAll(searchList(from.getText(),TST.get("Sankt Mortens Gade")));
    }

    private List<AddressObject> searchList(String searchWords, List<AddressObject> listOfStrings) {
        List<String> searchWordsArray = Arrays.asList(searchWords.trim().split(" "));
        String s = listOfStrings.toString();
        System.out.println(s);

        return listOfStrings.stream().filter(input -> {
            return searchWordsArray.stream().allMatch(word ->
                    input.toLowerCase().contains(word.toLowerCase()));
        }).collect(Collectors.toList());
    }*/
}