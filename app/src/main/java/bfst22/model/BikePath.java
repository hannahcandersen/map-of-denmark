package bfst22.model;

import java.awt.*;

// Cykelsti hvor man kun må cykle og ikke gå. Subklasse til road

public class BikePath extends Road{

    public BikePath(){
        super();
        this.speedLimit = 0;
        this.drivableByCar = false;
        this.drivableByBike = true;
        this.allowedToWalk = false;
        this.colorOnMap = Color.black; // vi kan bestemme farven senere
        this.isATrainRail = false;
    }


}
