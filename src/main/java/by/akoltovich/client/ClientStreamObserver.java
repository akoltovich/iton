package by.akoltovich.client;

import by.akoltovich.NumbersResponse;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientStreamObserver implements StreamObserver<NumbersResponse> {

    private static final Logger logger =
            LoggerFactory.getLogger(ClientStreamObserver.class);

    private int lastValue = 0;

    @Override
    public void onNext(NumbersResponse numbersResponse) {
        logger.info("New value:{}", numbersResponse.getResponse());
        setLastValue(numbersResponse.getResponse());
    }

    @Override
    public void onError(Throwable throwable) {
        logger.error("Got error", throwable);
    }

    @Override
    public void onCompleted() {
        logger.info("Request completed");
    }

    private synchronized void setLastValue(int value) {
        this.lastValue = value;
    }

    public synchronized int getLastValueAndReset() {
        int lastValuePrevious = this.lastValue;
        this.lastValue = 0;
        return lastValuePrevious;
    }
}
