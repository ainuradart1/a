package kittyassistant.dto;

public class StudySessionRequest {
    private String goalText;
    private Long subjectId;

    public String getGoalText() { return goalText; }
    public void setGoalText(String goalText) { this.goalText = goalText; }

    public Long getSubjectId() { return subjectId; }
    public void setSubjectId(Long subjectId) { this.subjectId = subjectId; }
}