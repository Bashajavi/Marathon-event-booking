package com.controller;

import com.entity.MailForm;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class MailParticipantController {

    @Autowired
    private JavaMailSender javaMailSender;

    @GetMapping("/mail-participant")
    public String showMailParticipantPage(Model model) {
        // Here, you can initialize a new MailForm object and pass it to the model
        MailForm mailForm = new MailForm();
        model.addAttribute("mailForm", mailForm);
        return "mail/mail-participant";
    }

    @PostMapping("/mail-participant")
    public String sendMailToParticipant(@ModelAttribute("mailForm") MailForm mailForm,
                                        @RequestParam(value = "fileAttachment", required = false) MultipartFile file,
                                        RedirectAttributes redirectAttributes) {
        try {
            // Construct and send the email using the provided data in the mailForm
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(mailForm.getRecipientEmail());
            helper.setSubject(mailForm.getSubject());
            helper.setText(mailForm.getContent());

            // Attach file if provided
            if (file != null && !file.isEmpty()) {
                helper.addAttachment(file.getOriginalFilename(), file);
            }

            // Send the email
            javaMailSender.send(message);

            // Add a flash attribute for the session message
            redirectAttributes.addFlashAttribute("message", "Email sent successfully to participant.");
        } catch (MessagingException | MailException e) {
            // Handle exceptions if any
            redirectAttributes.addFlashAttribute("error", "Failed to send email. Please try again later.");
        }

        // Redirect to some appropriate page after sending the mail
        return "redirect:/manage-participants"; // Assuming this redirects back to the participant management page
    }
}