package dio.budgeting.infrastructure.persistence;

import dio.budgeting.domain.Transaction;
import dio.budgeting.domain.TransactionId;
import dio.budgeting.domain.TransactionType;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "transactions")
public class TransactionEntity {

    @Id
    private UUID id;

    private String description;
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    private String category;
    private LocalDate date;

    protected TransactionEntity() {
        // JPA
    }

    public TransactionEntity(UUID id, String description, BigDecimal amount,
                              TransactionType type, String category, LocalDate date) {
        this.id = id;
        this.description = description;
        this.amount = amount;
        this.type = type;
        this.category = category;
        this.date = date;
    }

    public static TransactionEntity fromDomain(Transaction transaction) {
        return new TransactionEntity(
                transaction.id().value(),
                transaction.description(),
                transaction.amount(),
                transaction.type(),
                transaction.category(),
                transaction.date()
        );
    }

    public Transaction toDomain() {
        return new Transaction(
                new TransactionId(id),
                description,
                amount,
                type,
                category,
                date
        );
    }
}
