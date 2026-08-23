package dio.budgeting.infrastructure.ai;

import dio.budgeting.application.CreateTransactionUseCase;
import dio.budgeting.application.QueryBalanceByCategoryUseCase;
import dio.budgeting.application.QueryTransactionsUseCase;
import dio.budgeting.domain.Transaction;
import dio.budgeting.domain.TransactionType;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Exposes the same application use cases used by the REST layer as tools
 * the model can call. Nenhuma regra de negócio vive aqui: cada @Tool apenas
 * traduz a intenção do modelo para uma chamada de use case existente.
 */
@Component
public class BudgetTools {

    private final CreateTransactionUseCase createTransactionUseCase;
    private final QueryTransactionsUseCase queryTransactionsUseCase;
    private final QueryBalanceByCategoryUseCase queryBalanceByCategoryUseCase;

    public BudgetTools(CreateTransactionUseCase createTransactionUseCase,
                        QueryTransactionsUseCase queryTransactionsUseCase,
                        QueryBalanceByCategoryUseCase queryBalanceByCategoryUseCase) {
        this.createTransactionUseCase = createTransactionUseCase;
        this.queryTransactionsUseCase = queryTransactionsUseCase;
        this.queryBalanceByCategoryUseCase = queryBalanceByCategoryUseCase;
    }

    @Tool(description = "Registra uma nova transação financeira (receita ou despesa)")
    public Transaction registrarTransacao(String descricao, BigDecimal valor,
                                           TransactionType tipo, String categoria, LocalDate data) {
        return createTransactionUseCase.execute(descricao, valor, tipo, categoria, data);
    }

    @Tool(description = "Lista todas as transações financeiras registradas")
    public List<Transaction> listarTransacoes() {
        return queryTransactionsUseCase.all();
    }

    @Tool(description = "Consulta o saldo (receitas menos despesas) de uma categoria específica, ex: alimentação, transporte")
    public QueryBalanceByCategoryUseCase.BalanceResult consultarSaldoPorCategoria(String categoria) {
        return queryBalanceByCategoryUseCase.execute(categoria);
    }
}
