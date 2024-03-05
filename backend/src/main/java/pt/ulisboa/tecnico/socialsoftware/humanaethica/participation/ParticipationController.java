package pt.ulisboa.tecnico.socialsoftware.humanaethica.participation;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.dto.ParticipationDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser;

import java.security.Principal;

@RestController
@RequestMapping("/participations")
public class ParticipationController {
    @Autowired
    private ParticipationService participationService;

    private static final Logger logger = LoggerFactory.getLogger(ParticipationController.class);

    //um membro pode associar um voluntário a uma atividade
    @PostMapping()
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    public ParticipationDto createParticipation(@PathVariable Integer volunteerId, @PathVariable Integer activityId, @Valid @RequestBody ParticipationDto participationDto){
        return participationService.createParticipation(volunteerId, activityId, participationDto);
    }

    //TODO 2nd feature
}

