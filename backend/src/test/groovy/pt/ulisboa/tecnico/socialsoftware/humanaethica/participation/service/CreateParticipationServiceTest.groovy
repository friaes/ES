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
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.dto.ParticipationDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.InstitutionService
import spock.lang.Unroll


@DataJpaTest 
class CreateParticipationServiceTest extends SpockTest {
    public static final String EXIST = "exist"
    public static final String NO_EXIST = "noExist"

    def member
    def activity
    def activityDto
    def participation
    def institution


    def setup() {
        member = authUserService.loginDemoMemberAuth().getUser()
        
        institution = institutionService.getDemoInstitution()
        given: "activity info"
        activityDto = createActivityDto(ACTIVITY_NAME_1,ACTIVITY_REGION_1,1,ACTIVITY_DESCRIPTION_1,
                                            IN_ONE_DAY,IN_TWO_DAYS,IN_THREE_DAYS,null)
        and: "a theme   "
        def themes = new ArrayList<>()
        themes.add(createTheme(THEME_NAME_1,Theme.State.APPROVED,null))
        and: "an activity"
        activity = new Activity(activityDto, institution, themes)
        activityRepository.save(activity)

        

    }
    
    def "create participation"() {
        given: "participation info"
        def volunteer = new Volunteer(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, AuthUser.Type.NORMAL, User.State.SUBMITTED)
        userRepository.save(volunteer)
        def userDto = new UserDto()
        userDto.setName(USER_1_NAME)
        userDto.setUsername(USER_1_USERNAME)
        userDto.setEmail(USER_1_EMAIL)
        def participationDto = createParticipationDto(volunteer.getId(), NOW, 1)
        when: "create participation"
        def result = participationService.createParticipation(activity.getId(),participationDto)

        then: "participation is created"
        result != null
        result.getRating() == 1
        result.getVolunteer().getId() == volunteer.getId()
        result.getActivity().getId() == activity.getId()
    }

    /**
    @Unroll
    def 'invalid arguments: volunteerId=#volunteerId | activityId=#activityId'() {
        given: "an participation dto"
        def participationDto = createParticipationDto(getVolunteerId(volunteerId), 1, NOW)

        when:
        participationService.createParticipation(getActivityId(activityId), participationDto)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == errorMessage
        and: "no participation is stored in the database"
        participationRepository.findAll().size() == 0

        where:
        volunteerId | activityId || errorMessage
        null        | EXIST      || ErrorMessage.USER_NOT_FOUND
        NO_EXIST    | EXIST      || ErrorMessage.USER_NOT_FOUND
        EXIST       | null       || ErrorMessage.ACTIVITY_NOT_FOUND
        EXIST       | NO_EXIST   || ErrorMessage.ACTIVITY_NOT_FOUND
    }

    def getVolunteerId(volunteerId){
        if (volunteerId == EXIST)
            return volunteer.id
        else if (volunteerId == NO_EXIST)
            return 222
        return null
    }

    def getActivityId(activityId){
        if (activityId == EXIST)
            return activity.id
        else if (activityId == NO_EXIST)
            return 222
        return null
    }
    **/
    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}