package pt.ulisboa.tecnico.socialsoftware.humanaethica.participation;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.dto.ParticipationDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;



@RestController
@RequestMapping("/participations")
public class ParticipationController {
    @Autowired
    private ParticipationService participationService;

    private static final Logger logger = LoggerFactory.getLogger(ParticipationController.class);


    //um membro pode associar um voluntário a uma atividade
    @PostMapping("/{activityId}/create")
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    public ParticipationDto createParticipation(@PathVariable Integer activityId, @Valid @RequestBody ParticipationDto participationDto){
        return participationService.createParticipation(activityId, participationDto);
    }

    
    //um membro de uma instituição pode ver uma lista de todas as participações feitas numa atividade da sua instituição
    @GetMapping("/{activityId}")
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    public List<ParticipationDto> getActivityParticipation(@PathVariable Integer activityId){
        return participationService.getParticipationsByActivity(activityId);
    }

}

