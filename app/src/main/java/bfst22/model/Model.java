package bfst22.model;

import bfst22.model.kdtree.KdNode;
import bfst22.model.kdtree.KdTree;
import bfst22.view.Drawable;

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
    private static KdNode KDTreeUNKNOWN;
    private static KdNode KDTreeNAVIGATION_LINE;
    private static KdNode KDTreeRESIDENTIAL;
    private static KdNode KDTreeLAKE;
    private static KdNode KDTreeBEACH;
    private static KdNode KDTreeSAND;
    private static KdNode KDTreeFOREST;
    private static KdNode KDTreeWATER;
    private static KdNode KDTreeBUILDING;
    private static KdNode KDTreeINDUSTRIAL;
    private static KdNode KDTreeMEADOW;
    private static KdNode KDTreePARK;
    private static KdNode KDTreeCEMETERY;
    private static KdNode KDTreeALLOTMENTS;
    private static KdNode KDTreeCOMMERCIAL;
    private static KdNode KDTreeCONSTRUCTION;
    private static KdNode KDTreeRETAIL;
    private static KdNode KDTreeFARMLAND;
    private static KdNode KDTreeGRASS;
    private static KdNode KDTreeGREENHOUSE_HORTICULTURE;
    private static KdNode KDTreeRAILWAY;
    private static KdNode KDTreeRELIGIOUS;
    private static KdNode KDTreeCOASTLINE;
    private static KdNode KDTreeMOTORWAY;
    private static KdNode KDTreePRIMARY;
    private static KdNode KDTreeSECONDARY;
    private static KdNode KDTreeTERTIARY;
    private static KdNode KDTreeLIVING_STREET;
    private static KdNode KDTreeSERVICE;
    private static KdNode KDTreePEDESTRIAN;
    private static KdNode KDTreeTRACK;
    private static KdNode KDTreeROAD;
    private static KdNode KDTreeFOOTWAY_ANDMORE;
    private static KdNode KDTreeBRIDLEWAY;
    private static KdNode KDTreeCYCLEWAY;
    private static KdNode KDTreeRESIDENTIALWAY;
    private static KdNode KDTreeCONSTRUCTIONWAY;
    private static KdNode KDTreeRIVER_ANDMORE;


    NodeMap id2node = new NodeMap(); // NodeMap til nodes, der gemmer id, lon og lat for en node. NodeMap er en sorteret liste
    List<Runnable> observers = new ArrayList<>();  // Liste af observers. Observers gør så objekter kan blive notificeret om ændringer i andre objekter

    // Min og max koordinater for hele datasættet. Hedder bounds i osm filen
    private float minlat, minlon, maxlat, maxlon;
    private static List<PolyLine> list;

    // Map med type (om det f.eks. er lake eller highway) og tilhørende linje
    Map<WayType, List<Drawable>> lines = new EnumMap<>(WayType.class);

    {
        for (var type : WayType.values()) lines.put(type, new ArrayList<>());
    }

    //Map med coastlines
    static Map<WayType, List<Drawable>> coastlinesMap = new EnumMap<>(WayType.class);

    {
        for (var type : WayType.values()) coastlinesMap.put(type, new ArrayList<>());
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

        //Lav træer
        KDTreeUNKNOWN = KdTree.buildKDTree(lines.get(WayType.UNKNOWN), true);
        KDTreeLAKE = KdTree.buildKDTree(lines.get(WayType.LAKE), true);
        KDTreeBEACH = KdTree.buildKDTree(lines.get(WayType.BEACH), true);
        KDTreeSAND = KdTree.buildKDTree(lines.get(WayType.SAND), true);
        KDTreeFOREST = KdTree.buildKDTree(lines.get(WayType.FOREST), true);
        KDTreeBUILDING = KdTree.buildKDTree(lines.get(WayType.BUILDING), true);
        KDTreeRESIDENTIAL = KdTree.buildKDTree(lines.get(WayType.RESIDENTIAL), true);
        KDTreeINDUSTRIAL = KdTree.buildKDTree(lines.get(WayType.INDUSTRIAL), true);
        KDTreeMEADOW = KdTree.buildKDTree(lines.get(WayType.MEADOW), true);
        KDTreeWATER = KdTree.buildKDTree(lines.get(WayType.WATER), true);
        KDTreeBUILDING = KdTree.buildKDTree(lines.get(WayType.BUILDING), true);
        KDTreeRESIDENTIAL = KdTree.buildKDTree(lines.get(WayType.RESIDENTIAL), true);
        KDTreeINDUSTRIAL = KdTree.buildKDTree(lines.get(WayType.INDUSTRIAL), true);
        KDTreeMEADOW = KdTree.buildKDTree(lines.get(WayType.MEADOW), true);
        KDTreePARK = KdTree.buildKDTree(lines.get(WayType.PARK), true);
        KDTreeCEMETERY = KdTree.buildKDTree(lines.get(WayType.CEMETERY), true);
        KDTreeALLOTMENTS = KdTree.buildKDTree(lines.get(WayType.ALLOTMENTS), true);
        KDTreeCOMMERCIAL = KdTree.buildKDTree(lines.get(WayType.COMMERCIAL), true);
        KDTreeCONSTRUCTION = KdTree.buildKDTree(lines.get(WayType.CONSTRUCTION), true);
        KDTreeRETAIL = KdTree.buildKDTree(lines.get(WayType.RETAIL), true);
        KDTreeFARMLAND = KdTree.buildKDTree(lines.get(WayType.FARMLAND), true);
        KDTreeGRASS = KdTree.buildKDTree(lines.get(WayType.GRASS), true);
        KDTreeGREENHOUSE_HORTICULTURE = KdTree.buildKDTree(lines.get(WayType.GREENHOUSE_HORTICULTURE), true);
        KDTreeRAILWAY = KdTree.buildKDTree(lines.get(WayType.RAILWAY), true);
        KDTreeRELIGIOUS = KdTree.buildKDTree(lines.get(WayType.RELIGIOUS), true);
        KDTreeMOTORWAY = KdTree.buildKDTree(lines.get(WayType.MOTORWAY), true);
        KDTreePRIMARY = KdTree.buildKDTree(lines.get(WayType.PRIMARY), true);
        KDTreeSECONDARY = KdTree.buildKDTree(lines.get(WayType.SECONDARY), true);
        KDTreeTERTIARY = KdTree.buildKDTree(lines.get(WayType.TERTIARY), true);
        KDTreeLIVING_STREET = KdTree.buildKDTree(lines.get(WayType.LIVING_STREET), true);
        KDTreeSERVICE = KdTree.buildKDTree(lines.get(WayType.SERVICE), true);
        KDTreePEDESTRIAN = KdTree.buildKDTree(lines.get(WayType.PEDESTRIAN), true);
        KDTreeTRACK = KdTree.buildKDTree(lines.get(WayType.TRACK), true);
        KDTreeROAD = KdTree.buildKDTree(lines.get(WayType.ROAD), true);
        KDTreeFOOTWAY_ANDMORE = KdTree.buildKDTree(lines.get(WayType.FOOTWAY_ANDMORE), true);
        KDTreeROAD = KdTree.buildKDTree(lines.get(WayType.ROAD), true);
        KDTreeBRIDLEWAY = KdTree.buildKDTree(lines.get(WayType.BRIDLEWAY), true);
        KDTreeCYCLEWAY = KdTree.buildKDTree(lines.get(WayType.CYCLEWAY), true);
        KDTreeRESIDENTIALWAY = KdTree.buildKDTree(lines.get(WayType.RESIDENTIALWAY), true);
        KDTreeCONSTRUCTIONWAY = KdTree.buildKDTree(lines.get(WayType.CONSTRUCTIONWAY), true);
        KDTreeRIVER_ANDMORE = KdTree.buildKDTree(lines.get(WayType.RIVER_ANDMORE), true);
        KDTreeCOASTLINE = KdTree.buildKDTree(coastlinesMap.get(WayType.COASTLINE), true);}


    // metode til at gemme data i en .obj-fil
    public void save(String basename) throws FileNotFoundException, IOException {
        try (var out = new ObjectOutputStream(new FileOutputStream(basename + ".obj"))) {
            out.writeFloat(minlat);
            out.writeFloat(minlon);
            out.writeFloat(maxlat);
            out.writeFloat(maxlon);
            out.writeObject(lines);
        }}

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
        var innerRole = new ArrayList<Drawable>();
        var outerRole = new ArrayList<Drawable>();
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
                                        //currentElement.setWayType(WayType.valueOf(v.toUpperCase(Locale.ROOT)));
                                        //Hvis tagget ikke findes, sæt til unknown
                                        //Current type
                                        case "forest" -> WayType.FOREST;
                                        case "residential" -> WayType.RESIDENTIAL;
                                        case "industrial"-> WayType.INDUSTRIAL;
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
                                        case "brownfield" -> WayType.BROWNFIELD;
                                        case "garages" -> WayType.GARAGES;
                                        case "landfill" -> WayType.LANDFILL;
                                        case "railway" -> WayType.RAILWAY;
                                        case "religious" -> WayType.RELIGIOUS;
                                        default -> type;
                                    };
                                    break;
                                case "natural":
                                    type = switch (v) {
                                        case "beach" -> WayType.BEACH;
                                        case "wood" -> WayType.WOOD;
                                        case "sand" -> WayType.SAND;
                                        case "tree_row" -> WayType.TREE_ROW;
                                        case "water" -> WayType.WATER;
                                        case "scrub" -> WayType.SCRUB;
                                        case "grassland" -> WayType.GRASSLAND;
                                        case "heath" -> WayType.HEATH;
                                        case "bay" -> WayType.BAY;
                                        case "bare_rock", "rock" -> WayType.BARE_ROCK;
                                        case "coastline" -> WayType.COASTLINE;
                                        default -> type;
                                    };
                                    break;
                                case "water":
                                    type = switch (v) {
                                        case "lake", "lagoon" -> WayType.LAKE;
                                        case "canal", "river" -> WayType.RIVER_ANDMORE;
                                        case "pond", "basin", "reservoir", "dam", "pool" -> WayType.POND_ANDMORE;
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
                                        case "school" -> WayType.SCHOOL;
                                        case "parking" -> WayType.PARKING;
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
                                                "court_house"
                                                -> WayType.BUILDING;
                                        default -> type;
                                    };
                                    break;
                                case "leisure":
                                    type = switch (v) {
                                        case "park" -> WayType.PARK;
                                        case "playground" -> WayType.PLAYGROUND;
                                        case "garden" -> WayType.GARDEN;
                                        case "dog_park" -> WayType.DOG_PARK;
                                        case "golf_course" -> WayType.GOLF_COURSE;
                                        case "ice_rink" -> WayType.ICE_RINK;
                                        case "marina" -> WayType.MARINA;
                                        case "miniature_golf" -> WayType.MINIATURE_GOLF;
                                        case "nature_reserve" -> WayType.NATURE_RESERVE;
                                        case "pitch", "track" -> WayType.PITCH;
                                        case "sports_centre", "stadium" -> WayType.SPORTS_CENTRE;
                                        case "swimming_pool" -> WayType.SWIMMING_POOL;
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
                                    var maxspeed = Integer.parseInt(v);
                                    //primary, secondary = 80
                                    //primary, secondary, tertiary = 50
                                    //tertiary, residential = 40
                                    //nogle veje har ikke maxspeed (tertiary)
                                    break;
                                case "oneway":
                                    boolean isOneway;
                                    if(v.equals("no")) isOneway = false;
                                    else if(v.equals("yes")) isOneway = true;
                            }
                            break;
                        // relations har member-tags, der referer til
                        case "member":
                            ref = Long.parseLong(reader.getAttributeValue(null, "ref"));
                            var elm = id2way.get(ref); //man får et OSMWay objekt
                            if (elm != null) rel.add(elm); //tilføjer OSMWay objekt til listen rel

                            var role = reader.getAttributeValue(null, "role");
                            if(role.equals("inner")) {

                            } else if(role.equals("outer")) {

                            }
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
                            if(type == WayType.COASTLINE){
                                coastlinesMap.get(type).add(way);
                                nodes.clear(); // clearer nodes så den er tom når næste linje skal indlæses
                            }

                            //lav edges
                            //en edge består af en vægt + start og slut
                            //udregn vægten - tid=længde/maxspeed
                                //vi skal udregne længden af vejen
                            //way.getNodes().get(0) for at få start og slutpunkt

                            OSMWay osmway = id2way.get(relID);
                            OSMNode startNode = way.getNodes().get(0);
                            OSMNode endNode = way.getNodes().get(way.getNodes().size()-1);
                            System.out.println("StartNode: lon: " + startNode.getLon() + " lat: " + startNode.getLat());
                            System.out.println("EndNode: lon: " + endNode.getLon() + " lat: " + endNode.getLat());

                            //Kilde for formel at regne distance: https://www.geeksforgeeks.org/program-distance-two-points-earth/
                            //find længde for hver vej og læg dem sammen


                            break;

                        case "relation":
                            if (!rel.isEmpty()) { //type == WayType.FOREST &&
                                lines.get(type).add(new MultiPolygon(rel, type));
                            }
                            rel.clear();
                            break;}break;}}

        list = makeCoastlineObject(coastlinesMap);
        System.out.println("Done");}

    // tilføjer en observer til listen observers
    // Runnable = metode der ikke tager nogle argumenter eller returnerer noget
    public void addObserver(Runnable observer) {
        observers.add(observer);}

    /* alle der er registreret som observers, får af vide, at der er sket en ændring.
     bruges steder, hvor modellen vil fortælle dens observers, at der er sket en vigtig ændring i modellen.
     f.eks. for at fortælle repaint-metoden at der er kommet en ny linje den skal tegnes. */
    public void notifyObservers() {
        for (var observer : observers) {
            observer.run();}}

    // iterator over types
    public Iterable<Drawable> iterable(WayType type) {return lines.get(type);}
    public static KdNode getKDTreeUNKNOWN() {return KDTreeUNKNOWN;}
    public static KdNode getKDTreeNAVIGATION_LINE() { return KDTreeNAVIGATION_LINE; }
    public static KdNode getKDTreeRESIDENTIAL() {return KDTreeRESIDENTIAL;}
    public static KdNode getKDTreeLAKE() {return KDTreeLAKE;}
    public static KdNode getKDTreeBEACH() {return KDTreeBEACH;}
    public static KdNode getKDTreeSAND() {return KDTreeSAND;}
    public static KdNode getKDTreeFOREST() {return KDTreeFOREST;}
    public static KdNode getKDTreeWATER() {return KDTreeWATER;}
    public static KdNode getKDTreeBUILDING() {return KDTreeBUILDING;}
    public static KdNode getKDTreeINDUSTRIAL() {return KDTreeINDUSTRIAL;}
    public static KdNode getKDTreeMEADOW() {return KDTreeMEADOW;}
    public static KdNode getKDTreePARK() {return KDTreePARK;}
    public static KdNode getKDTreeCEMETERY() {return KDTreeCEMETERY;}
    public static KdNode getKDTreeALLOTMENTS() {return KDTreeALLOTMENTS;}
    public static KdNode getKDTreeCOMMERCIAL() {return KDTreeCOMMERCIAL;}
    public static KdNode getKDTreeCONSTRUCTION() {return KDTreeCONSTRUCTION;}
    public static KdNode getKDTreeRETAIL() {return KDTreeRETAIL;}
    public static KdNode getKDTreeFARMLAND() {return KDTreeFARMLAND;}
    public static KdNode getKDTreeGRASS () {return KDTreeGRASS;}
    public static KdNode getKDTreeGREENHOUSE_HORTICULTURE () {return KDTreeGREENHOUSE_HORTICULTURE;}
    public static KdNode getKDTreeRAILWAY () {return KDTreeRAILWAY;}
    public static KdNode getKDTreeRELIGIOUS () {return KDTreeRELIGIOUS;}
    public static KdNode getKDTreeMOTORWAY () {return KDTreeMOTORWAY;}
    public static KdNode getKDTreePRIMARY () {return KDTreePRIMARY;}
    public static KdNode getKDTreeSECONDARY () {return KDTreeSECONDARY;}
    public static KdNode getKDTreeTERTIARY () {return KDTreeTERTIARY;}
    public static KdNode getKDTreeLIVING_STREET () {return KDTreeLIVING_STREET;}
    public static KdNode getKDTreeSERVICE () {return KDTreeSERVICE;}
    public static KdNode getKDTreePEDESTRIAN () {return KDTreePEDESTRIAN;}
    public static KdNode getKDTreeTRACK () {return KDTreeTRACK;}
    public static KdNode getKDTreeROAD () {return KDTreeROAD;}
    public static KdNode getKDTreeFOOTWAY_ANDMORE () {return KDTreeFOOTWAY_ANDMORE;}
    public static KdNode getKDTreeBRIDLEWAY () {return KDTreeBRIDLEWAY;}
    public static KdNode getKDTreeCYCLEWAY () {return KDTreeCYCLEWAY;}
    public static KdNode getKDTreeRESIDENTIALWAY () {return KDTreeRESIDENTIALWAY;}
    public static KdNode getKDTreeRIVER_ANDMORE () {return KDTreeRIVER_ANDMORE;}

        public static KdNode getKDTreeCOASTLINE () {
            return KDTreeCOASTLINE;
        }
        public static Map<WayType, List<Drawable>> getCoastlinesMap () {
            return coastlinesMap;
        }

        public static List<OSMNode> getListOfOSMNodesOfCoastlines (Map < WayType, List < Drawable >> coastlines) {
            //så længe dens startpunkt ikke er dens slutpunkt - kør while loop
            //getLon/Lat fra OSMNode
            ArrayList<OSMNode> listOfOSMNodesOfCoastlines = new ArrayList<>();
            for (Drawable drawable : coastlines.get(WayType.COASTLINE)) {
                listOfOSMNodesOfCoastlines.addAll(((PolyLine) drawable).getNodes());
            }
            return listOfOSMNodesOfCoastlines;
        }


        public List<PolyLine> makeCoastlineObject (Map < WayType, List < Drawable >> linesList){
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
                // De coastlines som ikke passede
                List<Drawable> buildUpCoastlinesList = new ArrayList<>();
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

                } //Forloop stop


                // gammel liste = ny liste (ting vi mangler at kigge på)
                unSortedCoastlines = buildUpCoastlinesList;
                //System.out.println("unSortedCoastlines: " + unSortedCoastlines.size());

            }

            //hvis start og slut ikke er det samme - sæt en linje imellem
            System.out.println("done med kystliner");
            return listOfCoastlines;

        }

        public static List<PolyLine> getList () {
            return list;
        }
    }








