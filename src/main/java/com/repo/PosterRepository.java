package com.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import com.entity.Poster;

public interface PosterRepository extends JpaRepository<Poster, Long> {
}
