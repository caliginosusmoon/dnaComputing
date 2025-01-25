package dnabds;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CityPulseDataLoader {
    private String filePath;

    // Constructor to initialize the file path of the dataset
    public CityPulseDataLoader(String filePath) {
        this.filePath = "data/CityPulse_Traffic.csv";
    }

    // Method to load the dataset from CSV file into a list of maps
    public List<Map<String, String>> loadDataset() {
        List<Map<String, String>> dataset = new ArrayList<>();

        try (Reader reader = new FileReader(filePath);
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {

            // Process each record in the CSV file
            for (CSVRecord record : csvParser) {
                Map<String, String> row = new HashMap<>();
                row.put("avgMeasuredTime", record.get("avgMeasuredTime"));
                row.put("avgSpeed", record.get("avgSpeed"));
                row.put("extID", record.get("extID"));
                row.put("medianMeasuredTime", record.get("medianMeasuredTime"));
                row.put("vehicleCount", record.get("vehicleCount"));
                row.put("_id", record.get("_id"));
                row.put("REPORT_ID", record.get("REPORT_ID"));
                dataset.add(row);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return dataset;
    }
}
