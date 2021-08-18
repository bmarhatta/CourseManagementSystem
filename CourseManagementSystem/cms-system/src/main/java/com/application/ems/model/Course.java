package com.application.ems.model;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Course implements Serializable {
    @Id
    @Column(name = "COURSE_CODE")//,nullable = false, updatable = false)
    private String courseCode;

    @Column(name = "COURSE_NAME")
    private String courseName;

    @Column(name = "NUMBER_OF_PARTICIPANT")
    private int numberOfParticipants;

    @Column(name = "COURSE_DESCRIPTION")
    private String courseDescription;

    @Column(name = "COURSE_TYPE")
    private String courseType;

    @Column(name = "COURSE_DURATION")
    private String courseDuration;

    @Column(name = "COURSE_FEES")
    private int courseFees;
}
