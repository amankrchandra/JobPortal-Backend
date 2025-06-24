package com.zidio.jobportal.repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.zidio.jobportal.model.Job;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JobRepository extends JpaRepository<Job, UUID> {
    List<Job> findByPostedBy(String email);

    // ✅ Added method to support pagination
    Page<Job> findAll(Pageable pageable);

    // 🆕 Flexible search by title, location, or company name
    @Query("SELECT j FROM Job j WHERE " +
            "(:title IS NULL OR LOWER(j.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
            "(:location IS NULL OR LOWER(j.location) LIKE LOWER(CONCAT('%', :location, '%'))) AND " +
            "(:company IS NULL OR LOWER(j.companyName) LIKE LOWER(CONCAT('%', :company, '%')))")
    List<Job> searchJobs(
            @Param("title") String title,
            @Param("location") String location,
            @Param("company") String company
    );
}
