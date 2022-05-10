package bfst22.model;

import bfst22.model.dijkstra.DirectedEdge;
import bfst22.model.dijkstra.EdgeWeightedDigraph;
import bfst22.model.addresses.AddressObject;
import bfst22.model.addresses.TST;
import bfst22.model.kdtree.KdTree;
import bfst22.view.Drawable;
import javafx.scene.shape.Polyline;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
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
    private final KdTree KDTreeUNKNOWN;
    private final KdTree KDTreeRESIDENTIAL;
    private final KdTree KDTreeLAKE;
    private final KdTree KDTreeBEACH;
    private final KdTree KDTreeSAND;
    private final KdTree KDTreeFOREST;
    private final KdTree KDTreeWATER;
    private final KdTree KDTreeBUILDING;
    private final KdTree KDTreeINDUSTRIAL;
    private final KdTree KDTreeMEADOW;
    private final KdTree KDTreePARK;
    private final KdTree KDTreeCEMETERY;
    private final KdTree KDTreeALLOTMENTS;
    private final KdTree KDTreeCOMMERCIAL;
    private final KdTree KDTreeCONSTRUCTION;
    private final KdTree KDTreeRETAIL;
    private final KdTree KDTreeFARMLAND;
    private final KdTree KDTreeGRASS;
    private final KdTree KDTreeGREENHOUSE_HORTICULTURE;
    private final KdTree KDTreeRAILWAY;
    private final KdTree KDTreeRELIGIOUS;
    private final KdTree KDTreeMOTORWAY;
    private final KdTree KDTreeCOASTLINE;
    private final KdTree KDTreePRIMARY;
    private final KdTree KDTreeSECONDARY;
    private final KdTree KDTreeTERTIARY;
    private final KdTree KDTreeLIVING_STREET;
    private final KdTree KDTreeSERVICE;
    private final KdTree KDTreePEDESTRIAN;
    private final KdTree KDTreeTRACK;
    private final KdTree KDTreeROAD;
    private final KdTree KDTreeFOOTWAY_ANDMORE;
    private final KdTree KDTreeBRIDLEWAY;
    private final KdTree KDTreeCYCLEWAY;
    private final KdTree KDTreeRESIDENTIALWAY;
    private final KdTree KDTreeCONSTRUCTIONWAY;
    private final KdTree KDTreeRIVER_ANDMORE;
    private final KdTree KDTreeHOSPITAL;
    private final KdTree KDTreePITCH;
    private final KdTree KDTreeRECREATION_GROUND;
    private final KdTree KDTreeSCHOOL;
    private final KdTree KDTreeFERRY;
    private final KdTree KDTreeGRAVEYARD;
    private final KdTree KDTreeNodesInGraph;



//Måske static
    NodeMap id2node = new NodeMap(); // NodeMap til nodes, der gemmer id, lon og lat for en node. NodeMap er en sorteret liste
    List<Runnable> observers = new ArrayList<>();  // Liste af observers. Observers gør så objekter kan blive notificeret om ændringer i andre objekter

    // Min og max koordinater for hele datasættet. Hedder bounds i osm filen
    private float minlat, minlon, maxlat, maxlon;
    private int maxspeed = 0;
    private boolean isOneway;
    private boolean isRoundabout;
    private List<PolyLine> listOfCoastlines;
    private EdgeWeightedDigraph graph;
    private static OSMNode[] arrayOfOSMNodesInGraph;
    private TST tst;


    // Map med type (om det f.eks. er lake eller highway) og tilhørende linje
    Map<WayType, List<Drawable>> lines = new EnumMap<>(WayType.class);{
        for (var type : WayType.values()) lines.put(type, new ArrayList<>());
    }

    //Map med coastlines
    Map<WayType, List<Drawable>> coastlinesMap = new EnumMap<>(WayType.class);{
        for (var type : WayType.values()) coastlinesMap.put(type, new ArrayList<>());
    }

    //Liste med HIGHWAYS
    private static ArrayList<DirectedEdge> listOfAllEdgesInWays = new ArrayList<>();

    //Liste med alle OSMNodes in graph
    private List<OSMNode> listOfAlleNodesInGraph = new ArrayList<>();

    //Map med OSMNode to index. Bruges til at lave en graf
    private static Map<OSMNode, Integer> mapOfOSMNodesToIndex = new HashMap<>();



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
                lines = (Map<WayType, List<Drawable>>) input.readObject();
                listOfCoastlines = (List<PolyLine>) input.readObject();
                graph = (EdgeWeightedDigraph) input.readObject();
                //tst = (TST) input.readObject();

                //VIGTIGT at skrive i samme rækkefølge som i save()
                //tilføj de ting vi har tilføjet i save
            }
        } else {
            lines.put(WayType.UNKNOWN, Files.lines(Paths.get(filename))
                    .map(Line::new)
                    .collect(toList()));
        }
        // måler load time
        time += System.nanoTime(); // load time i nanosekunder
        System.out.println("Load time: " + (long) (time / 1e6) + " ms"); // printer load time i millisekunder

        // gemmer den indlæste data som en .obj-fil, så den vigtige data serialiseres
        if (!filename.endsWith(".obj")) save(filename);


        //Lav træer
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
        KDTreeNodesInGraph = new KdTree(listOfAlleNodesInGraph, 0);


        //lav liste med coastlines
        listOfCoastlines = makeCoastlineObject(coastlinesMap);

        //lav dijkstra graf
        makeMapOfOSMNodesToIndex(listOfAllEdgesInWays);
        makeGraph();
        //System.out.println(graph);

        //calculateDistance(listOfAllEdgesInWays.get(10).getStart(), listOfAllEdgesInWays.get(10).getEnd());
    }



    // metode til at gemme data i en .obj-fil
    public void save(String basename) throws FileNotFoundException, IOException {
        try (var out = new ObjectOutputStream(new FileOutputStream(basename + ".obj"))) {
            out.writeFloat(minlat);
            out.writeFloat(minlon);
            out.writeFloat(maxlat);
            out.writeFloat(maxlon);
            out.writeObject(lines);
            out.writeObject(listOfCoastlines);
            out.writeObject(graph);
            //out.writeObject(tst);



            //tilføj alle de ting vi har lavet: tst

            //Gem kystlinjer her
        }
    }

    // get-metoder til min og max koordinater
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

    // metode der bruges til at parse en OSM film
    private void loadOSM(InputStream input) throws XMLStreamException, FactoryConfigurationError {
        var reader = XMLInputFactory.newInstance().createXMLStreamReader(new BufferedInputStream(input)); // laver en ny XML Reader der læser tags
        var id2way = new HashMap<Long, OSMWay>(); // map til veje, der gemmer rel-id og listen af nodes for vejen
        var nodes = new ArrayList<OSMNode>(); // liste af nodes for den enkelte vej
        var rel = new ArrayList<OSMWay>(); // liste med osm ways
        String city = "";
        String housenumber = "";
        String postcode = "";
        String street = "";
        OSMNode osmNodeToAddress = null;
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
                            osmNodeToAddress = new OSMNode(id, 0.56f * lon, -lat);
                            id2node.add(osmNodeToAddress);
                            //map der en node, her er key gadenavn og value id
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
                                        //currentElement.setWayType(WayType.valueOf(v.toUpperCase(Locale.ROOT)));
                                        //Hvis tagget ikke findes, sæt til unknown
                                        //Current type
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
                                        default -> type;
                                    };
                                    break;
                                case "natural":
                                    type = switch (v) {
                                        case "beach" -> WayType.BEACH;
                                        case "sand" -> WayType.SAND;
                                        case "water" -> WayType.WATER;
                                        case "grassland" -> WayType.GRASSLAND;
                                        case "bare_rock", "rock" -> WayType.BARE_ROCK;
                                        case "coastline" -> WayType.COASTLINE;
                                        default -> type;
                                    };
                                    break;
                                case "water":
                                    type = switch (v) {
                                        case "lake", "lagoon" -> WayType.LAKE;
                                        case "canal", "river" -> WayType.RIVER_ANDMORE;
                                        case "harbour" -> WayType.HARBOUR;
                                        default -> type;
                                    };
                                    break;
                                case "waterway":
                                    type = switch (v) {
                                        case "river", "canal" -> WayType.RIVER_ANDMORE;
                                        case "stream", "ditch", "drain" -> WayType.STREAM_ANDMORE;
                                        case "dock" -> WayType.DOCK;
                                        case "lake" -> WayType.LAKE;
                                        default -> type;
                                    };
                                    break;
                                case "amenity":
                                    type = switch (v) {
                                        case "school", "kindergarten" -> WayType.SCHOOL;
                                        case "parking" -> WayType.PARKING;
                                        case "hospital" -> WayType.HOSPITAL;
                                        //case "kindergarten" -> WayType.SCHOOL;
                                        case "grave_yard" -> WayType.GRAVEYARD;
                                        default -> type;
                                    };
                                    break;
                                case "building":
                                    type = switch (v) {
                                        //Kunne være vi skulle have flere forskellige bygninger ENUM
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
                                        default -> type;
                                    };
                                    break;
                                case "route":
                                    type = switch (v) {
                                        case "ferry" -> WayType.FERRY;
                                        default -> type;
                                    };
                                    break;

                                case "leisure":
                                    type = switch (v) {
                                        case "park" -> WayType.PARK;
                                        case "garden" -> WayType.GARDEN;
                                        case "marina" -> WayType.MARINA;
                                        case "nature_reserve" -> WayType.NATURE_RESERVE;
                                        case "pitch", "track" -> WayType.PITCH;
                                        case "sports_centre", "stadium" -> WayType.SPORTS_CENTRE;
                                        default -> type;
                                    };
                                    break;
                                case "highway":
                                    type = switch (v) {
                                        //highway - primary.. ??
                                        //cycleway - track, lane ??
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
                                        default -> type;
                                    };
                                    break;
                                case "lanes":
                                    var lane = Integer.parseInt(v);
                                    //ikke alle har line
                                    //hvordan vælger vi hvilke der har to og ikke?
                                    break;
                                case "maxspeed":
                                    maxspeed = Integer.parseInt(v);

                                    //primary, secondary, tertiary = 80
                                    //primary, secondary, tertiary = 50
                                    //tertiary, residential = 40
                                    //nogle veje har ikke maxspeed (tertiary)

                                case "oneway":
                                    if (v.equals("no")) isOneway = false;
                                    else if (v.equals("yes")) isOneway = true;
                                case "junction":
                                    isRoundabout = v.equals("roundabout");
                            }
                            break;
                        // relations har member-tags, der referer til
                        case "member":
                            ref = Long.parseLong(reader.getAttributeValue(null, "ref"));
                            var elm = id2way.get(ref); //man får et OSMWay objekt
                            if (elm != null) rel.add(elm); //tilføjer OSMWay objekt til listen rel
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
                            var way = new PolyLine(nodes, type);
                            //var way = new PolyLine(nodes);
                            id2way.put(relID, new OSMWay(nodes)); // tilføjer vejen til mappet med linjer
                            lines.get(type).add(way); // tilføjer vejen til mappet med veje og dens tilhørende
                            nodes.clear(); // clearer nodes så den er tom når næste linje skal indlæses

                            //Tilføjer coastlines til en liste
                            if (type == WayType.COASTLINE) {
                                coastlinesMap.get(type).add(way);
                                nodes.clear(); // clearer nodes så den er tom når næste linje skal indlæses
                            }

                            if (type == WayType.MOTORWAY || type == WayType.PRIMARY || type == WayType.TRUNK || type == WayType.SECONDARY || type == WayType.TERTIARY
                                    || type == WayType.UNCLASSIFIED || type == WayType.RESIDENTIALWAY || type == WayType.LIVING_STREET || type == WayType.SERVICE
                                    || type == WayType.PEDESTRIAN || type == WayType.ROAD || type == WayType.FOOTWAY_ANDMORE || type == WayType.STEPS
                                    || type == WayType.CYCLEWAY || type == WayType.CONSTRUCTIONWAY) {

                                //if maxspeed != 0 - brug den maxspeed som er oplyst
                                //if maxspeed == 0 - find ud af hvad type vej det er og sæt en værdi
                                //vi vælger selv nogle standard værdier for de forskellige veje
                                if(maxspeed == 0) {
                                    maxspeed = switch (type) {
                                        case MOTORWAY -> 120; //110-130 km/t 130
                                        case TRUNK -> 110;
                                        case PRIMARY, SECONDARY, ROAD, CONSTRUCTIONWAY -> 80;
                                        //manlger typer til -LINK vejene
                                        case UNCLASSIFIED, TERTIARY -> 50; //secondary, road
                                        case RESIDENTIALWAY -> 40; //se kommentar nedenunder
                                        case LIVING_STREET, SERVICE, CYCLEWAY -> 20;  //se kommentar nedenunder
                                        case PEDESTRIAN, FOOTWAY_ANDMORE, STEPS -> 5;
                                        default -> 0; //skal vi have en default værdi på 20 måske?
                                    };
                                    break;
                                }
                                /////The order of values for the highway-tag ordered by assumed speed is://///
                                    //motorway - Usually the maxspeed can be kept for long distances, but these roads can be sensitive to long traffic jams. Usually forbidden for slow traffic (pedestrians, cyclists, agricultural, ...)
                                    //trunk - 	Similar to motorways, but these roads can have level crossings, so the stretches where the maximum speed can be reached are shorter. Best avoided when using slow vehicles (sometimes forbidden, depending on the local legislation).
                                    //primary, secondary, tertiary - Usually around 90-100 km/t in the countryside, and 50 (or even 30) km/t when crossing a residential center.
                                    //motorway-link - Reachable speed depends a lot on curvature, usually around 60-90 km/h
                                    //trunk-, primary-, secondary-, tertiary-link - The speed on these road pieces is very slow, as these often need to give way to the other traffic.
                                    //unclassified - will rarely exceed 50 km/t
                                    //residential - 50 km/t to 30 km/t
                                    //living_street - 20km/t
                                    //service - Service roads are usually found on private property or parking lots. The driver has to pay attention to manoeuvring vehicles, and must wait often
                                    //track (måske ikke tegne) - Tracks are roads of agricultural purpose. They usually connect fields to farms. The surface sometimes makes them inaccessible for regular cars, or at least limits the speeds to a very slow pace. Tracks should always be avoided for through-going traffic.
                                    //pedestrian - Pedestrian roads are normally forbidden for motorised vehicles, but can be allowed on certain hours (f.e. early in the morning), or for certain purposes (like delivering goods to shops). Even when allowed, only pedestrian speed is advisable. Cycling can also be forbidden on pedestrian highways.
                                    //road - er et wildcard; kan være motorway, men også living-street (vælger ca. midten af 20 og 110)
                                    //constuctionway - en vej, som er midlertidig under arbejde, men openstreetmap er måske ikke opdateret (sætter samme værdi som road)

                                for (int j = 0; j < way.getNodes().size()-1; j++){
                                    double weight = calculateTime(way.getNodes().get(j), way.getNodes().get(j+1));
                                    //System.out.println(weight);

                                    DirectedEdge edgeOneWay = new DirectedEdge(way.getNodes().get(j), way.getNodes().get(j+1), weight);
                                    DirectedEdge edgeOtherWay = new DirectedEdge(way.getNodes().get(j+1), way.getNodes().get(j), weight);

                                    //tilføj til liste med alle edges
                                    if(isOneway || isRoundabout) {
                                        listOfAllEdgesInWays.add(edgeOneWay);
                                        listOfAlleNodesInGraph.add(way.getNodes().get(j)); //måske lave denne liste om til et Set?
                                    } else {
                                        listOfAllEdgesInWays.add(edgeOneWay);
                                        listOfAlleNodesInGraph.add(way.getNodes().get(j)); // obs på at vi tilføjer nodes to gange

                                        listOfAllEdgesInWays.add(edgeOtherWay);
                                        listOfAlleNodesInGraph.add(way.getNodes().get(j+1)); /// obs på at vi tilføjer nodes to gange
                                    }

                                    //tjek om der er bil, cycle eller fodgænger
                                }

                                //sætter maxspeed til 0, fordi det er ikke alle veje der har tag maxspeed,
                                // så hvis vi ikke opdaterer dette, vil næste vej få samme værdi som den forrige
                                maxspeed = 0;
                            }
                            break;

                        case "relation":
                            if (type == WayType.FOREST && !rel.isEmpty()) {
                                lines.get(type).add(new MultiPolygon(rel, type));
                            }
                            rel.clear();
                            break;
                        case "node":
                            if(!city.equals("") && !housenumber.equals("") && !postcode.equals("") && !street.equals("")){
                                //laver Ternary Search Tree/Trie med alle adresserne
                                if(tst == null) {
                                    tst = new TST(new AddressObject(street, housenumber, postcode, city, osmNodeToAddress.getLat(), osmNodeToAddress.getLon()));
                                    //System.out.println("Laver TST");
                                } else {
                                    tst.put(street, new AddressObject(street, housenumber, postcode, city, osmNodeToAddress.getLat(), osmNodeToAddress.getLon()));
                                    //System.out.println("Tilføjer til TST");
                                }
                            }
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
    public Iterable<Drawable> iterable(WayType type) {return lines.get(type);}

    public KdTree getKDTreeUNKNOWN() {return KDTreeUNKNOWN;}
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
    public KdTree getKDTreeTRACK () {return KDTreeTRACK;}
    public KdTree getKDTreeROAD () {return KDTreeROAD;}
    public KdTree getKDTreeFOOTWAY_ANDMORE () {return KDTreeFOOTWAY_ANDMORE;}
    public KdTree getKDTreeBRIDLEWAY () {return KDTreeBRIDLEWAY;}
    public KdTree getKDTreeCYCLEWAY () {return KDTreeCYCLEWAY;}
    public KdTree getKDTreeRESIDENTIALWAY () {return KDTreeRESIDENTIALWAY;}
    public KdTree getKDTreeCONSTRUCTIONWAY() {return KDTreeCONSTRUCTIONWAY;}
    public KdTree getKDTreeRIVER_ANDMORE () {return KDTreeRIVER_ANDMORE;}
    public KdTree getKDTreeHOSPITAL () {return KDTreeHOSPITAL;}
    public KdTree getKDTreeRECREATION_GROUND () {return KDTreeRECREATION_GROUND;}
    public KdTree getKDTreePITCH () {return KDTreePITCH;}
    public KdTree getKDTreeSCHOOL () {return KDTreeSCHOOL;}
    public KdTree getKDTreeFERRY () {return KDTreeFERRY;}
    public KdTree getKDTreeGRAVEYARD () {return KDTreeGRAVEYARD;}
    public KdTree getKDTreeNodesInGraph () {return KDTreeNodesInGraph;}







    public Map<WayType, List<Drawable>> getCoastlinesMap() {return coastlinesMap;}

    public static List<OSMNode> getListOfOSMNodesOfCoastlines(Map<WayType, List<Drawable>> coastlines) {
        //så længe dens startpunkt ikke er dens slutpunkt - kør while loop
        //getLon/Lat fra OSMNode
        ArrayList<OSMNode> listOfOSMNodesOfCoastlines = new ArrayList<>();
        for (Drawable drawable : coastlines.get(WayType.COASTLINE)) {
            listOfOSMNodesOfCoastlines.addAll(((PolyLine) drawable).getNodes());
        }
        return listOfOSMNodesOfCoastlines;
    }


    public List<PolyLine> makeCoastlineObject(Map<WayType, List<Drawable>> linesList) {
        //det som er tilføjet til listen

        //Starten for CurrentWay
        OSMNode startOSMNode = null;

        //Slutning for CurrentWay
        OSMNode endOSMNode = null;
        //det som ikke endnu er tilføjet
        //Start for den coastline man kigger på lige nu
        OSMNode currentStartOSMNode;
        //Slut for den coastline man kigger på lige nu
        OSMNode currentEndOSMNode;
        PolyLine currentWay = null;


        List<OSMNode> listOfNodesInCoastline = new ArrayList<>();

        //Holder på vores færdige coastlines
        List<PolyLine> listOfCoastlines = new ArrayList<>();

        //Alle ways af typen coastline
        List<Drawable> unSortedCoastlines = linesList.get(WayType.COASTLINE);

        while (!unSortedCoastlines.isEmpty()) {

            //System.out.println("unSortedCoastlines size: " + unSortedCoastlines.size());
            // De coastlines som ikke passede
            List<Drawable> buildUpCoastlinesList = new ArrayList<>();
            //System.out.println("buildUpCoastlinesList size: " + buildUpCoastlinesList.size());
            for (Drawable coastline : unSortedCoastlines) {

                if (listOfNodesInCoastline.size() == 0) {
                    //listOfNodesInCoastline.addAll((lines.get(WayType.COASTLINE).get(0)));
                    listOfNodesInCoastline.addAll(((PolyLine) coastline).getNodes());
                    startOSMNode = ((PolyLine) coastline).getNodes().get(0);
                    endOSMNode = ((PolyLine) coastline).getNodes().get(((PolyLine) coastline).getNodes().size() - 1);
                    //Opretter currentWay med Nodes i vores første vej
                    currentWay = new PolyLine((ArrayList<OSMNode>) listOfNodesInCoastline);
                    //System.out.println("Vi er ved o case");
                    continue;
                }

                currentStartOSMNode = ((PolyLine) coastline).getNodes().get(0);
                currentEndOSMNode = ((PolyLine) coastline).getNodes().get(((PolyLine) coastline).getNodes().size() - 1);
//
                // SITUATION 1: Begge pile mod højre.
                if (currentStartOSMNode.getLat() == endOSMNode.getLat() && currentStartOSMNode.getLon() == endOSMNode.getLon()) {
                    listOfNodesInCoastline.addAll(((PolyLine) coastline).getNodes());
                    endOSMNode = currentEndOSMNode;
                    currentWay = new PolyLine((ArrayList<OSMNode>) listOfNodesInCoastline);
                    //System.out.println("Vi er ved situation 1");
                }
                // SITUATION 2: Begge pile mod midten
                else if (endOSMNode.getLat() == currentEndOSMNode.getLat() && endOSMNode.getLon() == currentEndOSMNode.getLon()) {
                    // Vend nummer to pil om og tilføjer til listOfNodesInCoastLine
                    Collections.reverse(((PolyLine) coastline).getNodes());
                    listOfNodesInCoastline.addAll(((PolyLine) coastline).getNodes());
                    endOSMNode = currentEndOSMNode;
                    currentWay = new PolyLine((ArrayList<OSMNode>) listOfNodesInCoastline);
                    //System.out.println("Vi er ved situation 2");
                }
                // SITUATION 3: Begge pile væk fra midten // Start og start mødes //Første pil drejet rundt
                else if (currentStartOSMNode.getLat() == startOSMNode.getLat() && currentStartOSMNode.getLon() == startOSMNode.getLon()) {
                    Collections.reverse(((PolyLine) coastline).getNodes());
                    listOfNodesInCoastline.addAll(0, ((PolyLine) coastline).getNodes());
                    startOSMNode = listOfNodesInCoastline.get(0);
                    currentWay = new PolyLine((ArrayList<OSMNode>) listOfNodesInCoastline);
                    //System.out.println("Vi er ved situation 3");
                }
                // SITUATION 4: Begge pile mod venstre // Vores start == nye slut
                else if (currentEndOSMNode.getLat() == startOSMNode.getLat() && currentEndOSMNode.getLon() == startOSMNode.getLon()) {
                    listOfNodesInCoastline.addAll(0, ((PolyLine) coastline).getNodes());
                    startOSMNode = listOfNodesInCoastline.get(0);
                    currentWay = new PolyLine((ArrayList<OSMNode>) listOfNodesInCoastline);
                    //System.out.println("Vi er ved situation 4");
                } else {
                    buildUpCoastlinesList.add(coastline);
                    //System.out.println("buildUpCoastlinesList: " + buildUpCoastlinesList.size());
                    //System.out.println("Jeg går ind i else");
                }
                //Hvis end og start det samme, ville alle coastlines være tilføjet
                if (endOSMNode.getLat() == startOSMNode.getLat() && endOSMNode.getLon() == startOSMNode.getLon()) {
                    listOfNodesInCoastline.addAll(((PolyLine) coastline).getNodes());
                    listOfCoastlines.add(currentWay);
                    listOfNodesInCoastline.clear();
                    //System.out.println("Vi er færdige med kystlinjen og har tilføjet den");

                }
            }

           if(buildUpCoastlinesList.size() == unSortedCoastlines.size()) {
               //System.out.println("listOfNodesInCoastline størelse: " + listOfNodesInCoastline.size());

                    //tilføjer den færdige polyline figur til listen, der gemmer alle øerne i form af polylines
                    listOfCoastlines.add(currentWay);
                    listOfNodesInCoastline.clear();

            }
            else {
               // gammel liste = ny liste (ting vi mangler at kigge på)
               unSortedCoastlines = buildUpCoastlinesList;
           }} //Forloop stop

        //hvis start og slut ikke er det samme - sæt en linje imellem
        System.out.println("done med kystliner");
        return listOfCoastlines;

    }

    public void makeMapOfOSMNodesToIndex(ArrayList<DirectedEdge> listOfAllEdgesInWays) {
        int counter = 0;
        for (DirectedEdge edge : listOfAllEdgesInWays) {
            OSMNode start = edge.getStart();
            OSMNode end = edge.getEnd();
            // Tjek om den allerede er der
            if (!mapOfOSMNodesToIndex.containsKey(start)){
                mapOfOSMNodesToIndex.put(start, counter);
                counter++;
            }
            if (!mapOfOSMNodesToIndex.containsKey(end)){
                mapOfOSMNodesToIndex.put(end, counter);
                counter++;
            }
        }
    }


    //Laver ikke grafen ordentligt - Mangler at tilføje random veje rundt omkring
    // Ikke noget med typen - nogle tertiary bliver tilføjet, nogle gør ikke
    // Nogle nodes i vejene peger ikke på nogen nodes i OSM-filen. Kan det være derfor???
    //hvis start-element er 'nd', så tilføjer den således: nodes.add(id2node.get(ref)) - det burde give NullPointerException (da ref ikke pejer på en node)??

    public void makeGraph(){
        graph = new EdgeWeightedDigraph(mapOfOSMNodesToIndex.size());

        for (DirectedEdge edge : listOfAllEdgesInWays){
            graph.addEdge(edge);
        }

        makeArrayOfOSMNodesInGraph(mapOfOSMNodesToIndex);
    }


    public double calculateDistance(OSMNode punkt1, OSMNode punkt2) {
        //Kilde for formel at regne distance: https://www.geeksforgeeks.org/program-distance-two-points-earth/
        // The math module contains a function named toRadians which converts from degrees to radians.
        double lon1 = Math.toRadians(punkt1.getLon());
        double lon2 = Math.toRadians(punkt2.getLon());
        double lat1 = Math.toRadians(punkt1.getLat());
        double lat2 = Math.toRadians(punkt1.getLat());
        //System.out.println("lon1: " + punkt1.getLon() + " lat1: " + punkt1.getLat());
        //System.out.println("lon2: " + punkt2.getLon() + " lat2: " + punkt2.getLat());
        //System.out.println("lon1RAD: " + lon1 + " lat1RAD: " + lat1);

        // Haversine formula
        double dlon = lon2 - lon1;
        double dlat = lat2 - lat1;
        double a = Math.pow(Math.sin(dlat / 2), 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.pow(Math.sin(dlon / 2), 2);

        double c = 2 * Math.asin(Math.sqrt(a));

        // Radius of earth in kilometers. Use 3956 for miles
        double r = 6371;

        // calculate the result
        return (c * r);
    }

    public double calculateTime(OSMNode punkt1, OSMNode punkt2) {
        double distance = calculateDistance(punkt1, punkt2);
        if(distance < 0) throw new IllegalArgumentException("Distance must be a non-negative integer");
        //if(distance == 0) throw new IllegalArgumentException("Distance must be a positive integer");
        /////////////nogle er 0.0 - hvad gør vi med det?

        //udregn vægten: tid=længde/maxspeed
        return distance / maxspeed;
    }

    public List<PolyLine> getListOfCoastlines() {
        return listOfCoastlines;
    }

    public static List<DirectedEdge> getListOfAllEdgesInWays() {
        return listOfAllEdgesInWays;
    }

    public static Map<OSMNode, Integer> getMapOfOSMNodesToIndex() {
        return mapOfOSMNodesToIndex;
    }


    //Laver et array der holder hver OSMNodes i grafen på dens tilhørende index. Bruges til at få OSMNode udfra index
    public void makeArrayOfOSMNodesInGraph(Map<OSMNode, Integer> mapOfOSMNodesToIndex){
        arrayOfOSMNodesInGraph = new OSMNode[mapOfOSMNodesToIndex.size()];
        for(Map.Entry<OSMNode,Integer> entry : mapOfOSMNodesToIndex.entrySet()) {
            int index = entry.getValue();
            arrayOfOSMNodesInGraph[index] = entry.getKey();
        }
    }

    public NodeMap getId2node() {
        return id2node;
    }


    public EdgeWeightedDigraph getGraph() {
        return graph;
    }


    public static OSMNode[] getArrayOfOSMNodesInGraph() {
        return arrayOfOSMNodesInGraph;
    }
}









