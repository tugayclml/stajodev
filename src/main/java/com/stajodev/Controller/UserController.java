package com.stajodev.Controller;

import com.stajodev.CustomException.ResourceNotFoundException;
import com.stajodev.Models.User;
import com.stajodev.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.mail.*;
import javax.mail.internet.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping(path = "/api")
public class UserController {

    @Autowired
    UserRepository userRepository;

    @GetMapping("/users")
    public List<User> getAllUser(){
        return userRepository.findAll();
    }

    @GetMapping("/users/{id}")
    public Optional<User> getUser(@PathVariable long id){
        return userRepository.findById(id);
    }

    @PostMapping(value = "/users/register", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> saveUser(@RequestBody User user) throws Exception{
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        user.setDate(formatter.format(date));
        Map<String, String> response = new HashMap<>();
        if (user.getName() == "" || user.getSurname() == ""){
            response.put("error", "Ad ve Soyad boş bırakılamaz!");
            return response;
        }

        if (user.getEmail() == ""){
            response.put("error", "Email alanı boş bırakılamaz");
        }
        userRepository.save(user);

        response.put("success", "İşleminiz başarıyla gerçekleşmiştir.");
        return response;
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<User> updateUser(@PathVariable long id, @RequestBody User user) throws ResourceNotFoundException{
        User updatedUser = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found on :: " + id));
        updatedUser.setName(user.getName());
        updatedUser.setSurname(user.getSurname());
        updatedUser.setDepartment(user.getDepartment());
        updatedUser.setEmail(user.getEmail());
        userRepository.save(updatedUser);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/users/{id}")
    public Map<String, Boolean> deleteUser(@PathVariable long id) throws ResourceNotFoundException{
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found on :: " + id));
        userRepository.deleteById(id);
        Map<String, Boolean> response = new HashMap<>();
        response.put("deleted", Boolean.TRUE);
        return response;
    }

}
