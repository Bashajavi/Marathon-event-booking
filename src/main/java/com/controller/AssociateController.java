package com.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.entity.Associate;
import com.repo.AssociateRepository;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Controller
public class AssociateController {

    @Autowired
    private AssociateRepository associateRepository;

    // File upload directory
    private static String UPLOAD_DIR = "src/main/resources/static/images/associates/";

    @GetMapping("/manage-associates")
    public String listAssociates(@RequestParam(name = "page", defaultValue = "0") int page,
                               @RequestParam(name = "size", defaultValue = "5") int size,
                               Model model) {
        // Retrieve a page of associates using pagination
        Page<Associate> associatesPage = associateRepository.findAll(PageRequest.of(page, size));

        // Add pagination information to the model
        model.addAttribute("pagedAssociates", associatesPage);

        return "associates/manage-associates";
    }        

    @GetMapping("/add-associate")
    public String showAddForm(Model model) {
        model.addAttribute("associate", new Associate());
        return "associates/add-associate";
    }

    @PostMapping("/add-associate")
    public String addAssociate(@ModelAttribute("associate") Associate associate,
                             @RequestParam("file") MultipartFile file,
                             RedirectAttributes redirectAttributes) {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Please select a file to upload");
            return "redirect:/add-associate";
        }
 
        try {
            // Get the filename
            String filename = StringUtils.normalizeSpace(file.getOriginalFilename());
            // Copy file to the target location (in the static folder)
            Path targetLocation = Paths.get(UPLOAD_DIR + filename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            // Set the image name in the Associate entity 
            associate.setLogoName(filename);
            // Save the associate entity to the database
            associateRepository.save(associate);
            redirectAttributes.addFlashAttribute("message", "Associate added successfully");
        } catch (IOException ex) {
            ex.printStackTrace();
            redirectAttributes.addFlashAttribute("message", "Failed to upload file");
        }

        return "redirect:/manage-associates";        
    }

    @GetMapping("/edit-associate/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        Associate associate = associateRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid associate Id:" + id));
        model.addAttribute("associate", associate);
        return "associates/edit-associate";
    }

    @PostMapping("/edit-associate/{id}")
    public String updateAssociate(@PathVariable("id") Long id, @ModelAttribute("associate") Associate updatedAssociate,
                                @RequestParam("file") MultipartFile file,
                                RedirectAttributes redirectAttributes) {
        associateRepository.findById(id)
                .map(associate -> {
                    associate.setName(updatedAssociate.getName());
                    // Check if a new file is provided
                    if (!file.isEmpty()) {
                        try {
                            // Get the filename
                            String filename = StringUtils.normalizeSpace(file.getOriginalFilename());
                            // Delete the old logo file if it exists
                            if (associate.getLogoName() != null && !associate.getLogoName().isEmpty()) {
                                Path oldLogoPath = Paths.get(UPLOAD_DIR + associate.getLogoName());
                                Files.deleteIfExists(oldLogoPath);
                            }
                            // Copy the new file to the target location (in the static folder)
                            Path targetLocation = Paths.get(UPLOAD_DIR + filename);
                            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
                            // Set the new logo name in the Associate entity
                            associate.setLogoName(filename);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                            throw new IllegalArgumentException("Failed to upload file");
                        }
                    }
                    associateRepository.save(associate);
                    return associate;
                })
                .orElseThrow(() -> new IllegalArgumentException("Invalid associate Id:" + id));
        redirectAttributes.addFlashAttribute("message", "Associate updated successfully");        
        return "redirect:/manage-associates";
    }

    @GetMapping("/delete-associate/{id}")
    public String deleteAssociate(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        associateRepository.findById(id)
                .ifPresentOrElse(associate -> {
                    // Get the filename of the image associated with the associate
                    String logoName = associate.getLogoName();
                    // Delete the image file from the file system
                    if (logoName != null && !logoName.isEmpty()) {
                        try {
                            Path logoPath = Paths.get(UPLOAD_DIR + logoName);
                            Files.deleteIfExists(logoPath);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                                    throw new RuntimeException("Failed to delete logo file");
                                }
                            }
                            // Delete the associate entity from the database
                            associateRepository.delete(associate);
                            redirectAttributes.addFlashAttribute("message", "Associate deleted successfully");
                        }, () -> {
                            throw new IllegalArgumentException("Invalid associate Id:" + id);
                        });        
                return "redirect:/manage-associates";
            }
        }
