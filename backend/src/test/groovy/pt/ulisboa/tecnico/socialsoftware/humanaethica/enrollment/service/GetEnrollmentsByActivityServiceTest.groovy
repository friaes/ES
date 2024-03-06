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
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.EnrollmentService
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException
import spock.lang.Unroll

@DataJpaTest
class GetEnrollmentsByActivityTest extends SpockTest {

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
    }

    def "get enrollments by activity, with no enrollments"() {
        when:
        activityRepository.save(activity)
        def result = enrollmentService.getEnrollmentsByActivity(activity.getId())

        then:"the returned data is correct"
        result==[]
    }

    def "get enrollments by activity, with one enrollment"() {
        given: "an enrollment"
        activityRepository.save(activity)
        def volunteer = new Volunteer(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, AuthUser.Type.NORMAL, User.State.SUBMITTED)
        userRepository.save(volunteer)
        def userDto = new UserDto()
        userDto.setName(USER_1_NAME)
        userDto.setUsername(USER_1_USERNAME)
        userDto.setEmail(USER_1_EMAIL)
        def enrollmentDto = createEnrollmentDto(activityDto, userDto, IN_ONE_DAY, "motivation to max")
        enrollment = new Enrollment(activity, volunteer, enrollmentDto)
        enrollmentRepository.save(enrollment)

        when:
        def result = enrollmentService.getEnrollmentsByActivity(activity.getId())

        then:"the returned data is correct" 
        result.size()==1
        result[0].getMotivation()=="motivation to max"
        result[0].getVolunteer().getId()==volunteer.getId()
        result[0].getActivity().getId()==activity.getId()
    }

    def "get enrollments by activity, when activity isn't present on the database"() {
        given:
        activityRepository.save(activity)
        def volunteer = new Volunteer(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, AuthUser.Type.NORMAL, User.State.SUBMITTED)
        userRepository.save(volunteer)
        def userDto = new UserDto()
        userDto.setName(USER_1_NAME)
        userDto.setUsername(USER_1_USERNAME)
        userDto.setEmail(USER_1_EMAIL)
        def enrollmentDto = createEnrollmentDto(activityDto, userDto, IN_ONE_DAY, "motivation to max")
        enrollment = new Enrollment(activity, volunteer, enrollmentDto)
        enrollmentRepository.save(enrollment)

        when:
        enrollmentService.getEnrollmentsByActivity(222)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.ACTIVITY_NOT_FOUND
    }

    def "get enrollment by activity, when recieves null"(){
        given:
        activityRepository.save(activity)
        def volunteer = new Volunteer(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, AuthUser.Type.NORMAL, User.State.SUBMITTED)
        userRepository.save(volunteer)
        def userDto = new UserDto()
        userDto.setName(USER_1_NAME)
        userDto.setUsername(USER_1_USERNAME)
        userDto.setEmail(USER_1_EMAIL)
        def enrollmentDto = createEnrollmentDto(activityDto, userDto, IN_ONE_DAY, "motivation to max")
        enrollment = new Enrollment(activity, volunteer, enrollmentDto)
        enrollmentRepository.save(enrollment)

        when:
        enrollmentService.getEnrollmentsByActivity(null)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.ACTIVITY_NOT_FOUND
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}