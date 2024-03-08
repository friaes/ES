package pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.domain.Assessment;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler;

import java.time.LocalDateTime;

import static pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler.toISOString;

public class AssessmentDto {

    private Integer id;
    private String review;
    private String reviewDate;

    public AssessmentDto() {}

    public AssessmentDto(Assessment a) {
        setReview(a.getReview());
        setReviewDate(toISOString(a.getReviewDate()));
        setId(a.getId());
    }

    public String getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(String reviewDate) {
        this.reviewDate = reviewDate;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
