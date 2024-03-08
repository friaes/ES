package pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.webservice

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.http.HttpStatus
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.dto.AssessmentDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.dto.ThemeDto

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CreateAssessmentWebServiceTest extends SpockTest {
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

   def "valid creation"() {
       demoVolunteerLogin()

       def assessmentDto = new AssessmentDto()
       assessmentDto.setReview("1234567890")

       when:
       AssessmentDto response = webClient.post()
               .uri('/assessment/register/' + institution.getId())
               .headers(httpHeaders -> httpHeaders.putAll(headers))
               .bodyValue(assessmentDto)
               .retrieve()
               .bodyToMono(AssessmentDto.class)
               .block()

       then:
       response.getReview().equals(assessmentDto.getReview())

       cleanup:
       deleteAll()
   }

    def "as non volunteer"() {
        demoMemberLogin()

        when:
        def response = webClient.post()
                .uri('/assessment/register/' + institution.getId())
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(new AssessmentDto())
                .retrieve()
                .bodyToMono(AssessmentDto.class)
                .block()

        then:
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.FORBIDDEN

        cleanup:
        deleteAll()
    }
}