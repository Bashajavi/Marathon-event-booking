package com.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.entity.Contact;
import com.repo.ContactRepository;

@Controller
public class ContactController {

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private JavaMailSender emailSender;

	@GetMapping("/contactus")
	public String contactUsPage(Model model) {
	    model.addAttribute("contact", new Contact());
		return "contactus";
	}
    
    @GetMapping("/manage-contacts")
    public String listContacts(@RequestParam(name = "page", defaultValue = "0") int page,
                               @RequestParam(name = "size", defaultValue = "5") int size,
                               Model model) {
        // Retrieve a page of contacts using pagination
        Page<Contact> contactPage = contactRepository.findAll(PageRequest.of(page, size));

        // Add pagination information to the model
        model.addAttribute("pagedContacts", contactPage);

        return "contact/manage-contacts";
    }

    @GetMapping("/add-contact")
    public String showAddForm(Model model) {
        model.addAttribute("contact", new Contact());
        return "contact/add-contact";
    }

    @PostMapping("/add-contact")
    public String addContact(@ModelAttribute("contact") Contact contact,
                             RedirectAttributes redirectAttributes) {
        contactRepository.save(contact);
        sendEmailNotification(contact);
        redirectAttributes.addFlashAttribute("message", "Query added successfully");
        return "redirect:/manage-contacts";
    }

    @GetMapping("/edit-contact/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        Contact contact = contactRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid contact Id:" + id));
        model.addAttribute("contact", contact);
        return "contact/edit-contact";
    }

    @PostMapping("/edit-contact/{id}")
    public String updateContact(@PathVariable("id") Long id, @ModelAttribute("contact") Contact updatedContact,
                                RedirectAttributes redirectAttributes) {
        contactRepository.findById(id)
                .map(contact -> {
                    // Update contact details
                    contact.setName(updatedContact.getName());
                    contact.setEmail(updatedContact.getEmail());
                    contact.setPhone(updatedContact.getPhone());
                    contact.setMessage(updatedContact.getMessage());
                    contact.setStatus(updatedContact.getStatus());
                    return contactRepository.save(contact);
                })
                .orElseThrow(() -> new IllegalArgumentException("Invalid contact Id:" + id));
        redirectAttributes.addFlashAttribute("message", "Query updated successfully");
        return "redirect:/manage-contacts";
    }

    @GetMapping("/delete-contact/{id}")
    public String deleteContact(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        contactRepository.findById(id)
                .ifPresentOrElse(contact -> {
                    // Delete the contact entity from the database
                    contactRepository.delete(contact);
                    redirectAttributes.addFlashAttribute("message", "Query deleted successfully");
                }, () -> {
                    throw new IllegalArgumentException("Invalid contact Id:" + id);
                });
        return "redirect:/manage-contacts";
    }

    private void sendEmailNotification(Contact contact) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("bashajavi@gmail.com"); // Replace with your email
        message.setSubject("Confirmation: Your Information Has Been Successfully Added");
        message.setText("Hello " + contact.getName() + ",\n\nThank you for contacting us! Your information has been successfully added to our system.\n\nHere are the details we received:\n\nName: " + contact.getName() + "\nEmail: " + contact.getEmail() + "\nPhone: " + contact.getPhone() + "\n\nWe'll be in touch soon.\n\nBest regards,\n[Your Organization]");
        emailSender.send(message);
    }
}
