package com.bobocode;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.util.Pair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;
import java.util.stream.IntStream;


@Slf4j
public class DemoApp {
    private static final String API_URL = "https://api.nasa.gov/mars-photos/api/v1/rovers/curiosity/photos?sol=10&api_key=DEMO_KEY";

    public static void main(String[] args) throws IOException {
        String jsonResponse = fetchDataFromAPI(API_URL);
        log.info(jsonResponse);
        List<String> urls = parseImageUrls(jsonResponse);

        Pair<Long, String> largeSizeAndUrl = findLargestImage(urls);
        log.info(largeSizeAndUrl.getFirst() + " " + largeSizeAndUrl.getSecond());
    }

    private static String fetchDataFromAPI(String apiUrl) throws IOException {
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        InputStream responseStream = connection.getInputStream();
        try (Scanner scanner = new Scanner(responseStream, StandardCharsets.UTF_8)) {
            return scanner.useDelimiter("\\A").next();
        }
    }

    private static List<String> parseImageUrls(String jsonResponse) {
        JSONObject jsonObject = new JSONObject(jsonResponse);
        JSONArray photosArray = jsonObject.getJSONArray("photos");

        return IntStream.range(0, photosArray.length())
                .mapToObj(photosArray::getJSONObject)
                .map(photoObj -> photoObj.getString("img_src"))
                .toList();
    }

    private static Pair<Long, String> findLargestImage(List<String> imageUrls) throws IOException {
        String largestImageUrl = null;
        long largestSize = 0;

        for (String imageUrl : imageUrls) {
            long imageSize = getImageSize(imageUrl);
            log.info("Image link {} size: {}", imageUrl, imageSize);
            if (imageSize > largestSize) {
                largestSize = imageSize;
                largestImageUrl = imageUrl;
            }
        }

        return new Pair<>(largestSize, largestImageUrl);
    }

    private static long getImageSize(String imageUrl) throws IOException {
        HttpURLConnection connection = null;

        try {
            URL url = new URL(imageUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setInstanceFollowRedirects(true);
            connection.setRequestMethod("HEAD");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_MOVED_TEMP || responseCode == HttpURLConnection.HTTP_MOVED_PERM) {
                if (responseCode == HttpURLConnection.HTTP_MOVED_TEMP || responseCode == HttpURLConnection.HTTP_MOVED_PERM) {
                    String newUrl = connection.getHeaderField("Location");
                    if (newUrl != null) {
                        connection.disconnect();
                        return getImageSize(newUrl);
                    }
                }
                return connection.getContentLengthLong();
            } else {
                throw new IOException("Error fetching image size, response code: " + responseCode);
            }
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
