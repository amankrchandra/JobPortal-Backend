package com.zidio.jobportal.controller;

import com.zidio.jobportal.model.User;
import com.zidio.jobportal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("/api/student")
@PreAuthorize("hasRole('STUDENT')")
public class StudentController {

    @Autowired
    private UserRepository userRepository;

    private final String uploadDir = System.getProperty("user.dir") + "/uploads/resumes/";

    @PostMapping("/upload-resume")
    public ResponseEntity<String> uploadResume(@RequestParam("file") MultipartFile file, Principal principal) throws IOException {
        if (file.isEmpty() || !file.getContentType().equals("application/pdf")) {
            return ResponseEntity.badRequest().body("Only PDF files allowed");
        }

        String email = principal.getName(); // gets logged-in user's email
        userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        File folder = new File(uploadDir);
        if (!folder.exists()) folder.mkdirs(); // create folder if not exist

        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        File savedFile = new File(uploadDir + filename);
        file.transferTo(savedFile); // save file

        return ResponseEntity.ok("Resume uploaded successfully!");
    }

    @GetMapping("/resume/{filename}")
    public ResponseEntity<?> downloadResume(@PathVariable String filename) throws IOException {
        File file = new File(uploadDir + filename);
        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }

        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=" + filename);

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(file.length())
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }

    @PutMapping("/resume/{oldFilename}")
    public ResponseEntity<String> updateResume(@PathVariable String oldFilename, @RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty() || !file.getContentType().equals("application/pdf")) {
            return ResponseEntity.badRequest().body("Only PDF files allowed");
        }

        File oldFile = new File(uploadDir + oldFilename);
        if (oldFile.exists()) oldFile.delete();

        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        File newFile = new File(uploadDir + filename);
        file.transferTo(newFile);

        return ResponseEntity.ok("Resume updated successfully!");
    }

    @DeleteMapping("/resume/{filename}")
    public ResponseEntity<String> deleteResume(@PathVariable String filename) {
        File file = new File(uploadDir + filename);
        if (!file.exists()) {
            return ResponseEntity.badRequest().body("No resume found to delete.");
        }

        file.delete();
        return ResponseEntity.ok("Resume deleted successfully!");
    }
}
