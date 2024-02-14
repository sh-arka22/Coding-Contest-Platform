package services;

import entities.Level;
import entities.Question;
import repositories.IQuestionRepository;

import java.util.List;

public class QuestionService {
    private final IQuestionRepository questionRepository;

    public QuestionService(IQuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    //create question
    public Question createQuestion(String title, Level level, Integer score){
        Question savedQustion = questionRepository.save(new Question(
                title, level, score
        ));
        return savedQustion;
    }


    //return list Questions
    public List<Question> getQuestions(Level level) {
        if(level == null){
            return questionRepository.findAll();
        }
        return questionRepository.findAllQuestionLevelWise(level);
    }
}
