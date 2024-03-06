package pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity;

import java.util.List;
import java.util.Set;
import java.util.Optional;


@Repository
@Transactional
public interface ActivityRepository extends JpaRepository<Activity, Integer> {
    @Query("SELECT a FROM Activity a WHERE a.institution.id = :institutionId")
    List<Activity> getActivitiesByInstitutionId(Integer institutionId);

    @Query("SELECT a FROM Activity a WHERE a.name = lower(:name)")
    Optional<Activity> findActivityByName(String name);

    @Modifying
    @Query(value = "DELETE FROM activity_themes", nativeQuery = true)
    void deleteAllActivityTheme();


}