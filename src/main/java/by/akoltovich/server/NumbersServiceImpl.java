package by.akoltovich.server;

import by.akoltovich.NumbersRequest;
import by.akoltovich.NumbersResponse;
import by.akoltovich.NumbersServiceGrpc;
import io.grpc.stub.StreamObserver;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class NumbersServiceImpl extends NumbersServiceGrpc.NumbersServiceImplBase {

    private static final org.slf4j.Logger logger =
            LoggerFactory.getLogger(NumbersServiceImpl.class);

    @Override
    public void generateValue(NumbersRequest request, StreamObserver<NumbersResponse> responseObserver) {
        logger.info("Request for new sequence of numbers, firstValue:{}, lastValue:{}",
                request.getFirstValue(), request.getLastValue()
        );
        AtomicInteger currentValue = new AtomicInteger(request.getFirstValue());
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

        Runnable runnable = () -> {
            int value = currentValue.incrementAndGet();
            NumbersResponse response = NumbersResponse.newBuilder()
                    .setResponse(value)
                    .build();
            responseObserver.onNext(response);
            if (value == request.getLastValue()) {
                executor.shutdown();
                responseObserver.onCompleted();
                logger.info("Sequence of numbers finished");
            }
        };
        executor.scheduleAtFixedRate(runnable, 0, 2, TimeUnit.SECONDS);
    }
}
