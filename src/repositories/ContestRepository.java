package repositories;

import entities.Contest;
import entities.Level;

import java.util.*;
import java.util.stream.Collectors;

public class ContestRepository implements IContestRepository{
    private final Map<Long, Contest> contestMap;
    private Long autoIncrement = 1L;

    public ContestRepository() {
        contestMap = new HashMap<Long, Contest>();
    }

    @Override
    public Contest save(Contest contest) {
        Contest savedContest = new Contest(contest.getTitle(), contest.getLevel(), contest.getCreator(), contest.getQuestions(), autoIncrement);
        contestMap.put(autoIncrement, savedContest);
        autoIncrement++;
        return savedContest;
    }

    @Override
    public List<Contest> findAll() {
        return new ArrayList<>(contestMap.values());
    }

    @Override
    public Optional<Contest> findById(Long id) {
        return Optional.ofNullable(contestMap.get(id));
    }

    @Override
    public List<Contest> findAllContestLevelWise(Level level) {
        return contestMap.values().stream().filter(c -> c.getLevel().equals(level)).collect(Collectors.toList());
    }
}
