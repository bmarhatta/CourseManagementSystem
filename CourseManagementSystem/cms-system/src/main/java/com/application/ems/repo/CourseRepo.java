package com.application.ems.repo;

import com.application.ems.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CourseRepo extends JpaRepository<Course, String> {

    void  deleteCourseBycourseCode(String courseCode);
    Optional<Course> findCourseBycourseCode(String courseCode);



}
