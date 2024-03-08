package pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.webservice

import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.http.HttpStatus
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.dto.ActivityDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain.Theme
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.dto.ThemeDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.domain.Participation
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.dto.ParticipationDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.ParticipationService
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.dto.UserDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.repository.UserRepository
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthNormalUser
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException
import spock.lang.Unroll


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GetParticipationsByActivityWebServiceIT extends SpockTest {
    @LocalServerPort
    private int port

    def participationDto
    def activityId
    def volunteerId
    def participation

    

    def setup() {
        deleteAll()

        webClient = WebClient.create("http://localhost:" + port)
        headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)

        def user = demoMemberLogin()
        def volunteer = authUserService.loginDemoVolunteerAuth().getUser()
        volunteerId = volunteer.id
        
        def theme = createTheme(THEME_NAME_1, Theme.State.APPROVED,null)
        def themesDto = new ArrayList<>()
        themesDto.add(new ThemeDto(theme,false,false,false))

        def activityDto = createActivityDto(ACTIVITY_NAME_1,ACTIVITY_REGION_1,2,ACTIVITY_DESCRIPTION_1,
                NOW,IN_TWO_DAYS,IN_THREE_DAYS,themesDto)

        def activity = activityService.registerActivity(user.id, activityDto)
        
        activityId = activity.id
    }


    def "get activity participations without having any"() {
        given: "login as member"
        demoMemberLogin()

        when: "get activity participations"
        def response = webClient.get()
                .uri('/participations/'+activityId)
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .retrieve()
                .bodyToMono(List<ParticipationDto>.class)
                .block()

        then: "check response"
        response.size() == 0

        cleanup:
        deleteAll()
    }

    def "login as member and get participations by activity"() {
        given:
        demoMemberLogin()

        def participationDto = createParticipationDto(volunteerId, IN_ONE_DAY, 1)
        participationService.createParticipation(activityId,participationDto)

        when:
        def response = webClient.get()
                .uri('/participations/' + activityId)
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(participationDto)
                .retrieve()
                .bodyToMono(List<ParticipationDto>.class)
                .block()


        then: "check response data"
        response.size() == 1
        ParticipationDto firstParticipation = response[0] as ParticipationDto
        firstParticipation.rating == 1
        firstParticipation.getVolunteerId() == volunteerId
        firstParticipation.getActivityId() == activityId
        
        cleanup:
        deleteAll()
    }


    def "login as volunteer and get activity participations by activity"() {
        given: "login as volunteer"
        demoVolunteerLogin()
        def participationDto = createParticipationDto(volunteerId, IN_ONE_DAY, 1)
        participationService.createParticipation(activityId,participationDto)
        when: "get activity participations"
        def response = webClient.get()
                .uri('/participations/'+activityId)
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .retrieve()
                .bodyToMono(List <ParticipationDto> .class)
                .block()

        then: "an error is thrown"
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.FORBIDDEN

        cleanup:
        deleteAll()
    }

}