package com.application.ems.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data @NoArgsConstructor @Entity
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false, updatable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) //we can only set this values. we can't read it
    private Long id;
    private String userId;
    private String name;
    private String email;
    private String username;
    //we put this @JsonProperty annotation to thing we do not want to show to our user
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) //we can only set this values. we can't read it
    private String password;
    private String profileImageUrl;
    private Date lastLoginDate;
    private Date lastLoginDateDisplay;
    private Date joinDate;

    private String roles;
    private String[] authorities;
    private boolean active;
    private boolean isNotLocked;
}

//https://fasterxml.github.io/jackson-annotations/javadoc/2.11/com/fasterxml/jackson/annotation/JsonProperty.Access.html
//Various options for JsonProperty.access() property, specifying how property may be accessed during serialization
// ("read") and deserialization ("write") (note that the direction of read and write is from
// perspective of the property, not from external data format: this may be confusing in some contexts).