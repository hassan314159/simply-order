package dev.simplyoder.order.clients;

public class ErrorHandlerGlobal {
}


//public String authorizePayment(UUID orderId, String sagaId, BigDecimal amount, String currency, String method) {
//    try {
//        // Example HTTP call
//        HttpResponse<String> response = client.send(
//                HttpRequest.newBuilder()
//                        .uri(URI.create("https://payments.example.com/authorize"))
//                        .POST(ofFormData(Map.of(
//                                "orderId", orderId.toString(),
//                                "sagaId", sagaId,
//                                "amount", amount.toString(),
//                                "currency", currency,
//                                "method", method
//                        )))
//                        .build(),
//                HttpResponse.BodyHandlers.ofString()
//        );
//
//        if (response.statusCode() == 200) {
//            return parseAuthId(response.body());
//        } else if (response.statusCode() == 402) {
//            // Payment permanently declined → don't retry
//            throw ApplicationFailure.newNonRetryableFailure(
//                    "Card declined for order " + orderId, "CardDeclined");
//        } else if (response.statusCode() >= 500) {
//            // Server error → safe to retry
//            throw ApplicationFailure.newFailure(
//                    "Payment service error: " + response.statusCode(),
//                    "PaymentServiceUnavailable");
//        } else {
//            // Other 4xx errors
//            throw ApplicationFailure.newNonRetryableFailure(
//                    "Bad request: " + response.body(), "PaymentBadRequest");
//        }
//
//    } catch (IOException | InterruptedException e) {
//        // Network issues → retry
//        throw ApplicationFailure.newFailure(
//                "Network error: " + e.getMessage(), "NetworkFailure", e);
//    }