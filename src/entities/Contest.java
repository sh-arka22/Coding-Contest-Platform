package entities;

import java.util.List;

public class Contest {
    private final String title;
    private final Level level;
    private final User createdBy;
    private final List<Question> questions;
    private ContestStatus contestStatus;
    private final Long id;

    public Contest(String title, Level level, User createdBy, List<Question> questions, Long id) {
        this.title = title;
        this.level = level;
        this.createdBy = createdBy;
        validateQuestions(questions,level);
        this.questions = questions;
        this.contestStatus = ContestStatus.NOT_STARTED;
        this.id = id;
    }

    public Contest(String title, Level level, User createdBy, List<Question> questions) {
        this(title, level, createdBy, questions,null);
    }
    public Contest(User createdBy, Contest contest){
        this(contest.title, contest.level, createdBy, contest.questions);
    }

    private void validateQuestions(List<Question> questions, Level level) {
        for(Question question: questions){
            if(!question.getLevel().equals(level))
                throw new RuntimeException();
        }
    }

    public String getTitle() {
        return title;
    }

    public Level getLevel() {
        return level;
    }

    public User getCreator() {
        return createdBy;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public ContestStatus getContestStatus() {
        return contestStatus;
    }

    public Long getId() {
        return id;
    }

    public void endContest(){
        this.contestStatus = ContestStatus.ENDED;
    }

    @Override
    public String toString() {
        return "Contest [id=" + id + "]";
    }
}
