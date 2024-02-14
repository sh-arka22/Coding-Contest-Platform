package services;

import entities.*;
import repositories.IContestRepository;
import repositories.IContestantRepository;
import repositories.IQuestionRepository;
import repositories.IUserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class ContestService {
    private final IContestantRepository contestantRepository;
    private final IUserRepository userRepository;
    private final IQuestionRepository questionRepository;
    private final IContestRepository contestRepository;

    public ContestService(IContestantRepository contestantRepository, IUserRepository userRepository, IQuestionRepository questionRepository, IContestRepository contestRepository) {
        this.contestantRepository = contestantRepository;
        this.userRepository = userRepository;
        this.questionRepository = questionRepository;
        this.contestRepository = contestRepository;
    }

    //create contest
    public Contest createContest(String title, Level level, String createdBy, Integer numQuestions){
        //check if user exist
        User creator = userRepository.findByName(createdBy).orElseThrow(
                () -> new RuntimeException("User with creator name: " + createdBy + " not found!"));

        //get total question
        Integer totalQuestionsCount = questionRepository.count();

        if (totalQuestionsCount <= numQuestions) {
            throw new RuntimeException(
                    "Requested Number of questions: " + numQuestions + " cannot be fulfilled!");
        }

        // Get questions with specified level from the repository.
        List<Question> questionList = questionRepository.findAllQuestionLevelWise(level);

        // Pick up random requested number of questions from the question list fetch from the repository.
        List<Question> randomList = pickRandomQuestions(questionList, numQuestions);

        //create new contest object
        Contest contest = new Contest(title, level, creator, randomList);

        //save on repository
        Contest savedContest = contestRepository.save(contest);

        //Note the creator is also attending the contest is assumed
        createContestant(savedContest.getId(), createdBy);

        return savedContest;
    }

    //list of all the contest
    public List<Contest> getContests(Level level) {
        if(level == null){
            return contestRepository.findAll();
        }
        else{
            return contestRepository.findAllContestLevelWise(level);
        }
    }

    //registering the user who will attend the contest with contestId
    public Contestant createContestant(Long contestId, String userName) throws RuntimeException{
        // Retrieve the contest from the repository.
        Contest contest = contestRepository.findById(contestId).orElseThrow(() -> new RuntimeException("Contest with id: " + contestId + " not found!"));
        // Retrieve the user from the repository.
        User user = userRepository.findByName(userName).orElseThrow(() -> new RuntimeException("User with name: " + userName + " not found!"));
        validateContest(contest, userName);
        // Create a new contestant using the contest, user, and current time as start time.
        Contestant contestant = new Contestant(user, contest);
        // Save the contestant in the repository and return it.
        return contestantRepository.save(contestant);
    }

    //withdrawing a user from a contest
    public String deleteContestant(Long contestId, String userName) {
        // Validate if the user is registered in the contest already.
        Contest contest = contestRepository.findById(contestId).orElseThrow(() -> new RuntimeException("CONTEST-ID is not valid"));
        User user = userRepository.findByName(userName).orElseThrow(() -> new RuntimeException("UserName is not valid"));
        Contestant contestant = contestantRepository.find(contestId, userName)
                .orElseThrow(() -> new RuntimeException(
                        "Contestant with " + contestId + " and " + userName + " not found!"));
        ContestStatus contestStatus = contestant.getContest().getContestStatus();
        // Check if contest is valid as per the required conditions.
        if (ContestStatus.IN_PROGRESS.equals(contestStatus)) {
            throw new RuntimeException("Contest has already started!");
        }
        if (ContestStatus.ENDED.equals(contestStatus)) {
            throw new RuntimeException("Contest has already ended!");
        }
        // Delete the contestant from the repository.
        contestantRepository.delete(contestant);
        return "Contestant with name " + userName + " for contest " + contestId + " deleted!";
    }

    //only the creator can start the contest
    public List<Contestant> runContest(Long contestId, String createdBy) {
        // Check if contest is valid as per the required conditions.
        Contest contest = contestRepository.findById(contestId).orElseThrow(() -> new RuntimeException("Contest: " + contestId + " not found!"));
        ContestStatus contestStatus = contest.getContestStatus();
        if (ContestStatus.IN_PROGRESS.equals(contestStatus)) {
            throw new RuntimeException("Contest has already started!");
        }
        if (ContestStatus.ENDED.equals(contestStatus)) {
            throw new RuntimeException("Contest has already ended!");
        }
        if (!contest.getCreator().getName().equals(createdBy)) {
            throw new RuntimeException("User " + createdBy + " is not a valid creator for contest "
                    + contestId + " !");
        }

        Level contestLevel = contest.getLevel();

        // Get all the contestants who registered for the given contest.
        List<Contestant> contestantList = contestantRepository.findAllByContestId(contestId);
        List<Question> contestQuestionList = contest.getQuestions();
        int contestQuestionListSize = contestQuestionList.size();
        // For each contestant,
        contestantList.forEach((contestant) -> {
            // Select random questions from the contest which will be considered as solved by the
            // contestant.
            List<Question> solvedQuestionsList = pickRandomQuestions(contestQuestionList,
                    getRandomNumberInRange(0, contestQuestionListSize));
            // Store the solved questions in the contestant.
            solvedQuestionsList.forEach((question) -> {
                contestant.addQuestion(question);
            });
            User user = contestant.getUser();
            // Generate new totalScore for the user as per the recently solved questions.
            int newScore = contestant.getCurrentContestPoints() + user.getTotalScore()
                    - contestLevel.getWeight();
            // Update the total score of the user.
            user.modifyScore(newScore);
        });
        // End the contest once scores are updated for all the users.
        contest.endContest();
        // Return the list of contestants in descending order( user with highest score is first ) as
        // per their score in the current contest.
        return contestantList.stream()
                .sorted((c1, c2) -> c2.getCurrentContestPoints() - c1.getCurrentContestPoints())
                .collect(Collectors.toList());
    }

    //list of all the participates int that contest
    public List<Contestant> contestHistory(Long contestId) throws RuntimeException{
        // Get the contest using contestId.
        Contest contest = contestRepository.findById(contestId).orElseThrow(() -> new RuntimeException("Contest with id: " + contestId + " not found!"));
        // Check if the contest is completed.
        if(contest.getContestStatus() == ContestStatus.NOT_STARTED){
            throw new RuntimeException("Contest with id: "+ contestId + " is still running!");
        }
        // Get the list of contestants.
        List<Contestant> contestants = contestantRepository.findAllByContestId(contestId);
        contestants.sort((c1, c2) -> c2.getCurrentContestPoints() - (c1.getCurrentContestPoints()));
        return contestants;
    }
    private void validateContest(final Contest contest, final String contestCreator) throws RuntimeException {
        if(contest.getContestStatus().equals(ContestStatus.IN_PROGRESS)){
            throw new RuntimeException("Cannot Run Contest. Contest for given id:"+contest.getId()+" is in progress!");
        }
        if(contest.getContestStatus().equals(ContestStatus.ENDED)){
            throw new RuntimeException("Cannot Run Contest. Contest for given id:"+contest.getId()+ " is ended!");
        }
    }

    private List<Question> pickRandomQuestions(List<Question> questionList, Integer numQuestions) {
        Random rand = new Random(); // object of Random class.
        // temporary list to hold selected items.
        List<Question> tempList = new ArrayList<>();
        for (int i = 0; i < numQuestions; i++) {
            int randomIndex = rand.nextInt(questionList.size());
            // the loop check on repetition of elements
            while (tempList.contains(questionList.get(randomIndex))) {
                randomIndex = rand.nextInt(questionList.size());
            }
            tempList.add(questionList.get(randomIndex));
        }
        return tempList;
    }
    private int getRandomNumberInRange(int min, int max) {
        Random r = new Random();
        return r.ints(min, (max + 1)).findFirst().getAsInt();
    }
}
