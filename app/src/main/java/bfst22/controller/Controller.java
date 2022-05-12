package bfst22.controller;

import bfst22.model.Model;
import bfst22.model.MapCanvas;
import bfst22.model.OSMNode;
import bfst22.model.dijkstra.Dijkstra;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import bfst22.model.addresses.AddressObject;
import bfst22.model.addresses.TST;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.IOException;
import java.util.List;

//Controller klassen: opdaterer objekterne fra model klassen og holder styr på hvad der bliver vist på canvas.

public class Controller {
    @FXML
    private MapCanvas canvas;
    @FXML
    private Point2D lastMouse;
    @FXML
    private TextField fromTextField, toTextField;
    @FXML
    private ListView<AddressObject> listViewFrom, listViewTo;
    @FXML
    private CheckBox colorCheckBox, graphCheckBox, debugBox;
    @FXML
    private Pane transportTypePane;
    @FXML
    private Label scaleInfo;
    @FXML
    private RadioButton checkBoxCar, checkBoxBicycle, checkBoxWalking;

    private int clickOnPartyModeCheckBoxCounter = 0;
    private AddressObject chosenAddressFrom, chosenAddressTo;


    //metoden init() er en predefineret metode der bruges til at initialisere et objekt efter objektet er skabt.
    public void init(Model model) {
        canvas.init(model);
        //scaleInfo.textProperty().bind(Bindings.format(canvas.getRationToString()));
        scaleInfo.textProperty().bind(Bindings.format(canvas.getDistToString()));
    }


    //getDeltaY metoden returnerer den vertikale mængde af pixels som forstørres eller formindskes når brugeren scroller.
    @FXML
    public void onScroll(ScrollEvent e) {
        var factor = e.getDeltaY();
        canvas.zoom(Math.pow(1.05, factor), e.getX(), e.getY());
        scaleInfo.textProperty().bind(Bindings.format(canvas.getDistToString()));
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

        //Hvis vi dobbeltklikker, så finder den nearest neighbour
        if (e.getClickCount() == 2) {
            Point2D MapPointX = canvas.mouseToModel(lastMouse);
            float mapPointx = (float) MapPointX.getX();
            float mapPointy = (float) MapPointX.getY();

            canvas.setDrawPunkt(canvas.getModel().getKDTreeNodesInGraphAllWays().findNearestNeighbour(canvas.getModel().getKDTreeNodesInGraphAllWays().getRoot(), mapPointy, mapPointx));
            canvas.repaint();
            //System.out.println("jeg har dobbeltklikket");
            //System.out.println("mapPointX " + mapPointx);
            //System.out.println("mapPointY " + mapPointy);
        }
    }

    @FXML
    public void openFile(ActionEvent event) throws XMLStreamException, IOException, ClassNotFoundException {
        FileChooser fc = new FileChooser();

        File selectedFile = fc.showOpenDialog(null);

        if(selectedFile != null) {
            Model model = new Model(selectedFile.getAbsolutePath());
            canvas.init(model);

            // Finder midten af kortet
            double middelX = (model.getMinLon()+model.getMaxlon())/2;
            double middelY = (model.getMinLat()+model.getMaxlat())/2;
            Point2D middleOfMap = new Point2D(middelX, middelY);

            // Panner hen til midten af kortet
            canvas.center(middleOfMap);
            canvas.repaint();
            scaleInfo.textProperty().bind(Bindings.format(canvas.getDistToString()));

            //flytter canvas
            //canvas.zoom(canvas.getWidth() / (model.getMaxlon() - model.getMinLon()), 0, 0);

        } else {
            System.out.println("File is not valid");
        }}


    ///////////VISER OG VÆLGER ADRESSER///////////
    @FXML
    public void getSearchResultFrom(KeyEvent keyEvent) {
        getSearchResult(fromTextField, listViewFrom);}

    @FXML
    public void getSearchResultTo(KeyEvent keyEvent) {
        getSearchResult(toTextField, listViewTo);}

    //Viser adresser efter mere end 3 bogstaver er indtastet.
    private void getSearchResult(TextField textField, ListView<AddressObject> listView) {
        String input = textField.getText();
        if(input.length() > 3) {
            List<AddressObject> searchListOfAddresses = canvas.getModel().getTst().search(input);

            //Sætter vores liste med adresser ind i observablelist
            ObservableList<AddressObject> listOfObservables = FXCollections.observableArrayList();
            listOfObservables.addAll(searchListOfAddresses);

            listView.setVisible(true);
            listView.setItems(listOfObservables);
        } else {
            listView.setVisible(false);}}

    @FXML
    public void chooseAddressFrom(MouseEvent event) {
        //viser valgt adresse i textfield
        chosenAddressFrom = listViewFrom.getSelectionModel().getSelectedItem();
        if(chosenAddressFrom != null) {
            fromTextField.setText(chosenAddressFrom.toString());
            listViewFrom.setVisible(false);}}

    @FXML
    public void chooseAddressTo(MouseEvent event) {
        //viser valgt adresse i textfield
        chosenAddressTo = listViewTo.getSelectionModel().getSelectedItem();
        if(chosenAddressTo != null) {
            toTextField.setText(chosenAddressTo.toString());
            listViewTo.setVisible(false);}}





    ///////////SØGEFUNKTION OG TRANSPORT MULIGHEDER///////////
    @FXML
    //Hver gang en bruger trykker på 'Find route' knappen, vil den som det første søge en rute for en bil
    //For at få vist ruten for en anden transportform, så kan man vælge cykel eller gågang
    public void searchRoute(ActionEvent actionEvent) {
        checkBoxCar.setSelected(true);
        onClickedcheckBoxCar(actionEvent);

        double middelX = canvas.getWidth()/2;
        double middelY = canvas.getHeight()/2;
        Point2D realmiddel = canvas.mouseToModel(new Point2D(middelX, middelY));

        double distFromMiddelX = chosenAddressFrom.getLat()-realmiddel.getX();
        double distFromMiddelY = chosenAddressFrom.getLon()-realmiddel.getY();

        //Panner hen så startpunkt er i midten af skærmen
        canvas.pan(distFromMiddelX*canvas.getRation(),distFromMiddelY*canvas.getRation());

        double middelXzoom = canvas.getWidth();
        double middelYzoom = canvas.getHeight();
        Point2D realmiddelzoom = canvas.mouseToModel(new Point2D(middelXzoom, middelYzoom));

        // TODO slet hvis det ikke virker, bruges til at zoome hen til adressen
       // canvas.zoom(canvas.getWidth()/(canvas.getModel().getMaxlon()-0.05)-(canvas.getModel().getMinLon()-0.05),0, 0);
        //canvas.zoom(canvas.getWidth()/5,realmiddelzoom.getX(), realmiddelzoom.getY());
        //canvas.zoom(1.05,chosenAddressFrom.getLat()/2, chosenAddressFrom.getLon());
        //canvas.repaint();


        //ZOOM:
        //getWidth og getHeight og lave om til MouseToModel. Den x værdi skal divideres med den x værdi vi gerne vil have


    }
    @FXML
    //Finder korteste rute for en bil
    public void onClickedcheckBoxCar(ActionEvent event) {
        if(checkBoxCar.isSelected()) {
            checkBoxBicycle.setSelected(false);
            checkBoxWalking.setSelected(false);
            canvas.setGoByCar(true);
            canvas.setGoByBike(false);
            canvas.setGoByFoot(false);

            //Finder den tætteste node fra søgt adresse (faktisk nearest neighbour adresse)
            try {
                OSMNode startOfRoute = canvas.getModel().getKDTreeNodesInGraphCAR().findNearestNeighbour(canvas.getModel().getKDTreeNodesInGraphCAR().getRoot(), chosenAddressFrom.getLon(), chosenAddressFrom.getLat());
                OSMNode endOfRoute = canvas.getModel().getKDTreeNodesInGraphCAR().findNearestNeighbour(canvas.getModel().getKDTreeNodesInGraphCAR().getRoot(), chosenAddressTo.getLon(), chosenAddressTo.getLat());
                transportTypePane.setVisible(true);
            //System.out.println("chosenAddressFrom: Lat+Lon " + chosenAddressFrom.getLat() + chosenAddressFrom.getLon());
            //System.out.println("chosenAddressTo: Lat+Lon " + chosenAddressTo.getLat() + chosenAddressTo.getLon());
            canvas.setDrawPunktStartOfRoute(startOfRoute);
            canvas.setDrawPunktEndOfRoute(endOfRoute);

            //Find indeks for startOfRoute og endOfRoute
            int startOfRouteIndex = canvas.getModel().getMapOfOSMNodesToIndexCAR().get(startOfRoute);
            int endOfRouteIndex = canvas.getModel().getMapOfOSMNodesToIndexCAR().get(endOfRoute);

            //Dijkstra
            Dijkstra dijkstra = new Dijkstra(canvas.getModel().getGraphCAR(), startOfRouteIndex, endOfRouteIndex, canvas.getModel().getArrayOfOSMNodesInGraphCAR(), canvas.getModel().getMapOfOSMNodesToIndexCAR());

            //Gemmer den korteste rute i et felt i Mapcanvas
            canvas.setDijkstraShortestPath(dijkstra.pathTo(endOfRouteIndex, canvas.getModel().getMapOfOSMNodesToIndexCAR()));
            canvas.repaint();
            } catch (NullPointerException e) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Please select a start and destination address");
                alert.show();}}}

    @FXML
    //Finder korteste rute for en cykel
    public void onClickedcheckBoxBicycle(ActionEvent event) {
        if(checkBoxBicycle.isSelected()) {
            checkBoxWalking.setSelected(false);
            checkBoxCar.setSelected(false);
            canvas.setGoByCar(false);
            canvas.setGoByBike(true);
            canvas.setGoByFoot(false);

            //Finder den tætteste node fra søgt adresse (faktisk nearest neighbour adresse)
            OSMNode startOfRoute = canvas.getModel().getKDTreeNodesInGraphBIKE().findNearestNeighbour(canvas.getModel().getKDTreeNodesInGraphBIKE().getRoot(), chosenAddressFrom.getLon(), chosenAddressFrom.getLat());
            OSMNode endOfRoute = canvas.getModel().getKDTreeNodesInGraphBIKE().findNearestNeighbour(canvas.getModel().getKDTreeNodesInGraphBIKE().getRoot(), chosenAddressTo.getLon(), chosenAddressTo.getLat());

            //System.out.println("chosenAddressFrom: Lat+Lon " + chosenAddressFrom.getLat() + chosenAddressFrom.getLon());
            //System.out.println("chosenAddressTo: Lat+Lon " + chosenAddressTo.getLat() + chosenAddressTo.getLon());
            canvas.setDrawPunktStartOfRoute(startOfRoute);
            canvas.setDrawPunktEndOfRoute(endOfRoute);

            //Find indeks for startOfRoute og endOfRoute
            int startOfRouteIndex = canvas.getModel().getMapOfOSMNodesToIndexBIKE().get(startOfRoute);
            int endOfRouteIndex = canvas.getModel().getMapOfOSMNodesToIndexBIKE().get(endOfRoute);

            //Dijkstra
            Dijkstra dijkstra = new Dijkstra(canvas.getModel().getGraphBIKE(), startOfRouteIndex, endOfRouteIndex, canvas.getModel().getArrayOfOSMNodesInGraphBIKE(), canvas.getModel().getMapOfOSMNodesToIndexBIKE());

            //Gemmer den korteste rute i et felt i Mapcanvas
            canvas.setDijkstraShortestPath(dijkstra.pathTo(endOfRouteIndex, canvas.getModel().getMapOfOSMNodesToIndexBIKE()));
            canvas.repaint();}}

    @FXML
    //Finder korteste rute for en fodgænger
    public void onClickedcheckBoxFoot(ActionEvent event) {
        if(checkBoxWalking.isSelected()) {
            checkBoxCar.setSelected(false);
            checkBoxBicycle.setSelected(false);
            canvas.setGoByCar(false);
            canvas.setGoByBike(false);
            canvas.setGoByFoot(true);

            //Finder den tætteste node fra søgt adresse (faktisk nearest neighbour adresse)
            OSMNode startOfRoute = canvas.getModel().getKDTreeNodesInGraphFOOT().findNearestNeighbour(canvas.getModel().getKDTreeNodesInGraphFOOT().getRoot(), chosenAddressFrom.getLon(), chosenAddressFrom.getLat());
            OSMNode endOfRoute = canvas.getModel().getKDTreeNodesInGraphFOOT().findNearestNeighbour(canvas.getModel().getKDTreeNodesInGraphFOOT().getRoot(), chosenAddressTo.getLon(), chosenAddressTo.getLat());

            //System.out.println("chosenAddressFrom: Lat+Lon " + chosenAddressFrom.getLat() + chosenAddressFrom.getLon());
            //System.out.println("chosenAddressTo: Lat+Lon " + chosenAddressTo.getLat() + chosenAddressTo.getLon());
            canvas.setDrawPunktStartOfRoute(startOfRoute);
            canvas.setDrawPunktEndOfRoute(endOfRoute);

            //Find indeks for startOfRoute og endOfRoute
            int startOfRouteIndex = canvas.getModel().getMapOfOSMNodesToIndexFOOT().get(startOfRoute);
            int endOfRouteIndex = canvas.getModel().getMapOfOSMNodesToIndexFOOT().get(endOfRoute);

            //Dijkstra
            Dijkstra dijkstra = new Dijkstra(canvas.getModel().getGraphFOOT(), startOfRouteIndex, endOfRouteIndex, canvas.getModel().getArrayOfOSMNodesInGraphFOOT(), canvas.getModel().getMapOfOSMNodesToIndexFOOT());

            //Gemmer den korteste rute i et felt i Mapcanvas
            canvas.setDijkstraShortestPath(dijkstra.pathTo(endOfRouteIndex, canvas.getModel().getMapOfOSMNodesToIndexFOOT()));
            canvas.repaint();}}

    @FXML
    public void onClickedTransportCloseButton(MouseEvent mouseEvent) {
        transportTypePane.setVisible(false);}




    ///////////EKSTRA FEATURES///////////
    @FXML
    public void partyColorToggle(ActionEvent event) {
        if(colorCheckBox.isSelected()){
            if(clickOnPartyModeCheckBoxCounter<1){
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setHeaderText("WARNING: party mode selected");
                alert.show();
            }
            canvas.setPartySelected(true);}
        else{canvas.setPartySelected(false);}
        canvas.repaint();
    clickOnPartyModeCheckBoxCounter++;
    }

    @FXML
    public void graphColorToggle(ActionEvent event){
        if(graphCheckBox.isSelected()){canvas.setGraphSelected(true);}
        else{canvas.setGraphSelected(false);}
        canvas.repaint();}
    @FXML
    public void onClickdebugBox(ActionEvent event) {
        if(debugBox.isSelected()){canvas.setDebugSelected(true);}
        else{canvas.setDebugSelected(false);}
        canvas.repaint();}
}