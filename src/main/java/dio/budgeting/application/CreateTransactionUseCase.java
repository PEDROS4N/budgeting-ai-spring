package dio.budgeting.application;

import dio.budgeting.domain.Transaction;
import dio.budgeting.domain.TransactionRepository;
import dio.budgeting.domain.TransactionType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Used by both the REST controller and the AI tool calling flow, so the
 * validation rules in Transaction's constructor are enforced exactly once,
 * regardless of entry point.
 */
@Component
public class CreateTransactionUseCase {

    private final TransactionRepository repository;

    public CreateTransactionUseCase(TransactionRepository repository) {
        this.repository = repository;
    }

    public Transaction execute(String description, BigDecimal amount,
                                TransactionType type, String category, LocalDate date) {
        Transaction transaction = Transaction.create(description, amount, type, category, date);
        return repository.save(transaction);
    }
}
