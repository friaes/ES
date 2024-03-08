package pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.repository;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.domain.Participation;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface ParticipationRepository extends JpaRepository<Participation, Integer>{

    @Query("SELECT e FROM Participation e WHERE e.volunteer.id = :volunteerId")
    List<Participation> findParticipationsByVolunteer(Integer volunteerId);

    @Query("SELECT e FROM Participation e WHERE e.activity.id = :activityId")
    List<Participation> findParticipationsByActivity(Integer activityId);
    
    @Modifying
    @Query(value = "DELETE FROM Participation", nativeQuery = true)
    void deleteAll();
}
