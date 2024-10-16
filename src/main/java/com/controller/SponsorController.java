package com.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.entity.Sponsor;
import com.repo.SponsorRepository;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Controller
public class SponsorController {

    @Autowired
    private SponsorRepository sponsorRepository;

    // File upload directory
    private static String UPLOAD_DIR = "src/main/resources/static/images/sponsors/";

    @GetMapping("/manage-sponsors")
    public String listSponsors(@RequestParam(name = "page", defaultValue = "0") int page,
                               @RequestParam(name = "size", defaultValue = "5") int size,
                               Model model) {
        // Retrieve a page of sponsors using pagination
        Page<Sponsor> sponsorsPage = sponsorRepository.findAll(PageRequest.of(page, size));

        // Add pagination information to the model
        model.addAttribute("pagedSponsors", sponsorsPage);

        return "sponsors/manage-sponsors";
    }        

    @GetMapping("/add-sponsor")
    public String showAddForm(Model model) {
        model.addAttribute("sponsor", new Sponsor());
        return "sponsors/add-sponsor";
    }

    @PostMapping("/add-sponsor")
    public String addSponsor(@ModelAttribute("sponsor") Sponsor sponsor,
                             @RequestParam("file") MultipartFile file,
                             RedirectAttributes redirectAttributes) {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Please select a file to upload");
            return "redirect:/sponsors/add-sponsor";
        }
 
        try {
            // Get the filename
            String filename = StringUtils.normalizeSpace(file.getOriginalFilename());
            // Copy file to the target location (in the static folder)
            Path targetLocation = Paths.get(UPLOAD_DIR + filename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            // Set the image name in the Sponsor entity 
            sponsor.setLogoName(filename);
            // Save the sponsor entity to the database
            sponsorRepository.save(sponsor);
            redirectAttributes.addFlashAttribute("message", "Sponsor added successfully");
        } catch (IOException ex) {
            ex.printStackTrace();
            redirectAttributes.addFlashAttribute("message", "Failed to upload file");
        }

        return "redirect:/manage-sponsors";        
    }

    @GetMapping("/edit-sponsor/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        Sponsor sponsor = sponsorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid sponsor Id:" + id));
        model.addAttribute("sponsor", sponsor);
        return "sponsors/edit-sponsor";
    }

    @PostMapping("/edit-sponsor/{id}")
    public String updateSponsor(@PathVariable("id") Long id, @ModelAttribute("sponsor") Sponsor updatedSponsor,
                                @RequestParam("file") MultipartFile file,
                                RedirectAttributes redirectAttributes) {
        sponsorRepository.findById(id)
                .map(sponsor -> {
                    sponsor.setName(updatedSponsor.getName());
                    // Check if a new file is provided
                    if (!file.isEmpty()) {
                        try {
                            // Get the filename
                            String filename = StringUtils.normalizeSpace(file.getOriginalFilename());
                            // Delete the old logo file if it exists
                            if (sponsor.getLogoName() != null && !sponsor.getLogoName().isEmpty()) {
                                Path oldLogoPath = Paths.get(UPLOAD_DIR + sponsor.getLogoName());
                                Files.deleteIfExists(oldLogoPath);
                            }
                            // Copy the new file to the target location (in the static folder)
                            Path targetLocation = Paths.get(UPLOAD_DIR + filename);
                            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
                            // Set the new logo name in the Sponsor entity
                            sponsor.setLogoName(filename);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                            throw new IllegalArgumentException("Failed to upload file");
                        }
                    }
                    sponsorRepository.save(sponsor);
                    return sponsor;
                })
                .orElseThrow(() -> new IllegalArgumentException("Invalid sponsor Id:" + id));
        redirectAttributes.addFlashAttribute("message", "Sponsor updated successfully");        
        return "redirect:/manage-sponsors";
    }

    @GetMapping("/delete-sponsor/{id}")
    public String deleteSponsor(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        sponsorRepository.findById(id)
                .ifPresentOrElse(sponsor -> {
                    // Get the filename of the logo associated with the sponsor
                    String logoName = sponsor.getLogoName();
                    // Delete the logo file from the file system
                    if (logoName != null && !logoName.isEmpty()) {
                        try {
                            Path logoPath = Paths.get(UPLOAD_DIR + logoName);
                            Files.deleteIfExists(logoPath);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                            throw new RuntimeException("Failed to delete logo file");
                        }
                    }
                    // Delete the sponsor entity from the database
                    sponsorRepository.delete(sponsor);
                    redirectAttributes.addFlashAttribute("message", "Sponsor deleted successfully");
                }, () -> {
                    throw new IllegalArgumentException("Invalid sponsor Id:" + id);
                });        
        return "redirect:/manage-sponsors";
    }

}
