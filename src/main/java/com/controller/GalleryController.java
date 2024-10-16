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
import com.entity.Contact;
import com.entity.Gallery;
import com.entity.Participant;
import com.entity.Partner;
import com.entity.Poster;
import com.entity.Sponsor;
import com.repo.GalleryRepository;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Controller
public class GalleryController {

    @Autowired
    private GalleryRepository galleryRepository;

    // File upload directory
    private static String UPLOAD_DIR = "src/main/resources/static/images/gallery/";

    @GetMapping("/services")
    public String servicePage(Model model) {
        // Fetch all galleries from the repository
        List<Gallery> galleries = galleryRepository.findAll();
        
        // Add the galleries to the model
        model.addAttribute("galleries", galleries);
        
        // Return the view name
        return "services";
    }
    
    @GetMapping("/manage-gallery")
    public String listGallery(@RequestParam(name = "page", defaultValue = "0") int page,
                              @RequestParam(name = "size", defaultValue = "5") int size,
                              Model model) {
        // Retrieve a page of gallery images using pagination
        Page<Gallery> galleryPage = galleryRepository.findAll(PageRequest.of(page, size));

        // Add pagination information to the model
        model.addAttribute("pagedImages", galleryPage);        

        return "gallery/manage-gallery";
    }

    
//  @GetMapping("/services")
//	public String index(Model model) {
//	    // Fetch all associates from the repository
//	    List<Gallery> associatesList = galleryRepository.findAll(); // Assuming findAll() retrieves all associates	    
//
//	    model.addAttribute("gallery", galleryList);	    	    
//	    
//	    return "services"; // Return the index page with the registration form
//	}
        
    
    @GetMapping("/add-image")
    public String showAddForm(Model model) {
        model.addAttribute("gallery", new Gallery());
        return "gallery/add-image";
    }

    @PostMapping("/add-image")
    public String addImage(@ModelAttribute("gallery") Gallery gallery,
                           @RequestParam("file") MultipartFile file,
                           RedirectAttributes redirectAttributes) {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Please select a file to upload");
            return "redirect:/add-image";
        }

        try {
            // Get the filename
            String filename = StringUtils.normalizeSpace(file.getOriginalFilename());
            // Copy file to the target location (in the static folder)
            Path targetLocation = Paths.get(UPLOAD_DIR + filename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            // Set the image name in the Gallery entity
            gallery.setImageName(filename);
            // Save the gallery entity to the database
            galleryRepository.save(gallery);
            redirectAttributes.addFlashAttribute("message", "Image Added successfully");
        } catch (IOException ex) {
            ex.printStackTrace();
            redirectAttributes.addFlashAttribute("message", "Failed to upload file");
        }

        return "redirect:/manage-gallery";
    }

    @GetMapping("/edit-image/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        Gallery gallery = galleryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid gallery Id:" + id));
        model.addAttribute("gallery", gallery);
        return "gallery/edit-image";
    }

    @PostMapping("/edit-image/{id}")
    public String updateImage(@PathVariable("id") Long id, @ModelAttribute("gallery") Gallery updatedGallery,
                              @RequestParam("file") MultipartFile file,
                              RedirectAttributes redirectAttributes) {
        galleryRepository.findById(id)
                .map(gallery -> {
                    // Update gallery details
                    gallery.setName(updatedGallery.getName());
                    // Check if a new file is provided
                    if (!file.isEmpty()) {
                        try {
                            // Get the filename
                            String filename = StringUtils.normalizeSpace(file.getOriginalFilename());
                            // Delete the old image file if it exists
                            if (gallery.getImageName() != null && !gallery.getImageName().isEmpty()) {
                                Path oldImagePath = Paths.get(UPLOAD_DIR + gallery.getImageName());
                                Files.deleteIfExists(oldImagePath);
                            }
                            // Copy the new file to the target location (in the static folder)
                            Path targetLocation = Paths.get(UPLOAD_DIR + filename);
                            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
                            // Set the new image name in the Gallery entity
                            gallery.setImageName(filename);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                            throw new IllegalArgumentException("Failed to upload file");
                        }
                    }
                    galleryRepository.save(gallery);
                    return gallery;
                })
                .orElseThrow(() -> new IllegalArgumentException("Invalid gallery Id:" + id));
        redirectAttributes.addFlashAttribute("message", "Image updated successfully");
        return "redirect:/manage-gallery";
    }

    @GetMapping("/delete-image/{id}")
    public String deleteImage(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        galleryRepository.findById(id)
                .ifPresentOrElse(gallery -> {
                    // Get the filename of the image associated with the gallery
                    String imageName = gallery.getImageName();
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
                    // Delete the gallery entity from the database
                    galleryRepository.delete(gallery);
                    redirectAttributes.addFlashAttribute("message", "Image deleted successfully");
                }, () -> {
                    throw new IllegalArgumentException("Invalid gallery Id:" + id);
                });
        return "redirect:/manage-gallery";
    }
}
