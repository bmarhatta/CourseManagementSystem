package com.application.ems.resource;

import com.application.ems.exception.domain.ExceptionHandling;
import com.application.ems.model.Course;
import com.application.ems.model.HttpResponse;
import com.application.ems.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/course")
@RequiredArgsConstructor
//@CrossOrigin
public class CourseResource extends ExceptionHandling {

    private final CourseService courseService;
    public static final String USER_DELETED_SUCCESSFULLY = "User deleted successfully";



    @GetMapping("/all")
    public ResponseEntity<List<Course>> getAllEmployees () {
        List<Course> course = courseService.findAllCourse();
        return new ResponseEntity<>(course, HttpStatus.OK);
    }


    @GetMapping("/find/{courseCode}")
    public ResponseEntity<Course>  getCourseByCourseCode (@PathVariable("courseCode") String courseCode){
        Course course = courseService.findCourseByCourseCode(courseCode);
        return new ResponseEntity<>(course, HttpStatus.OK);
    }


    @PostMapping("/add")
    @PreAuthorize("hasAnyAuthority('user:create')")
    public ResponseEntity<Course> addCourse (@RequestParam("courseCode") String courseCode,
                                             @RequestParam("courseName") String courseName,
                                             @RequestParam("numberOfParticipants") String numberOfParticipants,
                                             @RequestParam("courseDescription") String courseDescription,
                                             @RequestParam("courseType") String courseType,
                                             @RequestParam("courseDuration") String courseDuration,
                                             @RequestParam("courseFees") String courseFees){

        Course addCourse = new Course(courseCode,courseName,subStringJSON(numberOfParticipants),courseDescription,courseType,courseDuration,subStringJSON(courseFees));
        courseService.addCourse(addCourse);
        return new ResponseEntity<>(addCourse, HttpStatus.OK);
    }


    @PutMapping("/update")
    @PreAuthorize("hasAnyAuthority('user:create')")
    public ResponseEntity<Course> updateCourse(@RequestParam("courseCode") String courseCode,
                                               @RequestParam("courseName") String courseName,
                                               @RequestParam("numberOfParticipants") String numberOfParticipants,
                                               @RequestParam("courseDescription") String courseDescription,
                                               @RequestParam("courseType") String courseType,
                                               @RequestParam("courseDuration") String courseDuration,
                                               @RequestParam("courseFees") String courseFees){


        Course updateCourse = new Course(courseCode,courseName,subStringJSON(numberOfParticipants),courseDescription,courseType,courseDuration,subStringJSON(courseFees));
        return new ResponseEntity<>(updateCourse, HttpStatus.OK);
    }


    @DeleteMapping("/delete/{courseCode}")
    @PreAuthorize("hasAnyAuthority('user:delete')")
    public ResponseEntity<?> deleteCourse(@PathVariable("courseCode") String course){
        courseService.deleteCourse(course);
        return response(OK, USER_DELETED_SUCCESSFULLY);
    }

    //we madew this method to return a http responce body for methods that do not return anything (such as delete)
    private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message) {
        return new ResponseEntity<>(new HttpResponse(httpStatus.value(),httpStatus, httpStatus.getReasonPhrase().toUpperCase()
                ,message.toUpperCase()),httpStatus);
    }

    //used to substring the number getting sent from front end
    public int subStringJSON(String string){
        String string1 = string.substring(1);
        String string2 = string1.substring(0, string1.length() - 1);
        int numberOfParticipants_int = Integer.parseInt(string2);
        return numberOfParticipants_int;
    }
}
