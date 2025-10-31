package dev.simplyoder.inventory.controller.utils;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class ReservationIds {
    public static UUID fromKey(String idempotencyKey, String operation) {
        return UUID.nameUUIDFromBytes((operation + ":" + idempotencyKey).getBytes(StandardCharsets.UTF_8));
    }
}
