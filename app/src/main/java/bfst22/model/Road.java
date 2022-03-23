package bfst22.model;

// Abstrakt superklasse til alle typer af veje

import java.awt.*;

public abstract class Road {

    protected int speedLimit;
    protected boolean drivableByCar;
    protected boolean drivableByBike;
    protected boolean allowedToWalk;
    protected Color colorOnMap;
    protected boolean isATrainRail;

    public Road(){
    }

    // Getters og setters for alle felter

    public int getSpeedLimit() {
        return speedLimit;
    }

    public void setSpeedLimit(int speedLimit) {
        this.speedLimit = speedLimit;
    }

    public boolean isDrivableByCar() {
        return drivableByCar;
    }

    public void setDrivableByCar(boolean drivableByCar) {
        this.drivableByCar = drivableByCar;
    }

    public boolean isDrivableByBike() {
        return drivableByBike;
    }

    public void setDrivableByBike(boolean drivableByBike) {
        this.drivableByBike = drivableByBike;
    }

    public boolean isAllowedToWalk() {
        return allowedToWalk;
    }

    public void setAllowedToWalk(boolean allowedToWalk) {
        this.allowedToWalk = allowedToWalk;
    }

    public Color getColorOnMap() {
        return colorOnMap;
    }


    public void setColorOnMap(Color colorOnMap) {
        this.colorOnMap = colorOnMap;
    }

    public boolean isATrainRail() {
        return isATrainRail;
    }

    public void setATrainRail(boolean ATrainRail) {
        isATrainRail = ATrainRail;
    }
}
