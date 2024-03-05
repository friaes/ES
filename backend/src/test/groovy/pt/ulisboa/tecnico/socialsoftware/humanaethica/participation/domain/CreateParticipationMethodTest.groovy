package pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.domain

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.dto.ParticipationDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException

import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Member;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler
import spock.lang.Unroll

import java.time.LocalDateTime

@DataJpaTest
class CreateParticipationMethodTest extends SpockTest {
    Volunteer volunteer = Mock()
    Activity activity = Mock()
    Participation otherParticipation = Mock()
    def participationDto

    def setup() {
        given: "participation info"
        participationDto = new ParticipationDto()
        participationDto.rating = 1
        participationDto.acceptanceDate = DateHandler.toISOString(IN_ONE_DAY)
    }

    @Unroll
    def "create participation and violate limit number of participantions"() {
        given:
        activity.getParticipantsNumberLimit() >> 1
        activity.getParticipations() >> [otherParticipation]

        and: "a participation dto"
        participationDto = new ParticipationDto()
        participationDto.setRating(1)
        participationDto.setAcceptanceDate(DateHandler.toISOString(IN_ONE_DAY))

        when:
        new Participation(activity, volunteer, participationDto)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.PARTICIPATION_EXCEEDS_ACTIVITY_LIMIT
    }

    @Unroll
    def "create participation and violate duplicated participation"() {
        given:
        otherParticipation.getActivity() >> activity
        otherParticipation.getVolunteer() >> volunteer
        activity.getParticipantsNumberLimit() >> 2
        activity.getParticipations() >> [otherParticipation]
        volunteer.getParticipations() >> [otherParticipation]

        and: "a participation dto"
        participationDto = new ParticipationDto()
        participationDto.setRating(1)
        participationDto.setAcceptanceDate(DateHandler.toISOString(IN_ONE_DAY))

        when:
        new Participation(activity, volunteer, participationDto)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.PARTICIPATION_DUPLICATE
    }

    @Unroll
    def "create participation violate date precedence invariants: acceptance=#acceptance"() {
        given:
        activity.getParticipantsNumberLimit() >> 2
        activity.getApplicationDeadline() >> NOW
        
        and: "a participation dto"
        participationDto = new ParticipationDto()
        participationDto.setRating(1)
        participationDto.setAcceptanceDate(acceptance instanceof LocalDateTime ? DateHandler.toISOString(acceptance) : acceptance as String)
        
        when:
        new Participation(activity, volunteer, participationDto)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == errorMessage

        where:
        acceptance  || errorMessage
        NOW         || ErrorMessage.PARTICIPATION_BEFORE_APPLICATION_END
        ONE_DAY_AGO || ErrorMessage.PARTICIPATION_BEFORE_APPLICATION_END
        
     }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}