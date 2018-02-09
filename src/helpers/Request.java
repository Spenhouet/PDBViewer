package helpers;

import java.io.*;
import java.net.*;

public class Request {

    private Request() {
        //hide constructor
    }

    /**
     * Gets the result of a GET-request
     *
     * @param url Web url for a GET-request
     * @return lines received
     * @throws IOException Exception if connection can't be opened.
     */
    public static Reader getFromURL(String url) throws IOException {
        final HttpURLConnection connection = (HttpURLConnection) (new URL(url)).openConnection();
        connection.setRequestMethod("GET");
        connection.connect();
        return new InputStreamReader(connection.getInputStream());
    }
}
