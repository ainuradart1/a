package kittyassistant.dto;

public class StudyNoteRequest {
    private Long subjectId;
    private String content;

    public Long getSubjectId() { return subjectId; }
    public void setSubjectId(Long subjectId) { this.subjectId = subjectId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}