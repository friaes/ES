package pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.dto;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.domain.Participation;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler;

public class ParticipationDto {
    
    private Integer id;
    private Integer rating;
    private String acceptanceDate;
    private Integer volunteerId;
    private Integer activityId;

    public ParticipationDto() {
    }

    public ParticipationDto(Participation participation) {
        setId(participation.getId());
        setRating(participation.getRating());
        setAcceptanceDate(DateHandler.toISOString(participation.getAcceptanceDate()));
        setVolunteerId(participation.getVolunteer().getId());
        setActivityId(participation.getActivity().getId());
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

    public Integer getVolunteerId() {
        return this.volunteerId;
    }

    public void setVolunteerId(Integer volunteerId) {
        this.volunteerId = volunteerId;
    }

    public Integer getActivityId() {
        return this.activityId;
    }

    public void setActivityId(Integer activityId) {
        this.activityId = activityId;
    }
    

    @Override
    public String toString() {
        return "{" +
            " id='" + getId() + "'" +
            ", rating='" + getRating() + "'" +
            ", acceptanceDate='" + getAcceptanceDate() + "'" +
            ", volunteerId='" + getVolunteerId() + "'" +
            ", activityId='" + getActivityId() + "'" +
            "}";
    }


}
