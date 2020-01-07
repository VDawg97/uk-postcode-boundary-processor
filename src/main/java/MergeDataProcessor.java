import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class MergeDataProcessor {

    public static void main(String[] args) throws IOException, ParseException {
        JSONParser jsonParser = new JSONParser();
        File fileFolder = new File("./src/main/java/merge_data");
        JSONArray features = new JSONArray();
        for (File f : fileFolder.listFiles()) {
            FileReader fileReader = new FileReader(f);
            JSONObject obj = (JSONObject) jsonParser.parse(fileReader);
            features.add(obj);
        }
        System.out.println(features);
        // Create and populate result JSON object
        JSONObject result = new JSONObject();
        result.put("type", "FeatureCollection");
        result.put("features", features);
        // Create result file
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter result file name");
        String fileName = sc.nextLine();
        Path resultPath = Paths.get("./src/main/resources/merged_results/" +
                fileName + ".geojson");
        byte[] bytes = result.toString().getBytes();
        Files.write(resultPath, bytes);
        System.out.println("SUCCESS: file " + fileName + " has been created from merging");
    }
}
