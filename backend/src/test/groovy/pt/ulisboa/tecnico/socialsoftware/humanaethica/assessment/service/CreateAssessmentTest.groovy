package pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.dto.ActivityDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.AssessmentService
import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.domain.Assessment
import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.dto.AssessmentDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.repository.AssessmentRepository
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.AuthUserService
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain.Theme
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.UserService
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException

@DataJpaTest
class CreateAssessmentTest extends SpockTest {
    def assessment
    def assessmentDto
    def volunteer
    def institution
    def activity
    def activityDto

    def setup() {
        institution = institutionService.getDemoInstitution()

        activityDto = createActivityDto(ACTIVITY_NAME_1, ACTIVITY_REGION_1, 1, ACTIVITY_DESCRIPTION_1,
                IN_ONE_DAY, IN_TWO_DAYS, IN_THREE_DAYS, null)
        List<Theme> themes = new ArrayList<>()
        themes.add((Theme)createTheme(THEME_NAME_1, Theme.State.APPROVED, null))

        activity = new Activity(activityDto, institution, themes)

        volunteer = new Volunteer(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, AuthUser.Type.NORMAL, User.State.SUBMITTED)
        userRepository.save(volunteer)

        assessmentDto = new AssessmentDto()
        assessmentDto.setReview("1234567890")
        assessmentDto.setReviewDate(DateHandler.now())

        // assessment = new Assessment(institution, volunteer, assessmentDto)
    }

    def "create valid assessment"() {
        when:
        AssessmentDto result = assessmentService.createAssessment(volunteer.getId(), institution.getId(), assessmentDto)

        then:
        Objects.equals(result.getReview(), assessmentDto.getReview())
    }

    def "create invalid assessment (bad volunteer id)"() {
        when:
        AssessmentDto result = assessmentService.createAssessment(null, institution.getId(), assessmentDto)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.USER_NOT_FOUND
    }

    def "create invalid assessment (bad institution id)"() {
        when:
        AssessmentDto result = assessmentService.createAssessment(volunteer.getId(), null, assessmentDto)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.INSTITUTION_NOT_FOUND
    }

    def "create invalid assessment (invalid volunteer id)"() {
        when:
        AssessmentDto result = assessmentService.createAssessment(100, institution.getId(), assessmentDto)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.USER_NOT_FOUND
    }

    def "create invalid assessment (invalid institution id)"() {
        when:
        AssessmentDto result = assessmentService.createAssessment(volunteer.getId(), 100, assessmentDto)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.INSTITUTION_NOT_FOUND
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}