package pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.domain;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.dto.EnrollmentDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler
import spock.lang.Unroll

import java.time.LocalDateTime

@DataJpaTest
class CreateEnrollmentMethodTest extends SpockTest {

    //mocks the classes needed for the test
    Activity activity = Mock()
    Activity otherActivity = Mock()
    Volunteer volunteer = Mock()
    Enrollment otherEnrollment = Mock() 
    Enrollment otherEnrollment2 = Mock()
    def enrollmentDto

    def setup() {
       given: "enrollment info"
       enrollmentDto =new EnrollmentDto()
       enrollmentDto.enrollmentDate = DateHandler.toISOString(IN_TWO_DAYS)
       enrollmentDto.motivation = "Set me free pwease"
    }

    //tests the creation of an enrollment with volunteer with no enrollments
    def "create enrollment with valid data"() {
        given: "activity and volunteer"
        activity.getApplicationDeadline() >> IN_TWO_DAYS
        volunteer.getEnrollments(activity) >> []

        when: "create enrollment"
        Enrollment enrollment = new Enrollment(activity, volunteer, enrollmentDto)

        then: "enrollment is created"
        enrollment.activity == activity
        enrollment.volunteer == volunteer
        enrollment.enrollmentDate == IN_TWO_DAYS
        enrollment.motivation == "Set me free pwease"
    }

    //tests the creation of an enrollment with creation date after the application deadline
    def "create enrollment with enrollment date after application deadline"() {
        given: "activity and volunteer"
        activity.getApplicationDeadline() >> IN_ONE_DAY
        volunteer.getEnrollments(activity) >> []

        when: "create enrollment"
        new Enrollment(activity, volunteer, enrollmentDto)

        then: "exception is thrown"
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.ENROLLMENT_DATE_AFTER_ACTIVITY_DATE
    }

    //tests the creation of an enrollment with volunteer with an enrollment for the same activity
    def "create enrollment with volunteer with an enrollment for the same activity"() {
        given: "activity and volunteer"
        activity.getApplicationDeadline() >> IN_TWO_DAYS
        otherActivity.getApplicationDeadline() >> IN_TWO_DAYS
        volunteer.getEnrollments() >> [otherEnrollment, otherEnrollment2]

        and: "other enrollment is for the same activity"
        otherEnrollment.getActivity() >> otherActivity
        otherEnrollment2.getActivity() >> activity

        when: "create enrollment"
        new Enrollment(activity, volunteer, enrollmentDto)

        then: "exception is thrown"
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.VOLUNTERR_ALREADY_ENROLLED_IN_ACTIVITY
    }
    

    //test the motivation minimum length
    def "create enrollment with motivation with less than 10 characters"() {
        given: "activity and volunteer"
        activity.getApplicationDeadline() >> IN_TWO_DAYS
        volunteer.getEnrollments(activity) >> []

        and: "motivation with less than 10 characters"
        enrollmentDto.motivation = "123456789"

        when: "create enrollment"
        new Enrollment(activity, volunteer, enrollmentDto)

        then: "exception is thrown"
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.MINIMUM_MOTIVATION_LENGTH
    }

    //test enrollmentDto getId
    def "create enrollment with null id for volunteer"(){
        given: "enrollmentDto"
        enrollmentDto.setId(567)
        
        when: "getId"
        def id = enrollmentDto.getId()
        
        then: "id is 567"
        id == 567
    }

    //test toString do enrollmentDto
    def "create enrollment with null id for volunteer"(){
        given: "enrollmentDto"
        enrollmentDto.setId(567)
        enrollmentDto.setMotivation("set me free")
        enrollmentDto.setEnrollmentDate(null)
        
        when: "toString"
        def string = enrollmentDto.toString()
        
        then: "string is correct"
        string == "EnrollmentDto{id=567, motivation='set me free', enrollmentDate='null', volunteer=null, activity=null}"
    }
    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}