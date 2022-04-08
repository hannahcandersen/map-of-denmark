package bfst22.model;

import bfst22.model.kdtree.KdNode;
import bfst22.model.kdtree.KdTree;
import bfst22.view.Drawable;
// import bfst22.model.kdtree.KDTree2;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import static java.util.stream.Collectors.toList;

public class Model {

    //KD Trees
    private static KdNode KDTreeUNKNOWN;
    private static KdNode KDTreeRESIDENTIAL;

    NodeMap id2node = new NodeMap(); // NodeMap til nodes, der gemmer id, lon og lat for en node. NodeMap er en sorteret liste
    List<Runnable> observers = new ArrayList<>();  // Liste af observers. Observers gør så objekter kan blive notificeret om ændringer i andre objekter

    //Felter:
    // Min og max koordinater for hele datasættet. Hedder bounds i osm filen
    private float minlat, minlon, maxlat, maxlon;

    // Map med type (om det f.eks. er lake eller highway) og tilhørende linje
    Map<WayType,List<Drawable>> lines = new EnumMap<>(WayType.class); {
        for (var type : WayType.values()) lines.put(type, new ArrayList<>());
    }


    /////////////// Konstruktør (unchecked) ///////////////////
    public Model(String filename) throws IOException, XMLStreamException, FactoryConfigurationError, ClassNotFoundException {
        var time = -System.nanoTime();
        if (filename.endsWith(".zip")) {   // unzipper en zip-fil og læser den herefter på samme måde som osm-fil
            var zip = new ZipInputStream(new FileInputStream(filename)); //læser zip-filen én fil af gangen
            zip.getNextEntry(); // læser næste fil i zip-filen
            loadOSM(zip);
        } else if (filename.endsWith(".osm")) {// læser filen som en osm-fil
            loadOSM(new FileInputStream(filename));
        } else if (filename.endsWith(".obj")) { // kan læse den .obj-fil der er blevet gemt (binary file)
            try (var input = new ObjectInputStream(new BufferedInputStream(new FileInputStream(filename)))) {
                minlat = input.readFloat();
                minlon = input.readFloat();
                maxlat = input.readFloat();
                maxlon = input.readFloat();
                lines = (Map<WayType,List<Drawable>>) input.readObject();
            }
        } else {
            lines.put(WayType.UNKNOWN, Files.lines(Paths.get(filename))
                    .map(Line::new)
                    .collect(toList()));
        }
        // måler load time
        time += System.nanoTime(); // load time i nanosekunder
        System.out.println("Load time: " + (long)(time / 1e6) + " ms"); // printer load time i millisekunder

        // gemmer den indlæste data som en .obj-fil, så den vigtige data serialiseres
        if (!filename.endsWith(".obj")) save(filename);

        //Bygger KDTree og gemmer det i feltet KDTree
        //KDTreeLAKE = KDTree2.buildKDTree(lines.get(WayType.LAKE), true);
        //System.out.println("Har bygget KDTreeLAKE done");
        KDTreeUNKNOWN = KdTree.buildKDTree(lines.get(WayType.UNKNOWN), true);
        System.out.println("Har bygget KDTreeUNKNOWN done");
        KDTreeRESIDENTIAL = KdTree.buildKDTree(lines.get(WayType.RESIDENTIAL), true);
        System.out.println("Har bygget KDTreeRESIDENTIAL done");
    }


    // metode til at gemme data i en .obj-fil
    public void save(String basename) throws FileNotFoundException, IOException {
        try (var out = new ObjectOutputStream(new FileOutputStream(basename + ".obj"))) {
            out.writeFloat(minlat);
            out.writeFloat(minlon);
            out.writeFloat(maxlat);
            out.writeFloat(maxlon);
            out.writeObject(lines);
        }
    }

    // get-metoder til min og max koordinater
    public float getMinLat(){
        return minlat;
    }
    public float getMinLon(){
        return minlon;
    }
    public float getMaxlat(){
        return maxlat;
    }
    public float getMaxlon(){
        return maxlon;
    }

    // metode der bruges til at parse en OSM film
    private void loadOSM(InputStream input) throws XMLStreamException, FactoryConfigurationError {
        var reader = XMLInputFactory.newInstance().createXMLStreamReader(new BufferedInputStream(input)); // laver en ny XML Reader der læser tags
        var id2way = new HashMap<Long, OSMWay>(); // map til veje, der gemmer rel-id og listen af nodes for vejen
        var nodes = new ArrayList<OSMNode>(); // liste af nodes for den enkelte vej
        var rel = new ArrayList<OSMWay>(); // liste med osm ways
        long relID = 0; // giver et id til en relation
        var type = WayType.UNKNOWN; // sætter først typen til unknows. kan ændres undervejs i metoden til f.eks. LAKE
        while (reader.hasNext()) { // læser hver linje én af gangen
            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT: // får det første på den givne linje i osm-filen. f.eks. om det er en node
                    var name = reader.getLocalName();
                    // switch statement, som gør noget forskelligt ud fra hvad det første ord på linjen er
                    switch (name) {
                        // indlæser min og max koordinaterne for hele det valgte datasæt
                        case "bounds":
                            maxlat = -Float.parseFloat(reader.getAttributeValue(null, "minlat"));
                            minlon = 0.56f * Float.parseFloat(reader.getAttributeValue(null, "minlon"));
                            minlat = -Float.parseFloat(reader.getAttributeValue(null, "maxlat"));
                            maxlon = 0.56f * Float.parseFloat(reader.getAttributeValue(null, "maxlon"));
                            break;
                        // giver id, lat og lon på nodes og tilføjer det til et NodeMap
                        case "node":
                            var id = Long.parseLong(reader.getAttributeValue(null, "id"));
                            var lat = Float.parseFloat(reader.getAttributeValue(null, "lat"));
                            var lon = Float.parseFloat(reader.getAttributeValue(null, "lon"));
                            id2node.add(new OSMNode(id, 0.56f * lon, -lat));
                            break;
                        // en way har i osm-filen en reference til de nodes der angiver vejen kaldet nd, der indeholder nodes' id nummer.
                        // her gemmes disse nodes i en arrayliste "nodes"
                        case "nd":
                            var ref = Long.parseLong(reader.getAttributeValue(null, "ref"));
                            nodes.add(id2node.get(ref));
                            break;
                        // sætter type til UNKNOWN for veje (kan måske sættes til noget andet, når vi har defineret flere typer veje?)
                        case "way":
                            relID = Long.parseLong(reader.getAttributeValue(null, "id"));
                            type = WayType.UNKNOWN;
                            break;
                        // hvis et punkt har tag natural og water, er det en sø. ændrer WayType til LAKE
                        case "tag":
                            var k = reader.getAttributeValue(null, "k");
                            var v = reader.getAttributeValue(null, "v");
                            switch (k) {
                                case "landuse":
                                    type = switch (v) {
                                        case "forest" -> WayType.FOREST;
                                        case "residential" -> WayType.RESIDENTIAL;
                                        case "industrial", "port" -> WayType.INDUSTRIAL;
                                        case "meadow" -> WayType.MEADOW;
                                        case "cemetery" -> WayType.CEMETERY;
                                        case "allotments" -> WayType.ALLOTMENTS;
                                        case "farmyard" -> WayType.FARMYARD;
                                        case "commercial" -> WayType.COMMERCIAL;
                                        case "construction" -> WayType.CONSTRUCTION;
                                        case "retail" -> WayType.RETAIL;
                                        case "farmland" -> WayType.FARMLAND;
                                        case "orchard" -> WayType.ORCHARD;
                                        case "vineyard" -> WayType.VINEYARD;
                                        case "basin" -> WayType.BASIN;
                                        case "grass" -> WayType.GRASS;
                                        case "greenfield" -> WayType.GREENFIELD;
                                        case "greenhouse_horticulture" -> WayType.GREENHOUSE_HORTICULTURE;
                                        case "military" -> WayType.MILITARY;
                                        case "plant_nursery" -> WayType.PLANT_NURSERY;
                                        case "quarry" -> WayType.QUARRY;
                                        case "recreation_ground" -> WayType.RECREATION_GROUND;
                                        case "village_green" -> WayType.VILLAGE_GREEN;
                                        case "salt_pond" -> WayType.SALT_POND;
                                        case "brownfield" -> WayType.BROWNFIELD;
                                        case "garages" -> WayType.GARAGES;
                                        case "landfill" -> WayType.LANDFILL;
                                        case "railway" -> WayType.RAILWAY;
                                        case "religious" -> WayType.RELIGIOUS;
                                        default -> type;
                                    };
                                    break;
                            }
                            break;
                        // relations har member-tags, der referer til
                        case "member":
                            ref = Long.parseLong(reader.getAttributeValue(null, "ref"));
                            var elm = id2way.get(ref);
                            if (elm != null) rel.add(elm);
                            break;
                        case "relation":
                            id = Long.parseLong(reader.getAttributeValue(null, "id"));
                            if (id == 1305702) {
                                System.out.println("Done");
                            }
                            break;

                    }

                    break;
                case XMLStreamConstants.END_ELEMENT: // får det sidste på den givne linje i osm-filen.
                    switch (reader.getLocalName()) {
                        // laver en ny OSMWay ud fra listen af de nodes, som vejen indeholder
                        case "way":
                            var way = new PolyLine(nodes);
                            id2way.put(relID, new OSMWay(nodes)); // tilføjer vejen til mappet med linjer
                            lines.get(type).add(way); // tilføjer vejen til mappet med veje og dens tilhørende type
                            nodes.clear(); // clearer nodes så den er tom når næste linje skal indlæses
                            break;
                        case "relation":
                            if (type == WayType.LAKE && !rel.isEmpty()) {
                                lines.get(type).add(new MultiPolygon(rel));
                            }
                            rel.clear();
                            break;
                    }
                    break;
            }
        }
        System.out.println("Done");
    }

    // tilføjer en observer til listen observers
    // Runnable = metode der ikke tager nogle argumenter eller returnerer noget
    public void addObserver(Runnable observer) {
        observers.add(observer);
    }

    /* alle der er registreret som observers, får af vide, at der er sket en ændring.
     bruges steder, hvor modellen vil fortælle dens observers, at der er sket en vigtig ændring i modellen.
     f.eks. for at fortælle repaint-metoden at der er kommet en ny linje den skal tegnes. */
    public void notifyObservers() {
        for (var observer : observers) {
            observer.run();
        }
    }

    // iterator over types
    public Iterable<Drawable> iterable(WayType type) {
        return lines.get(type);
    }

    public static KdNode getKDTreeUNKNOWN() {
        return KDTreeUNKNOWN;
    }
    public static KdNode getKDTreeRESIDENTIAL() {
        return KDTreeRESIDENTIAL;
    }
}
