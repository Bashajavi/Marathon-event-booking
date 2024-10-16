package com.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import com.entity.Sponsor;

public interface SponsorRepository extends JpaRepository<Sponsor, Long> {
}
