package bfst22.controller;

import bfst22.model.Model;
import bfst22.model.MapCanvas;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

//Controller klassen håndterer alle kliks og scrolls eller andet som brugeren foretager sig.
//Klassen arbejder meget sammen med klassen MapCanvas som bruger koordinater til at tegne programmet.

public class Controller {
    private Point2D lastMouse;

    //Canvas er superklassen til vores MapCanvas.
    //Canvas er billede som bliver tegnet givet en højde og en bredde som afgør størrelsen af billede.
    //Den bruger pakken GraphicsContext.
    @FXML
    public MapCanvas canvas;

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


}