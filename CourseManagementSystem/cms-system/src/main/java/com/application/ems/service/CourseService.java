package com.application.ems.service;

import com.application.ems.exception.CMSException;
import com.application.ems.model.Course;
import com.application.ems.repo.CourseRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
@AllArgsConstructor
public class CourseService {

    private final CourseRepo courseRepo;

   public Course addCourse(Course course){
       return courseRepo.save(course);
   }

    public List<Course> findAllCourse() {
        return courseRepo.findAll();
    }

    public Course updateCourse (Course course){
        return courseRepo.save(course);
    }

    public Course findCourseByCourseCode(String courseCode){
        return courseRepo.findCourseBycourseCode(courseCode)
                .orElseThrow(() -> new CMSException("curse by course code" + courseCode + " was not found"));
    }

    public void deleteCourse (String courseCode){
        courseRepo.deleteCourseBycourseCode(courseCode);
    }

}
