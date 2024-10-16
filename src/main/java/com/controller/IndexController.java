package com.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.entity.Associate;
import com.entity.Participant;
import com.entity.Partner;
import com.entity.Poster;
import com.entity.Sponsor;
import com.repo.AssociateRepository;
import com.repo.PartnerRepository;
import com.repo.PosterRepository;
import com.repo.SponsorRepository;

@Controller
public class IndexController {

	@Autowired
    private AssociateRepository associateRepository; // Autowire the AssociateRepository
	
	@Autowired
    private SponsorRepository sponsorRepository; // Autowire the AssociateRepository
	
	@Autowired
    private PartnerRepository partnerRepository; // Autowire the AssociateRepository
	
	@Autowired
    private PosterRepository posterRepository; // Autowire the AssociateRepository
	
	@GetMapping("/")
	public String index(Model model) {
	    // Fetch all associates from the repository
	    List<Associate> associatesList = associateRepository.findAll(); // Assuming findAll() retrieves all associates
	    
	    // Fetch all sponsors from the repository
	    List<Sponsor> sponsorsList = sponsorRepository.findAll(); // Assuming findAll() retrieves all sponsors
	    
	    // Fetch all partners from the repository
	    List<Partner> partnersList = partnerRepository.findAll(); // Assuming findAll() retrieves all partners
	    
	    // Fetch all posters from the repository
	    List<Poster> postersList = posterRepository.findAll(); // Assuming findAll() retrieves all posters
	    
	    // Add any necessary model attributes for your index page
	    model.addAttribute("participant", new Participant());
	    model.addAttribute("associates", associatesList);
	    model.addAttribute("sponsors", sponsorsList);
	    model.addAttribute("partners", partnersList);
	    model.addAttribute("posters", postersList);
	    
	    return "index"; // Return the index page with the registration form
	}

}
