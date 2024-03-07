package pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.domain

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.dto.AssessmentDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Member
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer

@DataJpaTest
class CreateAssessmentTest extends SpockTest {

    Institution institution = Mock()
    Assessment otherAssessment = Mock()
    Member member = Mock()
    Volunteer volunteer = Mock()
    Activity activity = Mock()
    def assessmentDto

    def setup() {
        given: "assessment info"
        assessmentDto = new AssessmentDto()
        assessmentDto.setReview("1234567890")
        assessmentDto.setId(0)
        // assessmentDto.setReviewDate(LocalDateTime.now())
    }

    //create valid assessment - Done
    def "create a valid assessment"() {
        given: "institution and volunteer"
        volunteer.getAssessments() >> []
        institution.getActivities() >> [activity]
        activity.getState() >> Activity.State.APPROVED

        when: "create assessment"
        Assessment assessment = new Assessment(institution, volunteer, assessmentDto)

        then: "assessment is created"
        assessment.review == assessmentDto.review
        // assessment.reviewDate == assessmentDto.reviewDate
        assessment.volunteer == volunteer
        assessment.institution == institution
    }

    def "review to short"() {
        given: "institution and volunteer"
        volunteer.getAssessments() >> []
        institution.getActivities() >> [activity]
        activity.getState() >> Activity.State.APPROVED
        assessmentDto.review = "123456789"

        when: "create assessment"
        Assessment assessment = new Assessment(institution, volunteer, assessmentDto)

        then: "execption is thrown"
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.ASSESSMENT_REVIEW_TO_SHORT
    }

    def "at least one activity"() {
        given: "institution and volunteer"
        volunteer.getAssessments() >> []
        institution.getActivities() >> [activity]
        activity.getState() >> Activity.State.SUSPENDED

        when: "create assessment"
        Assessment assessment = new Assessment(institution, volunteer, assessmentDto)

        then: "execption is thrown"
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.ASSESSMENT_HAS_NO_FINALIZED_ACTIVITY
    }

    def "Assessment already created"() {
        given: "institution and volunteer"
        otherAssessment.getInstitution() >> institution
        institution.getActivities() >> [activity]
        institution.getId() >> 0
        volunteer.getAssessments() >> [otherAssessment]
        activity.getState() >> Activity.State.APPROVED

        when: "create assessment"
        Assessment assessment = new Assessment(institution, volunteer, assessmentDto)

        then: "execption is thrown"
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.ASSESSMENT_ALREADY_CREATED
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}