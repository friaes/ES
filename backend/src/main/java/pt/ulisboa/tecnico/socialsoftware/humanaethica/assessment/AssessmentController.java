package pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.dto.AssessmentDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Member;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/assessment")
public class AssessmentController {
    @Autowired
    private AssessmentService service;

    @GetMapping("/{institutionId}")
    public List<AssessmentDto> getAssessmentsByInstitution(@PathVariable int institutionId) {
        return service.getAssessmentsByInstitution(institutionId);
    }

    @PostMapping("/register/{institutionId}")
    @PreAuthorize("hasRole('ROLE_VOLUNTEER')")
    public AssessmentDto createAssessment(
            Principal principal,
            @PathVariable Integer institutionId,
            @Valid @RequestBody AssessmentDto assessmentDto) {

        int userId = ((AuthUser) ((Authentication) principal).getPrincipal()).getUser().getId();
        return service.createAssessment(userId, institutionId, assessmentDto);
    }

}
