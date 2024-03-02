package pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.domain.Enrollment;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity;

import java.util.List;

public class EnrollmentRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public List<Enrollment> findEnrollmentsByVolunteer(Volunteer volunteer) {
        return entityManager.createQuery("SELECT e FROM Enrollment e WHERE e.volunteer = :volunteer", Enrollment.class)
                .setParameter("volunteer", volunteer)
                .getResultList();
    }

    public List<Enrollment> findEnrollmentsByActivity(Activity activity) {
        return entityManager.createQuery("SELECT e FROM Enrollment e WHERE e.activity = :activity", Enrollment.class)
                .setParameter("activity", activity)
                .getResultList();
    }

    public Enrollment save(Enrollment enrollment) {
        entityManager.persist(enrollment);
        return enrollment;
    }

    public Enrollment update(Enrollment enrollment) {
        return entityManager.merge(enrollment);
    }

    public void delete(Enrollment enrollment) {
        entityManager.remove(enrollment);
    }
}