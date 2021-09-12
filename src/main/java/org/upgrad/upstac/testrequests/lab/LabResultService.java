package org.upgrad.upstac.testrequests.lab;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.upgrad.upstac.exception.AppException;
import org.upgrad.upstac.testrequests.TestRequest;
import org.upgrad.upstac.users.User;

import javax.transaction.Transactional;

import static java.time.LocalDate.now;
import static org.slf4j.LoggerFactory.getLogger;

@Service
@Validated
public class LabResultService {


    private static final Logger logger = getLogger(LabResultService.class);
    @Autowired
    private LabResultRepository labResultRepository;

    /**
     * create lab result
     *
     * @param tester tester
     * @param testRequest testRequest
     * @return {@link LabResult}
     * @see LabResult
     */
    private LabResult createLabResult(User tester, TestRequest testRequest) {
        LabResult labResult = new LabResult();
        labResult.setTester(tester);
        labResult.setRequest(testRequest);
        return saveLabResult(labResult);
    }

    /**
     * save lab result
     *
     * @param labResult labResult
     * @return {@link LabResult}
     * @see LabResult
     */
    @Transactional
    LabResult saveLabResult(LabResult labResult) {
        return labResultRepository.save(labResult);
    }


    /**
     * assign for lab test
     *
     * @param testRequest testRequest
     * @param tester tester
     * @return {@link LabResult}
     * @see LabResult
     */
    public LabResult assignForLabTest(TestRequest testRequest, User tester) {

        return createLabResult(tester, testRequest);


    }


    /**
     * update lab test
     *
     * @param testRequest testRequest
     * @param createLabResult createLabResult
     * @return {@link LabResult}
     * @see LabResult
     */
    public LabResult updateLabTest(TestRequest testRequest, CreateLabResult createLabResult) {

        LabResult labResult = labResultRepository.findByRequest(testRequest).orElseThrow(() -> new AppException("Invalid Request"));

        labResult.setBloodPressure(createLabResult.getBloodPressure());
        labResult.setComments(createLabResult.getComments());
        labResult.setHeartBeat(createLabResult.getHeartBeat());
        labResult.setOxygenLevel(createLabResult.getOxygenLevel());
        labResult.setTemperature(createLabResult.getTemperature());
        labResult.setResult(createLabResult.getResult());
        labResult.setUpdatedOn(now());

        return saveLabResult(labResult);
    }


}
