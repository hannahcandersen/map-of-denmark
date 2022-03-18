package bfst22.model;

import java.util.ArrayList;
import java.util.List;

public class BuildingsCollection {

    private List<Building> buildings;

    public BuildingsCollection(){
        buildings = new ArrayList<>();
    }

    public List<Building> getBuildings() {
        return buildings;
    }
}
