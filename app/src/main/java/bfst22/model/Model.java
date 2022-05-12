package bfst22.model;

import bfst22.model.dijkstra.DirectedEdge;
import bfst22.model.dijkstra.EdgeWeightedDigraph;
import bfst22.model.addresses.AddressObject;
import bfst22.model.addresses.TST;
import bfst22.model.kdtree.KdTree;
import bfst22.view.Drawable;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.zip.ZipFile;
import java.io.File;

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

    /////////////// FELTER ///////////////////

    // Min og max koordinater for hele datasættet. Hedder bounds i osm filen
    private float minlat, minlon, maxlat, maxlon;

    //Felter til alle KD-træerne
    private final KdTree KDTreeUNKNOWN, KDTreeRESIDENTIAL, KDTreeLAKE, KDTreeBEACH, KDTreeSAND, KDTreeFOREST, KDTreeWATER, KDTreeBUILDING, KDTreeINDUSTRIAL, KDTreeMEADOW, KDTreePARK,
            KDTreeCEMETERY, KDTreeALLOTMENTS, KDTreeCOMMERCIAL, KDTreeCONSTRUCTION, KDTreeRETAIL, KDTreeFARMLAND, KDTreeGRASS, KDTreeGREENHOUSE_HORTICULTURE, KDTreeRAILWAY,
            KDTreeRELIGIOUS, KDTreeMOTORWAY, KDTreeCOASTLINE, KDTreePRIMARY, KDTreeSECONDARY, KDTreeTERTIARY, KDTreeLIVING_STREET, KDTreeSERVICE, KDTreePEDESTRIAN, KDTreeTRACK,
            KDTreeROAD, KDTreeFOOTWAY_ANDMORE, KDTreeBRIDLEWAY, KDTreeCYCLEWAY, KDTreeRESIDENTIALWAY, KDTreeCONSTRUCTIONWAY, KDTreeRIVER_ANDMORE, KDTreeHOSPITAL, KDTreePITCH,
            KDTreeRECREATION_GROUND, KDTreeSCHOOL, KDTreeFERRY, KDTreeGRAVEYARD, KDTreeNodesInGraphAllWays, KDTreeNodesInGraphCAR, KDTreeNodesInGraphBIKE, KDTreeNodesInGraphFOOT,
            KDTreeTERRITORIAl;

    // Grafer
    private EdgeWeightedDigraph graphCAR, graphBIKE, graphFOOT;
    // Felter der bruges til at lave edges til graferne
    private double maxspeed = 0;
    private boolean isOneway, isRoundabout;
    private boolean vehicle, bicycle, foot;
    private boolean canGoByCar, canGoByBike, canGoByFoot;
    private TST tst; // Ternary Search Tree objekt
    private Coastline coastline = new Coastline(); // Coastline objekt
    private List<PolyLine> listOfCoastlines = new ArrayList<>(); // Liste med alle coastlines
    // Lister, maps og arrays der bruges til at koble OSMNodes og indeks sammen i graferne
    private OSMNode[] arrayOfOSMNodesInGraphCAR, arrayOfOSMNodesInGraphBIKE, arrayOfOSMNodesInGraphFOOT;
    private ArrayList<DirectedEdge> listOfAllEdgesInWaysAllWays = new ArrayList<>();
    private ArrayList<DirectedEdge> listOfAllEdgesInWaysCAR = new ArrayList<>();
    private ArrayList<DirectedEdge> listOfAllEdgesInWaysBIKE = new ArrayList<>();
    private ArrayList<DirectedEdge> listOfAllEdgesInWaysFOOT = new ArrayList<>();
    private ArrayList<OSMNode> listOfAlleNodesInGraphAllWays = new ArrayList<>();
    private ArrayList<OSMNode> listOfAlleNodesInGraphCAR = new ArrayList<>();
    private ArrayList<OSMNode> listOfAlleNodesInGraphBIKE = new ArrayList<>();
    private ArrayList<OSMNode> listOfAlleNodesInGraphFOOT = new ArrayList<>();
    private HashMap<OSMNode, Integer> mapOfOSMNodesToIndexCAR = new HashMap<>();
    private HashMap<OSMNode, Integer> mapOfOSMNodesToIndexBIKE = new HashMap<>();
    private HashMap<OSMNode, Integer> mapOfOSMNodesToIndexFOOT = new HashMap<>();

    // NodeMap til nodes, der gemmer id, lon og lat for en node. NodeMap er en sorteret liste
    NodeMap id2node = new NodeMap();
    // Liste af observers. Observers gør så objekter kan blive notificeret om ændringer i andre objekter
    List<Runnable> observers = new ArrayList<>();
    // Map med type (om det f.eks. er lake eller highway) og tilhørende linje
    Map<WayType, List<Drawable>> lines = new EnumMap<>(WayType.class);{
        for (var type : WayType.values()) lines.put(type, new ArrayList<>());}
    // Map med coastlines
    Map<WayType, List<Drawable>> coastlinesMap = new EnumMap<>(WayType.class);{
        for (var type : WayType.values()) coastlinesMap.put(type, new ArrayList<>());}




    /////////////// KONSTRUKTØR ///////////////////
    public Model(String filename) throws IOException, XMLStreamException, FactoryConfigurationError, ClassNotFoundException {
        var time = -System.nanoTime();

        if (filename.endsWith(".zip")) {   // Unzipper en zip-fil og læser den herefter på samme måde som osm-fil
            var zip = new ZipFile(new File(filename)); // Læser zip-filen én fil af gangen
            var entry = zip.entries().nextElement(); // Læser næste fil i zip-filen
            loadOSM(zip.getInputStream(entry));
            System.out.println("Færdig med at loade OSM");


        } else if (filename.endsWith(".osm")) {// Læser filen som en osm-fil
            loadOSM(new FileInputStream(filename));
            System.out.println("Færdig med at loade OSM");


            listOfCoastlines = coastline.makeCoastlineObject(coastlinesMap, WayType.COASTLINE);

            //listOfCoastlines = makeCoastlineObject(coastlinesMap);
            //Laver et map for veje hvor man kan henholdsvis køre bil, cykle og gå
            mapOfOSMNodesToIndexCAR = makeMapOfOSMNodesToIndex(listOfAllEdgesInWaysCAR, mapOfOSMNodesToIndexCAR);
            mapOfOSMNodesToIndexBIKE = makeMapOfOSMNodesToIndex(listOfAllEdgesInWaysBIKE, mapOfOSMNodesToIndexBIKE);
            mapOfOSMNodesToIndexFOOT = makeMapOfOSMNodesToIndex(listOfAllEdgesInWaysFOOT, mapOfOSMNodesToIndexFOOT);
            //Laver graf for veje hvor man kan henholdsvis køre bil, cykle og gå
            graphCAR = makeGraph(mapOfOSMNodesToIndexCAR, listOfAllEdgesInWaysCAR, graphCAR);
            graphBIKE = makeGraph(mapOfOSMNodesToIndexBIKE, listOfAllEdgesInWaysBIKE, graphBIKE);
            graphFOOT = makeGraph(mapOfOSMNodesToIndexFOOT, listOfAllEdgesInWaysFOOT, graphFOOT);
            // Laver arrays for veje hvor man kan henholdsvis køre bil, cykle og gå
            arrayOfOSMNodesInGraphCAR = makeArrayOfOSMNodesInGraph(mapOfOSMNodesToIndexCAR, arrayOfOSMNodesInGraphCAR);
            arrayOfOSMNodesInGraphBIKE = makeArrayOfOSMNodesInGraph(mapOfOSMNodesToIndexBIKE, arrayOfOSMNodesInGraphBIKE);
            arrayOfOSMNodesInGraphFOOT = makeArrayOfOSMNodesInGraph(mapOfOSMNodesToIndexFOOT, arrayOfOSMNodesInGraphFOOT);


        } else if (filename.endsWith(".obj")) { // Læser filen som en binary file (obj)
            try (var input = new ObjectInputStream(new BufferedInputStream(new FileInputStream(filename)))) {
                minlat = input.readFloat();
                minlon = input.readFloat();
                maxlat = input.readFloat();
                maxlon = input.readFloat();
                lines = (Map<WayType, List<Drawable>>) input.readObject();
                listOfCoastlines = (List<PolyLine>) input.readObject();
                Object o = input.readObject();
                tst = (TST) o;
                listOfAllEdgesInWaysAllWays = (ArrayList<DirectedEdge>) input.readObject();
                listOfAllEdgesInWaysCAR =(ArrayList<DirectedEdge>) input.readObject();
                listOfAllEdgesInWaysBIKE = (ArrayList<DirectedEdge>) input.readObject();
                listOfAllEdgesInWaysFOOT = (ArrayList<DirectedEdge>) input.readObject();
                listOfAlleNodesInGraphAllWays = (ArrayList<OSMNode>) input.readObject();
                listOfAlleNodesInGraphCAR =(ArrayList<OSMNode>) input.readObject();
                listOfAlleNodesInGraphBIKE = (ArrayList<OSMNode>) input.readObject();
                listOfAlleNodesInGraphFOOT = (ArrayList<OSMNode>) input.readObject();

                mapOfOSMNodesToIndexCAR = (HashMap<OSMNode, Integer>) input.readObject();
                mapOfOSMNodesToIndexBIKE = (HashMap<OSMNode, Integer>) input.readObject();
                mapOfOSMNodesToIndexFOOT = (HashMap<OSMNode, Integer>) input.readObject();

                graphCAR = (EdgeWeightedDigraph) input.readObject();
                graphBIKE = (EdgeWeightedDigraph) input.readObject();
                graphFOOT = (EdgeWeightedDigraph) input.readObject();

                arrayOfOSMNodesInGraphCAR = (OSMNode[]) input.readObject();
                arrayOfOSMNodesInGraphBIKE = (OSMNode[]) input.readObject();
                arrayOfOSMNodesInGraphFOOT = (OSMNode[]) input.readObject();

                //VIGTIGT at skrive i samme rækkefølge som i save()
            }
        } else {
            lines.put(WayType.UNKNOWN, Files.lines(Paths.get(filename))
                    .map(Line::new)
                    .collect(toList()));

        }
        // Måler load time
        time += System.nanoTime(); // Load time i nanosekunder
        System.out.println("Load time: " + (long) (time / 1e6) + " ms"); // Printer load time i millisekunder

        // Gemmer den indlæste data som en .obj-fil, så den vigtige data serialiseres
        if (!filename.endsWith(".obj")) save(filename);

        //Laver træer
        KDTreeUNKNOWN = new KdTree(lines.get(WayType.UNKNOWN));
        KDTreeLAKE = new KdTree(lines.get(WayType.LAKE));
        KDTreeBEACH = new KdTree(lines.get(WayType.BEACH));
        KDTreeSAND = new KdTree(lines.get(WayType.SAND));
        KDTreeFOREST = new KdTree(lines.get(WayType.FOREST));
        KDTreeBUILDING = new KdTree(lines.get(WayType.BUILDING));
        KDTreeRESIDENTIAL = new KdTree(lines.get(WayType.RESIDENTIAL));
        KDTreeINDUSTRIAL = new KdTree(lines.get(WayType.INDUSTRIAL));
        KDTreeMEADOW = new KdTree(lines.get(WayType.MEADOW));
        KDTreeWATER = new KdTree(lines.get(WayType.WATER));
        KDTreePARK = new KdTree(lines.get(WayType.PARK));
        KDTreeCEMETERY = new KdTree(lines.get(WayType.CEMETERY));
        KDTreeALLOTMENTS = new KdTree(lines.get(WayType.ALLOTMENTS));
        KDTreeCOMMERCIAL = new KdTree(lines.get(WayType.COMMERCIAL));
        KDTreeCONSTRUCTION = new KdTree(lines.get(WayType.CONSTRUCTION));
        KDTreeRETAIL = new KdTree(lines.get(WayType.RETAIL));
        KDTreeFARMLAND = new KdTree(lines.get(WayType.FARMLAND));
        KDTreeGRASS = new KdTree(lines.get(WayType.GRASS));
        KDTreeGREENHOUSE_HORTICULTURE = new KdTree(lines.get(WayType.GREENHOUSE_HORTICULTURE));
        KDTreeRAILWAY = new KdTree(lines.get(WayType.RAILWAY));
        KDTreeRELIGIOUS = new KdTree(lines.get(WayType.RELIGIOUS));
        KDTreeMOTORWAY = new KdTree(lines.get(WayType.MOTORWAY));
        KDTreePRIMARY = new KdTree(lines.get(WayType.PRIMARY));
        KDTreeSECONDARY = new KdTree(lines.get(WayType.SECONDARY));
        KDTreeTERTIARY = new KdTree(lines.get(WayType.TERTIARY));
        KDTreeLIVING_STREET = new KdTree(lines.get(WayType.LIVING_STREET));
        KDTreeSERVICE = new KdTree(lines.get(WayType.SERVICE));
        KDTreePEDESTRIAN = new KdTree(lines.get(WayType.PEDESTRIAN));
        KDTreeTRACK = new KdTree(lines.get(WayType.TRACK));
        KDTreeROAD = new KdTree(lines.get(WayType.ROAD));
        KDTreeFOOTWAY_ANDMORE = new KdTree(lines.get(WayType.FOOTWAY_ANDMORE));
        KDTreeBRIDLEWAY = new KdTree(lines.get(WayType.BRIDLEWAY));
        KDTreeCYCLEWAY = new KdTree(lines.get(WayType.CYCLEWAY));
        KDTreeRESIDENTIALWAY = new KdTree(lines.get(WayType.RESIDENTIALWAY));
        KDTreeCONSTRUCTIONWAY = new KdTree(lines.get(WayType.CONSTRUCTIONWAY));
        KDTreeHOSPITAL = new KdTree(lines.get(WayType.HOSPITAL));
        KDTreeRECREATION_GROUND = new KdTree(lines.get(WayType.RECREATION_GROUND));
        KDTreePITCH = new KdTree(lines.get(WayType.PITCH));
        KDTreeSCHOOL = new KdTree(lines.get(WayType.SCHOOL));
        KDTreeFERRY = new KdTree(lines.get(WayType.FERRY));
        KDTreeGRAVEYARD = new KdTree(lines.get(WayType.GRAVEYARD));
        KDTreeRIVER_ANDMORE = new KdTree(lines.get(WayType.RIVER_ANDMORE));
        KDTreeCOASTLINE = new KdTree(lines.get(WayType.COASTLINE));
        KDTreeTERRITORIAl = new KdTree(lines.get(WayType.TERRITORIAl));
        KDTreeNodesInGraphAllWays = new KdTree(listOfAlleNodesInGraphAllWays, 0);
        KDTreeNodesInGraphCAR = new KdTree(listOfAlleNodesInGraphCAR, 0);
        KDTreeNodesInGraphBIKE = new KdTree(listOfAlleNodesInGraphBIKE, 0);
        KDTreeNodesInGraphFOOT = new KdTree(listOfAlleNodesInGraphFOOT, 0);


        // TIL TEST:
        //System.out.println(graphCAR);
        //System.out.println(graphBIKE);
        //System.out.println(graphFOOT);
        //System.out.println("indegree " + graph.getIndegree()[]);
    }

    // Metode til at gemme data i en .obj-fil
    public void save(String basename) throws FileNotFoundException, IOException {
        try (var out = new ObjectOutputStream(new FileOutputStream(basename + ".obj"))) {
            out.writeFloat(minlat);
            out.writeFloat(minlon);
            out.writeFloat(maxlat);
            out.writeFloat(maxlon);
            out.writeObject(lines);
            out.writeObject(listOfCoastlines);
            out.writeObject(tst);

            out.writeObject(listOfAllEdgesInWaysAllWays);
            out.writeObject(listOfAllEdgesInWaysCAR);
            out.writeObject(listOfAllEdgesInWaysBIKE);
            out.writeObject(listOfAllEdgesInWaysFOOT);
            out.writeObject(listOfAlleNodesInGraphAllWays);
            out.writeObject(listOfAlleNodesInGraphCAR);
            out.writeObject(listOfAlleNodesInGraphBIKE);
            out.writeObject(listOfAlleNodesInGraphFOOT);

            out.writeObject(mapOfOSMNodesToIndexCAR);
            out.writeObject(mapOfOSMNodesToIndexBIKE);
            out.writeObject(mapOfOSMNodesToIndexFOOT);

            out.writeObject(graphCAR);
            out.writeObject(graphBIKE);
            out.writeObject(graphFOOT);

            out.writeObject(arrayOfOSMNodesInGraphCAR);
            out.writeObject(arrayOfOSMNodesInGraphBIKE);
            out.writeObject(arrayOfOSMNodesInGraphFOOT);

        }
    }

    // Metode der bruges til at parse en OSM fil
    private void loadOSM(InputStream input) throws XMLStreamException, FactoryConfigurationError {
        var reader = XMLInputFactory.newInstance().createXMLStreamReader(new BufferedInputStream(input)); // laver en ny XML Reader der læser tags
        var id2way = new HashMap<Long, OSMWay>(); // map til veje, der gemmer rel-id og listen af nodes for vejen
        var nodes = new ArrayList<OSMNode>(); // liste af nodes for den enkelte vej
        var rel = new ArrayList<OSMWay>(); // liste med osm ways for en relation
        String city = "";
        String housenumber = "";
        String postcode = "";
        String street = "";
        OSMNode osmNodeToAddress = null;
        long relID = 0; // giver et id til en relation
        var type = WayType.UNKNOWN; // sætter først typen til unknows. dette ændres senere i parseren
        while (reader.hasNext()) { // læser hver linje i filen én af gangen
            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT: // Læser det første element på linjen
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
                            osmNodeToAddress = new OSMNode(id, 0.56f * lon, -lat);
                            id2node.add(osmNodeToAddress); //map der en node, her er key gadenavn og value id
                            break;
                        // nodes i en way starter med tagget 'nd'. Tilføjer disse nodes til nodes-listen for vejen
                        case "nd":
                            var ref = Long.parseLong(reader.getAttributeValue(null, "ref"));
                            var node = id2node.get(ref);
                            if (node == null) break; // fordi nogle nodes i veje peger ikke på nogle nodes i filen
                            nodes.add(node);
                            break;
                        // starter med at sætte WayType til UNKNOWN
                        case "way":
                            relID = Long.parseLong(reader.getAttributeValue(null, "id"));
                            type = WayType.UNKNOWN;
                            break;
                        // læser tags og opdaterer felter og lokalvariable udfra det
                        case "tag":
                            var k = reader.getAttributeValue(null, "k");
                            var v = reader.getAttributeValue(null, "v");
                            switch (k) {
                                case "maxspeed":
                                    try {
                                        maxspeed = Double.parseDouble(v);
                                    } catch (NumberFormatException e) {
                                        //System.out.println("Invalid maxspeed: " + v); // fordi maxspeed nogle gange er angivet som noget andet end et tal
                                    }
                                case "oneway":
                                    isOneway = v.equals("yes");
                                case "junction":
                                    isRoundabout = v.equals("roundabout");
                                case "motor-vehicle":
                                    vehicle = v.equals("yes");
                                case "vehicle":
                                    vehicle = v.equals("yes");
                                case "bicycle":
                                    if(v.equals("yes") || v.equals("designated")) {
                                        bicycle = true;
                                    }
                                case "foot":
                                    foot = v.equals("designated");
                                case "addr:city":
                                    city = v;
                                    break;
                                case "addr:housenumber":
                                    housenumber = v;
                                    break;
                                case "addr:postcode":
                                    postcode = v;
                                    break;
                                case "addr:street":
                                    street = v;
                                    break;
                                case "landuse":
                                    type = switch (v) {
                                        // Sætter WayType for tagget landuse
                                        // Hvis tagget ikke findes, sæt til unknown
                                        case "forest" -> WayType.FOREST;
                                        case "residential" -> WayType.RESIDENTIAL;
                                        case "industrial" -> WayType.INDUSTRIAL;
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
                                        case "grass" -> WayType.GRASS;
                                        case "greenhouse_horticulture" -> WayType.GREENHOUSE_HORTICULTURE;
                                        case "railway" -> WayType.RAILWAY;
                                        case "religious" -> WayType.RELIGIOUS;
                                        case "recreation_ground" -> WayType.RECREATION_GROUND;
                                        default -> WayType.UNKNOWN;
                                    };
                                    break;
                                case "natural":
                                    // Sætter WayType for tagget natural
                                    type = switch (v) {
                                        case "beach" -> WayType.BEACH;
                                        case "sand" -> WayType.SAND;
                                        case "water" -> WayType.WATER;
                                        case "grassland" -> WayType.GRASSLAND;
                                        case "heath" -> WayType.GRASS;
                                        case "bare_rock", "rock" -> WayType.BARE_ROCK;
                                        case "coastline" -> WayType.COASTLINE;
                                        default -> WayType.UNKNOWN;
                                    };
                                    break;
                                case "water":
                                    // Sætter WayType for tagget water
                                    type = switch (v) {
                                        case "lake", "lagoon" -> WayType.LAKE;
                                        case "canal", "river" -> WayType.RIVER_ANDMORE;
                                        case "harbour" -> WayType.HARBOUR;
                                        default -> WayType.UNKNOWN;
                                    };
                                    break;
                                case "waterway":
                                    // Sætter WayType for tagget waterway
                                    type = switch (v) {
                                        case "river", "canal" -> WayType.RIVER_ANDMORE;
                                        case "stream", "ditch", "drain" -> WayType.STREAM_ANDMORE;
                                        case "dock" -> WayType.DOCK;
                                        case "lake" -> WayType.LAKE;
                                        default -> WayType.UNKNOWN;
                                    };
                                    break;
                                case("border_type"):
                                    // Sætter WayType for tagget border_type
                                    type = switch (v){
                                        case "territorial" -> WayType.TERRITORIAl;
                                        default -> WayType.UNKNOWN;
                                    };
                                    break;
                                case "amenity":
                                    // Sætter WayType for tagget amenity
                                    type = switch (v) {
                                        case "school", "kindergarten" -> WayType.SCHOOL;
                                        case "parking" -> WayType.PARKING;
                                        case "hospital" -> WayType.HOSPITAL;
                                        //case "kindergarten" -> WayType.SCHOOL;
                                        case "grave_yard" -> WayType.GRAVEYARD;
                                        default -> WayType.UNKNOWN;
                                    };
                                    break;
                                case "building":
                                    // Sætter WayType for tagget building
                                    type = switch (v) {
                                        case "yes", "church", "retail", "detached", "house", "toilets",
                                                "shed", "garage", "school", "hotel", "commercial",
                                                "public", "slurry_tank", "greenhouse", "office", "chapel", "semidetached_house",
                                                "bungalow", "lighthouse", "service", "warehouse", "farm", "electricity",
                                                "farm_auxiliary", "silo", "roof", "storage_tank", "kindergarten", "barn",
                                                "stable", "terrace", "apartments", "sports_hall", "cabin",
                                                "kiosk", "windmill", "cowshed", "supermarket", "watermill", "civic",
                                                "hut", "carport", "ruins", "construction", "hospital", "residential",
                                                "bunker", "transformer_tower", "dormitory", "bank", "university",
                                                "sports_centre", "cathedral", "manor", "college", "hangar", "government",
                                                "prison", "train_station", "manufacture", "transportation", "watertower",
                                                "synagogue", "fortress", "garages", "childcare", "stilt_house", "clinic",
                                                "castle", "observatory", "mansion", "museum", "stadium", "riding_hall",
                                                "temple", "cottage", "storage", "bakehouse", "religious", "mosque", "shop",
                                                "theatre", "fire_station", "allotment_house", "library", "sports_center",
                                                "data_center", "pumping_station", "townhall", "tower", "cinema", "monastery",
                                                "private", "skyscraper", "factory", "restaurant", "chalet", "brewery", "pagoda",
                                                "cold_store", "ground_station", "skyway", "outbuilding", "pavilion", "shelter",
                                                "institution", "palace", "hall", "outhouse", "production_hall", "mall",
                                                "chapel_of_rest", "dentist", "conservatory", "bell_tower", "farmhouse",
                                                "court_house" -> WayType.BUILDING;
                                        default -> WayType.UNKNOWN;
                                    };
                                    break;
                                case "route":
                                    // Sætter WayType for tagget route
                                    type = switch (v) {
                                        case "ferry" -> WayType.FERRY;
                                        default -> WayType.UNKNOWN;
                                    };
                                    break;
                                case "leisure":
                                    // Sætter WayType for tagget leisure
                                    type = switch (v) {
                                        case "park" -> WayType.PARK;
                                        case "garden" -> WayType.GARDEN;
                                        case "marina" -> WayType.MARINA;
                                        case "nature_reserve" -> WayType.NATURE_RESERVE;
                                        case "pitch", "track" -> WayType.PITCH;
                                        case "sports_centre", "stadium" -> WayType.SPORTS_CENTRE;
                                        default -> WayType.UNKNOWN;
                                    };
                                    break;
                                case "highway":
                                    // Sætter WayType for tagget hihway. Highway er alle veje man kan køre, cykle eller gå på
                                    type = switch (v) {
                                        case "motorway", "motorway_link" -> WayType.MOTORWAY;
                                        case "primary", "primary_link" -> WayType.PRIMARY;
                                        case "trunk", "trunk_link" -> WayType.TRUNK;
                                        case "secondary", "secondary_link" -> WayType.SECONDARY;
                                        case "tertiary", "tertiary_link" -> WayType.TERTIARY;
                                        case "unclassified" -> WayType.UNCLASSIFIED;
                                        case "residential" -> WayType.RESIDENTIALWAY;
                                        case "living_street" -> WayType.LIVING_STREET;
                                        case "service" -> WayType.SERVICE;
                                        case "pedestrian" -> WayType.PEDESTRIAN;
                                        case "track" -> WayType.TRACK;
                                        case "road" -> WayType.ROAD;
                                        case "raceway" -> WayType.RACEWAY;
                                        case "footway", "path", "crossing" -> WayType.FOOTWAY_ANDMORE;
                                        case "bridleway" -> WayType.BRIDLEWAY;
                                        case "steps" -> WayType.STEPS;
                                        case "cycleway" -> WayType.CYCLEWAY;
                                        case "construction" -> WayType.CONSTRUCTIONWAY;
                                        default -> WayType.UNKNOWN;
                                    };
                                    break;
                                    }
                            break;
                        case "member": // relations har member-tags, der referer til det, der er en del af den givne relation
                            ref = Long.parseLong(reader.getAttributeValue(null, "ref"));
                            var elm = id2way.get(ref);
                            if (elm != null && type != WayType.TERRITORIAl) rel.add(elm);
                            break;
                        case "relation":
                            id = Long.parseLong(reader.getAttributeValue(null, "id"));
                            break;
                    }
                    break;
                case XMLStreamConstants.END_ELEMENT: // Læser det sidste element på listen.
                    switch (reader.getLocalName()) {
                        case "way":
                            if(type == null) break;
                            if (nodes.size() == 0) break;

                            var way = new PolyLine(nodes, type); // laver en ny Polyline ud fra listen af de nodes, som vejen indeholder
                            id2way.put(relID, new OSMWay(nodes)); // tilføjer vejen til mappet med linjer
                            lines.get(type).add(way); // tilføjer vejen til mappet med veje og dens tilhørende type
                            nodes.clear(); // clearer nodes så den er tom når næste linje skal indlæses

                            // Tilføjer coastlines til et map
                            if (type == WayType.COASTLINE) {
                                coastlinesMap.get(type).add(way);
                                nodes.clear(); // clearer nodes så den er tom når næste linje skal indlæses
                            }

                            // Går herind, hvis way er en vej man kan køre, cykle eller gå på. Så skal den nemlig med i en af graferne
                            if (type == WayType.MOTORWAY || type == WayType.PRIMARY || type == WayType.TRUNK || type == WayType.SECONDARY || type == WayType.TERTIARY
                                    || type == WayType.UNCLASSIFIED || type == WayType.RESIDENTIALWAY || type == WayType.LIVING_STREET || type == WayType.SERVICE
                                    || type == WayType.PEDESTRIAN || type == WayType.ROAD || type == WayType.FOOTWAY_ANDMORE || type == WayType.STEPS
                                    || type == WayType.CYCLEWAY || type == WayType.CONSTRUCTIONWAY) {

                                // De fleste veje har defineret en maxspeed. Hvis ikke, sætter vi her en selv
                                // Baseret på oplysninger fra OpenStreetMap om de forskellige typers typiske fartgrænser
                                if(maxspeed == 0) {
                                    maxspeed = switch (type) {
                                        case MOTORWAY -> 120;
                                        case TRUNK -> 110;
                                        case PRIMARY, SECONDARY, ROAD, CONSTRUCTIONWAY -> 80;
                                        case UNCLASSIFIED, TERTIARY -> 50;
                                        case RESIDENTIALWAY -> 40;
                                        case LIVING_STREET, SERVICE, CYCLEWAY -> 20;
                                        case PEDESTRIAN, FOOTWAY_ANDMORE, STEPS -> 5;
                                        default -> 0;
                                    };
                                }

                                // Sætter booleans for om der må køres, cykles eller gås på vejen
                                if(type == WayType.MOTORWAY || type == WayType.TRUNK) {
                                    canGoByCar = true; // MAN MÅ KUN KØRE BIL
                                }
                                if(type == WayType.PRIMARY || type == WayType.SECONDARY || type == WayType.TERTIARY ||
                                        type == WayType.UNCLASSIFIED || type == WayType.RESIDENTIALWAY || type == WayType.LIVING_STREET ||
                                        type == WayType.SERVICE || type == WayType.ROAD || type == WayType.CONSTRUCTIONWAY) {
                                    canGoByCar = true; // MAN MÅ KØRE BIL
                                    canGoByBike = true; // OG CYKLE
                                    canGoByFoot = true; // OG GÅ
                                }
                                if(type == WayType.PEDESTRIAN || type == WayType.FOOTWAY_ANDMORE || type == WayType.STEPS) {
                                    canGoByFoot = true; // MAN MÅ KUN GÅ
                                    if(bicycle) {
                                        canGoByBike = true; // MED MINDRE VEJEN ER TAGGET MED, AT MAN GERNE MÅ CYKLE
                                    }
                                    if(vehicle) { // MED MINDRE VEJEN ER TAGGET MED, AT MAN GERNE MÅ KØRE BIL
                                        canGoByCar = true;
                                    }
                                }
                                if(type == WayType.CYCLEWAY) {
                                    canGoByBike = true; // MAN MÅ KUN CYKLE
                                    if(foot) {
                                        canGoByFoot = true; // MED MINDRE VEJEN ER TAGGET MED, AT MAN GERNE MÅ GÅ
                                    }
                                }

                                //Laver alle edges'ene til graferne
                                for (int j = 0; j < way.getNodes().size()-1; j++){

                                    double weightTime = calculateTime(way.getNodes().get(j), way.getNodes().get(j + 1)); // Hvor lang tid det tager at køre vejen
                                    double weightDistance = calculateDistance(way.getNodes().get(j), way.getNodes().get(j+1)); // Hvor lang vejen er

                                    // Grafen for CAR får weightTime som vægt
                                    DirectedEdge edgeOneWayCAR = new DirectedEdge(way.getNodes().get(j), way.getNodes().get(j+1), weightTime);
                                    DirectedEdge edgeOtherWayCAR = new DirectedEdge(way.getNodes().get(j+1), way.getNodes().get(j), weightTime);
                                    // Graferne for BIKE og FOOT får weightDistance som vægt
                                    DirectedEdge edgeOneWayBIKEandFOOT = new DirectedEdge(way.getNodes().get(j), way.getNodes().get(j+1), weightDistance);
                                    DirectedEdge edgeOtherWayBIKEandFOOT = new DirectedEdge(way.getNodes().get(j+1), way.getNodes().get(j), weightDistance);

                                    //Tilføjer edge og dens nodes til listerne med edges og nodes
                                    if(isOneway || isRoundabout) {  // Tilføjer en edge den ene vej
                                        if(canGoByCar) {
                                          listOfAllEdgesInWaysCAR.add(edgeOneWayCAR); // tilføjer til graf for CAR
                                            listOfAlleNodesInGraphCAR.add(way.getNodes().get(j)); // tilføjer til liste af OSMNodes for CAR
                                        }
                                        if(canGoByBike) {
                                            listOfAllEdgesInWaysBIKE.add(edgeOneWayBIKEandFOOT); // tilføjer til graf for BIKE
                                            listOfAlleNodesInGraphBIKE.add(way.getNodes().get(j)); // tilføjer til liste af OSMNodes for BIKE
                                        }
                                        if(canGoByFoot) {
                                            listOfAllEdgesInWaysFOOT.add(edgeOneWayBIKEandFOOT); // tilføjer til graf for FOOT
                                            listOfAlleNodesInGraphFOOT.add(way.getNodes().get(j)); // tilføjer til liste af OSMNodes for FOOT
                                        }
                                        // Tilføjer også egde og dens start node til de lister for alle veje. Bruges til når man dobbeltklikker eller skal tegne grafen
                                        listOfAllEdgesInWaysAllWays.add(edgeOneWayCAR);
                                        listOfAlleNodesInGraphAllWays.add(way.getNodes().get(j));

                                    } else { // Tilføjer en edge den ene vej og den anden vej
                                        if(canGoByCar) {
                                            listOfAllEdgesInWaysCAR.add(edgeOneWayCAR); // tilføjer til graf for CAR
                                            listOfAlleNodesInGraphCAR.add(way.getNodes().get(j)); // tilføjer til liste af OSMNodes for CAR
                                            listOfAllEdgesInWaysCAR.add(edgeOtherWayCAR); // tilføjer til graf for CAR
                                            listOfAlleNodesInGraphCAR.add(way.getNodes().get(j+1)); // tilføjer til liste af OSMNodes for CAR
                                        }
                                        if(canGoByBike) {
                                            listOfAllEdgesInWaysBIKE.add(edgeOneWayBIKEandFOOT); // tilføjer til graf for BIKE
                                            listOfAlleNodesInGraphBIKE.add(way.getNodes().get(j)); // tilføjer til liste af OSMNodes for BIKE
                                            listOfAllEdgesInWaysBIKE.add(edgeOtherWayBIKEandFOOT); // tilføjer til graf for BIKE
                                            listOfAlleNodesInGraphBIKE.add(way.getNodes().get(j+1)); // tilføjer til liste af OSMNodes for BIKE
                                        }
                                        if(canGoByFoot) {
                                            listOfAllEdgesInWaysFOOT.add(edgeOneWayBIKEandFOOT); // tilføjer til graf for FOOT
                                            listOfAlleNodesInGraphFOOT.add(way.getNodes().get(j)); // tilføjer til liste af OSMNodes for FOOT
                                            listOfAllEdgesInWaysFOOT.add(edgeOtherWayBIKEandFOOT); // tilføjer til graf for FOOT
                                            listOfAlleNodesInGraphFOOT.add(way.getNodes().get(j+1)); // tilføjer til liste af OSMNodes for FOOT
                                        }
                                        // Tilføjer også de to egdes og deres nodes til de lister for alle veje. Bruges til når man dobbeltklikker eller skal tegne grafen
                                        listOfAllEdgesInWaysAllWays.add(edgeOneWayCAR);
                                        listOfAllEdgesInWaysAllWays.add(edgeOtherWayCAR);
                                        listOfAlleNodesInGraphAllWays.add(way.getNodes().get(j));
                                        listOfAlleNodesInGraphAllWays.add(way.getNodes().get(j+1));
                                    }
                                }
                                // Sætter felter tilbage til start, så næste vej ikke får dem med
                                maxspeed = 0;
                                isOneway = false;
                                canGoByCar = false;
                                canGoByBike = false;
                                canGoByFoot = false;
                            }
                            type = null;
                            break;
                        case "relation":
                            if (type == WayType.FOREST || type == WayType.BUILDING && type !=WayType.TERRITORIAl && type != WayType.TERTIARY && !rel.isEmpty()) {
                                lines.get(type).add(new MultiPolygon(rel, type)); // tilføjer Multipolygon til typens liste
                            }
                            rel.clear();
                            break;
                        case "node":
                            if(!city.equals("") && !housenumber.equals("") && !postcode.equals("") && !street.equals("")){
                                //laver Ternary Search Tree/Trie med alle adresserne
                                if(tst == null) {
                                    tst = new TST(new AddressObject(street, housenumber, postcode, city, osmNodeToAddress.getLat(), osmNodeToAddress.getLon()));
                                } else {
                                    tst.put(street, new AddressObject(street, housenumber, postcode, city, osmNodeToAddress.getLat(), osmNodeToAddress.getLon()));
                                }
                            }
                            break;
                    }
                    break;
            }
        }
    }



    /////////////// METODER OG HJÆLPE-METODER DER LAVER GRAFER ///////////////////

    // Laver og retunerer en EdgeWeigtedGraph
    private EdgeWeightedDigraph makeGraph(HashMap<OSMNode, Integer> mapOfOSMNodesToIndex, ArrayList<DirectedEdge> listOfAllEdgesInWays, EdgeWeightedDigraph graph) {
        graph = new EdgeWeightedDigraph(mapOfOSMNodesToIndex.size());

        // Tilføjer hver edge fra den givne liste til grafen
        for (DirectedEdge edge : listOfAllEdgesInWays) {
            graph.addEdge(edge, mapOfOSMNodesToIndex);
        }
        return graph;
    }

    // Laver map der mapper OSMNodes i en graf til et index
    private HashMap<OSMNode, Integer> makeMapOfOSMNodesToIndex(ArrayList<DirectedEdge> listOfAllEdgesInWays, HashMap<OSMNode, Integer> mapOfOSMNodesToIndex) {
        int counter = 0;
        for (DirectedEdge edge : listOfAllEdgesInWays) {
            OSMNode start = edge.getStart();
            OSMNode end = edge.getEnd();

            if (!mapOfOSMNodesToIndex.containsKey(start)){ // Tjek om den allerede er der
                mapOfOSMNodesToIndex.put(start, counter); // Ellers tilføj på næste ledige plads
                counter++;
            }
            if (!mapOfOSMNodesToIndex.containsKey(end)){ // Tjek om den allerede er der
                mapOfOSMNodesToIndex.put(end, counter); // Ellers tilføj på næste ledige plads
                counter++;
            }
        }
        return mapOfOSMNodesToIndex;
    }


    // Laver et array der holder hver OSMNodes i grafen på dens tilhørende index. Bruges til at få OSMNode udfra index.
    // En OSMNode vil have samme index i arrayet og i mappet (metoden ovenover)
    private OSMNode[] makeArrayOfOSMNodesInGraph(HashMap<OSMNode, Integer> mapOfOSMNodesToIndex, OSMNode[] arrayOfOSMNodesInGraph){
        arrayOfOSMNodesInGraph = new OSMNode[mapOfOSMNodesToIndex.size()];

        // Tjekker index for en OSMNode og tilføjer den til samme index i arrayet
        for(Map.Entry<OSMNode,Integer> entry : mapOfOSMNodesToIndex.entrySet()) {
            int index = entry.getValue();
            arrayOfOSMNodesInGraph[index] = entry.getKey();
        }
        return arrayOfOSMNodesInGraph;
    }


    /////////////// UDREGNER DISTANCE OG TID METODER ///////////////////

    // Bruges til at regne vægten (weightDistance) ud for edge:
    private double calculateDistance(OSMNode punkt1, OSMNode punkt2) {
        // Kilde for formel at regne distance: https://www.geeksforgeeks.org/program-distance-two-points-earth/
        // Converts from degrees to radians.
        double lon1 = Math.toRadians(punkt1.getLon());
        double lon2 = Math.toRadians(punkt2.getLon());
        double lat1 = Math.toRadians(punkt1.getLat());
        double lat2 = Math.toRadians(punkt2.getLat());

        // Haversine formula
        double dlon = lon2 - lon1;
        double dlat = lat2 - lat1;
        double a = Math.pow(Math.sin(dlat / 2), 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.pow(Math.sin(dlon / 2), 2);

        double c = 2 * Math.asin(Math.sqrt(a));

        // Radius of earth in kilometers
        double r = 6371;

        // calculate the result
        return (c * r);
    }

    // Bruges til at regne vægten (weightTime) ud for edge:
    private double calculateTime(OSMNode punkt1, OSMNode punkt2) {
        double distance = calculateDistance(punkt1, punkt2);
        if(distance < 0) throw new IllegalArgumentException("Distance must be a non-negative integer");
        return distance / maxspeed;
    }




    /////////////// TILFØJER OBSERVERS ///////////////////

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



    /////////////// GETTERS ///////////////////

    // Getters til min og max koordinater
    public float getMinLat() {
        return minlat;
    }
    public float getMinLon() {
        return minlon;
    }
    public float getMaxlat() {
        return maxlat;
    }
    public float getMaxlon() {
        return maxlon;
    }
    // Getters til Kd træerne
    public KdTree getKDTreeRESIDENTIAL() {return KDTreeRESIDENTIAL;}
    public KdTree getKDTreeLAKE() {return KDTreeLAKE;}
    public KdTree getKDTreeBEACH() {return KDTreeBEACH;}
    public KdTree getKDTreeSAND() {return KDTreeSAND;}
    public KdTree getKDTreeFOREST() {return KDTreeFOREST;}
    public KdTree getKDTreeWATER() {return KDTreeWATER;}
    public KdTree getKDTreeBUILDING() {return KDTreeBUILDING;}
    public KdTree getKDTreeINDUSTRIAL() {return KDTreeINDUSTRIAL;}
    public KdTree getKDTreeMEADOW() {return KDTreeMEADOW;}
    public KdTree getKDTreePARK() {return KDTreePARK;}
    public KdTree getKDTreeCEMETERY() {return KDTreeCEMETERY;}
    public KdTree getKDTreeALLOTMENTS() {return KDTreeALLOTMENTS;}
    public KdTree getKDTreeCONSTRUCTION() {return KDTreeCONSTRUCTION;}
    public KdTree getKDTreeCOMMERCIAL() {return KDTreeCOMMERCIAL;}
    public KdTree getKDTreeRETAIL() {return KDTreeRETAIL;}
    public KdTree getKDTreeFARMLAND() {return KDTreeFARMLAND;}
    public KdTree getKDTreeGRASS () {return KDTreeGRASS;}
    public KdTree getKDTreeGREENHOUSE_HORTICULTURE () {return KDTreeGREENHOUSE_HORTICULTURE;}
    public KdTree getKDTreeRAILWAY () {return KDTreeRAILWAY;}
    public KdTree getKDTreeRELIGIOUS () {return KDTreeRELIGIOUS;}
    public KdTree getKDTreeMOTORWAY () {return KDTreeMOTORWAY;}
    public KdTree getKDTreePRIMARY () {return KDTreePRIMARY;}
    public KdTree getKDTreeSECONDARY () {return KDTreeSECONDARY;}
    public KdTree getKDTreeTERTIARY () {return KDTreeTERTIARY;}
    public KdTree getKDTreeLIVING_STREET () {return KDTreeLIVING_STREET;}
    public KdTree getKDTreeSERVICE () {return KDTreeSERVICE;}
    public KdTree getKDTreePEDESTRIAN () {return KDTreePEDESTRIAN;}
    public KdTree getKDTreeROAD () {return KDTreeROAD;}
    public KdTree getKDTreeFOOTWAY_ANDMORE () {return KDTreeFOOTWAY_ANDMORE;}
    public KdTree getKDTreeBRIDLEWAY () {return KDTreeBRIDLEWAY;}
    public KdTree getKDTreeCYCLEWAY () {return KDTreeCYCLEWAY;}
    public KdTree getKDTreeRESIDENTIALWAY () {return KDTreeRESIDENTIALWAY;}
    public KdTree getKDTreeRIVER_ANDMORE () {return KDTreeRIVER_ANDMORE;}
    public KdTree getKDTreeHOSPITAL () {return KDTreeHOSPITAL;}
    public KdTree getKDTreeRECREATION_GROUND () {return KDTreeRECREATION_GROUND;}
    public KdTree getKDTreePITCH () {return KDTreePITCH;}
    public KdTree getKDTreeSCHOOL () {return KDTreeSCHOOL;}
    public KdTree getKDTreeFERRY () {return KDTreeFERRY;}
    public KdTree getKDTreeGRAVEYARD () {return KDTreeGRAVEYARD;}
    public KdTree getKDTreeTERRITORIAl () {return KDTreeTERRITORIAl;}
    public KdTree getKDTreeNodesInGraphAllWays() {return  KDTreeNodesInGraphAllWays;}
    public KdTree getKDTreeNodesInGraphCAR () {return KDTreeNodesInGraphCAR;}
    public KdTree getKDTreeNodesInGraphBIKE () {return KDTreeNodesInGraphBIKE;}
    public KdTree getKDTreeNodesInGraphFOOT () {return KDTreeNodesInGraphFOOT;}
    // Getter til coastlines
    public List<PolyLine> getListOfCoastlines() {
        return listOfCoastlines;
    }
    //Getters til graferne:
    public EdgeWeightedDigraph getGraphCAR() {return graphCAR;}
    public EdgeWeightedDigraph getGraphBIKE() {return graphBIKE;}
    public EdgeWeightedDigraph getGraphFOOT() {return graphFOOT;}
    //Getters til maps fra index til OSMNode
    public HashMap<OSMNode, Integer> getMapOfOSMNodesToIndexCAR() {
        return mapOfOSMNodesToIndexCAR;
    }
    public HashMap<OSMNode, Integer> getMapOfOSMNodesToIndexBIKE() {
        return mapOfOSMNodesToIndexBIKE;
    }
    public HashMap<OSMNode, Integer> getMapOfOSMNodesToIndexFOOT() {
        return mapOfOSMNodesToIndexFOOT;
    }
    //Getters til arrays fra index til OSMNode
    public OSMNode[] getArrayOfOSMNodesInGraphCAR() {
        return arrayOfOSMNodesInGraphCAR;
    }
    public OSMNode[] getArrayOfOSMNodesInGraphBIKE() {
        return arrayOfOSMNodesInGraphBIKE;
    }
    public OSMNode[] getArrayOfOSMNodesInGraphFOOT() {
        return arrayOfOSMNodesInGraphFOOT;
    }
    //Getter til listen med alle edges i veje
    public List<DirectedEdge> getListOfAllEdgesInWaysAllWays() {
        return listOfAllEdgesInWaysAllWays;
    }
    //Getter til Ternary search tree med adresser
    public TST getTst() {return tst;}
}











