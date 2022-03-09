package com.crystal;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    private static final String driver = "com.mysql.cj.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/nasadb";
    private static final String DB_Username = "root";
    private static final String DB_Password = "";
    private static final String FILE_PATH_DEFAULT = "NASA_data";
    private static final List<String> HEADERS = List.of("id", "created_on", "neo_reference_id", "name", "absolute_magnitude_h",
            "is_potentially_hazardous_asteroid", "is_sentry_object");
    public static Integer LAST_READ_ID = 0;
    private final static long ONCE_PER_DAY = 1000 * 3600 * 24;

    public static Connection getConnection() {
        try {
            Class.forName(driver);
            System.out.println("Connecting to database: " + DB_URL);
            Connection con = DriverManager.getConnection(DB_URL, DB_Username, DB_Password);
            System.out.println("Connection successful!");
            return con;
        } catch (Exception e) {
            System.out.println(e);
        }

        return null;
    }

    public static void insertDataToDb(Integer neo_reference_id_value, String name_value, String nasa_jpl_url_value,
                                      Double absolute_magnitude_h, Boolean is_potentially_hazardous_asteroid_value, Boolean is_sentry_object_value) {
        try {
            Connection con = getConnection();
            PreparedStatement queryInsert = con.prepareStatement("INSERT INTO nasa (neo_reference_id, name, nasa_jpl_url, absolute_magnitude_h, is_potentially_hazardous_asteroid, is_sentry_object) VALUES (?, ?, ?, ?, ?, ?)");
            queryInsert.setInt(1, neo_reference_id_value);
            queryInsert.setString(2, name_value);
            queryInsert.setString(3, nasa_jpl_url_value);
            queryInsert.setDouble(4, absolute_magnitude_h);
            queryInsert.setBoolean(5, is_potentially_hazardous_asteroid_value);
            queryInsert.setBoolean(6, is_sentry_object_value);

            queryInsert.executeUpdate();
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            System.out.println("Insert completed");
        }
    }

    public static void generateCsv() {
        // get data from database and generate csv file
        try {
            Connection con = getConnection();
            Statement statement = con.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM nasa WHERE id > " + LAST_READ_ID);
            int previousLastReadId = LAST_READ_ID;
            // call method to read the resultSet and then write it to a file
            List<List<String>> content = readDataFromResultSet(resultSet);
            if (writeDataToFile(HEADERS, content, ",", FILE_PATH_DEFAULT + "_" + LocalDate.now() + ".csv")) {
                System.out.println("Your file is ready at path: " + FILE_PATH_DEFAULT + "_" + LocalDate.now());
            } else {
                LAST_READ_ID = previousLastReadId;
                System.err.println("Error in generating file!");
            }
        } catch (SQLException e) {
            System.err.println("Error accessing database: " + e.getMessage());
        }
    }

    private static List<List<String>> readDataFromResultSet(ResultSet resultSet) throws SQLException {
        List<List<String>> content = new ArrayList<>();

        while (resultSet.next()) {
            List<String> contentLine = new ArrayList<>();
            contentLine.add(String.valueOf(resultSet.getInt(HEADERS.get(0))));
            contentLine.add(String.valueOf(resultSet.getDate(HEADERS.get(1))));
            contentLine.add(String.valueOf(resultSet.getDouble(HEADERS.get(2))));
            contentLine.add(resultSet.getString(HEADERS.get(3)));
            contentLine.add(String.valueOf(resultSet.getDouble(HEADERS.get(4))));
            contentLine.add(String.valueOf(resultSet.getBoolean(HEADERS.get(5))));
            contentLine.add(String.valueOf(resultSet.getBoolean(HEADERS.get(6))));
            LAST_READ_ID = resultSet.getInt(HEADERS.get(0));
            content.add(contentLine);
        }

        return content;
    }

    private static boolean writeDataToFile(List<String> headers, List<List<String>> content, String separator, String filePath) {
        try (FileWriter fileWriter = new FileWriter(filePath);
             BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {

            // writing header in the file
            writeLineInFile(headers, separator, bufferedWriter);

            // writing content in the file
            for (List<String> contentLine : content) {
                writeLineInFile(contentLine, separator, bufferedWriter);
            }

            return true;

        } catch (IOException e) {
            System.err.println("Error when trying to write data into the file");
            e.printStackTrace();
            return false;
        }
    }

    private static void writeLineInFile(List<String> content, String separator, BufferedWriter bufferedWriter) throws IOException {
        String line = content.stream().collect(Collectors.joining(separator));
        bufferedWriter.write(line);
        bufferedWriter.newLine();
    }

    private static Calendar getTodayTwoPM() {
        Calendar today2PM = Calendar.getInstance();
        today2PM.set(Calendar.HOUR_OF_DAY, 14);
        today2PM.set(Calendar.MINUTE, 0);
        today2PM.set(Calendar.SECOND, 0);
        return today2PM;
    }

    public static void main(String[] args) throws Exception {

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                // the task needs to be in the run() method
                System.out.println("Run the task: " + getTodayTwoPM().getTime());
            }
        }, getTodayTwoPM().getTime(), ONCE_PER_DAY);


        // API from 07/09/2015 to 08/09/2015
        URL url = new URL("https://api.nasa.gov/neo/rest/v1/feed?start_date=2015-09-07&end_date=2015-09-08&api_key=jFUFa3GxUXIA14LsJBEfPLzGHCBwzMpqksCIgKnd");
        URLConnection connection = url.openConnection();

        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(connection.getInputStream()))) {
            String jsonLine;
            while ((jsonLine = in.readLine()) != null) {
                System.out.println(jsonLine);

                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode root = objectMapper.readTree(jsonLine);

                // Get near_earth_objects Node
                JsonNode nearEarthObjectsNode = root.path("near_earth_objects");
                if (!nearEarthObjectsNode.isMissingNode()) { // if near_earth_objects Node exists

                    for (JsonNode dateNode : nearEarthObjectsNode) {
                        System.out.println(dateNode);

                        if (dateNode.isArray()) {
                            System.out.println("este array: " + dateNode.isArray());

                            // iterate dateNode Array
                            for (JsonNode node : dateNode) {
                                Integer neo_reference_id = node.path("neo_reference_id").asInt();
                                String name = node.path("name").asText();
                                String nasa_jpl_url = node.path("nasa_jpl_url").asText();
                                Double absolute_magnitude_h = node.path("absolute_magnitude_h").asDouble();
                                Boolean is_potentially_hazardous_asteroid = node.path("is_potentially_hazardous_asteroid").asBoolean();
                                Boolean is_sentry_object = node.path("is_sentry_object").asBoolean();

                                // insert data into database
                                insertDataToDb(neo_reference_id, name, nasa_jpl_url, absolute_magnitude_h,
                                        is_potentially_hazardous_asteroid, is_sentry_object);

                            }
                        }
                    }
                }
            }
            generateCsv();
        }
    }
}

