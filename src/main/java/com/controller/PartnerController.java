package com.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.entity.Partner;
import com.repo.PartnerRepository;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Controller
public class PartnerController {

    @Autowired
    private PartnerRepository partnerRepository;

    // File upload directory
    private static String UPLOAD_DIR = "src/main/resources/static/images/partners/";

    @GetMapping("/manage-partners")
    public String listPartners(@RequestParam(name = "page", defaultValue = "0") int page,
                               @RequestParam(name = "size", defaultValue = "5") int size,
                               Model model) {
        // Retrieve a page of partners using pagination
        Page<Partner> partnersPage = partnerRepository.findAll(PageRequest.of(page, size));

        // Add pagination information to the model
        model.addAttribute("pagedPartners", partnersPage);

        return "partners/manage-partners";
    }        

    @GetMapping("/add-partner")
    public String showAddForm(Model model) {
        model.addAttribute("partner", new Partner());
        return "partners/add-partner";
    }

    @PostMapping("/add-partner")
    public String addPartner(@ModelAttribute("partner") Partner partner,
                             @RequestParam("file") MultipartFile file,
                             RedirectAttributes redirectAttributes) {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Please select a file to upload");
            return "redirect:/partners/add-partner";
        }
 
        try {
            // Get the filename
            String filename = StringUtils.normalizeSpace(file.getOriginalFilename());
            // Copy file to the target location (in the static folder)
            Path targetLocation = Paths.get(UPLOAD_DIR + filename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            // Set the image name in the Partner entity 
            partner.setLogoName(filename);
            // Save the partner entity to the database
            partnerRepository.save(partner);
            redirectAttributes.addFlashAttribute("message", "Partner added successfully");
        } catch (IOException ex) {
            ex.printStackTrace();
            redirectAttributes.addFlashAttribute("message", "Failed to upload file");
        }

        return "redirect:/manage-partners";        
    }

    @GetMapping("/edit-partner/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        Partner partner = partnerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid partner Id:" + id));
        model.addAttribute("partner", partner);
        return "partners/edit-partner";
    }

    @PostMapping("/edit-partner/{id}")
    public String updatePartner(@PathVariable("id") Long id, @ModelAttribute("partner") Partner updatedPartner,
                                @RequestParam("file") MultipartFile file,
                                RedirectAttributes redirectAttributes) {
        partnerRepository.findById(id)
                .map(partner -> {
                    partner.setName(updatedPartner.getName());
                    // Check if a new file is provided
                    if (!file.isEmpty()) {
                        try {
                            // Get the filename
                            String filename = StringUtils.normalizeSpace(file.getOriginalFilename());
                            // Delete the old logo file if it exists
                            if (partner.getLogoName() != null && !partner.getLogoName().isEmpty()) {
                                Path oldLogoPath = Paths.get(UPLOAD_DIR + partner.getLogoName());
                                Files.deleteIfExists(oldLogoPath);
                            }
                            // Copy the new file to the target location (in the static folder)
                            Path targetLocation = Paths.get(UPLOAD_DIR + filename);
                            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
                            // Set the new logo name in the Partner entity
                            partner.setLogoName(filename);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                            throw new IllegalArgumentException("Failed to upload file");
                        }
                    }
                    partnerRepository.save(partner);
                    return partner;
                })
                .orElseThrow(() -> new IllegalArgumentException("Invalid partner Id:" + id));
        redirectAttributes.addFlashAttribute("message", "Partner updated successfully");        
        return "redirect:/manage-partners";
    }

    @GetMapping("/delete-partner/{id}")
    public String deletePartner(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        partnerRepository.findById(id)
                .ifPresentOrElse(partner -> {
                    // Get the filename of the logo associated with the partner
                    String logoName = partner.getLogoName();
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
                    // Delete the partner entity from the database
                    partnerRepository.delete(partner);
                    redirectAttributes.addFlashAttribute("message", "Partner deleted successfully");
                }, () -> {
                    throw new IllegalArgumentException("Invalid partner Id:" + id);
                });        
        return "redirect:/manage-partners";
    }
}
