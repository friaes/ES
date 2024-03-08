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
class GetParticipationsByActivityTest extends SpockTest {

    def activity
    def activityDto
    def institution
    def volunteer

    def setup() {
        given: "activity info"
        def activityDto = createActivityDto(ACTIVITY_NAME_1,ACTIVITY_REGION_1,1,ACTIVITY_DESCRIPTION_1,
                                            NOW,IN_TWO_DAYS,IN_THREE_DAYS,null)
        and: "a volunteer"
        volunteer = authUserService.loginDemoVolunteerAuth().getUser()
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

    def "get participations by activity, with just one participation"() {
        given: "an participation"
        def participationDto = createParticipationDto(volunteer.getId(), IN_ONE_DAY, 1)
        participationService.createParticipation(activity.getId(),participationDto)

        when:
        def result = participationService.getParticipationsByActivity(activity.getId())

        then:"the returned data is correct" 
        result.size()==1
        result[0].getRating()== 1
        result[0].getVolunteerId()==volunteer.getId()
        result[0].getActivityId()==activity.getId()
    }

    @Unroll
    def "get participations by activity if the activity doesn't exist"() {
        given: "an participation"
        def participationDto = createParticipationDto(volunteer.getId(), IN_ONE_DAY, 1)
        participationService.createParticipation(activity.getId(),participationDto)

        when:
        participationService.getParticipationsByActivity(-1)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.ACTIVITY_NOT_FOUND
    }

    @Unroll
    def "get participation by activity if Id passed id null"(){
        given: "an participation"
        def participationDto = createParticipationDto(volunteer.getId(), IN_ONE_DAY, 1)
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
