package org.upgrad.upstac.testrequests;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.internal.util.Contracts;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.web.server.ResponseStatusException;
import org.upgrad.upstac.config.security.UserLoggedInService;
import org.upgrad.upstac.exception.AppException;
import org.upgrad.upstac.testrequests.TestRequest;
import org.upgrad.upstac.testrequests.consultation.Consultation;
import org.upgrad.upstac.testrequests.consultation.ConsultationController;
import org.upgrad.upstac.testrequests.consultation.CreateConsultationRequest;
import org.upgrad.upstac.testrequests.consultation.DoctorSuggestion;
import org.upgrad.upstac.testrequests.lab.LabResult;
import org.upgrad.upstac.testrequests.lab.TestStatus;
import org.upgrad.upstac.testrequests.RequestStatus;
import org.upgrad.upstac.testrequests.TestRequestQueryService;
import org.upgrad.upstac.users.User;
import org.upgrad.upstac.users.models.Gender;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
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
    public void calling_assignForConsultation_with_valid_test_request_id_should_update_the_request_status(){

        TestRequest testRequest = getTestRequestByStatus(LAB_TEST_COMPLETED);

        //Implement this method

        //Create another object of the TestRequest method and explicitly assign this object for Consultation using assignForConsultation() method
        // from consultationController class. Pass the request id of testRequest object.
        TestRequest mockestResponse =getMockedTestRequest(DIAGNOSIS_IN_PROCESS);
        when(userLoggedInService.getLoggedInUser()).thenReturn(createUser());
        when(testRequestUpdateService.assignForConsultation(anyLong(), any())).thenReturn(mockestResponse);
        Long mockId=1l;
        TestRequest result = consultationController.assignForConsultation(mockId);

        //Use assertThat() methods to perform the following two comparisons
        //  1. the request ids of both the objects created should be same
        //  2. the status of the second object should be equal to 'DIAGNOSIS_IN_PROCESS'
        // make use of assertNotNull() method to make sure that the consultation value of second object is not null
        // use getConsultation() method to get the lab result

        assertNotNull(result);
        assertThat(result.getRequestId(), equalTo(mockestResponse.getRequestId()));
        assertThat(result.getStatus(), equalTo(DIAGNOSIS_IN_PROCESS));
        assertNotNull(result.getConsultation());
    }



    @Test
    @WithUserDetails(value = "doctor")
    public void calling_assignForConsultation_with_valid_test_request_id_should_throw_exception(){

        Long InvalidRequestId= -34L;

        //Implement this method

        when(userLoggedInService.getLoggedInUser()).thenReturn(createUser());
        // Create an object of ResponseStatusException . Use assertThrows() method and pass assignForConsultation() method
        // of consultationController with InvalidRequestId as Id
        when(testRequestUpdateService.assignForConsultation(anyLong(), any())).thenThrow(new AppException("Invalid ID"));

        ResponseStatusException result = assertThrows(ResponseStatusException.class, () -> {
            consultationController.assignForConsultation(InvalidRequestId);
        });
        //Use assertThat() method to perform the following comparison
        //  the exception message should be contain the string "Invalid ID"

        assertNotNull(result);
        assertThat(result.getMessage(), containsString("Invalid ID"));

    }


    @Test
    @WithUserDetails(value = "doctor")
    public void calling_updateConsultation_with_valid_test_request_id_should_update_the_request_status_and_update_consultation_details(){

        TestRequest testRequest = getTestRequestByStatus(DIAGNOSIS_IN_PROCESS);

        //Implement this method
        //Create an object of CreateConsultationRequest and call getCreateConsultationRequest() to create the object. Pass the above created object as the parameter
        CreateConsultationRequest createConsultationRequest = getCreateConsultationRequest(testRequest);


        //Create another object of the TestRequest method and explicitly update the status of this object
        // to be 'COMPLETED'. Make use of updateConsultation() method from consultationController class
        // (Pass the previously created two objects as parameters)
        // (for the object of TestRequest class, pass its ID using getRequestId())
        TestRequest mockestResponse=getMockedTestRequest(COMPLETED);
        when(userLoggedInService.getLoggedInUser()).thenReturn(createUser());
        when(testRequestUpdateService.updateConsultation(anyLong(), any(),any())).thenReturn(mockestResponse);
        Long mockId = 3l;
        TestRequest result = consultationController.updateConsultation(mockId, createConsultationRequest);


        //Use assertThat() methods to perform the following three comparisons
        //  1. the request ids of both the objects created should be same
        //  2. the status of the second object should be equal to 'COMPLETED'
        // 3. the suggestion of both the objects created should be same. Make use of getSuggestion() method to get the results.
        assertNotNull(result);
        assertThat(result.getRequestId(), equalTo(mockestResponse.getRequestId()));
        assertThat(result.getStatus(), equalTo(COMPLETED));
        assertThat(result.getConsultation().getSuggestion(),equalTo(mockestResponse.getConsultation().getSuggestion()));


    }


    @Test
    @WithUserDetails(value = "doctor")
    public void calling_updateConsultation_with_invalid_test_request_id_should_throw_exception(){

        TestRequest testRequest = getTestRequestByStatus(DIAGNOSIS_IN_PROCESS);

        //Implement this method

        //Create an object of CreateConsultationRequest and call getCreateConsultationRequest() to create the object. Pass the above created object as the parameter
        CreateConsultationRequest createConsultationRequest = getCreateConsultationRequest(testRequest);
        // Create an object of ResponseStatusException . Use assertThrows() method and pass updateConsultation() method
        // of consultationController with a negative long value as Id and the above created object as second parameter
        //Refer to the TestRequestControllerTest to check how to use assertThrows() method
        when(userLoggedInService.getLoggedInUser()).thenReturn(createUser());
        when(testRequestUpdateService.updateConsultation(anyLong(), any(),any())).thenThrow(new AppException("Invalid ID"));

        ResponseStatusException result = assertThrows(ResponseStatusException.class, () -> {
            Long InvalidRequestId =-34l;
            consultationController.updateConsultation(InvalidRequestId,createConsultationRequest);
        });
        //Use assertThat() method to perform the following comparison
        //  the exception message should be contain the string "Invalid ID"
        assertNotNull(result);
        assertThat(result.getMessage(), containsString("Invalid ID"));


    }

    @Test
    @WithUserDetails(value = "doctor")
    public void calling_updateConsultation_with_invalid_empty_status_should_throw_exception(){

        TestRequest testRequest = getTestRequestByStatus(DIAGNOSIS_IN_PROCESS);

        //Implement this method
        User user = createUser();
        //Create an object of CreateConsultationRequest and call getCreateConsultationRequest() to create the object. Pass the above created object as the parameter
        // Set the suggestion of the above created object to null.
        CreateConsultationRequest createConsultationRequest = getCreateConsultationRequest(testRequest);
        createConsultationRequest.setSuggestion(null);
        // Create an object of ResponseStatusException . Use assertThrows() method and pass updateConsultation() method
        // of consultationController with request Id of the testRequest object and the above created object as second parameter
        //Refer to the TestRequestControllerTest to check how to use assertThrows() method
        Long InvalidRequestId =-34l;
        Mockito.when(userLoggedInService.getLoggedInUser()).thenReturn(user);
        when(testRequestUpdateService.updateConsultation(InvalidRequestId,createConsultationRequest,user)).thenThrow(new AppException("Invalid data"));
        ResponseStatusException result = assertThrows(ResponseStatusException.class, () -> {
            consultationController.updateConsultation(InvalidRequestId,createConsultationRequest);
        });

        assertNotNull(result);
    }

    public CreateConsultationRequest getCreateConsultationRequest(TestRequest testRequest) {

        //Create an object of CreateLabResult and set all the values
        // if the lab result test status is Positive, set the doctor suggestion as "HOME_QUARANTINE" and comments accordingly
        // else if the lab result status is Negative, set the doctor suggestion as "NO_ISSUES" and comments as "Ok"
        // Return the object
        CreateConsultationRequest createConsultationRequest=new CreateConsultationRequest();
        createConsultationRequest.setComments("done");
        LabResult labResult = testRequest.getLabResult();
        if(labResult.getResult().equals(POSITIVE)){
            createConsultationRequest.setComments("Be in home quarantine for 15 days");
            createConsultationRequest.setSuggestion(DoctorSuggestion.HOME_QUARANTINE);
        }else{
            createConsultationRequest.setComments("Ok");
            createConsultationRequest.setSuggestion(DoctorSuggestion.NO_ISSUES);
        }
        return createConsultationRequest; // Replace this line with your code
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
        testRequest.setCreated(LocalDate.now());
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
        Consultation consultation =new Consultation();
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