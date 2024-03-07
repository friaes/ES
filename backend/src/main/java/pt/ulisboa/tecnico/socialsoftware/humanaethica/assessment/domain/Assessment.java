package pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.domain;

import com.github.javaparser.quality.NotNull;
import jakarta.persistence.*;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.dto.AssessmentDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage.*;

@Entity
@Table(name = "assessment")
public class Assessment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String review;
    private LocalDateTime reviewDate;

    @ManyToOne
    private Volunteer volunteer;

    @ManyToOne
    private Institution institution;

    public Assessment(@NotNull Institution institution, @NotNull Volunteer volunteer, AssessmentDto assessmentDto) {
        setReview(assessmentDto.getReview());
        setReviewDate(assessmentDto.getReviewDate());
        setVolunteer(volunteer);
        setInstitution(institution);
        setId(assessmentDto.getId());

        if (this.review == null || this.review.length() < 10) {
            throw new HEException(ASSESSMENT_REVIEW_TO_SHORT);
        }

        List<Assessment> assessments = this.volunteer.getAssessments();
        if (assessments != null && assessments.stream().anyMatch(a -> Objects.equals(a.getInstitution().getId(), institution.getId()))) {
            throw new HEException(ASSESSMENT_ALREADY_CREATED);
        }

        if (institution.getActivities().stream().noneMatch(a -> a.getState() == Activity.State.APPROVED)) {
            throw new HEException(ASSESSMENT_HAS_NO_FINALIZED_ACTIVITY, institution.getName());
        }
    }

    public Assessment() {}

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public LocalDateTime getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(LocalDateTime reviewDate) {
        this.reviewDate = reviewDate;
    }

    public Institution getInstitution() {
        return institution;
    }

    public void setInstitution(Institution i) {
        this.institution = i;
    }

    public Volunteer getVolunteer() {
        return volunteer;
    }

    public void setVolunteer(Volunteer v) {
        this.volunteer = v;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
