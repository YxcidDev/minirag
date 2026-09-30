package com.tecnologico.minirag.service;

import com.tecnologico.minirag.model.QueryLog;
import com.tecnologico.minirag.repository.QueryLogRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

@Service
public class ChatService {

    private final ChatClient chatClient;
    private final QueryLogRepository queryLogRepository;

    public ChatService(
            ChatClient.Builder builder,
            VectorStore vectorStore,
            QueryLogRepository queryLogRepository) {

        this.queryLogRepository = queryLogRepository;

        SearchRequest searchRequest = SearchRequest.builder()
                .topK(4)
                .similarityThreshold(0.50)
                .build();

        QuestionAnswerAdvisor ragAdvisor = QuestionAnswerAdvisor
                .builder(vectorStore)
                .searchRequest(searchRequest)
                .build();

        this.chatClient = builder
                .defaultSystem("""
                        Eres un asistente académico.
                        Responde utilizando exclusivamente
                        la información recuperada de la
                        base documental.
                        Si la información necesaria no está
                        disponible en el contexto proporcionado,
                        responde exactamente: "No encuentro esa información en los
                        documentos disponibles."
                        No inventes información.
                        Responde de manera clara, breve y
                        académicamente correcta.
                        """)
                .defaultAdvisors(ragAdvisor)
                .build();
    }

    public String ask(String question) {
        String answer = chatClient
                .prompt()
                .user(question)
                .call()
                .content();

        QueryLog queryLog = new QueryLog(question, answer);
        queryLogRepository.save(queryLog);

        return answer;
    }
}