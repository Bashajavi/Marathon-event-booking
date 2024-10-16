package com.repo;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.entity.Participant;

@Repository
public interface ParticipantRepository extends JpaRepository<Participant, Long> {
    // You can define custom methods here if needed
    @Query("SELECT MAX(p.bibNumber) FROM Participant p")
    Integer findMaxBibNumber();
    
	List<Participant> findByCategoryOrderByFinishTimeAsc(String category);
}
