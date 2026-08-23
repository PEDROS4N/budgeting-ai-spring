package dio.budgeting.application;

import dio.budgeting.domain.Transaction;
import dio.budgeting.domain.TransactionRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class QueryTransactionsUseCase {

    private final TransactionRepository repository;

    public QueryTransactionsUseCase(TransactionRepository repository) {
        this.repository = repository;
    }

    public List<Transaction> all() {
        return repository.findAll();
    }

    public List<Transaction> byCategory(String category) {
        return repository.findByCategory(category);
    }
}
