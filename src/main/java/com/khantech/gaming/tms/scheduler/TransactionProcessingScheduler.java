package com.khantech.gaming.tms.scheduler;

import com.khantech.gaming.tms.service.TransactionOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import static com.khantech.gaming.tms.util.Constants.TRANSACTION_BATCH_PROCESSOR;
import static com.khantech.gaming.tms.util.LogMessages.SCHEDULER_END_PROCESSING;
import static com.khantech.gaming.tms.util.LogMessages.SCHEDULER_START_PROCESSING;

@Component
public class TransactionProcessingScheduler {
    private static final Logger log = LoggerFactory.getLogger(TransactionProcessingScheduler.class);

    private final TransactionOperation<Void, Void> transactionBatchProcessor;

    public TransactionProcessingScheduler(
            @Qualifier(TRANSACTION_BATCH_PROCESSOR) TransactionOperation<Void, Void> transactionBatchProcessor
    ) {
        this.transactionBatchProcessor = transactionBatchProcessor;
    }

    @Scheduled(cron = "${scheduling.cron.process-transactions}")
    public void schedulePendingTransactionProcessing() {
        log.info(SCHEDULER_START_PROCESSING);

        // Process pending transactions
        transactionBatchProcessor.execute(null);

        log.info(SCHEDULER_END_PROCESSING);
    }
}
