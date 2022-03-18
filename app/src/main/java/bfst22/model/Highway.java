package bfst22.model;

import java.awt.*;

// Motorvej hvor man kun må køre i bil. Subklasse til road

public class Highway extends Road {

    public Highway(){
        super();
        this.speedLimit = 110;
        this.drivableByCar = true;
        this.drivableByBike = false;
        this.allowedToWalk = false;
        this.colorOnMap = Color.black; // vi kan bestemme farven senere
        this.isATrainRail = false;
    }


}
