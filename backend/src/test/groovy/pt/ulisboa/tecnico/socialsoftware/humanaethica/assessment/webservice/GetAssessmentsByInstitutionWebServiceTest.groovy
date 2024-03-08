package pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.webservice

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
import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.dto.AssessmentDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.dto.InstitutionDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain.Theme
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.dto.ThemeDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.dto.UserDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.repository.UserRepository
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User

import java.time.LocalDateTime
import java.util.List
import java.util.ArrayList

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.databind.SerializationFeature

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GetAssessmentsByInstitutionWebServiceTest extends SpockTest {
    @LocalServerPort
    private int port

    def institution

    def setup() {
        deleteAll()

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        webClient = WebClient.create("http://localhost:" + port)
        headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)

        def user = demoMemberLogin()

        institution = institutionService.getDemoInstitution()

        def activityDto = createActivityDto(ACTIVITY_NAME_1,
                ACTIVITY_REGION_1,
                2,
                ACTIVITY_DESCRIPTION_1,
                IN_ONE_DAY,
                IN_TWO_DAYS,IN_THREE_DAYS,
                new ArrayList<ThemeDto>()
        )

        def activity = new Activity(activityDto, institution, new ArrayList<>())

        activity = activityService.registerActivity(user.getId(), activityDto)
    }

    def "get assessments by institutionId, no assessments"() {
        demoVolunteerLogin()

        when:
        List<AssessmentDto> response = webClient.get()
                .uri('/assessment/' + institution.getId())
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .retrieve()
                .bodyToMono(List<AssessmentDto> .class)
                .block()

        then:
        response.size() == 0

        cleanup:
        deleteAll()
    }

    def "get assessments by institutionId, a assessments"() {
        def volunteer = demoVolunteerLogin()

        def assessmentDto = new AssessmentDto()
        assessmentDto.setReview("1234567890")
        assessmentDto.setReviewDate(DateHandler.toISOString(DateHandler.now()))

        def result = assessmentService.createAssessment(volunteer.getId(), institution.getId(), assessmentDto)

        when:
        List<AssessmentDto> response = response = webClient.get()
                .uri('/assessment/' + institution.getId())
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .retrieve()
                .bodyToMono(List<AssessmentDto> .class)
                .block()

        then:
        response.size() == 1
        AssessmentDto e1 = response.get(0)
        e1.getReview().equals("1234567890")

        cleanup:
        deleteAll()
    }
}