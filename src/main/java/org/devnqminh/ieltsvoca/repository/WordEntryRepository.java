package org.devnqminh.ieltsvoca.repository;

import org.devnqminh.ieltsvoca.entity.WordEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WordEntryRepository extends JpaRepository<WordEntry, Long> {
    Optional<WordEntry> findByWord(String word);
}
