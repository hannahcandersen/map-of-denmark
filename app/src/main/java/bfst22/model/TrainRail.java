package bfst22.model;

// Togbane. Her kan man ikke køre bil, cykle eller gå. Subklasse af road

import java.awt.*;

public class TrainRail extends Road {

    public TrainRail(){
        super();
        this.speedLimit = 0;
        this.drivableByCar = false;
        this.drivableByBike = false;
        this.allowedToWalk = false;
        this.colorOnMap = Color.black; // vi kan bestemme farven senere
        this.isATrainRail = true;
    }
}
