package com.stajodev.Controller;

import com.stajodev.CustomException.ResourceNotFoundException;
import com.stajodev.Models.Announcement;
import com.stajodev.Models.User;
import com.stajodev.Repository.AnnouncementPagingAndSortingRepository;
import com.stajodev.Repository.AnnouncementRepository;
import com.stajodev.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.mail.*;
import javax.mail.internet.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping(path = "/api")
public class AnnouncementController {

    @Autowired
    AnnouncementRepository announcementRepository;

    @Autowired
    AnnouncementPagingAndSortingRepository announcementPagingAndSortingRepository;

    @Autowired
    UserRepository userRepository;

    @GetMapping("/announcements")
    public List<Announcement> getAllAnnouncement(){
        return announcementRepository.findAll();
    }

    @GetMapping("/department/announcements/{department}")
    public List<Announcement> getAnnouncementByDepartment(@PathVariable String department){
        if (department.equals("all")){
            return announcementRepository.findAll();
        }else{
            return announcementRepository.findAllByDepartment(department);
        }
    }

    @GetMapping("/announcements/{id}")
    public Optional<Announcement> getAnnouncement(@PathVariable long id){
        return announcementRepository.findById(id);
    }

    @GetMapping("/announcements/pagination/{pageNumber}")
    public Page<Announcement> getAllAnnouncementPagination(@PathVariable int pageNumber){
        Pageable pageable = PageRequest.of(pageNumber, 5);
        Page<Announcement> page = announcementPagingAndSortingRepository.findAll(pageable);
        return page;
    }

    @GetMapping("/announcements/pagination/{department}/{pageNumber}")
    public Page<Announcement> getAnnouncementPagination(@PathVariable int pageNumber,@PathVariable String department){
        Pageable pageable = PageRequest.of(pageNumber, 5);
        Page<Announcement> page;
        System.out.println(department);
        if (department.equals("all")){
            page = announcementPagingAndSortingRepository.findAll(pageable);
        }else{
            page = announcementPagingAndSortingRepository.findAllByDepartment(department, pageable);
        }
        return page;
    }

    @PostMapping(value = "/announcements/register", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> saveAnnouncements(@RequestBody Announcement announcement) throws IOException, MessagingException {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        announcement.setDate(formatter.format(date));
        Map<String, String> response = new HashMap<>();
        if (announcement.getTitle() == "" || announcement.getContent() == ""){
            response.put("error", "Başlık ve içerik boş bırakılamaz!");
            return response;
        }

        announcementRepository.save(announcement);

        List<User> users;
        if (announcement.getDepartment().equals("all")){
            users = userRepository.findAll();
        }else{
            users = userRepository.findAllByDepartment(announcement.getDepartment());
        }
        users.forEach(user -> {
            try {
                System.out.println(user.getEmail());
                sendEmail(user.getEmail(), announcement.getTitle(), announcement.getContent());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        response.put("success", "İşleminiz başarıyla gerçekleşmiştir.");
        return response;
    }

    public boolean sendEmail(String email, String title, String content) throws AddressException, MessagingException, IOException {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("stajodev897@gmail.com", "stajodev123456");
            }
        });
        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(email));

        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
        msg.setSubject(title);
        msg.setContent(content, "text/html");
        msg.setSentDate(new Date());
        
        Transport.send(msg);
        return true;
    }

    @PutMapping("/announcements/{id}")
    public Map<String, String> updateAnnouncements(@RequestBody Announcement announcement, @PathVariable long id) throws Exception {
        Announcement updateAnnouncement = announcementRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Announcement not found on :: " + id));
        Map<String, String> response = new HashMap<>();
        if (announcement.getTitle() == "" || announcement.getContent() == ""){
            response.put("error", "Başlık ve içerik boş bırakılamaz!");
            return response;
        }
        updateAnnouncement.setTitle(announcement.getTitle());
        updateAnnouncement.setContent(announcement.getContent());
        updateAnnouncement.setDepartment(announcement.getDepartment());
        announcementRepository.save(updateAnnouncement);

        List<User> users;
        if (announcement.getDepartment().equals("all")){
            users = userRepository.findAll();
        }else{
            users = userRepository.findAllByDepartment(announcement.getDepartment());
        }
        users.forEach(user -> {
            try {
                System.out.println(user.getEmail());
                sendEmail(user.getEmail(), announcement.getTitle(), announcement.getContent());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        response.put("success", "İşleminiz başarıyla gerçekleşmiştir.");
        return response;
    }

    @DeleteMapping("/announcements/{id}")
    public Map<String, Boolean> deleteAnnouncement(@PathVariable long id) throws Exception{
        Announcement announcement = announcementRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Announcement not found on :: " + id));
        announcementRepository.deleteById(id);
        Map<String, Boolean> response = new HashMap<>();
        response.put("deleted", Boolean.TRUE);
        return response;
    }

}
