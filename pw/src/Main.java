import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("No arguments provided. Use -h for help.");
            return;
        }

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
        String host = urlString.replace("http://", "").replace("https://", "").split("/")[0];
        int port = urlString.startsWith("https://") ? 443 : 80;
        String path = urlString.substring(urlString.indexOf(host) + host.length());

        if (path.isEmpty()) {
            path = "/";
        }

        try (Socket socket = new Socket(host, port);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Send HTTP GET request
            out.println("GET " + path + " HTTP/1.1");
            out.println("Host: " + host);
            out.println("User-Agent: Mozilla/5.0");
            out.println("Connection: close");
            out.println(""); // blank line to end the header section

            // Read the response
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line).append("\n");
            }

            // Print the response
            System.out.println(response.toString());

        } catch (Exception e) {
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