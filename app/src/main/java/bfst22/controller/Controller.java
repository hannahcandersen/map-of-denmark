package bfst22.controller;

import bfst22.model.Model;
import bfst22.model.MapCanvas;
import bfst22.model.OSMNode;
import bfst22.model.dijkstra.Dijkstra;
import bfst22.model.dijkstra.DirectedEdge;
import bfst22.model.kdtree.KdTree;
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
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.stage.FileChooser;
import javafx.scene.control.CheckBox;
import javafx.scene.Node;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.IOException;
import javafx.stage.FileChooser;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.IOException;
import java.util.List;

//LINK//https://gist.github.com/Da9el00/cde4381b8f17299693bab886ec700a78
import java.util.Map;

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
    @FXML
    private CheckBox colorCheckBox;
    @FXML
    private CheckBox pinkCheckBox;
    @FXML
    private Pane warningPane;

    @FXML
    private Button warningOkButton;

    private int clickOnWaringButtonCounter = 0;
    private AddressObject chosenAddressFrom;
    private AddressObject chosenAddressTo;

    KdTree kdTree = new KdTree();


    //Canvas er superklassen til vores MapCanvas.
    //Canvas er billede som bliver tegnet givet en højde og en bredde som afgør størrelsen af billede.
    //Den bruger pakken GraphicsContext.
    @FXML
    private MapCanvas canvas;

    //metoden init() er en predefineret metode der bruges til at initialisere et objekt efter objektet er skabt.
    public void init(Model model) {
        warningPane.setVisible(false);
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


        if(e.getClickCount() == 2){

            Point2D MapPointX = canvas.mouseToModel(lastMouse);
            float mapPointx = (float) MapPointX.getX();
            float mapPointy = (float) MapPointX.getY();

            //canvas.TestdrawPunkt = new OSMNode(mapPointx, mapPointy);

            canvas.setDrawPunkt(canvas.getModel().getKDTreeNodesInGraph().findNearestNeighbour(canvas.getModel().getKDTreeNodesInGraph().getRoot(), mapPointy, mapPointx));

            //canvas.drawPunkt = kdTree.findNearestNeighbour(mapPointy, mapPointx);
            canvas.repaint();
            System.out.println("jeg har dobbeltklikket");
            System.out.println("mapPointX " + mapPointx);
            System.out.println("mapPointY " + mapPointy);
        }
    }

    @FXML
    public void addressParse(KeyEvent keyEvent) {

    }
    public void AddressFromUserInput(KeyEvent keyEvent) {}

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
        chosenAddressFrom = listViewFrom.getSelectionModel().getSelectedItem();
        if(chosenAddressFrom != null) {
            fromTextField.setText(chosenAddressFrom.toString());
            listViewFrom.setVisible(false);
        }
    }

    @FXML
    public void chooseAddressTo(MouseEvent event) {
        //viser valgt adresse i textfield
        chosenAddressTo = listViewTo.getSelectionModel().getSelectedItem();
        if(chosenAddressTo != null) {
            toTextField.setText(chosenAddressTo.toString());
            listViewTo.setVisible(false);
        }}

    @FXML
    public void partyColorToggle(ActionEvent event) {
       if(colorCheckBox.isSelected()){
           if(clickOnWaringButtonCounter<1){
            warningPane.setVisible(true);}
           canvas.isPartySelected = true;}
       else{
           canvas.isPartySelected = false;}
       canvas.repaint();
    }

    @FXML
    public void pinkColorToggle(ActionEvent event){
        if(pinkCheckBox.isSelected()){
            canvas.isPinkSelected = true;}
        else{
            canvas.isPinkSelected = false;}
        canvas.repaint();
    }

    @FXML
    public void onClickWarningOk(ActionEvent event){
       warningPane.setVisible(false);
        clickOnWaringButtonCounter++;
    }

    public AddressObject getChosenAddressFrom() {
        return chosenAddressFrom;
    }
    public AddressObject getChosenAddressTo() {
        return chosenAddressTo;
    }
}