package com.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import com.entity.Partner;

public interface PartnerRepository extends JpaRepository<Partner, Long> {
}
