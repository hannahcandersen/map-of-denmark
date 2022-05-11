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
    @FXML
    private CheckBox colorCheckBox;
    @FXML
    private CheckBox graphCheckBox;
    @FXML
    private CheckBox debugBox;
    @FXML
    private Pane warningPane;
    @FXML
    private Button warningOkButton;
    @FXML
    private Label scaleInfo;
    @FXML
    private CheckBox checkBoxBicycle;
    @FXML
    private CheckBox checkBoxWalking;
    @FXML
    private CheckBox checkBoxCar;
    @FXML
    private Pane transportTypePane;
    @FXML
    private Button findRouteButton;
    @FXML
    private Button transportCloseButton;

    private int clickOnWaringButtonCounter = 0;
    private AddressObject chosenAddressFrom;
    private AddressObject chosenAddressTo;
    private int onSearchRouteClickCounter = 0;


    //Canvas er superklassen til vores MapCanvas.
    //Canvas er billede som bliver tegnet givet en højde og en bredde som afgør størrelsen af billede.
    //Den bruger pakken GraphicsContext.
    @FXML
    private MapCanvas canvas;

    //metoden init() er en predefineret metode der bruges til at initialisere et objekt efter objektet er skabt.
    public void init(Model model) {
        canvas.init(model);
        warningPane.setVisible(false);
        //scaleInfo.textProperty().bind(Bindings.format(canvas.getRationToString()));
        scaleInfo.textProperty().bind(Bindings.format(canvas.getDistToString()));







    }

    //Metoden har et scroll event, som handler efter når brugeren scroller i kortet.
    //getDeltaY metoden returnerer den vertikale mængde af pixels som forstørres eller formindskes når brugeren scroller.
    @FXML
    public void onScroll(ScrollEvent e) {
        var factor = e.getDeltaY();
        canvas.zoom(Math.pow(1.05, factor), e.getX(), e.getY());
        //scaleInfo.textProperty().bind(Bindings.format(canvas.getRationToString()));
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


        if (e.getClickCount() == 2) {

            Point2D MapPointX = canvas.mouseToModel(lastMouse);
            float mapPointx = (float) MapPointX.getX();
            float mapPointy = (float) MapPointX.getY();

            //canvas.TestdrawPunkt = new OSMNode(mapPointx, mapPointy);

            canvas.setDrawPunkt(canvas.getModel().getKDTreeNodesInGraphAllWays().findNearestNeighbour(canvas.getModel().getKDTreeNodesInGraphAllWays().getRoot(), mapPointy, mapPointx));

            //canvas.drawPunkt = kdTree.findNearestNeighbour(mapPointy, mapPointx);
            canvas.repaint();
            //System.out.println("jeg har dobbeltklikket");
            //System.out.println("mapPointX " + mapPointx);
            //System.out.println("mapPointY " + mapPointy);
        }
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
        //Finder den tætteste node fra søgt adresse (nearest neighbour)
        //OSMNode startOfRoute = canvas.getModel().getKDTreeNodesInGraphX().findNearestNeighbour(canvas.getModel().getKDTreeNodesInGraphX().getRoot(), chosenAddressFrom.getLon(), chosenAddressFrom.getLat());
        //OSMNode endOfRoute = canvas.getModel().getKDTreeNodesInGraphX().findNearestNeighbour(canvas.getModel().getKDTreeNodesInGraphX().getRoot(), chosenAddressTo.getLon(), chosenAddressTo.getLat());

        //System.out.println("chosenAddressFrom: Lat+Lon " + chosenAddressFrom.getLat() + chosenAddressFrom.getLon());
        //System.out.println("chosenAddressTo: Lat+Lon " + chosenAddressTo.getLat() + chosenAddressTo.getLon());
        //canvas.setDrawPunktStartOfRoute(startOfRoute);
        //canvas.setDrawPunktEndOfRoute(endOfRoute);

        //Find indeks for startOfRoute og endOfRoute
        //int startOfRouteIndex = Model.getMapOfOSMNodesToIndexX().get(startOfRoute);
        //int endOfRouteIndex = Model.getMapOfOSMNodesToIndexX().get(endOfRoute);

        //Dijkstra
        //Dijkstra dijkstra = new Dijkstra(canvas.getModel().getGraphX(), startOfRouteIndex, endOfRouteIndex);

        //Gemmer den korteste route i et felt
        //canvas.setDijkstraShortestPath(dijkstra.pathTo(endOfRouteIndex));
        //canvas.setDijkstraShortestPath(dijkstra.pathTo(endOfRouteIndex)); //tilføj Hashmap

        //if()
        //kald på searchByCar
        //kald på searchByBike
        transportTypePane.setVisible(true);
        checkBoxCar.setSelected(true);
        onClickedcheckBoxCar(actionEvent);


        canvas.repaint();

        //onClickedSearchRoute();

    }
 /*
    @FXML
    public void onClickedSearchRoute() {
        onSearchRouteClickCounter++;
        System.out.println(onSearchRouteClickCounter);
        if(onSearchRouteClickCounter % 2 == 1 ) {
            transportTypePane.setVisible(true);
            System.out.println("pane vises");
        } else {
            transportTypePane.setVisible(false);
            System.out.println("pane vises ikke");

        }
    }

  */

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
        }}

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
           canvas.setPartySelected(true);}
       else{
           canvas.setPartySelected(false);}
       canvas.repaint();
    }

    @FXML
    public void graphColorToggle(ActionEvent event){
        if(graphCheckBox.isSelected()){
            canvas.setGraphSelected(true);}
        else{
            canvas.setGraphSelected(false);}
        canvas.repaint();
    }

    @FXML
    public void onClickdebugBox(ActionEvent event) {
        if(debugBox.isSelected()){
            canvas.setDebugSelected(true);
        }
        else{
            canvas.setDebugSelected(false);
        }
        canvas.repaint();
    }

    @FXML
    public void onClickWarningOk(ActionEvent event){
       warningPane.setVisible(false);
        clickOnWaringButtonCounter++;
    }


    @FXML
    public void onClickedcheckBoxBicycle(ActionEvent event) {
        if(checkBoxBicycle.isSelected()) {
            checkBoxWalking.setSelected(false);
            checkBoxCar.setSelected(false);

            //Finder den tætteste node fra søgt adresse (nearest neighbour)
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

            //Gemmer den korteste route i et felt
            canvas.setDijkstraShortestPath(dijkstra.pathTo(endOfRouteIndex, canvas.getModel().getMapOfOSMNodesToIndexBIKE()));
            canvas.repaint();
        }
    }

    @FXML
    public void onClickedcheckBoxFoot(ActionEvent event) {
        if(checkBoxWalking.isSelected()) {
            checkBoxCar.setSelected(false);
            checkBoxBicycle.setSelected(false);

            //Finder den tætteste node fra søgt adresse (nearest neighbour)
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

            //Gemmer den korteste route i et felt
            canvas.setDijkstraShortestPath(dijkstra.pathTo(endOfRouteIndex, canvas.getModel().getMapOfOSMNodesToIndexFOOT()));
            canvas.repaint();
        }
    }

    @FXML
    public void onClickedcheckBoxCar(ActionEvent event) {
        if(checkBoxCar.isSelected()) {
            checkBoxBicycle.setSelected(false);
            checkBoxWalking.setSelected(false);

            //Finder den tætteste node fra søgt adresse (nearest neighbour)
            OSMNode startOfRoute = canvas.getModel().getKDTreeNodesInGraphCAR().findNearestNeighbour(canvas.getModel().getKDTreeNodesInGraphCAR().getRoot(), chosenAddressFrom.getLon(), chosenAddressFrom.getLat());
            OSMNode endOfRoute = canvas.getModel().getKDTreeNodesInGraphCAR().findNearestNeighbour(canvas.getModel().getKDTreeNodesInGraphCAR().getRoot(), chosenAddressTo.getLon(), chosenAddressTo.getLat());

            //System.out.println("chosenAddressFrom: Lat+Lon " + chosenAddressFrom.getLat() + chosenAddressFrom.getLon());
            //System.out.println("chosenAddressTo: Lat+Lon " + chosenAddressTo.getLat() + chosenAddressTo.getLon());
            canvas.setDrawPunktStartOfRoute(startOfRoute);
            canvas.setDrawPunktEndOfRoute(endOfRoute);

            //Find indeks for startOfRoute og endOfRoute
            int startOfRouteIndex = canvas.getModel().getMapOfOSMNodesToIndexCAR().get(startOfRoute);
            int endOfRouteIndex = canvas.getModel().getMapOfOSMNodesToIndexCAR().get(endOfRoute);

            //Dijkstra
            Dijkstra dijkstra = new Dijkstra(canvas.getModel().getGraphCAR(), startOfRouteIndex, endOfRouteIndex, canvas.getModel().getArrayOfOSMNodesInGraphCAR(), canvas.getModel().getMapOfOSMNodesToIndexCAR());

            //Gemmer den korteste route i et felt
            canvas.setDijkstraShortestPath(dijkstra.pathTo(endOfRouteIndex, canvas.getModel().getMapOfOSMNodesToIndexCAR()));
            canvas.repaint();
        }
    }

    @FXML
    public void onClickedTransportCloseButton(MouseEvent mouseEvent) {
        transportTypePane.setVisible(false);
    }
    /*public OSMNode getStartOfRoute() {
        return canvas.getModel().getKDTreeNodesInGraphX().findNearestNeighbour(canvas.getModel().getKDTreeNodesInGraphX().getRoot(), chosenAddressFrom.getLon(), chosenAddressFrom.getLat()); //EVT byt om på lat og lon
    }
    public OSMNode getEndOfRoute() {
        return canvas.getModel().getKDTreeNodesInGraphX().findNearestNeighbour(canvas.getModel().getKDTreeNodesInGraphX().getRoot(), chosenAddressTo.getLon(), chosenAddressTo.getLat());
    }*/



}