package com.jorge.bears.repository;

import com.jorge.bears.model.Bear;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BearRepository extends JpaRepository<Bear, Long> {
}
