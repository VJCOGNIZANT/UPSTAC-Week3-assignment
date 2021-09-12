package org.upgrad.upstac.testrequests;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.upgrad.upstac.exception.AppException;
import org.upgrad.upstac.testrequests.consultation.Consultation;
import org.upgrad.upstac.testrequests.consultation.ConsultationService;
import org.upgrad.upstac.testrequests.consultation.CreateConsultationRequest;
import org.upgrad.upstac.testrequests.flow.TestRequestFlowService;
import org.upgrad.upstac.testrequests.lab.CreateLabResult;
import org.upgrad.upstac.testrequests.lab.LabResult;
import org.upgrad.upstac.testrequests.lab.LabResultService;
import org.upgrad.upstac.users.User;

import javax.transaction.Transactional;
import javax.validation.Valid;

import static org.upgrad.upstac.testrequests.RequestStatus.*;

@Service
@Slf4j
@Validated
public class TestRequestUpdateService {

    @Autowired
    private TestRequestRepository testRequestRepository;


    @Autowired
    private TestRequestFlowService testRequestFlowService;


    @Autowired
    private LabResultService labResultService;


    @Autowired
    private ConsultationService consultationService;


    @Transactional
    public TestRequest saveTestRequest(@Valid TestRequest result) {
        return testRequestRepository.save(result);
    }


    /**
     * update status and save
     *
     * @param testRequest testRequest
     * @param status status
     * @return {@link TestRequest}
     * @see TestRequest
     */
    TestRequest updateStatusAndSave(TestRequest testRequest, RequestStatus status) {
        testRequest.setStatus(status);
        return saveTestRequest(testRequest);
    }


    /**
     * assign for lab test
     *
     * @param id id
     * @param tester tester
     * @return {@link TestRequest}
     * @see TestRequest
     */
    public TestRequest assignForLabTest(Long id, User tester) {
        TestRequest testRequest = testRequestRepository.findByRequestIdAndStatus(id, INITIATED).orElseThrow(() -> new AppException("Invalid ID"));
        LabResult labResult = labResultService.assignForLabTest(testRequest, tester);
        testRequestFlowService.log(testRequest, INITIATED, LAB_TEST_IN_PROGRESS, tester);
        testRequest.setLabResult(labResult);
        return updateStatusAndSave(testRequest, LAB_TEST_IN_PROGRESS);
    }

    /**
     * update lab test
     *
     * @param id id
     * @param createLabResult createLabResult
     * @param tester tester
     * @return {@link TestRequest}
     * @see TestRequest
     */
    public TestRequest updateLabTest(Long id, @Valid CreateLabResult createLabResult, User tester) {
        TestRequest testRequest = testRequestRepository.findByRequestIdAndStatus(id, LAB_TEST_IN_PROGRESS).orElseThrow(() -> new AppException("Invalid ID or State"));

        labResultService.updateLabTest(testRequest, createLabResult);
        testRequestFlowService.log(testRequest, LAB_TEST_IN_PROGRESS, LAB_TEST_COMPLETED, tester);
        return updateStatusAndSave(testRequest, LAB_TEST_COMPLETED);
    }

    /**
     * assign for consultation
     *
     * @param id id
     * @param doctor doctor
     * @return {@link TestRequest}
     * @see TestRequest
     */
    public TestRequest assignForConsultation(Long id, User doctor) {
        TestRequest testRequest = testRequestRepository.findByRequestIdAndStatus(id, LAB_TEST_COMPLETED).orElseThrow(() -> new AppException("Invalid ID or State"));
        Consultation consultation = consultationService.assignForConsultation(testRequest, doctor);
        testRequestFlowService.log(testRequest, LAB_TEST_COMPLETED, DIAGNOSIS_IN_PROCESS, doctor);
        testRequest.setConsultation(consultation);
        return updateStatusAndSave(testRequest, DIAGNOSIS_IN_PROCESS);
    }


    /**
     * update consultation
     *
     * @param id id
     * @param createConsultationRequest createConsultationRequest
     * @param doctor doctor
     * @return {@link TestRequest}
     * @see TestRequest
     */
    public TestRequest updateConsultation(Long id, @Valid CreateConsultationRequest createConsultationRequest, User doctor) {

        TestRequest testRequest = testRequestRepository.findByRequestIdAndStatus(id, DIAGNOSIS_IN_PROCESS).orElseThrow(() -> new AppException("Invalid ID or State"));
        consultationService.updateConsultation(testRequest, createConsultationRequest);
        testRequestFlowService.log(testRequest, DIAGNOSIS_IN_PROCESS, COMPLETED, doctor);
        return updateStatusAndSave(testRequest, COMPLETED);
    }


}
