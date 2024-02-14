package repositories;

import java.util.List;
import java.util.Optional;

import entities.Contest;
import entities.Level;

public interface IContestRepository {
    Contest save(Contest question);
    List<Contest> findAll();
    Optional<Contest> findById(Long id);
    List<Contest> findAllContestLevelWise(Level level);
}
