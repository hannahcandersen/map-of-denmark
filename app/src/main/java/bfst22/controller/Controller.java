package bfst22.controller;

import bfst22.model.Model;
import bfst22.model.MapCanvas;
import bfst22.view.View;
import com.sun.glass.ui.CommonDialogs;
import javafx.event.ActionEvent;
import bfst22.model.addresses.AddressObject;
import bfst22.model.addresses.TST;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.stage.FileChooser;
import javafx.scene.control.CheckBox;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.IOException;
import javafx.stage.FileChooser;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.IOException;
import java.util.List;

//LINK//https://gist.github.com/Da9el00/cde4381b8f17299693bab886ec700a78

//Controller klassen håndterer alle kliks og scrolls eller andet som brugeren foretager sig.
//Klassen arbejder meget sammen med klassen MapCanvas som bruger koordinater til at tegne programmet.

public class Controller {
    @FXML
    private Point2D lastMouse;
    @FXML
    private TextField fromTextField;
    @FXML
    private TextField toTextField;
    @FXML
    private ListView<AddressObject> listViewFrom;
    @FXML
    private ListView<AddressObject> listViewTo;


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
            canvas.init(model);
            //flytter canvas
            //canvas.zoom(650 / (model.getMaxlon() - model.getMinLon()), 0, 0);

        } else {
            System.out.println("File is not valid");
        }
    }

    //gør ikke helt det jeg håbede
    private void moveCanvas(float x, float y) {
        canvas.setTranslateX(x);
        canvas.setTranslateY(y);
    }

    @FXML
    public void etEllerAndet(ActionEvent event) {
        toTextField.setText("Bye");
    }
    @FXML
    public void searchRoute(ActionEvent actionEvent) {

    }
    @FXML
    public void getSearchResultFrom(KeyEvent keyEvent) {
        String input = fromTextField.getText();
        if(input.length() > 3) {
            TST tst = new TST();
            List<AddressObject> searchListOfAddresses = tst.search(input);

            //sætte vores liste med adresse ind i observablelist
            ObservableList<AddressObject> listOfObservables = FXCollections.observableArrayList();
            listOfObservables.addAll(searchListOfAddresses);
            //sorterer listen - måske det ikke virker, når det er et objekt og den ikke ved hvad den skal sortere på..
            //SortedList<AddressObject> sortedList = new SortedList<>(listOfObservables);

            listViewFrom.setVisible(true);
            listViewFrom.setItems(listOfObservables);
        } else {
            listViewFrom.setVisible(false);
        }
    }

    @FXML
    public void getSearchResultTo(KeyEvent keyEvent) {
        String input = toTextField.getText(); //UNDGÅ KODEDUPLIKERING
        if(input.length() > 3) {
            TST tst = new TST();
            List<AddressObject> searchListOfAddresses = tst.search(input);

            //sætte vores liste med adresse ind i observablelist
            ObservableList<AddressObject> listOfObservables = FXCollections.observableArrayList();
            listOfObservables.addAll(searchListOfAddresses);
            //sorterer listen - måske det ikke virker, når det er et objekt og den ikke ved hvad den skal sortere på..
            //SortedList<AddressObject> sortedList = new SortedList<>(listOfObservables);

            listViewTo.setVisible(true);
            listViewTo.setItems(listOfObservables);
        } else {
            listViewTo.setVisible(false);
        }
    }


    @FXML
    public void chooseAddressFrom(MouseEvent event) {
        //viser valgt adresse i textfield
        AddressObject chosenAddress = listViewFrom.getSelectionModel().getSelectedItem();
        if(chosenAddress != null) {
            fromTextField.setText(chosenAddress.toString());
            listViewFrom.setVisible(false);
        }
    }

    @FXML
    public void chooseAddressTo(MouseEvent event) {
        //viser valgt adresse i textfield
        AddressObject chosenAddress = listViewTo.getSelectionModel().getSelectedItem();
        if(chosenAddress != null) {
            toTextField.setText(chosenAddress.toString());
            listViewTo.setVisible(false);
        }
    }





}