package com.pm.patientservice.grpc;

import billing.BillingRequest;
import billing.BillingResponse;
import billing.BillingServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;

@Slf4j
@Service
public class BillingGrpcServiceClient {

    private final BillingServiceGrpc.BillingServiceBlockingStub blockingStub;
    private final ManagedChannel channel;

    public BillingGrpcServiceClient(
            @Value("${grpc.billing-service.host:localhost}") String host,
            @Value("${grpc.billing-service.port:9001}") int port) {

        // 2. Create the channel (the connection to the server)
        this.channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext() // Use only for development; use SSL/TLS for production
                .build();

        log.info("Connecting to Billing Service GRPC at {}:{} ...", host, port);
        // 3. Create the blocking stub
        this.blockingStub = BillingServiceGrpc.newBlockingStub(channel);

        log.info("Connected to Billing Service GRPC at {}:{}", host, port);
    }

    public BillingResponse creatBillingAccount(String name, String email, String patientId) {
        BillingRequest billingRequest = BillingRequest.newBuilder()
                .setName(name)
                .setEmail(email)
                .setPatientId(patientId)
                .build();


        BillingResponse response = blockingStub.createBillingAccount(billingRequest);

        log.info("create billing account response: {}", response);
        return response;
    }

    // 4. Proper cleanup when the Spring context closes
    @PreDestroy
    public void shutdown() {
        if (channel != null && !channel.isShutdown()) {
            channel.shutdownNow();
        }
    }

}
