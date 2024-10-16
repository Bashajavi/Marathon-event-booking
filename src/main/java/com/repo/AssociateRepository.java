package com.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import com.entity.Associate;

public interface AssociateRepository extends JpaRepository<Associate, Long> {
}
