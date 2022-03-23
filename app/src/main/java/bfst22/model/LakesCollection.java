package bfst22.model;

import java.util.ArrayList;
import java.util.List;

public class LakesCollection {

    private List<Lake> lakes;

    public LakesCollection(){
        lakes = new ArrayList<>();
    }

    public List<Lake> getLakes() {
        return lakes;
    }
}
