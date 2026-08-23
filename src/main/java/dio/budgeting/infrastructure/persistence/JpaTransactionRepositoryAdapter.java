package dio.budgeting.infrastructure.persistence;

import dio.budgeting.domain.Transaction;
import dio.budgeting.domain.TransactionId;
import dio.budgeting.domain.TransactionRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class JpaTransactionRepositoryAdapter implements TransactionRepository {

    private final SpringDataTransactionRepository jpaRepository;

    public JpaTransactionRepositoryAdapter(SpringDataTransactionRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Transaction save(Transaction transaction) {
        TransactionEntity saved = jpaRepository.save(TransactionEntity.fromDomain(transaction));
        return saved.toDomain();
    }

    @Override
    public Optional<Transaction> findById(TransactionId id) {
        return jpaRepository.findById(id.value()).map(TransactionEntity::toDomain);
    }

    @Override
    public List<Transaction> findAll() {
        return jpaRepository.findAll().stream().map(TransactionEntity::toDomain).toList();
    }

    @Override
    public List<Transaction> findByCategory(String category) {
        return jpaRepository.findByCategoryIgnoreCase(category).stream()
                .map(TransactionEntity::toDomain)
                .toList();
    }
}
