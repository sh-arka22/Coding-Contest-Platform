package repositories;

import entities.Level;
import entities.Question;

import java.util.List;
import java.util.Optional;

public interface IQuestionRepository {
    Question save(Question question);
    List<Question> findAll();
    Optional<Question> findById(Long id);
    List<Question> findAllQuestionLevelWise(Level level);
    Integer count();
}
