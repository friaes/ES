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


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CreateParticipationWebServiceIT extends SpockTest {
    @LocalServerPort
    private int port

    def participationDto
    def activityId
    def volunteerId

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

        participationDto = createParticipationDto(volunteer.getId(), IN_ONE_DAY, 1)
    }

    def "login as member, and create a participation"() {
        given:
        demoMemberLogin()

        when:
        def response = webClient.post()
                .uri('/participations/' + activityId + '/create')
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(participationDto)
                .retrieve()
                .bodyToMono(ParticipationDto.class)
                .block()

        then: "check response data"
        response != null
        response.rating == 1
        response.acceptanceDate == DateHandler.toISOString(IN_ONE_DAY)
        response.getVolunteerId() == volunteerId
        response.getActivityId() == activityId
        and: 'check database data'
        participationRepository.count() == 1
        def participation = participationRepository.findAll().get(0)
        participation != null
        participation.rating == 1
        participation.acceptanceDate.withNano(0) == IN_ONE_DAY.withNano(0)
        participation.getVolunteer().getId() == volunteerId
        participation.getActivity().getId() == activityId
        cleanup:
        deleteAll()
    }

    def "login as member, and create a participation with error"() {
        given: 'a member'
        demoMemberLogin()
        and: 'acceptance not after deadline'
        participationDto.acceptanceDate = DateHandler.toISOString(NOW)

        when:
        def response = webClient.post()
                .uri('/participations/' + activityId + '/create')
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(participationDto)
                .retrieve()
                .bodyToMono(ParticipationDto.class)
                .block()

        then: "check response status"
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.BAD_REQUEST
        participationRepository.count() == 0

        cleanup:
        deleteAll()
    }

    def "login as volunteer, and create a participation"() {
        given: 'a volunteer'
        demoVolunteerLogin()

        when:
        webClient.post()
            .uri('/participations/' + activityId + '/create')
            .headers(httpHeaders -> httpHeaders.putAll(headers))
            .bodyValue(participationDto)
            .retrieve()
            .bodyToMono(ParticipationDto.class)
            .block()

        then: "an error is returned"
        participationRepository.count() == 0
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.FORBIDDEN

        cleanup:
        deleteAll()
    }

    def "login as admin, and create participation"() {
        given: 'a demo'
        demoAdminLogin()

        when:
        webClient.post()
            .uri('/participations/' + activityId + '/create')
            .headers(httpHeaders -> httpHeaders.putAll(headers))
            .bodyValue(participationDto)
            .retrieve()
            .bodyToMono(ParticipationDto.class)
            .block()

        then: "an error is returned"
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.FORBIDDEN
        participationRepository.count() == 0

        cleanup:
        deleteAll()
    }

}