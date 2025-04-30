package com.cashtracker.controller;

import com.cashtracker.model.Name;
import com.cashtracker.model.Transaction;
import com.cashtracker.repository.NameRepository;
import com.cashtracker.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@CrossOrigin(origins = "*")
public class ApiController {

    @Autowired
    private NameRepository nameRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    // Fetch all names
    @GetMapping("/names")
    public List<Name> getAllNames() {
        return nameRepository.findAll();
    }

    // Add a new name
    @PostMapping("/names")
    public Name addName(@RequestBody Map<String, String> request) {
        String nameValue = request.get("name").trim();
        if (nameRepository.findByNameIgnoreCase(nameValue) != null) {
            throw new RuntimeException("Name already exists!");
        }
        Name name = new Name();
        name.setName(nameValue);
        return nameRepository.save(name);
    }
    
    @PutMapping("/names/{id}")
    public ResponseEntity<?> updateName(@PathVariable Integer id, @RequestBody Name nameRequest) {
        String newName = nameRequest.getName().trim();

        if (newName.isEmpty()) {
            return ResponseEntity.badRequest().body("Name cannot be empty");
        }

        // Check if name already exists for another record
        Optional<Name> existing = nameRepository.findByNameIgnoreCaseAndIdNot(newName, id);
        if (existing.isPresent()) {
            return ResponseEntity.status(409).body("Name already exists");
        }

        return nameRepository.findById(id).map(name -> {
            name.setName(newName);
            nameRepository.save(name);
            return ResponseEntity.ok("Name updated successfully");
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Fetch transactions with paging (default 10 per page)
    @GetMapping("/transactions")
    public List<Transaction> getTransactions(@RequestParam(defaultValue = "0") int page) {
        int pageSize = 100;
        return transactionRepository.findTransactionsPaged(page * pageSize, pageSize);
    }

    // Add a new transaction
    @PostMapping("/transactions")
    public Transaction addTransaction(@RequestBody Map<String, Object> request) {
        String date = (String) request.get("date");
        String nameValue = (String) request.get("name");
        String description = (String) request.get("description");
        Double amount = Double.valueOf(request.get("amount").toString());

        // If name is not found in database, add it
        Name name = nameRepository.findByNameIgnoreCase(nameValue);
        if (name == null) {
            Name newName = new Name();
            newName.setName(nameValue);
            name = nameRepository.save(newName);
        }

        Transaction transaction = new Transaction();
        transaction.setDate(date);
        transaction.setName(name.getName());
        transaction.setDescription(description);
        transaction.setAmount(amount);
        transaction.setPaid(false);

        return transactionRepository.save(transaction);
    }

    // Mark multiple transactions as Paid
    @PutMapping("/transactions/paid")
    public String markTransactionsPaid(@RequestBody Map<String, List<Integer>> request) {
        List<Integer> ids = request.get("ids");
        List<Transaction> transactions = transactionRepository.findAllById(ids);
        for (Transaction t : transactions) {
            t.setPaid(true);
        }
        transactionRepository.saveAll(transactions);
        return "Marked as Paid";
    }
}
