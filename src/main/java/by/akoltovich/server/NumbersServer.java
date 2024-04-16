package by.akoltovich.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;
import java.util.logging.Logger;

public class NumbersServer {

    private static final Logger logger =
            Logger.getLogger(NumbersServer.class.getName());

    public static void main(String[] args) throws IOException, InterruptedException {
        logger.info("Numbers server is starting");
        Server server = ServerBuilder
                .forPort(8080)
                .addService(new NumbersServiceImpl())
                .build();

        server.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Received shutdown request");
            server.shutdown();
            logger.info("Server stopped");

        }));
        logger.info("Server is waiting for client, port 8080");
        server.awaitTermination();
    }
}
