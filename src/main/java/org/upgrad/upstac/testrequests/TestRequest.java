package org.upgrad.upstac.testrequests;

import lombok.Data;
import org.upgrad.upstac.testrequests.consultation.Consultation;
import org.upgrad.upstac.testrequests.lab.LabResult;
import org.upgrad.upstac.users.User;
import org.upgrad.upstac.users.models.Gender;

import javax.persistence.*;
import java.time.LocalDate;

import static javax.persistence.GenerationType.IDENTITY;
import static org.upgrad.upstac.testrequests.RequestStatus.INITIATED;

@Data
@Entity
public class TestRequest {


    @Id
    @GeneratedValue(strategy = IDENTITY)
    Long requestId;
    @OneToOne(mappedBy = "request")
    Consultation consultation;
    @OneToOne(mappedBy = "request")
    LabResult labResult;
    @ManyToOne
    private User createdBy;
    private LocalDate created = LocalDate.now();
    private RequestStatus status = INITIATED;
    private String name;
    private Gender gender;
    private String address;
    private Integer age;
    private String email;
    private String phoneNumber;
    private Integer pinCode;

}
