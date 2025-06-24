package com.zidio.jobportal.repository;

import com.zidio.jobportal.model.Application;
import com.zidio.jobportal.model.Job;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ApplicationRepository extends JpaRepository<Application, UUID> {
    List<Application> findByStudentEmail(String email);
    List<Application> findByJob(Job job);
}
