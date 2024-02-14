import constants.UserOrder;
import entities.*;
import repositories.*;
import services.ContestService;
import services.QuestionService;
import services.UserService;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {

        String filePath = "./src/sample_input/input1.txt";
        List<String> commandLineArgs = new LinkedList<>();
        commandLineArgs.add("filePath=" + filePath);
        run(commandLineArgs);
    }

    public static void run(List<String> commandLineArgs) throws FileNotFoundException {
        //Initialise Repositories
        IUserRepository userRepository = new UserRepository();
        IQuestionRepository questionRepository = new QuestionRepository();
        IContestRepository contestRepository = new ContestRepository();
        IContestantRepository contestantRepository = new ContestantRepository();


        //Initialise services
        UserService userService = new UserService(userRepository);
        QuestionService questionService = new QuestionService(questionRepository);
        ContestService contestService = new ContestService(contestantRepository, userRepository, questionRepository, contestRepository);

        String inputFile = commandLineArgs.get(0).split("=")[1];

        try(BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
            while (true) {
                String line = reader.readLine();
                if (line == null){
                    break;
                }
                List<String> tokens = Arrays.asList(line.split(" "));

                // Execute Services
                switch(tokens.get(0)){
                    case "CREATE_USER":
                    {
                        String userName = tokens.get(1);
                        User user = userService.createUser(userName);
                        System.out.println("User has been created");
                        System.out.println(user);
                    }
                    break;

                    case "CREATE_QUESTION":
                    {
                        String title = tokens.get(1);
                        String level = tokens.get(2);
                        Integer score = Integer.parseInt(tokens.get(3));
                        Question question = questionService.createQuestion(title, Level.valueOf(level),score);
                        System.out.println("Question has been created");
                        System.out.println(question);
                    }
                    break;

                    case "LIST_QUESTION":
                    {
                        if(tokens.size() == 1){

                            List<Question> qList = questionService.getQuestions(null);
                            System.out.println("printing lists of questions");
                            System.out.println(qList);

                        }
                        else if(tokens.size() == 2){

                            String level = tokens.get(1);
                            List<Question> qList = questionService.getQuestions(Level.valueOf(level));
                            System.out.println("printing lists of questions");
                            System.out.println(qList);

                        }
                    }
                    break;

                    case "CREATE_CONTEST":
                    {
                        String contestName = tokens.get(1);
                        String level = tokens.get(2);
                        String creatorName = tokens.get(3);
                        Integer numQuestion = Integer.parseInt(tokens.get(4));
                        Contest contest = contestService.createContest(contestName,Level.valueOf(level), creatorName,numQuestion);
                        System.out.println("Contest has been created");
                        System.out.println(contest);
                    }
                    break;

                    case "LIST_CONTEST":
                    {
                        if(tokens.size() == 1){

                            List<Contest> qList = contestService.getContests(null);
                            System.out.println("contest lists");
                            System.out.println(qList);

                        }else if(tokens.size() == 2){

                            String level = tokens.get(1);
                            List<Contest> qList = contestService.getContests(Level.valueOf(level));
                            System.out.println("contest lists");
                            System.out.println(qList);

                        }
                    }
                    break;

                    case "ATTEND_CONTEST":
                    {
                        Long contestId = Long.parseLong(tokens.get(1));
                        String userName = tokens.get(2);
                        Contestant contestant = contestService.createContestant(contestId, userName);
                        System.out.println("attending a contest");
                        System.out.println(contestant);
                    }
                    break;

                    case "WITHDRAW_CONTEST":
                    {
                        Long contestId = Long.parseLong(tokens.get(1));
                        String userName = tokens.get(2);
                        String message = contestService.deleteContestant(contestId, userName);
                        System.out.println("withdrawn a contest");
                        System.out.println(message);
                    }
                    break;

                    case "RUN_CONTEST":
                    {
                        Long contestId = Long.parseLong(tokens.get(1));
                        String contestCreator = tokens.get(2);
                        List<Contestant> contestants = contestService.runContest(contestId, contestCreator);
                        System.out.println("running a contest");
                        System.out.println(contestants);
                    }
                    break;

                    case "CONTEST_HISTORY":
                    {
                        Long contestId = Long.parseLong(tokens.get(1));
                        List<Contestant> contestants = contestService.contestHistory(contestId);
                        System.out.println("printing contest history");
                        System.out.println(contestants);
                    }
                    break;

                    case "LEADERBOARD":
                    {
                        String order = tokens.get(1);
                        List<User> uList = userService.getUsers(UserOrder.valueOf("SCORE_"+order));
                        System.out.println("leaderboard");
                        System.out.print("[");
                        for(int  i=0; i < uList.size(); i++){
                            if(i == (uList.size()-1)){
                                System.out.print("User [id=" + uList.get(i).getId() + ", totalScore=" + uList.get(i).getTotalScore() + "]");
                            }else{
                                System.out.print("User [id=" + uList.get(i).getId() + ", totalScore=" + uList.get(i).getTotalScore() + "], ");
                            }
                        }
                        System.out.print("]");
                    }
                    break;


                    default:
                        throw new RuntimeException("Invalid Command");
                }
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}