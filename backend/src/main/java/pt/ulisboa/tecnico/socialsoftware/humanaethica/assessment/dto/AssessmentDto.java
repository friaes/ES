package pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.dto;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.domain.Assessment;

import java.time.LocalDateTime;

public class AssessmentDto {

    private Integer id;
    private String review;
    private LocalDateTime reviewDate;

    public AssessmentDto() {}

    public AssessmentDto(Assessment a) {
        setReview(a.getReview());
        setReviewDate(a.getReviewDate());
        setId(a.getId());
    }

    public LocalDateTime getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(LocalDateTime reviewDate) {
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
