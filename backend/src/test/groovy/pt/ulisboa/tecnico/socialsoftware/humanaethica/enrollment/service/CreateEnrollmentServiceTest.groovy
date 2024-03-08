package pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.domain.Enrollment
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.EnrollmentService
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain.Theme
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.dto.ThemeDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.dto.UserDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.repository.UserRepository
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthNormalUser
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.dto.EnrollmentDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException
import spock.lang.Unroll

@DataJpaTest
class CreateEnrollmentTest extends SpockTest {

    def activity
    def enrollment
    def activityDto
    def institution

    def setup() {
        institution = institutionService.getDemoInstitution()
        given: "activity info"
        activityDto = createActivityDto(ACTIVITY_NAME_1,ACTIVITY_REGION_1,1,ACTIVITY_DESCRIPTION_1,
                                            IN_ONE_DAY,IN_TWO_DAYS,IN_THREE_DAYS,null)
        and: "a theme"
        def themes = new ArrayList<>()
        themes.add(createTheme(THEME_NAME_1,Theme.State.APPROVED,null))
        and: "an activity"
        activity = new Activity(activityDto, institution, themes)
        activityRepository.save(activity)
    }

    def "create enrollment with valid data"() {
        given: "enrollment info"
        def volunteer = new Volunteer(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, AuthUser.Type.NORMAL, User.State.SUBMITTED)
        userRepository.save(volunteer)
        def userDto = new UserDto()
        userDto.setName(USER_1_NAME)
        userDto.setUsername(USER_1_USERNAME)
        userDto.setEmail(USER_1_EMAIL)
        def enrollmentDto = createEnrollmentDto(activityDto, userDto, IN_ONE_DAY, "motivation to max")
        when: "create enrollment"
        def result = enrollmentService.createEnrollment(volunteer.getId(), activity.getId(),enrollmentDto)

        then: "enrollment is created"
        activity.getEnrollments().size() == 1
        volunteer.getEnrollments().size() == 1
        enrollmentRepository.count() == 1
        result != null
        result.getMotivation() == "motivation to max"
        result.getVolunteer().getId() == volunteer.getId()
        result.getActivity().getId() == activity.getId()
    }

    def "create enrollment with null id for volunteer"(){
        given: "enrollment info"
        def volunteer = new Volunteer(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, AuthUser.Type.NORMAL, User.State.SUBMITTED)
        userRepository.save(volunteer)
        def userDto = new UserDto()
        userDto.setName(USER_1_NAME)
        userDto.setUsername(USER_1_USERNAME)
        userDto.setEmail(USER_1_EMAIL)
        def enrollmentDto = createEnrollmentDto(activityDto, userDto, IN_ONE_DAY, "motivation to max")
        when: "create enrollment"
        enrollmentService.createEnrollment(null, activity.getId(),enrollmentDto)

        then: "error is thrown"
        enrollmentRepository.count() == 0
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.USER_NOT_FOUND
    }

    def "create enrollment with null id for activity"(){
        given: "enrollment info"
        def volunteer = new Volunteer(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, AuthUser.Type.NORMAL, User.State.SUBMITTED)
        userRepository.save(volunteer)
        def userDto = new UserDto()
        userDto.setName(USER_1_NAME)
        userDto.setUsername(USER_1_USERNAME)
        userDto.setEmail(USER_1_EMAIL)
        def enrollmentDto = createEnrollmentDto(activityDto, userDto, IN_ONE_DAY, "motivation to max")
        when: "create enrollment"
        enrollmentService.createEnrollment(volunteer.getId(), null, enrollmentDto)

        then: "error is thrown"
        enrollmentRepository.count() == 0
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.ACTIVITY_NOT_FOUND
    }

    def "create enrollment with wrong activity id"(){
        given: "enrollment info"
        def volunteer = new Volunteer(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, AuthUser.Type.NORMAL, User.State.SUBMITTED)
        userRepository.save(volunteer)
        def userDto = new UserDto()
        userDto.setName(USER_1_NAME)
        userDto.setUsername(USER_1_USERNAME)
        userDto.setEmail(USER_1_EMAIL)
        def enrollmentDto = createEnrollmentDto(activityDto, userDto, IN_ONE_DAY, "motivation to max")
        when: "error is thrown"
        enrollmentService.createEnrollment(volunteer.getId(), 222,enrollmentDto)

        then: "enrollment is created"
        enrollmentRepository.count() == 0
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.ACTIVITY_NOT_FOUND

    }

    def "create enrollment with wrong volunteer id"(){
        given: "enrollment info"
        def volunteer = new Volunteer(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, AuthUser.Type.NORMAL, User.State.SUBMITTED)
        userRepository.save(volunteer)
        def userDto = new UserDto()
        userDto.setName(USER_1_NAME)
        userDto.setUsername(USER_1_USERNAME)
        userDto.setEmail(USER_1_EMAIL)
        def enrollmentDto = createEnrollmentDto(activityDto, userDto, IN_ONE_DAY, "motivation to max")
        when: "create enrollment"
        enrollmentService.createEnrollment(222, activity.getId(),enrollmentDto)

        then: "enrollment is created"
        enrollmentRepository.count() == 0
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.USER_NOT_FOUND
    }
    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}

