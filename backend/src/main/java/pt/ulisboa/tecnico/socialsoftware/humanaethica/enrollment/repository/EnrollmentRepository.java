package pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.repository;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.domain.Enrollment;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface EnrollmentRepository extends JpaRepository<Enrollment, Integer>{

    @Query("SELECT e FROM Enrollment e WHERE e.volunteer = :volunteer")
    List<Enrollment> findEnrollmentsByVolunteer(Volunteer volunteer);

    @Query("SELECT e FROM Enrollment e WHERE e.activity = :activity")
    List<Enrollment> findEnrollmentsByActivity(Activity activity);
}