package repositories;

import entities.Level;
import entities.Question;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class QuestionRepository implements IQuestionRepository{
    private final HashMap<Long, Question> questionHashMap;
    private Long autoIncrement = 1L;
    public QuestionRepository() {

        questionHashMap = new HashMap<>();
    }

    @Override
    public Question save(Question question) {
        Question savedQuestion = new Question(question.getTitle(),question.getLevel(),question.getScore(),autoIncrement);
        questionHashMap.put(autoIncrement, savedQuestion);
        ++autoIncrement;
        return savedQuestion;
    }

    @Override
    public List<Question> findAll() {
        return new ArrayList<>(questionHashMap.values());
    }

    @Override
    public Optional<Question> findById(Long id) {

        return Optional.ofNullable(questionHashMap.get(id));
    }

    @Override
    public List<Question> findAllQuestionLevelWise(Level level) {
        return questionHashMap.values().stream().filter(q -> q.getLevel() == level).collect(Collectors.toList());
    }

    @Override
    public Integer count() {
        return questionHashMap.size();
    }
}
