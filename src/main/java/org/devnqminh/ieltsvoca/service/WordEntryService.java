package org.devnqminh.ieltsvoca.service;

import org.devnqminh.ieltsvoca.dto.WordEntryDTO;
import org.devnqminh.ieltsvoca.entity.WordEntry;

import java.util.List;

public interface WordEntryService {
    List<WordEntryDTO> getAllWords();
    WordEntryDTO getWordById(Long id);
    WordEntryDTO createWord(WordEntry word);
    WordEntryDTO updateWord(Long id, WordEntry wordDetails);
    void deleteWord(Long id);
    WordEntryDTO lookupWord(String word);
}
