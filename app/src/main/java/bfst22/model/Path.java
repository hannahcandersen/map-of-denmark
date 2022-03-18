package bfst22.model;

import java.awt.*;

// Sti hvor man kun må cykle og gå. Subklasse til road

public class Path extends Road {

    public Path(){
        super();
        this.speedLimit = 0;
        this.drivableByCar = false;
        this.drivableByBike = true;
        this.allowedToWalk = true;
        this.colorOnMap = Color.black; // vi kan bestemme farven senere
        this.isATrainRail = false;
    }
}
