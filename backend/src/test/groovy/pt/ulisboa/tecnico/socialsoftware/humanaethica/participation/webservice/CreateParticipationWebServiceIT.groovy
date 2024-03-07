package pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.webservice

import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.http.HttpStatus
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.dto.ActivityDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain.Theme
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.dto.ThemeDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class createParticipationWebServiceIT extends SpockTest {
    @LocalServerPort
    private int port

    def participationDto

    def setup() {
        deleteAll()

        webClient = WebClient.create("http://localhost:" + port)
        headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)

        

        //participationDto = createParticipationDto(volunteer.getId(), 1, NOW)
    }
/**
    def "login as member, and create an activity"() {
        given:
        demoMemberLogin()

        when:
        def response = webClient.post()
                .uri('/activities')
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(activityDto)
                .retrieve()
                .bodyToMono(ActivityDto.class)
                .block()

        then: "check response data"
        response.name == ACTIVITY_NAME_1
        response.region == ACTIVITY_REGION_1
        response.participantsNumberLimit == 2
        response.description == ACTIVITY_DESCRIPTION_1
        response.startingDate == DateHandler.toISOString(IN_TWO_DAYS)
        response.endingDate == DateHandler.toISOString(IN_THREE_DAYS)
        response.applicationDeadline == DateHandler.toISOString(IN_ONE_DAY)
        response.themes.get(0).getName() == THEME_NAME_1
        and: 'check database data'
        activityRepository.count() == 1
        def activity = activityRepository.findAll().get(0)
        activity.getName() == ACTIVITY_NAME_1
        activity.getRegion() == ACTIVITY_REGION_1
        activity.getParticipantsNumberLimit() == 2
        activity.getDescription() == ACTIVITY_DESCRIPTION_1
        activity.getStartingDate().withNano(0) == IN_TWO_DAYS.withNano(0)
        activity.getEndingDate().withNano(0) == IN_THREE_DAYS.withNano(0)
        activity.getApplicationDeadline().withNano(0) == IN_ONE_DAY.withNano(0)
        activity.themes.get(0).getName() == THEME_NAME_1

        cleanup:
        deleteAll()
    }

    def "login as member, and create an activity with error"() {
        given: 'a member'
        demoMemberLogin()
        and: 'a name with blanks'
        activityDto.name = "  "

        when: 'the member registers the activity'
        webClient.post()
                .uri('/activities')
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(activityDto)
                .retrieve()
                .bodyToMono(ActivityDto.class)
                .block()

        then: "check response status"
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.BAD_REQUEST
        activityRepository.count() == 0

        cleanup:
        deleteAll()
    }

    def "login as volunteer, and create an activity"() {
        given: 'a volunteer'
        demoVolunteerLogin()

        when: 'the volunteer registers the activity'
        webClient.post()
                .uri('/activities')
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(activityDto)
                .retrieve()
                .bodyToMono(ActivityDto.class)
                .block()

        then: "an error is returned"
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.FORBIDDEN
        activityRepository.count() == 0

        cleanup:
        deleteAll()
    }

    def "login as admin, and create an activity"() {
        given: 'a demo'
        demoAdminLogin()

        when: 'the admin registers the activity'
        webClient.post()
                .uri('/activities')
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(activityDto)
                .retrieve()
                .bodyToMono(ActivityDto.class)
                .block()

        then: "an error is returned"
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.FORBIDDEN
        activityRepository.count() == 0

        cleanup:
        deleteAll()
    }
**/
}
