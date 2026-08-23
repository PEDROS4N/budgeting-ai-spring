package dio.budgeting.infrastructure.web;

import dio.budgeting.application.CreateTransactionUseCase;
import dio.budgeting.application.QueryBalanceByCategoryUseCase;
import dio.budgeting.application.QueryTransactionsUseCase;
import dio.budgeting.domain.Transaction;
import dio.budgeting.domain.TransactionType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final CreateTransactionUseCase createTransactionUseCase;
    private final QueryTransactionsUseCase queryTransactionsUseCase;
    private final QueryBalanceByCategoryUseCase queryBalanceByCategoryUseCase;

    public TransactionController(CreateTransactionUseCase createTransactionUseCase,
                                  QueryTransactionsUseCase queryTransactionsUseCase,
                                  QueryBalanceByCategoryUseCase queryBalanceByCategoryUseCase) {
        this.createTransactionUseCase = createTransactionUseCase;
        this.queryTransactionsUseCase = queryTransactionsUseCase;
        this.queryBalanceByCategoryUseCase = queryBalanceByCategoryUseCase;
    }

    @PostMapping
    public ResponseEntity<Transaction> create(@RequestBody CreateTransactionRequest request) {
        Transaction transaction = createTransactionUseCase.execute(
                request.description(),
                request.amount(),
                request.type(),
                request.category(),
                request.date()
        );
        return ResponseEntity.ok(transaction);
    }

    @GetMapping
    public List<Transaction> all() {
        return queryTransactionsUseCase.all();
    }

    @GetMapping("/category/{category}")
    public List<Transaction> byCategory(@PathVariable String category) {
        return queryTransactionsUseCase.byCategory(category);
    }

    // Endpoint novo, ligado à evolução do desafio: saldo consolidado por categoria.
    @GetMapping("/category/{category}/balance")
    public QueryBalanceByCategoryUseCase.BalanceResult balanceByCategory(@PathVariable String category) {
        return queryBalanceByCategoryUseCase.execute(category);
    }

    public record CreateTransactionRequest(
            String description,
            BigDecimal amount,
            TransactionType type,
            String category,
            LocalDate date
    ) {
    }
}
