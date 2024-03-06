package pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.repository;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.domain.Participation;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface ParticipationRepository extends JpaRepository<Participation, Integer>{

    @Query("SELECT e FROM Participation e WHERE e.volunteer = :volunteer")
    List<Participation> findParticipationsByVolunteer(Volunteer volunteer);

    @Query("SELECT e FROM Participation e WHERE e.activity = :activity")
    List<Participation> findParticpationsByActivity(Activity activity);
    
}
