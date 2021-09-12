package org.upgrad.upstac.testrequests;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.web.server.ResponseStatusException;
import org.upgrad.upstac.config.security.UserLoggedInService;
import org.upgrad.upstac.exception.AppException;
import org.upgrad.upstac.testrequests.consultation.Consultation;
import org.upgrad.upstac.testrequests.consultation.ConsultationController;
import org.upgrad.upstac.testrequests.consultation.CreateConsultationRequest;
import org.upgrad.upstac.testrequests.consultation.DoctorSuggestion;
import org.upgrad.upstac.testrequests.lab.LabResult;
import org.upgrad.upstac.users.User;
import org.upgrad.upstac.users.models.Gender;

import java.util.ArrayList;
import java.util.List;

import static java.time.LocalDate.now;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.upgrad.upstac.testrequests.RequestStatus.*;
import static org.upgrad.upstac.testrequests.lab.TestStatus.POSITIVE;


@SpringBootTest
@Slf4j
class ConsultationControllerTest {


    @InjectMocks
    ConsultationController consultationController;


    @Mock
    TestRequestQueryService testRequestQueryService;

    @Mock
    UserLoggedInService userLoggedInService;
    @Mock
    TestRequestUpdateService testRequestUpdateService;
    @Mock
    TestRequestRepository testRequestRepository;

    @Test
    @WithUserDetails(value = "doctor")
    public void calling_assignForConsultation_with_valid_test_request_id_should_update_the_request_status() {
        //Arrange
        Long mockId = 1l;
        TestRequest testRequest = getTestRequestByStatus(LAB_TEST_COMPLETED);
        TestRequest mockTestResponse = getMockedTestRequest(DIAGNOSIS_IN_PROCESS);
        when(userLoggedInService.getLoggedInUser()).thenReturn(createUser());
        when(testRequestUpdateService.assignForConsultation(anyLong(), any())).thenReturn(mockTestResponse);

        //Act
        TestRequest result = consultationController.assignForConsultation(mockId);

        //Assert
        assertNotNull(result);
        assertThat(result.getRequestId(), equalTo(mockTestResponse.getRequestId()));
        assertThat(result.getStatus(), equalTo(DIAGNOSIS_IN_PROCESS));
        assertNotNull(result.getConsultation());
    }


    @Test
    @WithUserDetails(value = "doctor")
    public void calling_assignForConsultation_with_valid_test_request_id_should_throw_exception() {
        //Arrange
        Long InvalidRequestId = -34L;

        when(userLoggedInService.getLoggedInUser()).thenReturn(createUser());
        when(testRequestUpdateService.assignForConsultation(anyLong(), any())).thenThrow(new AppException("Invalid ID"));

        //Act
        ResponseStatusException result = assertThrows(ResponseStatusException.class, () -> {
            consultationController.assignForConsultation(InvalidRequestId);
        });

        //Assert
        assertNotNull(result);
        assertThat(result.getMessage(), containsString("Invalid ID"));

    }


    @Test
    @WithUserDetails(value = "doctor")
    public void calling_updateConsultation_with_valid_test_request_id_should_update_the_request_status_and_update_consultation_details() {
        //Arrange
        TestRequest testRequest = getTestRequestByStatus(DIAGNOSIS_IN_PROCESS);

        CreateConsultationRequest createConsultationRequest = getCreateConsultationRequest(testRequest);

        TestRequest mockTestResponse = getMockedTestRequest(COMPLETED);
        when(userLoggedInService.getLoggedInUser()).thenReturn(createUser());
        when(testRequestUpdateService.updateConsultation(anyLong(), any(), any())).thenReturn(mockTestResponse);
        Long mockId = 3l;

        //Act
        TestRequest result = consultationController.updateConsultation(mockId, createConsultationRequest);

        //Assert
        assertNotNull(result);
        assertThat(result.getRequestId(), equalTo(mockTestResponse.getRequestId()));
        assertThat(result.getStatus(), equalTo(COMPLETED));
        assertThat(result.getConsultation().getSuggestion(), equalTo(mockTestResponse.getConsultation().getSuggestion()));


    }


    @Test
    @WithUserDetails(value = "doctor")
    public void calling_updateConsultation_with_invalid_test_request_id_should_throw_exception() {
        //Arrange
        TestRequest testRequest = getTestRequestByStatus(DIAGNOSIS_IN_PROCESS);
        CreateConsultationRequest createConsultationRequest = getCreateConsultationRequest(testRequest);

        when(userLoggedInService.getLoggedInUser()).thenReturn(createUser());
        when(testRequestUpdateService.updateConsultation(anyLong(), any(), any())).thenThrow(new AppException("Invalid ID"));

        //Act
        ResponseStatusException result = assertThrows(ResponseStatusException.class, () -> {
            Long InvalidRequestId = -34l;
            consultationController.updateConsultation(InvalidRequestId, createConsultationRequest);
        });

        //Assert
        assertNotNull(result);
        assertThat(result.getMessage(), containsString("Invalid ID"));
    }

    @Test
    @WithUserDetails(value = "doctor")
    public void calling_updateConsultation_with_invalid_empty_status_should_throw_exception() {
        //Arrange
        TestRequest testRequest = getTestRequestByStatus(DIAGNOSIS_IN_PROCESS);

        User user = createUser();
        CreateConsultationRequest createConsultationRequest = getCreateConsultationRequest(testRequest);
        createConsultationRequest.setSuggestion(null);

        Long InvalidRequestId = -34l;
        Mockito.when(userLoggedInService.getLoggedInUser()).thenReturn(user);
        when(testRequestUpdateService.updateConsultation(InvalidRequestId, createConsultationRequest, user)).thenThrow(new AppException("Invalid data"));

        //Act
        ResponseStatusException result = assertThrows(ResponseStatusException.class, () -> {
            consultationController.updateConsultation(InvalidRequestId, createConsultationRequest);
        });
        //Assert
        assertNotNull(result);
    }

    public CreateConsultationRequest getCreateConsultationRequest(TestRequest testRequest) {
        CreateConsultationRequest createConsultationRequest = new CreateConsultationRequest();
        createConsultationRequest.setComments("done");
        LabResult labResult = testRequest.getLabResult();
        if (labResult.getResult().equals(POSITIVE)) {
            createConsultationRequest.setComments("Be in home quarantine for 15 days");
            createConsultationRequest.setSuggestion(DoctorSuggestion.HOME_QUARANTINE);
        } else {
            createConsultationRequest.setComments("Ok");
            createConsultationRequest.setSuggestion(DoctorSuggestion.NO_ISSUES);
        }
        return createConsultationRequest;
    }

    public TestRequest getTestRequestByStatus(RequestStatus status) {
        List<TestRequest> testRequestList = new ArrayList<>();
        TestRequest mockTestRequest = getMockedTestRequest(status);
        testRequestList.add(mockTestRequest);
        when(testRequestQueryService.findBy(any())).thenReturn(testRequestList);
        return testRequestQueryService.findBy(status).stream().findFirst().get();
    }

    public TestRequest getMockedTestRequest(RequestStatus requestStatus) {
        CreateTestRequest createTestRequest = createTestRequest();
        TestRequest testRequest = new TestRequest();
        testRequest.setRequestId(1l);
        testRequest.setName(createTestRequest.getName());
        testRequest.setCreated(now());
        testRequest.setStatus(requestStatus);
        testRequest.setAge(createTestRequest.getAge());
        testRequest.setEmail(createTestRequest.getEmail());
        testRequest.setPhoneNumber(createTestRequest.getPhoneNumber());
        testRequest.setPinCode(createTestRequest.getPinCode());
        testRequest.setAddress(createTestRequest.getAddress());
        testRequest.setGender(createTestRequest.getGender());
        testRequest.setCreatedBy(createUser());
        LabResult labResult = new LabResult();
        labResult.setResultId(2l);
        labResult.setResult(POSITIVE);
        testRequest.setLabResult(labResult);
        Consultation consultation = new Consultation();
        consultation.setId(3l);
        testRequest.setConsultation(consultation);
        return testRequest;
    }

    public CreateTestRequest createTestRequest() {
        CreateTestRequest createTestRequest = new CreateTestRequest();
        createTestRequest.setAddress("some Addres");
        createTestRequest.setAge(98);
        createTestRequest.setEmail("someone" + "123456789" + "@somedomain.com");
        createTestRequest.setGender(Gender.MALE);
        createTestRequest.setName("someuser");
        createTestRequest.setPhoneNumber("123456789");
        createTestRequest.setPinCode(716768);
        return createTestRequest;
    }

    private User createUser() {
        User user = new User();
        user.setId(1L);
        user.setUserName("someuser");
        return user;
    }
}