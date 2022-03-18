package bfst22.model;

import java.awt.*;

// Motortrafikvej hvor man må køre i bil og cykle, men ikke gå. Subklasse til road

public class Expressway extends Road {

    public Expressway(){
        super();
        this.speedLimit = 80;
        this.drivableByCar = true;
        this.drivableByBike = true;
        this.allowedToWalk = false;
        this.colorOnMap = Color.black; // vi kan bestemme farven senere
        this.isATrainRail = false;
    }


}
