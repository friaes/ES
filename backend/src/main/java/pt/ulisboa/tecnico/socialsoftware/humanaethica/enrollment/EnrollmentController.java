package pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.dto.EnrollmentDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/enrollments")
public class EnrollmentController {
    @Autowired
    private EnrollmentService enrollmentService;

    private static final Logger logger = LoggerFactory.getLogger(EnrollmentController.class);

    //um voluntário pode inscrever-se numa atividade
    @PostMapping("/{activityId}/create")
    @PreAuthorize("hasRole('ROLE_VOLUNTEER')")
    public EnrollmentDto createEnrollment(Principal principal, @PathVariable Integer activityId, @Valid @RequestBody EnrollmentDto enrollmentDto){
        int userId = ((AuthUser) ((Authentication) principal).getPrincipal()).getUser().getId();
        return enrollmentService.createEnrollment(userId, activityId, enrollmentDto);
    }

    //um membro de uma instituição pode ver uma lista de todas as inscrições feitas numa atividade da sua instituição
    @GetMapping("/{activityId}")
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    public List<EnrollmentDto> getActivityEnrollments(@PathVariable Integer activityId){
        return enrollmentService.getEnrollmentsByActivity(activityId);
    }
}
