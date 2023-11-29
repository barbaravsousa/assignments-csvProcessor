import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONObject;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class CsvProcessor {

    private static final Logger logger = Logger.getLogger(CsvProcessor.class.getName());
    private static Properties config = new Properties();

    public static void main(String[] args) {
        setupLogger();
        loadConfig();
        if (args.length != 1) {
            logger.info("Usage: java CsvProcessor <path_to_csv>");
            return;
        }

        String filePath = args[0];
        Set<String> expectedHeadersSet = new HashSet<>(Arrays.asList("customerRef", "customerName", "addressLine1", "addressLine2", "town", "county", "country", "postcode"));

        try {
            FileReader fileReader = new FileReader(filePath);
            CSVParser parser = CSVFormat.DEFAULT.withHeader().parse(fileReader);

            if (!parser.getHeaderMap().keySet().equals(expectedHeadersSet)) {
                logger.info("Invalid or missing headers in CSV.");
                return;
            }

            for (CSVRecord record : parser) {
                JSONObject json = new JSONObject();
                json.put("customerRef", record.get("customerRef"));
                json.put("customerName", record.get("customerName"));
                json.put("addressLine1", record.get("addressLine1"));
                json.put("addressLine2", record.get("addressLine2"));
                json.put("town", record.get("town"));
                json.put("county", record.get("county"));
                json.put("country", record.get("country"));
                json.put("postcode", record.get("postcode"));

                HttpResponse response = sendPostRequest(json.toString());

                if(response.getStatusLine().getStatusCode()==201){
                    logger.info("Customer was created.");
                }
                else if (response.getStatusLine().getStatusCode()==400){
                    logger.info("Customer already exists.");
                }
                else {
                    logger.info("Could not create customer.");
                }

            }
        } catch (IOException e) {
            logger.severe("Error: " + e.getMessage());
        }
    }

    private static HttpResponse sendPostRequest(String jsonData) throws IOException {
        HttpClient httpClient = HttpClients.createDefault();
        String serverUrl = config.getProperty("server.url");
        HttpPost post = new HttpPost(serverUrl);
        post.setHeader("Content-Type", "application/json");
        post.setEntity(new StringEntity(jsonData));

        return httpClient.execute(post);
    }

    private static void loadConfig() {
        try (InputStream input = CsvProcessor.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                logger.severe("Unable to find config.properties");
                return;
            }
            config.load(input);
            if (config.getProperty("server.url", null) == null) {
                logger.severe("Unable to find config.properties");
                throw new IOException("Error loading config.properties");
            }
        } catch (IOException ex) {
            logger.severe("Error loading config.properties: " + ex.getMessage());
        }
    }

    private static void setupLogger() {
        try {
            FileHandler fileHandler = new FileHandler("app.log", true);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);
        } catch (IOException e) {
            logger.severe("Failed to setup logger: " + e.getMessage());
        }
    }
}
