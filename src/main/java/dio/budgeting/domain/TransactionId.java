package dio.budgeting.domain;

import java.util.UUID;

public record TransactionId(UUID value) {

    public static TransactionId generate() {
        return new TransactionId(UUID.randomUUID());
    }

    public static TransactionId of(String raw) {
        return new TransactionId(UUID.fromString(raw));
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
