package com.zidio.jobportal.controller;

import com.zidio.jobportal.model.Application;
import com.zidio.jobportal.model.Job;
import com.zidio.jobportal.repository.ApplicationRepository;
import com.zidio.jobportal.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private JobRepository jobRepository;

    // ✅ 1. Student applies to a job
    @PostMapping("/apply/{jobId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> applyToJob(@PathVariable UUID jobId, Principal principal) {
        Optional<Job> optionalJob = jobRepository.findById(jobId);
        if (!optionalJob.isPresent()) {
            return ResponseEntity.badRequest().body("Job not found.");
        }

        String studentEmail = principal.getName();

        // Check if student already applied to this job
        List<Application> existing = applicationRepository.findByStudentEmail(studentEmail);
        for (Application app : existing) {
            if (app.getJob().getId().equals(jobId)) {
                return ResponseEntity.badRequest().body("You have already applied to this job.");
            }
        }

        Application application = new Application(
                studentEmail,
                optionalJob.get(),
                LocalDateTime.now()
        );
        applicationRepository.save(application);

        return ResponseEntity.ok("Application submitted successfully.");
    }

    // ✅ 2. Student views all jobs they applied to
    @GetMapping("/my")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> getMyApplications(Principal principal) {
        String studentEmail = principal.getName();
        List<Application> myApps = applicationRepository.findByStudentEmail(studentEmail);
        return ResponseEntity.ok(myApps);
    }

    // ✅ 3. HR views applicants for a job they posted
    @GetMapping("/job/{jobId}")
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<?> getApplicantsForJob(@PathVariable UUID jobId, Principal principal) {
        Optional<Job> optionalJob = jobRepository.findById(jobId);
        if (optionalJob.isEmpty()) {
            return ResponseEntity.badRequest().body("Job not found");
        }

        Job job = optionalJob.get();
        String hrEmail = principal.getName();

        if (!job.getPostedBy().equals(hrEmail)) {
            return ResponseEntity.status(403).body("You are not authorized to view applicants for this job.");
        }

        List<Application> applications = applicationRepository.findByJob(job);
        return ResponseEntity.ok(applications);
    }
    @DeleteMapping("/withdraw/{applicationId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> withdrawApplication(@PathVariable UUID applicationId, Principal principal) {
        Optional<Application> optionalApp = applicationRepository.findById(applicationId);

        if (optionalApp.isEmpty()) {
            return ResponseEntity.status(404).body("Application not found.");
        }

        Application application = optionalApp.get();

        // Make sure this application belongs to the current student
        if (!application.getStudentEmail().equals(principal.getName())) {
            return ResponseEntity.status(403).body("You are not authorized to delete this application.");
        }

        applicationRepository.delete(application);
        return ResponseEntity.ok("Application withdrawn successfully.");
    }

}
