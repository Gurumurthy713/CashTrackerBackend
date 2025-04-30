package com.cashtracker.repository;

import com.cashtracker.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

    @Query(value = "SELECT * FROM transaction ORDER BY date DESC LIMIT ?2 OFFSET ?1", nativeQuery = true)
    List<Transaction> findTransactionsPaged(int offset, int limit);
}
