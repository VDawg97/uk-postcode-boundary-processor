import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Main {

    private static String folderName;

    public static void main(String[] args) throws IOException, ParseException {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter results folder name");
        folderName = sc.nextLine();
        File folder = new File("C:\\Users\\vaida\\Documents\\GitHub\\postcodedistrict\\src\\main\\resources\\results\\" + folderName);
        folder.mkdir();
        while(true) {
            System.out.println("Enter a postcode area e.g. PL,Plymouth");
            String input = sc.nextLine();
            // Check if list of postcodes
            if (input.contains("[") && input.contains("]")) {
                String[] inputSplit = input.split("],");
                // Expect two args
                if (inputSplit.length < 2) {
                    System.err.println("Input format: <post_code>,<area name> (e.g. PL,Plymouth)");
                    continue;
                }
                String areaName = inputSplit[1];
                String inputs = inputSplit[0].replace("[", "").replace("]", "");
                String[] postcodes = inputs.split(",");
                for (String postcode : postcodes) {
                    String trimmedPostcode = postcode.trim();
                    parsePostcode(trimmedPostcode, areaName);
                }
            } else {
                String[] inputSplit = input.split(",");
                // Expect two args
                if (inputSplit.length < 2) {
                    System.err.println("Input format: <post_code>,<area name> (e.g. PL,Plymouth)");
                    continue;
                }
                // Combine the remainder of inputSplit for area name
                String areaName = inputSplit[1];
                if (inputSplit.length > 2) {
                    for (int i = 2; i < inputSplit.length; i++) {
                        areaName = areaName.concat(inputSplit[i]);
                    }
                }
                // Process inputs
                String postcode = inputSplit[0];
                parsePostcode(postcode, areaName);
            }
        }
    }

    private static void parsePostcode(String postcode, String areaName) throws IOException, ParseException {
        File file = new File ("./src/main/java/PostalDistrict.json");
        JSONParser jsonParser = new JSONParser();
        if (file.isFile()) {
            FileReader fileReader = new FileReader(file);
            Object obj = jsonParser.parse(fileReader);
            JSONObject featureCollection = (JSONObject) obj;
            JSONArray features = parseFeatureCollection(featureCollection);
            JSONArray foundFeatures = findFeature(features, postcode);
            if (!foundFeatures.isEmpty()) {
                parseFeatures(foundFeatures, folderName, areaName);
            } else {
                System.err.println("Post code '" + postcode +"' not found");
            }
        }
    }

    private static JSONArray parseFeatureCollection(JSONObject features) {
        System.out.println("--- Retrieving features ---");
        return (JSONArray) features.get("features");
    }

    private static JSONArray findFeature(JSONArray features, String postcode) {
        System.out.println("--- Looking up feature for post code: " + postcode + " ---");
        JSONArray results = new JSONArray();
        for (Object obj : features) {
            JSONObject jsonObject = (JSONObject) obj;
            JSONObject properties = (JSONObject) jsonObject.get("properties");
            String name = properties.get("PostDist").toString();
            name = name.replaceAll("[0-9]", "");
            if (name.equals(postcode)) {
                results.add(obj);
            }
        }
        return results;
    }

    private static void parseFeatures(JSONArray features, String folderName, String areaName) throws IOException {
        // Make districts folder
        File districtFolder = new File("./src/main/resources/results/" +
                folderName + "/districts/" + areaName);
        districtFolder.mkdirs();
        // Overriding properties
        JSONObject result = new JSONObject();
        result.put("type", "FeatureCollection");
        for (Object o : features) {
            JSONObject feature = (JSONObject) o;
            JSONObject properties = new JSONObject();
            JSONObject oProperties = (JSONObject) ((JSONObject) o).get("properties");
            properties.put("title", oProperties.get("PostDist").toString());
            findTitle(properties,
                    oProperties.get("PostArea").toString(),
                    oProperties.get("PostDist").toString()
            );
            feature.put("properties", properties);
            // Save file on its own
            Path path = Paths.get("./src/main/resources/results/" +
                    folderName + "/districts/" + areaName + "/" +
                    oProperties.get("PostDist").toString() + ".geojson");
            byte[] bytes = feature.toString().getBytes();
            Files.write(path, bytes);
        }
        result.put("features", features);

        // Write to file
        Path path = Paths.get("./src/main/resources/results/" +
                folderName + "/" +
                areaName + ".geojson");
        byte[] bytes = result.toString().getBytes();
        Files.write(path, bytes);
        System.out.println("SUCCESS: result saved in a file");
    }

    private static void findTitle(JSONObject props, String postArea, String postDist) throws FileNotFoundException {
        File file = new File("./src/main/java/postcode_names/" + postArea);
        if (file.exists()) {
            FileInputStream fstream = new FileInputStream("./src/main/java/postcode_names/" + postArea);
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
            List<String> names = br.lines().collect(Collectors.toList());
            for (String name : names) {
                String[] content = name.split(",");
                if (content[0].equals(postDist)) {
                    props.put("areaName", processAreaName(content[1]));
                }
            }
        }
    }

    private static String processAreaName(String areaName) {
        StringBuilder result = new StringBuilder();
        String[] strs = areaName.split(" ");
        for(String s : strs) {
            result.append(" ").append(capitalizeFirstLetter(s));
        }
        return result.toString();
    }

    private static String capitalizeFirstLetter(String s) {
        // Filter out words that do not need capitalization
        if (s.toLowerCase().equals("of")) {
            return s.toLowerCase();
        } else {
            return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
        }
    }
}
