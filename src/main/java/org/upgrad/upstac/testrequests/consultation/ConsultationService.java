package org.upgrad.upstac.testrequests.consultation;

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
public class ConsultationService {

    private static final Logger logger = getLogger(ConsultationService.class);
    @Autowired
    private ConsultationRepository consultationRepository;

    /**
     * assign for consultation
     *
     * @param testRequest testRequest
     * @param doctor doctor
     * @return {@link Consultation}
     * @see Consultation
     */
    @Transactional
    public Consultation assignForConsultation(TestRequest testRequest, User doctor) {
        Consultation consultation = new Consultation();
        consultation.setDoctor(doctor);
        consultation.setRequest(testRequest);
        return consultationRepository.save(consultation);
    }

    /**
     * update consultation
     *
     * @param testRequest testRequest
     * @param createConsultationRequest createConsultationRequest
     * @return {@link Consultation}
     * @see Consultation
     */
    public Consultation updateConsultation(TestRequest testRequest, CreateConsultationRequest createConsultationRequest) {
        Consultation consultation = consultationRepository.findByRequest(testRequest).orElseThrow(() -> new AppException("Invalid Request"));

        consultation.setSuggestion(createConsultationRequest.getSuggestion());
        consultation.setComments(createConsultationRequest.getComments());
        consultation.setUpdatedOn(now());
        return consultationRepository.save(consultation);
    }

}
