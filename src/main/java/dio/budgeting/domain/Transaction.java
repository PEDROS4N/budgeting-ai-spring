package dio.budgeting.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Domain entity. Validation lives here so that no matter which entry point
 * creates a transaction (REST or the AI tool calling flow), the same rules apply.
 */
public record Transaction(
        TransactionId id,
        String description,
        BigDecimal amount,
        TransactionType type,
        String category,
        LocalDate date
) {

    public Transaction {
        Objects.requireNonNull(id, "id é obrigatório");
        Objects.requireNonNull(type, "type é obrigatório");
        Objects.requireNonNull(date, "date é obrigatório");

        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("description não pode ser vazia");
        }
        if (category == null || category.isBlank()) {
            throw new IllegalArgumentException("category não pode ser vazia");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("amount deve ser maior que zero");
        }
        if (date.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("date não pode ser no futuro");
        }
    }

    public static Transaction create(String description, BigDecimal amount,
                                      TransactionType type, String category, LocalDate date) {
        return new Transaction(TransactionId.generate(), description, amount, type, category, date);
    }

    /** Signed amount: expenses count negative towards the balance. */
    public BigDecimal signedAmount() {
        return type == TransactionType.EXPENSE ? amount.negate() : amount;
    }
}
