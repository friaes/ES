package pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.domain.Participation
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.ParticipationService
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
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.ParticipationService
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException
import spock.lang.Unroll

@DataJpaTest
class GetParticipationsByActivityServiceTest extends SpockTest {

    def activity
    def activityDto
    def institution
    def volunteer1
    def volunteer2

    def setup() {
        given: "activity info"
        def activityDto = createActivityDto(ACTIVITY_NAME_1,ACTIVITY_REGION_1,2,ACTIVITY_DESCRIPTION_1,
                                            NOW,IN_TWO_DAYS,IN_THREE_DAYS,null)
        and: "two volunteers"
        volunteer1 = authUserService.loginDemoVolunteerAuth().getUser()
        
        volunteer2 = new Volunteer(USER_2_NAME, USER_2_USERNAME, USER_2_EMAIL, AuthUser.Type.NORMAL, User.State.SUBMITTED)
        userRepository.save(volunteer2)
        ((AuthNormalUser) volunteer2.authUser).setActive(true)
        
        and: "a instituition"
        def institution = institutionService.getDemoInstitution()
        and: "a theme"
        def themes = new ArrayList<>()
        themes.add(createTheme(THEME_NAME_1,Theme.State.APPROVED,null))
        and: "an activity"
        activity = new Activity(activityDto, institution, themes)
        activityRepository.save(activity)
    }

    def "get participations by activity, with no participations"() {
        when:
        activityRepository.save(activity)
        def result = participationService.getParticipationsByActivity(activity.getId())

        then:"the returned data is correct"
        result==[]
    }

    def "get participations by activity, with two participations"() {
        given: "two participations"
        def participationDto1 = createParticipationDto(volunteer1.getId(), IN_ONE_DAY, 1)
        participationService.createParticipation(activity.getId(),participationDto1)
        def participationDto2 = createParticipationDto(volunteer2.getId(), IN_ONE_DAY, 5)
        participationService.createParticipation(activity.getId(),participationDto2)


        when:
        def result = participationService.getParticipationsByActivity(activity.getId())

        then:"the returned data is correct" 
        result.size()==2
        result[0].getRating()==1
        result[0].getVolunteerId()==volunteer1.getId()
        result[0].getActivityId()==activity.getId()

        result[1].getRating()==5
        result[1].getVolunteerId()==volunteer2.getId()
        result[1].getActivityId()==activity.getId()

    }

    @Unroll
    def "get participations by activity if the activity doesn't exist"() {
        given: "a participation"
        def participationDto = createParticipationDto(volunteer1.getId(), IN_ONE_DAY, 1)
        participationService.createParticipation(activity.getId(),participationDto)

        when:
        participationService.getParticipationsByActivity(-1)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.ACTIVITY_NOT_FOUND
    }

    @Unroll
    def "get participation by activity if Id passed id null"(){
        given: "a participation"
        def participationDto = createParticipationDto(volunteer1.getId(), IN_ONE_DAY, 1)
        participationService.createParticipation(activity.getId(),participationDto)
        
        when:
        participationService.getParticipationsByActivity(null)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.ACTIVITY_NOT_FOUND
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
