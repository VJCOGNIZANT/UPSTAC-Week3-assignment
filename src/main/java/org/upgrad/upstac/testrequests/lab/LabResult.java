package org.upgrad.upstac.testrequests.lab;

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
public class LabResult {


    @Id
    @GeneratedValue(strategy = IDENTITY)
    Long resultId;

    @OneToOne(fetch = LAZY)
    @JsonIgnore
    @ToString.Exclude
    private TestRequest request;

    private String bloodPressure;
    private String heartBeat;
    private String temperature;
    private String oxygenLevel;
    private String comments;
    private TestStatus result;
    private LocalDate updatedOn;

    @ManyToOne
    private User tester;


}
