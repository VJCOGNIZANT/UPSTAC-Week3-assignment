package org.upgrad.upstac.testrequests.consultation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;
import org.upgrad.upstac.testrequests.TestRequest;
import org.upgrad.upstac.users.User;

import javax.persistence.*;
import java.time.LocalDate;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Data
@Entity
public class Consultation {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    Long id;
    @ManyToOne
    User doctor;
    @OneToOne(fetch = LAZY)
    @JsonIgnore
    @ToString.Exclude
    private TestRequest request;
    private DoctorSuggestion suggestion;
    private String comments;
    private LocalDate updatedOn;

}
