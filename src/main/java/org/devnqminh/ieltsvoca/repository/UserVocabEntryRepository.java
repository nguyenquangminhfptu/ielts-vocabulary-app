package org.devnqminh.ieltsvoca.repository;

import org.devnqminh.ieltsvoca.entity.UserVocabEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserVocabEntryRepository extends JpaRepository<UserVocabEntry, Long> {
    List<UserVocabEntry> findByUserId(Long userId);
    Optional<UserVocabEntry> findByUserIdAndWordId(Long userId, Long wordId);
}
