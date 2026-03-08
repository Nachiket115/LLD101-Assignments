package com.example.payments;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class App {
    public static void main(String[] args) {
        Map<String, PaymentGateway> gateways = new HashMap<>();
        gateways.put("fastpay", new FastPayAdapter(new FastPayClient()));
        gateways.put("safecash", new SafeCashAdapter(new SafeCashClient()));

        String id1 = chargeWith("fastpay", gateways, "cust-1", 1299);
        String id2 = chargeWith("safecash", gateways, "cust-2", 1299);
        System.out.println(id1);
        System.out.println(id2);
    }

    private static String chargeWith(String provider, Map<String, PaymentGateway> gateways,
                                     String customerId, int amountCents) {
        Objects.requireNonNull(provider, "provider");
        PaymentGateway gateway = gateways.get(provider);
        if (gateway == null) {
            throw new IllegalArgumentException("unknown provider: " + provider);
        }
        OrderService service = new OrderService(gateway);
        return service.charge(customerId, amountCents);
    }
}
