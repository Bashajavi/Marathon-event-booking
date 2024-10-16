package com.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import com.entity.Gallery;

public interface GalleryRepository extends JpaRepository<Gallery, Long> {
}
