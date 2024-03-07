package pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.dto.ParticipationDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.dto.RegisterUserDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.dto.UserDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException

import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Member;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.dto.ActivityDto

import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain.Theme
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.dto.ThemeDto



import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler
import spock.lang.Unroll

import java.time.LocalDateTime

@DataJpaTest 
class CreateParticipationServiceTest extends SpockTest {
    public static final String EXIST = "exist"
    public static final String NO_EXIST = "noExist"

    def member
    def theme
    def activity


    def setup() {
        member = authUserService.loginDemoMemberAuth().getUser()

        def institution = institutionService.getDemoInstitution()
    
        theme = new Theme(THEME_NAME_1, Theme.State.APPROVED,null)
        themeRepository.save(theme)
        def themesDto = new ArrayList<>()
        themesDto.add(new ThemeDto(theme,false,false,false))

        def activityDto = createActivityDto(ACTIVITY_NAME_2,ACTIVITY_REGION_1,2,ACTIVITY_DESCRIPTION_1,
                ONE_DAY_AGO,IN_ONE_DAY,IN_THREE_DAYS,themesDto)  
        activity = activityService.registerActivity(member.getId(), activityDto)

    }
    
    def "create participation"() {
        given:
        def volunteer = new Volunteer(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, AuthUser.Type.NORMAL, User.State.SUBMITTED)
        userRepository.save(volunteer)
        def userDto = new UserDto()
        userDto.setName(USER_1_NAME)
        userDto.setUsername(USER_1_USERNAME)
        userDto.setEmail(USER_1_EMAIL)

        def participationDto = createParticipationDto(volunteer.getId(), 1, NOW)

        when:
        def result = participationService.createParticipation(activity.getId(), participationDto)

        then: "the returned data is correct"
        result.rating == 1
        result.acceptanceDate == DateHandler.toISOString(NOW)
        result.volunteer.id == volunteer.id
        result.activity.id == activity.id
    
        and: "the participation is saved in the database"
        participationRepository.findAll().size() == 1
        and: "the stored data is correct"
        def storedParticipation = participationRepository.findById(result.id).get()
        storedParticipation.rating == 1
        storedParticipation.acceptanceDate == DateHandler.toISOString(NOW)
        storedParticipation.volunteer.id == volunteer.id 
        storedParticipation == activity.id
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