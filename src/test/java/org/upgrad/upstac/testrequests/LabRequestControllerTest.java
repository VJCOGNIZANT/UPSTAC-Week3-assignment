package org.upgrad.upstac.testrequests;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.web.server.ResponseStatusException;
import org.upgrad.upstac.config.security.UserLoggedInService;
import org.upgrad.upstac.exception.AppException;
import org.upgrad.upstac.testrequests.lab.CreateLabResult;
import org.upgrad.upstac.testrequests.lab.LabRequestController;
import org.upgrad.upstac.testrequests.lab.LabResult;
import org.upgrad.upstac.users.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.upgrad.upstac.testrequests.RequestStatus.*;
import static org.upgrad.upstac.testrequests.lab.TestStatus.NEGATIVE;
import static org.upgrad.upstac.users.models.Gender.MALE;


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

        //Arrange
        TestRequest testRequest = getTestRequestByStatus(INITIATED);
        Long mockId = 1l;

        when(userLoggedInService.getLoggedInUser()).thenReturn(createUser());
        TestRequest mockTestRequest = getMockedTestRequest(LAB_TEST_IN_PROGRESS);
        when(testRequestUpdateService.assignForLabTest(anyLong(), any())).thenReturn(mockTestRequest);

        //Act
        TestRequest result = labRequestController.assignForLabTest(mockId);

        //Assert
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

        //Arrange
        Long InvalidRequestId = -34L;

        User user = createUser();

        when(userLoggedInService.getLoggedInUser()).thenReturn(user);
        when(testRequestUpdateService.assignForLabTest(anyLong(), any())).thenThrow(new AppException("Invalid ID"));

        //Act
        ResponseStatusException result = assertThrows(ResponseStatusException.class, () -> {
            labRequestController.assignForLabTest(InvalidRequestId);
        });

        //Assert
        assertNotNull(result);
        assertThat(result.getMessage(), containsString("Invalid ID"));

    }

    @Test
    @WithUserDetails(value = "tester")
    public void calling_updateLabTest_with_valid_test_request_id_should_update_the_request_status_and_update_test_request_details() {

        //Arrange
        TestRequest testRequest = getTestRequestByStatus(LAB_TEST_IN_PROGRESS);
        Long mockId = 1l;
        CreateLabResult createLabResult = getCreateLabResult(testRequest);

        when(userLoggedInService.getLoggedInUser()).thenReturn(createUser());
        when(testRequestUpdateService.updateLabTest(anyLong(), any(), any())).thenReturn(getMockedTestRequest(LAB_TEST_COMPLETED));

        //Act
        TestRequest result = labRequestController.updateLabTest(mockId, createLabResult);

        //Assert
        assertNotNull(result);
        assertThat(result.getRequestId(), equalTo(testRequest.getRequestId()));
        assertThat(result.getStatus(), equalTo(LAB_TEST_COMPLETED));
        assertThat(result.getLabResult(), equalTo(testRequest.getLabResult()));

    }


    @Test
    @WithUserDetails(value = "tester")
    public void calling_updateLabTest_with_invalid_test_request_id_should_throw_exception() {
        //Arrange
        Long mockId = 1l;
        TestRequest testRequest = getTestRequestByStatus(LAB_TEST_IN_PROGRESS);
        CreateLabResult createLabResult = getCreateLabResult(testRequest);

        when(userLoggedInService.getLoggedInUser()).thenReturn(createUser());
        when(testRequestUpdateService.updateLabTest(anyLong(), any(), any())).thenThrow(new AppException("Invalid ID"));

        //Act
        ResponseStatusException result = assertThrows(ResponseStatusException.class, () -> {
            labRequestController.updateLabTest(mockId, createLabResult);
        });

        //Assert
        assertNotNull(result);
        assertThat(result.getMessage(), containsString("Invalid ID"));

    }

    @Test
    @WithUserDetails(value = "tester")
    public void calling_updateLabTest_with_invalid_empty_status_should_throw_exception() {

        //Arrange
        TestRequest testRequest = getTestRequestByStatus(LAB_TEST_IN_PROGRESS);

        CreateLabResult createLabResult = getCreateLabResult(testRequest);

        when(userLoggedInService.getLoggedInUser()).thenReturn(createUser());
        when(testRequestUpdateService.updateLabTest(anyLong(), any(), any())).thenThrow(new AppException("ConstraintViolationException"));

        //Act
        ResponseStatusException result = assertThrows(ResponseStatusException.class, () -> {
            Long mockId = 1l;
            labRequestController.updateLabTest(mockId, createLabResult);
        });

        //Assert
        assertNotNull(result);
        assertThat(result.getMessage(), containsString("ConstraintViolationException"));

    }

    public CreateLabResult getCreateLabResult(TestRequest testRequest) {

        CreateLabResult createLabResult = new CreateLabResult();
        createLabResult.setHeartBeat("120");
        createLabResult.setBloodPressure("78");
        createLabResult.setTemperature("98.0");
        createLabResult.setOxygenLevel("50.9");
        createLabResult.setComments("Tested");
        createLabResult.setResult(NEGATIVE);
        return createLabResult;
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
        createTestRequest.setGender(MALE);
        createTestRequest.setName("someuser");
        createTestRequest.setPhoneNumber("123456789");
        createTestRequest.setPinCode(716768);
        return createTestRequest;
    }
}