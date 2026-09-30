package com.tecnologico.minirag.service;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class DocumentService {

    private final VectorStore vectorStore;

    public DocumentService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    @PostConstruct
    public void loadDocuments() throws IOException {
        System.out.println("------------------------------------");
        System.out.println("LOADING DOCUMENTS FOR RAG");
        System.out.println("------------------------------------");

        PathMatchingResourcePatternResolver resolver =
                new PathMatchingResourcePatternResolver();

        Resource[] resources =
                resolver.getResources("classpath:documents/*.txt");

        TokenTextSplitter splitter = TokenTextSplitter.builder()
                .withChunkSize(300)
                .build();

        List<Document> chunks = new ArrayList<>();

        for (Resource resource : resources) {
            String filename = resource.getFilename();
            System.out.println("Reading: " + filename);

            List<Document> fileDocuments = new TextReader(resource).get();
            List<Document> fileChunks = splitter.apply(fileDocuments);

            for (int i = 0; i < fileChunks.size(); i++) {
                Document chunk = fileChunks.get(i);

                String id = UUID.nameUUIDFromBytes(
                        (filename + "#" + i).getBytes(StandardCharsets.UTF_8)
                ).toString();

                chunks.add(Document.builder()
                        .id(id)
                        .text(chunk.getText())
                        .metadata(chunk.getMetadata())
                        .build());
            }
        }

        System.out.println("Chunks generated: " + chunks.size());

        vectorStore.add(chunks);

        System.out.println("Embeddings stored successfully.");
        System.out.println("------------------------------------");
    }
}