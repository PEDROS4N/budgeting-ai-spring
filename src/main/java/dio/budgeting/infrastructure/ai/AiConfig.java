package dio.budgeting.infrastructure.ai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {

    @Bean
    public ChatClient chatClient(ChatModel chatModel, BudgetTools budgetTools) {
        return ChatClient.builder(chatModel)
                .defaultSystem("""
                        Você é um assistente financeiro. Interprete o comando do usuário
                        e utilize as ferramentas disponíveis para registrar ou consultar
                        transações. Nunca invente valores: se faltar uma informação
                        obrigatória (valor, categoria ou tipo), peça esclarecimento.
                        """)
                .defaultTools(budgetTools)
                .build();
    }
}
