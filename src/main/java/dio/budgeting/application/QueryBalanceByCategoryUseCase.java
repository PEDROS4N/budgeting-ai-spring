package dio.budgeting.application;

import dio.budgeting.domain.Transaction;
import dio.budgeting.domain.TransactionRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * Evolução implementada no desafio: um novo tipo de consulta financeira.
 * Responde perguntas do tipo "qual o meu saldo em alimentação?" somando
 * receitas e despesas de uma categoria específica.
 */
@Component
public class QueryBalanceByCategoryUseCase {

    private final TransactionRepository repository;

    public QueryBalanceByCategoryUseCase(TransactionRepository repository) {
        this.repository = repository;
    }

    public BalanceResult execute(String category) {
        if (category == null || category.isBlank()) {
            throw new IllegalArgumentException("category não pode ser vazia");
        }

        List<Transaction> transactions = repository.findByCategory(category);

        BigDecimal balance = transactions.stream()
                .map(Transaction::signedAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new BalanceResult(category, balance, transactions.size());
    }

    public record BalanceResult(String category, BigDecimal balance, int transactionCount) {
    }
}
