package org.upgrad.upstac.testrequests;


import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.upgrad.upstac.config.security.UserLoggedInService;
import org.upgrad.upstac.exception.AppException;
import org.upgrad.upstac.users.User;

import java.util.List;
import java.util.Optional;

import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.http.HttpStatus.BAD_REQUEST;


@RestController
public class TestRequestController {

    private static final Logger log = getLogger(TestRequestController.class);

    @Autowired
    private TestRequestService testRequestService;

    @Autowired
    private UserLoggedInService userLoggedInService;

    @Autowired
    private TestRequestQueryService testRequestQueryService;


    @PostMapping("/api/testrequests")
    public TestRequest createRequest(@RequestBody CreateTestRequest testRequest) {
        try {
            final User user = userLoggedInService.getLoggedInUser();
            TestRequest result = testRequestService.createTestRequestFrom(user, testRequest);
            return result;
        } catch (AppException e) {
            throw new ResponseStatusException(BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/api/testrequests")
    public List<TestRequest> requestHistory() {
        User user = userLoggedInService.getLoggedInUser();
        return testRequestService.getHistoryFor(user);
    }

    @GetMapping("/api/testrequests/{id}")
    public Optional<TestRequest> getById(@PathVariable Long id) {
        return testRequestQueryService.getTestRequestById(id);
    }

}
