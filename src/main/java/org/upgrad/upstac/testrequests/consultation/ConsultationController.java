package org.upgrad.upstac.testrequests.consultation;


import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.upgrad.upstac.config.security.UserLoggedInService;
import org.upgrad.upstac.exception.AppException;
import org.upgrad.upstac.testrequests.TestRequest;
import org.upgrad.upstac.testrequests.TestRequestQueryService;
import org.upgrad.upstac.testrequests.TestRequestUpdateService;
import org.upgrad.upstac.testrequests.flow.TestRequestFlowService;
import org.upgrad.upstac.users.User;

import javax.validation.ConstraintViolationException;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;
import static org.upgrad.upstac.exception.UpgradResponseStatusException.asBadRequest;
import static org.upgrad.upstac.exception.UpgradResponseStatusException.asConstraintViolation;
import static org.upgrad.upstac.testrequests.RequestStatus.LAB_TEST_COMPLETED;


@RestController
@RequestMapping("/api/consultations")
public class ConsultationController {

    private static final Logger log = getLogger(ConsultationController.class);
    @Autowired
    TestRequestFlowService testRequestFlowService;
    @Autowired
    private TestRequestUpdateService testRequestUpdateService;
    @Autowired
    private TestRequestQueryService testRequestQueryService;
    @Autowired
    private UserLoggedInService userLoggedInService;


    /**
     * get for consultations
     *
     * @return {@link List}
     * @see List
     * @see TestRequest
     */
    @GetMapping("/in-queue")
    @PreAuthorize("hasAnyRole('DOCTOR')")
    public List<TestRequest> getForConsultations() {
        return testRequestQueryService.findBy(LAB_TEST_COMPLETED);
    }

    /**
     * get for doctor
     *
     * @return {@link List}
     * @see List
     * @see TestRequest
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('DOCTOR')")
    public List<TestRequest> getForDoctor() {
        final User doctor = userLoggedInService.getLoggedInUser();
        return testRequestQueryService.findByDoctor(doctor);
    }


    /**
     * assign for consultation
     *
     * @param id id
     * @return {@link TestRequest}
     * @see TestRequest
     */
    @PreAuthorize("hasAnyRole('DOCTOR')")
    @PutMapping("/assign/{id}")
    public TestRequest assignForConsultation(@PathVariable Long id) {
        final User doctor = userLoggedInService.getLoggedInUser();
        try {
            return testRequestUpdateService.assignForConsultation(id, doctor);
        } catch (AppException e) {
            throw asBadRequest(e.getMessage());
        }
    }


    /**
     * update consultation
     *
     * @param id id
     * @param testResult testResult
     * @return {@link TestRequest}
     * @see TestRequest
     */
    @PreAuthorize("hasAnyRole('DOCTOR')")
    @PutMapping("/update/{id}")
    public TestRequest updateConsultation(@PathVariable Long id, @RequestBody CreateConsultationRequest testResult) {
        final User doctor = userLoggedInService.getLoggedInUser();
        try {
            return testRequestUpdateService.updateConsultation(id, testResult, doctor);
        } catch (ConstraintViolationException e) {
            throw asConstraintViolation(e);
        } catch (AppException e) {
            throw asBadRequest(e.getMessage());
        }
    }

}
