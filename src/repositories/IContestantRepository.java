package repositories;

import entities.Contestant;

import java.util.List;
import java.util.Optional;

public interface IContestantRepository {
    Contestant save(Contestant contestant);
    Optional<Contestant> find(Long contestId, String userName);
    List<Contestant> findAllByContestId(Long contestId);
    void delete(Contestant contestant);
}
