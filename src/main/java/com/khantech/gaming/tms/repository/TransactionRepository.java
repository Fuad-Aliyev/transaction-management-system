package com.khantech.gaming.tms.repository;

import com.khantech.gaming.tms.model.Transaction;
import com.khantech.gaming.tms.model.TransactionStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    @Query("SELECT t FROM Transaction t JOIN FETCH t.wallet WHERE t.status = :status")
    List<Transaction> findTransactionsWithWalletsByStatus(@Param("status") TransactionStatus status);

    // Fetch transactions for a wallet with AWAITING_APPROVAL and PENDING statuses using pessimistic lock
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t FROM Transaction t WHERE t.wallet.id = :walletId AND t.status IN :statuses")
    List<Transaction> findTransactionsByWalletIdAndStatusesWithLock(@Param("walletId") Long walletId,
                                                                    @Param("statuses") List<TransactionStatus> statuses);

    @Query("SELECT t FROM Transaction t WHERE t.id = :transactionId AND t.status = :status")
    Optional<Transaction> findByIdAndStatus(@Param("transactionId") Long transactionId, @Param("status") TransactionStatus status);
}
