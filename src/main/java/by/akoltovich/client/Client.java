package by.akoltovich.client;

import by.akoltovich.NumbersRequest;
import by.akoltovich.NumbersServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Client {

    private static final Logger logger =
            LoggerFactory.getLogger(Client.class);
    private static final int LIMIT = 50;

    private int value = 0;

    public static void main(String[] args) {
        logger.info("Numbers client is starting");
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 8080)
                .usePlaintext()
                .build();

        NumbersServiceGrpc.NumbersServiceStub stub
                = NumbersServiceGrpc.newStub(channel);

        new Client().clientAction(stub);

        logger.info("Numbers client is shutdown");
        channel.shutdown();
    }

    private void clientAction(NumbersServiceGrpc.NumbersServiceStub stub) {
        NumbersRequest numbersRequest = makeNumberRequest();
        ClientStreamObserver clientStreamObserver = new ClientStreamObserver();
        stub.generateValue(numbersRequest, clientStreamObserver);
        for (int i = 0; i < LIMIT; i++) {
            long valueForPrint = nextValue(clientStreamObserver);
            logger.info("Current value:{}", valueForPrint);
            sleep();
        }
    }

    private long nextValue(ClientStreamObserver clientStreamObserver) {
        value = value + clientStreamObserver.getLastValueAndReset() + 1;
        return value;
    }

    private NumbersRequest makeNumberRequest() {
        return NumbersRequest.newBuilder()
                .setFirstValue(1)
                .setLastValue(10)
                .build();
    }

    private static void sleep() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
