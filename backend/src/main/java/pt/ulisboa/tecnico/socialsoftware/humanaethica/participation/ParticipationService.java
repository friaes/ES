package pt.ulisboa.tecnico.socialsoftware.humanaethica.participation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.domain.Participation;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.dto.ParticipationDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.repository.ParticipationRepository;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.repository.ActivityRepository;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.repository.UserRepository;

import static pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage.*;
import java.util.List;

@Service
public class ParticipationService {

    @Autowired
    ParticipationRepository participationRepository;
    @Autowired
    ActivityRepository activityRepository;
    @Autowired
    UserRepository userRepository;

    //um voluntário pode inscrever-se numa atividade
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ParticipationDto createParticipation(Integer activityId, ParticipationDto participationDto) {

        Integer volunteerId = participationDto.getVolunteerId();
        if (volunteerId == null) throw new HEException(USER_NOT_FOUND);
        Volunteer volunteer = (Volunteer) userRepository.findById(volunteerId).orElseThrow(() -> new HEException(USER_NOT_FOUND, volunteerId));

        if (activityId == null) throw new HEException(ACTIVITY_NOT_FOUND);
        Activity activity = activityRepository.findById(activityId).orElseThrow(() -> new HEException(ACTIVITY_NOT_FOUND, activityId));

        Participation participation = new Participation(activity, volunteer, participationDto);

        participationRepository.save(participation);

        return new ParticipationDto(participation);
    }


    //um membro de uma instituição pode ver uma lista de todas as participações numa atividade da sua instituição
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<ParticipationDto> getParticipationsByActivity(Integer activityID) {
        
        if (activityID == null) throw new HEException(ACTIVITY_NOT_FOUND);
        Activity activity = (Activity) activityRepository.findById(activityID).orElseThrow(() -> new HEException(ACTIVITY_NOT_FOUND, activityID));
        
        List <Participation> participations = participationRepository.findParticipationsByActivity(activity.getId());
        //cria a lista de ParticipationDto a partir da lista de Participation
        return participations.stream().map(ParticipationDto::new).toList();
    }


}
