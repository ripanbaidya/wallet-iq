package com.walletiq.backend.service.impl;

import com.walletiq.backend.entity.Transaction;
import com.walletiq.backend.service.EmbeddingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
@Primary
@RequiredArgsConstructor
public class PgVectorEmbeddingServiceImpl implements EmbeddingService {

    private final VectorStore vectorStore;

    @Override
    public String store(Transaction txn) {
        try {
            Document doc = toDocument(txn);
            vectorStore.add(List.of(doc));
            log.debug("Stored embedding for transaction id={}", txn.getId());
            return doc.getId();
        } catch (Exception e) {
            log.error("Failed to store embedding for transaction id={}", txn.getId(), e);
            return null;
        }
    }

    @Override
    public String update(String embeddingId, Transaction txn) {
        delete(embeddingId);
        return store(txn);
    }

    @Override
    public void delete(String embeddingId) {
        if (embeddingId == null) return;
        try {
            vectorStore.delete(List.of(embeddingId));
            log.debug("Deleted embedding id={}", embeddingId);
        } catch (Exception e) {
            log.error("Failed to delete embedding id={}", embeddingId, e);
        }
    }

    // Helper method

    private Document toDocument(Transaction txn) {
        // This text is what gets embedded — make it information-dense
        String content = """
            Transaction ID: %s
            Date: %s
            Type: %s
            Amount: %.2f
            Category: %s
            Payment Mode: %s
            Note: %s
            """.formatted(txn.getId(), txn.getDate(), txn.getType(), txn.getAmount(),
            txn.getCategory() != null ? txn.getCategory().getName() : "Uncategorized",
            txn.getPaymentMode() != null ? txn.getPaymentMode().getName() : "Unknown",
            txn.getNote() != null ? txn.getNote() : ""
        );

        // Metadata is used for filtering — userId
        Map<String, Object> metadata = Map.of(
            "transactionId", txn.getId().toString(),
            "userId", txn.getUser().getId().toString(),
            "type", txn.getType().name(),
            "date", txn.getDate().toString(),
            "amount", txn.getAmount()
        );

        return new Document(content, metadata);
    }
}
