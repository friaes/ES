package pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.dto.AssessmentDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser;

import java.security.Principal;
import java.util.List;

@RestController()
@RequestMapping(value = "/institutions")
public class AssessmentController {
    @Autowired
    AssessmentService assessmentService;

    @GetMapping(value = "{institutionId}/assessments")
    public List<AssessmentDto> getInstitutionAssessments(@PathVariable Integer institutionId) {
        return assessmentService.getAssessmentsByInstitution(institutionId);
    }

    @PreAuthorize("(hasRole('ROLE_VOLUNTEER'))")
    @PostMapping(value = "{institutionId}/assessments")
    public AssessmentDto createAssessment(Principal principal, @PathVariable Integer institutionId, @Valid @RequestBody AssessmentDto assessmentDto) {
        int userId = ((AuthUser) ((Authentication) principal).getPrincipal()).getUser().getId();
        return assessmentService.createAssessment(userId, institutionId, assessmentDto);
    }

    @PreAuthorize("(hasRole('ROLE_VOLUNTEER'))")
    @GetMapping(value = "myassessments")
    public List<AssessmentDto> getVolunteerAssessments(Principal principal) {
        int userId = ((AuthUser) ((Authentication) principal).getPrincipal()).getUser().getId();
        return assessmentService.getVolunteerAssessments(userId);
    }
}
