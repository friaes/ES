package pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.domain.Enrollment;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.dto.EnrollmentDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.repository.EnrollmentRepository;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.repository.ActivityRepository;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Member;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.repository.UserRepository;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution;

import static pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage.*;

import java.util.List;  

@Service
public class EnrollmentService {
    @Autowired
    EnrollmentRepository enrollmentRepository;
    @Autowired
    ActivityRepository activityRepository;
    @Autowired
    UserRepository userRepository;

    //um voluntário pode inscrever-se numa atividade
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public EnrollmentDto registerEnrollment(Integer userId, Integer activityId, EnrollmentDto enrollmentDto) {
        if (userId == null) throw new HEException(USER_NOT_FOUND);
        Volunteer volunteer = (Volunteer) userRepository.findById(userId).orElseThrow(() -> new HEException(USER_NOT_FOUND, userId));
        Activity activity = activityRepository.findById(activityId).orElseThrow(() -> new HEException(ACTIVITY_NOT_FOUND, activityId));

        Enrollment enrollment = new Enrollment(enrollmentDto, volunteer, activity);

        enrollmentRepository.save(enrollment);

        return new EnrollmentDto(enrollment);
    }

    //um membro de uma instituição pode ver uma lista de todas as inscrições feitas numa atividade da sua instituição
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<EnrollmentDto> getEnrollmentsByActivity(Integer userId, Integer volunteerId, Integer activityId) {
        //ver que o userId é um membro de uma instituição que tem a atividade
        if (userId == null) throw new HEException(USER_NOT_FOUND);
        Member member = (Member) userRepository.findById(userId).orElseThrow(() -> new HEException(USER_NOT_FOUND, userId));
        if (activityRepository.findById(activityId).isEmpty()) throw new HEException(ACTIVITY_NOT_FOUND, activityId);
        if (userRepository.findById(volunteerId).isEmpty()) throw new HEException(USER_NOT_FOUND, volunteerId);
        Institution institution = member.getInstitution();
        //verificar que a instituição do membro tem a atividade
        if (activityRepository.findById(activityId).get().getInstitution() != institution) throw new HEException(ACTIVITY_NOT_FOUND, activityId);

        return enrollmentRepository.findEnrollmentsByActivity(activityRepository.findById(activityId).get()).stream()
                .map(enrollment -> new EnrollmentDto(enrollment))
                .toList();
    }
}
