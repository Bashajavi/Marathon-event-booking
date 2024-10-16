package com.controller;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.entity.Participant;
import com.repo.ParticipantRepository;

import java.util.List;

@Controller
public class LeaderboardController {

    private final ParticipantRepository participantRepository;

    public LeaderboardController(ParticipantRepository participantRepository) {
        this.participantRepository = participantRepository;
    }

	@GetMapping("/5k-leaderboard")
	public String viewresult10k(Model model) {
	    // Fetch participants from the 5K category and order them by finish time
	    List<Participant> participants = participantRepository.findByCategoryOrderByFinishTimeAsc("5k");

	    // Pass participants to the Thymeleaf template
	    model.addAttribute("participants", participants);

	    return "results/5k-leaderboard";
	}

	@GetMapping("/10k-leaderboard")
	public String viewResult10k(Model model) {
	    // Fetch participants from the 10K category and order them by finish time
	    List<Participant> participants = participantRepository.findByCategoryOrderByFinishTimeAsc("10k");

	    // Pass participants to the Thymeleaf template
	    model.addAttribute("participants", participants);

	    return "results/10k-leaderboard";
	}

	@GetMapping("/21k-leaderboard")
	public String viewResult21k(Model model) {
	    // Fetch participants from the 21K category and order them by finish time
	    List<Participant> participants = participantRepository.findByCategoryOrderByFinishTimeAsc("21k");

	    // Pass participants to the Thymeleaf template
	    model.addAttribute("participants", participants);

	    return "results/21k-leaderboard";
	}

	@GetMapping("/42k-leaderboard")
	public String viewResult42k(Model model) {
	    // Fetch participants from the 42K category and order them by finish time
	    List<Participant> participants = participantRepository.findByCategoryOrderByFinishTimeAsc("42k");

	    // Pass participants to the Thymeleaf template
	    model.addAttribute("participants", participants);

	    return "results/42k-leaderboard";
	}
	
}


