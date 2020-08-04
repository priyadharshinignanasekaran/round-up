package tech.test;

import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import spark.Spark;
import tech.test.client.StarlingClient;
import tech.test.error.handling.ErrorHandler;
import tech.test.spark.RoundUpRoute;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import static spark.Spark.before;
import static tech.test.deserialization.GsonFactory.create;

public class App {
    private final static Logger LOGGER = LogManager.getLogger();

    private final int serverPort;
    private final StarlingClient client;

    public App(final int serverPort, final StarlingClient client) {
        this.serverPort = serverPort;
        this.client = client;
    }

    public static void main(String[] args) throws Exception {
        final Properties envProperties = envProperties();
        final String token = envProperties.getProperty("bearer.token");
        final String openApiUrl = envProperties.getProperty("starling.open-api.url");
        final int serverPort = Integer.parseInt(envProperties.getProperty("roundup.server.port"));
        final Gson gson = create();
        final StarlingClient client = new StarlingClient(openApiUrl, token, gson);
        final ErrorHandler errorHandler = new ErrorHandler(gson);
        new App(serverPort, client).start(gson, errorHandler);
    }

    private static Properties envProperties() throws IOException {
        final Properties properties = new Properties();
        properties.load(new FileInputStream("config/env.properties"));
        return properties;
    }

    void start(final Gson gson, final ErrorHandler errorHandler) {
        LOGGER.info("Initialise Routes");
        Spark.port(serverPort);
        Spark.get("/ping", (req, resp) -> "pong");
        before((request, response) -> response.type("application/json"));
        Spark.put(RoundUpRoute.PATH, new RoundUpRoute(client, gson, errorHandler));

    }
}

