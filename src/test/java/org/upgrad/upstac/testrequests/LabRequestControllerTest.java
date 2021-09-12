package org.upgrad.upstac.testrequests;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.web.server.ResponseStatusException;
import org.upgrad.upstac.config.security.UserLoggedInService;
import org.upgrad.upstac.exception.AppException;
import org.upgrad.upstac.testrequests.lab.CreateLabResult;
import org.upgrad.upstac.testrequests.lab.LabRequestController;
import org.upgrad.upstac.testrequests.lab.LabResult;
import org.upgrad.upstac.testrequests.lab.TestStatus;
import org.upgrad.upstac.users.User;
import org.upgrad.upstac.users.models.Gender;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.upgrad.upstac.testrequests.RequestStatus.*;


@SpringBootTest
@Slf4j
class LabRequestControllerTest {

    @InjectMocks
    LabRequestController labRequestController;

    @Mock
    TestRequestQueryService testRequestQueryService;

    @Mock
    UserLoggedInService userLoggedInService;

    @Mock
    TestRequestUpdateService testRequestUpdateService;

    @Test
    @WithUserDetails(value = "tester")
    public void calling_assignForLabTest_with_valid_test_request_id_should_update_the_request_status() {


        TestRequest testRequest = getTestRequestByStatus(INITIATED);
        //Implement this method
        Long mockId = 1l;
        //Create another object of the TestRequest method and explicitl
        // y assign this object for Lab Test using assignForLabTest() method
        // from labRequestController class. Pass the request id of testRequest object.
        when(userLoggedInService.getLoggedInUser()).thenReturn(createUser());
        TestRequest mockTestRequest = getMockedTestRequest(LAB_TEST_IN_PROGRESS);
        when(testRequestUpdateService.assignForLabTest(anyLong(), any())).thenReturn(mockTestRequest);

        TestRequest result = labRequestController.assignForLabTest(mockId);

        //Use assertThat() methods to perform the following two comparisons
        //  1. the request ids of both the objects created should be same
        //  2. the status of the second object should be equal to 'LAB_TEST_IN_PROGRESS'
        // make use of assertNotNull() method to make sure that the lab result of second object is not null
        // use getLabResult() method to get the lab result
        assertNotNull(result);
        assertThat(result.getRequestId(), equalTo(testRequest.getRequestId()));
        assertThat(result.getStatus(), equalTo(LAB_TEST_IN_PROGRESS));
        assertNotNull(result.getLabResult());
    }

    public TestRequest getTestRequestByStatus(RequestStatus status) {
        List<TestRequest> testRequestList = new ArrayList<>();
        TestRequest mockTestRequest = getMockedTestRequest(status);
        testRequestList.add(mockTestRequest);
        when(testRequestQueryService.findBy(any())).thenReturn(testRequestList);
        return testRequestQueryService.findBy(status).stream().findFirst().get();
    }

    @Test
    @WithUserDetails(value = "tester")
    public void calling_assignForLabTest_with_valid_test_request_id_should_throw_exception() {

        Long InvalidRequestId = -34L;

        User user = createUser();

        //Implement this method
        Mockito.when(userLoggedInService.getLoggedInUser()).thenReturn(user);
        when(testRequestUpdateService.assignForLabTest(anyLong(), any())).thenThrow(new AppException("Invalid ID"));
        //when(testRequestUpdateService.assignForLabTest(InvalidRequestId, any())).thenThrow(new AppException());
        // Create an object of ResponseStatusException . Use assertThrows() method and pass assignForLabTest() method
        // of labRequestController with InvalidRequestId as Id
        ResponseStatusException result = assertThrows(ResponseStatusException.class, () -> {
            labRequestController.assignForLabTest(InvalidRequestId);
        });

        //Use assertThat() method to perform the following comparison
        //  the exception message should be contain the string "Invalid ID"
        Assertions.assertNotNull(result);
        assertThat(result.getMessage(), containsString("Invalid ID"));

    }

    @Test
    @WithUserDetails(value = "tester")
    public void calling_updateLabTest_with_valid_test_request_id_should_update_the_request_status_and_update_test_request_details() {

        TestRequest testRequest = getTestRequestByStatus(LAB_TEST_IN_PROGRESS);
        Long mockId = 1l;
        //Implement this method
        //Create an object of CreateLabResult and call getCreateLabResult() to create the object. Pass the above created object as the parameter
        CreateLabResult createLabResult = getCreateLabResult(testRequest);
        //Create another object of the TestRequest method and explicitly update the status of this object
        // to be 'LAB_TEST_IN_PROGRESS'. Make use of updateLabTest() method from labRequestController class (Pass the previously created two objects as parameters)

        Mockito.when(userLoggedInService.getLoggedInUser()).thenReturn(createUser());
        when(testRequestUpdateService.updateLabTest(anyLong(), any(), any())).thenReturn(getMockedTestRequest(LAB_TEST_COMPLETED));
        TestRequest result = labRequestController.updateLabTest(mockId, createLabResult);

        //Use assertThat() methods to perform the following three comparisons
        //  1. the request ids of both the objects created should be same
        //  2. the status of the second object should be equal to 'LAB_TEST_COMPLETED'
        // 3. the results of both the objects created should be same. Make use of getLabResult() method to get the results.
        assertNotNull(result);
        assertThat(result.getRequestId(), equalTo(testRequest.getRequestId()));
        assertThat(result.getStatus(), equalTo(LAB_TEST_COMPLETED));
        assertThat(result.getLabResult(), equalTo(testRequest.getLabResult()));

    }


    @Test
    @WithUserDetails(value = "tester")
    public void calling_updateLabTest_with_invalid_test_request_id_should_throw_exception() {
        Long mockId = 1l;
        TestRequest testRequest = getTestRequestByStatus(LAB_TEST_IN_PROGRESS);
        //Implement this method

        //Create an object of CreateLabResult and call getCreateLabResult() to create the object. Pass the above created object as the parameter
        CreateLabResult createLabResult = getCreateLabResult(testRequest);

        // Create an object of ResponseStatusException . Use assertThrows() method and pass updateLabTest() method
        // of labRequestController with a negative long value as Id and the above created object as second parameter
        //Refer to the TestRequestControllerTest to check how to use assertThrows() method

        Mockito.when(userLoggedInService.getLoggedInUser()).thenReturn(createUser());
        when(testRequestUpdateService.updateLabTest(anyLong(), any(), any())).thenThrow(new AppException("Invalid ID"));

        ResponseStatusException result = assertThrows(ResponseStatusException.class, () -> {
            labRequestController.updateLabTest(mockId, createLabResult);
        });

        //Use assertThat() method to perform the following comparison
        //  the exception message should be contain the string "Invalid ID"
        Assertions.assertNotNull(result);
        assertThat(result.getMessage(), containsString("Invalid ID"));


    }

    @Test
    @WithUserDetails(value = "tester")
    public void calling_updateLabTest_with_invalid_empty_status_should_throw_exception() {

        TestRequest testRequest = getTestRequestByStatus(LAB_TEST_IN_PROGRESS);

        //Implement this method

        //Create an object of CreateLabResult and call getCreateLabResult() to create the object. Pass the above created object as the parameter
        // Set the result of the above created object to null.
        CreateLabResult createLabResult = getCreateLabResult(testRequest);

        // Create an object of ResponseStatusException . Use assertThrows() method and pass updateLabTest() method
        // of labRequestController with request Id of the testRequest object and the above created object as second parameter
        //Refer to the TestRequestControllerTest to check how to use assertThrows() method
        Mockito.when(userLoggedInService.getLoggedInUser()).thenReturn(createUser());
        when(testRequestUpdateService.updateLabTest(anyLong(), any(), any())).thenThrow(new AppException("ConstraintViolationException"));

        ResponseStatusException result = assertThrows(ResponseStatusException.class, () -> {
            Long mockId = 1l;
            labRequestController.updateLabTest(mockId, createLabResult);
        });


        //Use assertThat() method to perform the following comparison
        //  the exception message should be contain the string "ConstraintViolationException"

        Assertions.assertNotNull(result);
        assertThat(result.getMessage(), containsString("ConstraintViolationException"));

    }

    public CreateLabResult getCreateLabResult(TestRequest testRequest) {

        //Create an object of CreateLabResult and set all the values
        // Return the object
        CreateLabResult createLabResult = new CreateLabResult();
        createLabResult.setHeartBeat("120");
        createLabResult.setBloodPressure("78");
        createLabResult.setTemperature("98.0");
        createLabResult.setOxygenLevel("50.9");
        createLabResult.setComments("Tested");
        createLabResult.setResult(TestStatus.NEGATIVE);
        return createLabResult; // Replace this line with your code
    }

    private User createUser() {
        User user = new User();
        user.setId(1L);
        user.setUserName("someuser");
        return user;
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
        testRequest.setLabResult(labResult);
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
}