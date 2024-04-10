import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("No arguments provided. Use -h for help.");
            return;
        }

        // Process the arguments
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-h":
                    displayHelp();
                    break;
                case "-s":
                    if (i + 1 < args.length) {
                        String secondArgumentS = args[i + 1];
                        performActionS(secondArgumentS);
                    } else {
                        System.out.println("No second argument provided for -s. Use -h for help.");
                    }
                    break;
                case "-u":
                    if (i + 1 < args.length) {
                        String term = args[i + 1];
                        performActionU(term);
                    } else {
                        System.out.println("No second argument provided for -u. Use -h for help.");
                    }
                    break;
                default:
                    System.out.println("Use -h for help.");
                    break;
            }
        }
    }

    private static void displayHelp() {
        System.out.println("Help - Command Line Tool");
        System.out.println("Usage:");
        System.out.println("  -h    Display this help message");
        System.out.println("  -s    Scrape first 10 elements from 999 that is like the introduced word");
        System.out.println("  -u    Make a request to a website");
    }

    private static void performActionS(String term) {
        System.out.println("Performing action S...");
        search(term);
    }

    private static void performActionU(String url) {
        System.out.println("Performing action U...");
        requestUrl(url);
    }

    private static void requestUrl(String urlString) {
        try {
            // Create a URL object from the string.
            URL url = new URL(urlString);

            // Open a connection to the URL.
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Set the request method. In this case, we are sending a GET request.
            connection.setRequestMethod("GET");

            // Set a user-agent or any other headers you need.
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");

            // Get the response code to determine if the request was successful.
            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            // If the request was successful (response code 200),
            // read the response from the input stream.
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while (((inputLine = in.readLine())) != null) {
                    response.append(inputLine);
                }
                in.close();

                // Print the response
                System.out.println(response.toString());
            } else {
                System.out.println("GET request not worked");
            }
        } catch (
                IOException e) {
            e.printStackTrace();
        }
    }

    public static void search(String term) {
        String searchUrl = "https://999.md/ro/search?query=" + term;
        try {
            Document doc = Jsoup.connect(searchUrl).get();
            Pattern pattern = Pattern.compile("^/ro/\\d{8}$");
            int count = 1;
            Elements links = doc.select("a");
            for (Element link : links) {
                String href = link.attr("href");
                Matcher matcher = pattern.matcher(href);
                if (matcher.find() && link.text().trim().length() > 1 && count <= 10) {
                    System.out.println(count + ". " + link.text() + " - https://999.md" + href);
                    count++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}