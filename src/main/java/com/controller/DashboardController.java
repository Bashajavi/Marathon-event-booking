package com.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.repo.SponsorRepository;
import com.repo.PartnerRepository;
import com.repo.AssociateRepository;
import com.repo.ContactRepository;
import com.repo.PosterRepository;
import com.repo.GalleryRepository;
//import com.repo.CertificationRepository;
//import com.repo.ResultRepository;
import com.repo.ParticipantRepository;

@Controller
public class DashboardController {

    @Autowired
    private SponsorRepository sponsorRepository;

    @Autowired
    private PartnerRepository partnerRepository;

    @Autowired
    private AssociateRepository associateRepository;

    @Autowired
    private PosterRepository posterRepository;

    @Autowired
    private GalleryRepository galleryRepository;
    
    @Autowired
    private ContactRepository contactRepository;

//    @Autowired
//    private CertificationRepository certificationRepository;
//
//    @Autowired
//    private ResultRepository resultRepository;

    @Autowired
    private ParticipantRepository participantRepository;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        long totalSponsors = sponsorRepository.count();
        long totalPartners = partnerRepository.count();
        long totalAssociates = associateRepository.count();
        long totalPosters = posterRepository.count();
        long totalGalleryImages = galleryRepository.count();
//        long totalCertifications = certificationRepository.count();
//        long totalResults = resultRepository.count();
        long totalParticipants = participantRepository.count();
        long totalContacts = contactRepository.count();

        model.addAttribute("totalSponsors", totalSponsors);
        model.addAttribute("totalPartners", totalPartners);
        model.addAttribute("totalAssociates", totalAssociates);
        model.addAttribute("totalPosters", totalPosters);
        model.addAttribute("totalGalleryImages", totalGalleryImages);
//        model.addAttribute("totalCertifications", totalCertifications);
//        model.addAttribute("totalResults", totalResults);
        model.addAttribute("totalParticipants", totalParticipants);
        model.addAttribute("totalContacts", totalContacts);
                
        return "dashboard";
    }
}
