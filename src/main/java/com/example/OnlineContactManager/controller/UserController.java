package com.example.OnlineContactManager.controller;


import com.example.OnlineContactManager.dao.ContactRepository;
import com.example.OnlineContactManager.dao.UserRepository;
import com.example.OnlineContactManager.models.Contact;
import com.example.OnlineContactManager.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.security.Principal;

@Controller
@RequestMapping("/user")
public class UserController {


    @Autowired
    private UserRepository userRepository;


    @ModelAttribute
    public void addCommmonData(Model model, Principal principal) {
        String userName = principal.getName();
        System.out.println("Username :" + userName);
        User user = this.userRepository.getUserByUsername(userName);
        System.out.println("User :" + user);
        model.addAttribute("user", user);
    }


    @RequestMapping("/index")
    public String dashboard(Model model, Principal principal) {

        model.addAttribute("title", "User Dashboard");
        return "normal/user_dashboard";
    }

    @RequestMapping("/add-contact")
    public String openAddContactForm(Model model) {

        model.addAttribute("title", "Add Contact");
        model.addAttribute("contact", new Contact());
        return "normal/add_contact_form";
    }

    @PostMapping("/process-contact")
    public String processContact(@ModelAttribute Contact contact, Principal principal, BindingResult result, Model model) {
        String name = principal.getName();
        User user = this.userRepository.getUserByUsername(name);
        contact.setImage("contact.png");

        user.getContacts().add(contact);
        contact.setUser(user);
        this.userRepository.save(user);

        System.out.println("Data :" + contact);
        model.addAttribute("message", "Contact added successfully!");

        return "normal/add_contact_form";
    }
    @Autowired
    private ContactRepository contactRepository;


    @RequestMapping("/show-contact")
    public String showContacts( Model model, Principal principal) {
        model.addAttribute("title", "Show User Contacts");
        String userName = principal.getName();
        User user = this.userRepository.getUserByUsername(userName);
        List<Contact> contacts = contactRepository.findContactsByUserUid(user.getId());


        if (user.getId()==contacts.get(0).getUser().getId()) {
            model.addAttribute("contacts", contacts);
        } else {
            model.addAttribute("message", "Contact not found or you do not have permission to view this contact.");
        }


       // model.addAttribute("contacts", user.getContacts());
        System.out.println("Contacts :" + user.getContacts());
        return "normal/show-contact";
    }


    @RequestMapping("contact/delete/{cid}")
    public String deleteContact(@PathVariable("cid") Integer cid, Model model, Principal principal) {
        System.out.println("CID :" + cid);
        User user = this.userRepository.getUserByUsername(principal.getName());
        Contact contact = this.contactRepository.getContactByCid(cid);

        if (contact != null && contact.getUser().getId() == user.getId()) {
            user.getContacts().remove(contact);
            this.contactRepository.delete(contact);
            this.userRepository.save(user);
        } else {
            model.addAttribute("message", "Contact not found or you do not have permission to delete this contact.");
        }

        return "redirect:/user/show-contact";
    }

    @PostMapping("/update-contact/{cid}")
    public String updateForm(@PathVariable("cid") Integer cid, Model model) {
        model.addAttribute("title", "Update Contact");
        Contact contact = this.contactRepository.getContactByCid(cid);
        model.addAttribute("contact", contact);
        return "normal/update_contact_form";
    }

    @PostMapping("/process-update")
    public String updateHandler(@ModelAttribute Contact contact, Model model, Principal principal) {
        User user = this.userRepository.getUserByUsername(principal.getName());
        contact.setUser(user);
        this.contactRepository.save(contact);
        model.addAttribute("message", "Contact updated successfully!");
        return "redirect:/user/show-contact";
    }

    @RequestMapping("/profile")
    public String userProfile(Model model, Principal principal) {

        model.addAttribute("title", "User Profile");
        return "normal/show-profile";
    }

}
