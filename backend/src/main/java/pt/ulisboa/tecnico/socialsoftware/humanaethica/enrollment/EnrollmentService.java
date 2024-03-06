package pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.domain.Enrollment;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.dto.EnrollmentDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.repository.EnrollmentRepository;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.dto.ActivityDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.repository.ActivityRepository;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Member;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.dto.UserDto;
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
    public EnrollmentDto createEnrollment(Integer userId, Integer activityId, EnrollmentDto enrollmentDto) {
        if (userId == null) throw new HEException(USER_NOT_FOUND);
        Volunteer volunteer = (Volunteer) userRepository.findById(userId).orElseThrow(() -> new HEException(USER_NOT_FOUND, userId));
        if (activityId == null) throw new HEException(ACTIVITY_NOT_FOUND);
        Activity activity = activityRepository.findById(activityId).orElseThrow(() -> new HEException(ACTIVITY_NOT_FOUND, activityId));

        Enrollment enrollment = new Enrollment(activity, volunteer, enrollmentDto);

        enrollmentRepository.save(enrollment);

        return new EnrollmentDto(enrollment);
    }

    //um membro de uma instituição pode ver uma lista de todas as inscrições feitas numa atividade da sua instituição
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<EnrollmentDto> getEnrollmentsByActivity(Integer activityID) {
        if (activityID == null) throw new HEException(ACTIVITY_NOT_FOUND);
        Activity activity = (Activity) activityRepository.findById(activityID).orElseThrow(() -> new HEException(ACTIVITY_NOT_FOUND, activityID));
        List <Enrollment> enrollments = enrollmentRepository.findEnrollmentsByActivity(activity.getId());
        //cria a lista de EnrollmentDto a partir da lista de Enrollment
        return enrollments.stream().map(EnrollmentDto::new).toList();
    }
}
