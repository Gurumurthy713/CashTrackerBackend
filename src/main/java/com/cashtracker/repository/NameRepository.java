package com.cashtracker.repository;

import com.cashtracker.model.Name;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NameRepository extends JpaRepository<Name, Integer> {
    Name findByNameIgnoreCase(String name);
    Optional<Name> findByNameIgnoreCaseAndIdNot(String name, Integer id);
}
