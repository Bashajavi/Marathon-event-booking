package com.controller;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.entity.Participant;
import com.repo.ParticipantRepository;

import jakarta.validation.Valid;

@Controller
public class ParticipantController {

    @Autowired
    private ParticipantRepository participantRepository;
    
    @Autowired
    private JavaMailSender emailSender;
    
    @GetMapping("/manage-participants")
    public String listParticipants(@RequestParam(name = "page", defaultValue = "0") int page,
                                   @RequestParam(name = "size", defaultValue = "5") int size,
                                   Model model) {
        // Retrieve a page of participants using pagination
        Page<Participant> participantsPage = participantRepository.findAll(PageRequest.of(page, size));

        // Add pagination information to the model
        model.addAttribute("pagedParticipants", participantsPage);
        
        // Retrieve all participants Result
        List<Participant> allParticipants = participantRepository.findAll();

        // Add all participants to the model
        model.addAttribute("allParticipants", allParticipants);

        return "participants/manage-participants";
    }

    @GetMapping("/add-participant")
    public String showAddForm(Model model) {
        model.addAttribute("participant", new Participant());     
        return "participants/add-participant";
    }

    
    @PostMapping("/add-participant")
    public String addParticipant(@ModelAttribute("participant") @Valid Participant participant,
                                 BindingResult result,
                                 RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "participants/add-participant";
        }

        // Fetch the maximum bibNumber from the database
        Integer maxBibNumber = participantRepository.findMaxBibNumber();

        // If no participant exists, start with bibNumber 2000, otherwise, increment the maximum bibNumber by one
        int nextBibNumber = maxBibNumber != null ? maxBibNumber + 1 : 2000;

        // Set the bibNumber for the new participant
        participant.setBibNumber(nextBibNumber);

        // Add participant to the database
        participantRepository.save(participant);

        // Send email
        sendEmail(participant);

        // Redirect to the participant management page with a success message
        redirectAttributes.addFlashAttribute("message", "Participant added successfully");
        return "redirect:/";
    }


    private void sendEmail(Participant participant) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(participant.getEmail());
        message.setSubject("Confirmation: Participant Registration");
        message.setText("Dear " + participant.getName() + ",\n\n"
                + "Thank you for registering for the event. Your registration details:\n"
                + "Name: " + participant.getName() + "\n"
                + "Age: " + participant.getAge() + "\n"
                + "Email: " + participant.getEmail() + "\n"
                + "Category: " + participant.getCategory() + "\n"
                + "Gender: " + participant.getGender() + "\n"
                + "Address: " + participant.getAddress() + "\n"    
                + "Bib Number: " + participant.getBibNumber() + "\n"                
                + "T-Shirt Size: " + participant.getTshirtSize() + "\n\n"
                + "We look forward to seeing you at the event!\n\n"
                + "Best regards,\nYour Event Team");
        emailSender.send(message);
    }
    
    
    
    @GetMapping("/edit-participant/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        Participant participant = participantRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid participant Id:" + id));
        model.addAttribute("participant", participant);
        return "participants/edit-participant";
    }

    
    @PostMapping("/edit-participant/{id}")
    public String updateParticipant(@PathVariable("id") Long id, @ModelAttribute("participant") @Valid Participant updatedParticipant,
                                     BindingResult result,
                                     RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "participants/edit-participant";
        }

        // Fetch the maximum bibNumber from the database
        Integer maxBibNumber = participantRepository.findMaxBibNumber();

        // If no participant exists, start with bibNumber 2000, otherwise, increment the maximum bibNumber by one
        int nextBibNumber = maxBibNumber != null ? maxBibNumber + 1 : 2000;

        // Set the bibNumber for the updated participant
        updatedParticipant.setBibNumber(nextBibNumber);

        // Update participant in the database
        participantRepository.findById(id)
                .map(participant -> {
                    // Update participant fields
                    participant.setName(updatedParticipant.getName());
                    participant.setAge(updatedParticipant.getAge());
                    participant.setGender(updatedParticipant.getGender());
                    participant.setEmail(updatedParticipant.getEmail());
                    participant.setCategory(updatedParticipant.getCategory());
                    participant.setTshirtSize(updatedParticipant.getTshirtSize());
                    participant.setAddress(updatedParticipant.getAddress());
                    participant.setBibNumber(updatedParticipant.getBibNumber());
                    participant.setStatus(updatedParticipant.getStatus());
                    participant.setFinishTime(updatedParticipant.getFinishTime());                    
                    return participantRepository.save(participant);
                })
                .orElseThrow(() -> new IllegalArgumentException("Invalid participant Id:" + id));

        redirectAttributes.addFlashAttribute("message", "Participant updated successfully");
        return "redirect:/manage-participants";
    }


    @GetMapping("/delete-participant/{id}")
    public String deleteParticipant(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        participantRepository.findById(id)
                .ifPresentOrElse(participant -> {
                    participantRepository.delete(participant);
                    redirectAttributes.addFlashAttribute("message", "Participant deleted successfully");
                }, () -> {
                    throw new IllegalArgumentException("Invalid participant Id:" + id);
                });

        return "redirect:/manage-participants";
    }
}
