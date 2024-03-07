package pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment;

import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.domain.Assessment;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.dto.AssessmentDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.repository.AssessmentRepository;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.repository.InstitutionRepository;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.repository.UserRepository;

import java.util.List;

import static pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage.*;

@Service
public class AssessmentService {
    @Autowired
    AssessmentRepository ar;

    @Autowired
    UserRepository ur;

    @Autowired
    InstitutionRepository ir;

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<AssessmentDto> getAssessmentsByInstitution(Integer institutionId) {
        if (institutionId == null) {
            throw new HEException(INSTITUTION_NOT_FOUND);
        }

        ir.findById(institutionId).orElseThrow(() -> new HEException(INSTITUTION_NOT_FOUND, institutionId));

        List<Assessment> a = ar.getAssessments(institutionId);
        return a.stream()
                .map(AssessmentDto::new)
                .toList();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public AssessmentDto createAssessment(Integer userId, Integer institutionId, AssessmentDto assessmentDto) {

        if (institutionId == null) throw new HEException(INSTITUTION_NOT_FOUND);
        if (userId == null) throw new HEException(USER_NOT_FOUND);

        Institution i = ir.findById(institutionId).orElseThrow(() -> new HEException(INSTITUTION_NOT_FOUND, institutionId));
        Volunteer v = (Volunteer)ur.findById(userId).orElseThrow(() -> new HEException(USER_NOT_FOUND, userId));

        Assessment a = new Assessment(i, v, assessmentDto);
        ar.save(a);

        return new AssessmentDto(a);
    }
}
