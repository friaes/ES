package pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.dto;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.domain.Participation;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.dto.UserDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.dto.ActivityDto;

public class ParticipationDto {
    
    private Integer id;
    private Integer rating;
    private String acceptanceDate;
    private UserDto volunteer;
    private ActivityDto activity;

    public ParticipationDto() {
    }

    public ParticipationDto(Participation participation) {
        setId(participation.getId());
        setRating(participation.getRating());
        setAcceptanceDate(DateHandler.toISOString(participation.getAcceptanceDate()));
        setVolunteer(new UserDto(participation.getVolunteer()));
        setActivity(new ActivityDto(participation.getActivity(), false));
    }


    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getRating() {
        return this.rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getAcceptanceDate() {
        return this.acceptanceDate;
    }

    public void setAcceptanceDate(String acceptanceDate) {
        this.acceptanceDate = acceptanceDate;
    }

    public UserDto getVolunteer() {
        return this.volunteer;
    }

    public void setVolunteer(UserDto volunteer) {
        this.volunteer = volunteer;
    }

    public ActivityDto getActivity() {
        return this.activity;
    }

    public void setActivity(ActivityDto activity) {
        this.activity = activity;
    }


    @Override
    public String toString() {
        return "{" +
            " id='" + getId() + "'" +
            ", rating='" + getRating() + "'" +
            ", acceptanceDate='" + getAcceptanceDate() + "'" +
            ", volunteer='" + getVolunteer() + "'" +
            ", activity='" + getActivity() + "'" +
            "}";
    }


}
