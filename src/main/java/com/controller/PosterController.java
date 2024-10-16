package com.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.entity.Poster;
import com.repo.PosterRepository;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Controller
public class PosterController {

    @Autowired
    private PosterRepository posterRepository;

    // File upload directory
    private static String UPLOAD_DIR = "src/main/resources/static/images/posters/";

    @GetMapping("/manage-posters")
    public String listPosters(@RequestParam(name = "page", defaultValue = "0") int page,
                               @RequestParam(name = "size", defaultValue = "5") int size,
                               Model model) {
        // Retrieve a page of posters using pagination
        Page<Poster> postersPage = posterRepository.findAll(PageRequest.of(page, size));

        // Add pagination information to the model
        model.addAttribute("pagedPosters", postersPage);

        return "posters/manage-posters";
    }        

    @GetMapping("/add-poster")
    public String showAddForm(Model model) {
        model.addAttribute("poster", new Poster());
        return "posters/add-poster";
    }

    @PostMapping("/add-poster")
    public String addPoster(@ModelAttribute("poster") Poster poster,
                             @RequestParam("file") MultipartFile file,
                             RedirectAttributes redirectAttributes) {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Please select a file to upload");
            return "redirect:/posters/add-poster";
        }
 
        try {
            // Get the filename
            String filename = StringUtils.normalizeSpace(file.getOriginalFilename());
            // Copy file to the target location (in the static folder)
            Path targetLocation = Paths.get(UPLOAD_DIR + filename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            // Set the image name in the Poster entity 
            poster.setImageName(filename);
            // Save the poster entity to the database
            posterRepository.save(poster);
            redirectAttributes.addFlashAttribute("message", "Poster added successfully");
        } catch (IOException ex) {
            ex.printStackTrace();
            redirectAttributes.addFlashAttribute("message", "Failed to upload file");
        }

        return "redirect:/manage-posters";        
    }

    @GetMapping("/edit-poster/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        Poster poster = posterRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid poster Id:" + id));
        model.addAttribute("poster", poster);
        return "posters/edit-poster";
    }

    @PostMapping("/edit-poster/{id}")
    public String updatePoster(@PathVariable("id") Long id, @ModelAttribute("poster") Poster updatedPoster,
                                @RequestParam("file") MultipartFile file,
                                RedirectAttributes redirectAttributes) {
        posterRepository.findById(id)
                .map(poster -> {
                    poster.setName(updatedPoster.getName());
                    // Check if a new file is provided
                    if (!file.isEmpty()) {
                        try {
                            // Get the filename
                            String filename = StringUtils.normalizeSpace(file.getOriginalFilename());
                            // Delete the old image file if it exists
                            if (poster.getImageName() != null && !poster.getImageName().isEmpty()) {
                                Path oldImagePath = Paths.get(UPLOAD_DIR + poster.getImageName());
                                Files.deleteIfExists(oldImagePath);
                            }
                            // Copy the new file to the target location (in the static folder)
                            Path targetLocation = Paths.get(UPLOAD_DIR + filename);
                            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
                            // Set the new image name in the Poster entity
                            poster.setImageName(filename);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                            throw new IllegalArgumentException("Failed to upload file");
                        }
                    }
                    posterRepository.save(poster);
                    return poster;
                })
                .orElseThrow(() -> new IllegalArgumentException("Invalid poster Id:" + id));
        redirectAttributes.addFlashAttribute("message", "Poster updated successfully");        
        return "redirect:/manage-posters";
    }

    @GetMapping("/delete-poster/{id}")
    public String deletePoster(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        posterRepository.findById(id)
                .ifPresentOrElse(poster -> {
                    // Get the filename of the image associated with the poster
                    String imageName = poster.getImageName();
                    // Delete the image file from the file system
                    if (imageName != null && !imageName.isEmpty()) {
                        try {
                            Path imagePath = Paths.get(UPLOAD_DIR + imageName);
                            Files.deleteIfExists(imagePath);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                            throw new RuntimeException("Failed to delete image file");
                        }
                    }
                    // Delete the poster entity from the database
                    posterRepository.delete(poster);
                    redirectAttributes.addFlashAttribute("message", "Poster deleted successfully");
                }, () -> {
                    throw new IllegalArgumentException("Invalid poster Id:" + id);
                });        
        return "redirect:/manage-posters";
    }

}
