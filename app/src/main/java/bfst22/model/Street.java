package bfst22.model;

// Gade hvor man må køre i bil, cykle og gå

import java.awt.*;

public class Street extends Road{

    public Street(){
        super();
        this.speedLimit = 50;
        this.drivableByCar = true;
        this.drivableByBike = true;
        this.allowedToWalk = true;
        this.colorOnMap = Color.black; // vi kan bestemme farven senere
        this.isATrainRail = false;
    }

}
