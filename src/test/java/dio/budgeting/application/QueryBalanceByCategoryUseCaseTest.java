package dio.budgeting.application;

import dio.budgeting.domain.Transaction;
import dio.budgeting.domain.TransactionRepository;
import dio.budgeting.domain.TransactionType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class QueryBalanceByCategoryUseCaseTest {

    private final InMemoryTransactionRepository repository = new InMemoryTransactionRepository();
    private final QueryBalanceByCategoryUseCase useCase = new QueryBalanceByCategoryUseCase(repository);
    private final CreateTransactionUseCase createUseCase = new CreateTransactionUseCase(repository);

    @Test
    void deveSomarReceitasESubtrairDespesasDaCategoria() {
        createUseCase.execute("Salário", new BigDecimal("3000"), TransactionType.INCOME, "renda", LocalDate.now());
        createUseCase.execute("Mercado", new BigDecimal("400"), TransactionType.EXPENSE, "alimentação", LocalDate.now());
        createUseCase.execute("Restaurante", new BigDecimal("150"), TransactionType.EXPENSE, "alimentação", LocalDate.now());

        var result = useCase.execute("alimentação");

        assertEquals(new BigDecimal("-550"), result.balance());
        assertEquals(2, result.transactionCount());
    }

    @Test
    void deveRejeitarCategoriaVazia() {
        assertThrows(IllegalArgumentException.class, () -> useCase.execute(" "));
    }

    @Test
    void deveRejeitarTransacaoComValorNegativoOuZero() {
        assertThrows(IllegalArgumentException.class, () ->
                createUseCase.execute("Teste", BigDecimal.ZERO, TransactionType.EXPENSE, "lazer", LocalDate.now()));
    }

    @Test
    void deveRejeitarTransacaoSemCategoria() {
        assertThrows(IllegalArgumentException.class, () ->
                createUseCase.execute("Teste", new BigDecimal("10"), TransactionType.EXPENSE, "", LocalDate.now()));
    }

    /** Simple in-memory adapter, used only to keep this test independent from Spring/JPA. */
    static class InMemoryTransactionRepository implements TransactionRepository {
        private final List<Transaction> data = new ArrayList<>();

        @Override
        public Transaction save(Transaction transaction) {
            data.add(transaction);
            return transaction;
        }

        @Override
        public Optional<Transaction> findById(dio.budgeting.domain.TransactionId id) {
            return data.stream().filter(t -> t.id().equals(id)).findFirst();
        }

        @Override
        public List<Transaction> findAll() {
            return data;
        }

        @Override
        public List<Transaction> findByCategory(String category) {
            return data.stream().filter(t -> t.category().equalsIgnoreCase(category)).toList();
        }
    }
}
