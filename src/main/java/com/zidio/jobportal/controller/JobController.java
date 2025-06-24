package com.zidio.jobportal.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.zidio.jobportal.model.Application;
import com.zidio.jobportal.model.Job;
import com.zidio.jobportal.repository.ApplicationRepository;
import com.zidio.jobportal.repository.JobRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    // ✅ HR / RECRUITER - Create Job
    @PostMapping("/create")
    @PreAuthorize("hasRole('HR') or hasRole('RECRUITER')")
    public ResponseEntity<?> createJob(@RequestBody Job job, Principal principal) {
        System.out.println("🛠️ Inside createJob: " + principal.getName());

        String email = principal.getName();
        job.setPostedBy(email);
        jobRepository.save(job);

        return ResponseEntity.ok("Job posted successfully!");
    }

    // ✅ HR / RECRUITER - View My Jobs
    @GetMapping("/my")
    @PreAuthorize("hasRole('HR') or hasRole('RECRUITER')")
    public ResponseEntity<List<Job>> getMyJobs(Principal principal) {
        String email = principal.getName();
        List<Job> myJobs = jobRepository.findByPostedBy(email);
        return ResponseEntity.ok(myJobs);
    }

    // ✅ HR / RECRUITER - Delete Own Job
    @DeleteMapping("/delete/{jobId}")
    @PreAuthorize("hasRole('HR') or hasRole('RECRUITER')")
    public ResponseEntity<?> deleteOwnJob(@PathVariable UUID jobId, Principal principal) {
        String email = principal.getName();

        Optional<Job> jobOpt = jobRepository.findById(jobId);
        if (jobOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Job not found.");
        }

        Job job = jobOpt.get();

        if (!job.getPostedBy().equals(email)) {
            return ResponseEntity.status(403).body("You are not authorized to delete this job.");
        }

        jobRepository.delete(job);
        return ResponseEntity.ok("Job deleted successfully.");
    }

    // ✅ HR / RECRUITER - Update Own Job
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('HR') or hasRole('RECRUITER')")
    public ResponseEntity<?> updateJob(
            @PathVariable UUID id,
            @RequestBody Job updatedJob,
            Principal principal) {

        String email = principal.getName();

        Optional<Job> jobOpt = jobRepository.findById(id);
        if (jobOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Job not found.");
        }

        Job job = jobOpt.get();

        if (!job.getPostedBy().equals(email)) {
            return ResponseEntity.status(403).body("You are not authorized to update this job.");
        }

        job.setTitle(updatedJob.getTitle());
        job.setDescription(updatedJob.getDescription());
        job.setLocation(updatedJob.getLocation());
        job.setCompanyName(updatedJob.getCompanyName());
        job.setSalary(updatedJob.getSalary());

        jobRepository.save(job);

        return ResponseEntity.ok("Job updated successfully!");
    }

    // ✅ HR / RECRUITER - View Applications to Their Jobs
    @GetMapping("/applications")
    @PreAuthorize("hasRole('HR') or hasRole('RECRUITER')")
    public ResponseEntity<?> getApplicationsForMyJobs(Principal principal) {
        String email = principal.getName();

        List<Job> myJobs = jobRepository.findByPostedBy(email);
        if (myJobs.isEmpty()) {
            return ResponseEntity.ok("You haven't posted any jobs yet.");
        }

        List<Application> allApplications = new ArrayList<>();
        for (Job job : myJobs) {
            List<Application> applications = applicationRepository.findByJob(job);
            allApplications.addAll(applications);
        }

        return ResponseEntity.ok(allApplications);
    }

    // ✅ Public - Get All Jobs
    @GetMapping("/all")
    public ResponseEntity<List<Job>> getAllJobs() {
        return ResponseEntity.ok(jobRepository.findAll());
    }

    // ✅ Public - Get Paginated Jobs
    @GetMapping("/paginated")
    public ResponseEntity<?> getPaginatedJobs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Job> pagedJobs = jobRepository.findAll(pageable);
        return ResponseEntity.ok(pagedJobs);
    }

    // ✅ Public - Search & Filter Jobs
    @GetMapping("/search")
    public ResponseEntity<?> searchJobs(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String company) {

        List<Job> results = jobRepository.searchJobs(title, location, company);
        return ResponseEntity.ok(results);
    }
}
