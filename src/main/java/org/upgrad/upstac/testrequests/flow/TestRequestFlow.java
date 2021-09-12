package org.upgrad.upstac.testrequests.flow;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.upgrad.upstac.testrequests.RequestStatus;
import org.upgrad.upstac.testrequests.TestRequest;
import org.upgrad.upstac.users.User;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.time.LocalDate;

import static java.time.LocalDate.now;
import static javax.persistence.GenerationType.IDENTITY;

@Data
@Entity
public class TestRequestFlow {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    Long id;

    @ManyToOne
    @JsonIgnore
    private TestRequest request;

    private RequestStatus fromStatus;
    private RequestStatus toStatus;

    @ManyToOne
    private User changedBy;

    private LocalDate happenedOn = now();

}