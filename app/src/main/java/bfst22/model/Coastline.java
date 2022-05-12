package bfst22.model;

import bfst22.view.Drawable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Coastline implements Serializable {

    public Coastline() {
    }

    public List<PolyLine> makeCoastlineObject(Map<WayType, List<Drawable>> linesList, WayType wt) {
        List<PolyLine> listOfCoastlines = new ArrayList<>(); //Holder på vores færdige coastlines - Den vi returnerer til sidst
        List<Drawable> unSortedCoastlines = linesList.get(wt); //Alle ways af typen coastline
        PolyLine currentWay = null;
        OSMNode startOSMNode = null; //Starten for CurrentWay
        OSMNode endOSMNode = null; //Slutning for CurrentWay
        OSMNode currentStartOSMNode; //Start for den coastline man kigger på lige nu
        OSMNode currentEndOSMNode; //Slut for den coastline man kigger på lige nu
        ArrayList<OSMNode> listOfNodesInCoastline = new ArrayList<>(); // Alle nodes i currentWay

        while (!unSortedCoastlines.isEmpty()) {
            List<Drawable> buildUpCoastlinesList = new ArrayList<>(); // Liste for de coastlines som ikke passer med nogen
            for (Drawable coastline : unSortedCoastlines) {

                //Når det er første iteration
                if (listOfNodesInCoastline.size() == 0) {
                    listOfNodesInCoastline.addAll(coastline.getNodes());
                    startOSMNode = coastline.getNodes().get(0);
                    endOSMNode = coastline.getNodes().get(coastline.getNodes().size() - 1);
                    //Opretter currentWay med nodes i vores første vej
                    currentWay = new PolyLine(listOfNodesInCoastline); // Opretter currentWay. Starter med at være den første coastline vi møder
                    continue;
                }

                // Når det ikke er første iteration
                currentStartOSMNode = coastline.getNodes().get(0);
                currentEndOSMNode = coastline.getNodes().get(coastline.getNodes().size() - 1);
//
                // SITUATION 1: Begge pile mod højre.
                if (currentStartOSMNode.getLat() == endOSMNode.getLat() && currentStartOSMNode.getLon() == endOSMNode.getLon()) {
                    listOfNodesInCoastline.addAll(coastline.getNodes());
                    endOSMNode = currentEndOSMNode;
                    currentWay = new PolyLine(listOfNodesInCoastline); // Tilføjer den der passer og laver ny, længere, currentway
                    //System.out.println("Vi er ved situation 1");
                }
                // SITUATION 2: Begge pile mod midten
                else if (endOSMNode.getLat() == currentEndOSMNode.getLat() && endOSMNode.getLon() == currentEndOSMNode.getLon()) {
                    // Vend nummer to pil om og tilføjer til listOfNodesInCoastLine
                    Collections.reverse(coastline.getNodes());
                    listOfNodesInCoastline.addAll(coastline.getNodes());
                    endOSMNode = currentEndOSMNode;
                    currentWay = new PolyLine(listOfNodesInCoastline); // Tilføjer den der passer og laver ny, længere, currentway
                    //System.out.println("Vi er ved situation 2");
                }
                // SITUATION 3: Begge pile væk fra midten // Start og start mødes //Første pil drejet rundt
                else if (currentStartOSMNode.getLat() == startOSMNode.getLat() && currentStartOSMNode.getLon() == startOSMNode.getLon()) {
                    // Vend nummer et pil om og tilføjer til listOfNodesInCoastLine
                    Collections.reverse(coastline.getNodes());
                    listOfNodesInCoastline.addAll(0, coastline.getNodes());
                    startOSMNode = listOfNodesInCoastline.get(0);
                    currentWay = new PolyLine(listOfNodesInCoastline); // Tilføjer den der passer og laver ny, længere, currentway
                    //System.out.println("Vi er ved situation 3");
                }
                // SITUATION 4: Begge pile mod venstre // Vores start == nye slut
                else if (currentEndOSMNode.getLat() == startOSMNode.getLat() && currentEndOSMNode.getLon() == startOSMNode.getLon()) {
                    listOfNodesInCoastline.addAll(0, coastline.getNodes()); // Tilføjer foran det andet
                    startOSMNode = listOfNodesInCoastline.get(0);
                    currentWay = new PolyLine(listOfNodesInCoastline); // Tilføjer den der passer og laver ny, længere, currentway
                    //System.out.println("Vi er ved situation 4");
                } else {
                    buildUpCoastlinesList.add(coastline); // Hvis den ikke passer på nogen, tilføjes den til listen med dem, der ikke passer
                    //System.out.println("Jeg går ind i else");
                }
                //Hvis end og start det samme, ville alle coastlines være tilføjet
                if (endOSMNode.getLat() == startOSMNode.getLat() && endOSMNode.getLon() == startOSMNode.getLon()) {
                    listOfNodesInCoastline.addAll(coastline.getNodes());
                    listOfCoastlines.add(currentWay); // Tilføjer den fulde kystline til linjen med kystlinjer
                    listOfNodesInCoastline.clear();
                    //System.out.println("Vi er færdige med kystlinjen og har tilføjet den");
                }
            }

            // Hvis der har været en iteration, hvor ingen passede, afslutter vi selv kystlinjen
            if(buildUpCoastlinesList.size() == unSortedCoastlines.size()) {
                //tilføjer den færdige polyline figur til listen, der gemmer alle øerne i form af polylines
                listOfCoastlines.add(currentWay);
                listOfNodesInCoastline.clear();
            }
            else {
                // Laver ny iteartion med listen af de kystlinjer, der endnu ikke er tilføjet
                unSortedCoastlines = buildUpCoastlinesList;
            }
        }
        //System.out.println("Done med kystliner");
        return listOfCoastlines;

    }
}
