package org.devnqminh.ieltsvoca.service;

import org.devnqminh.ieltsvoca.dto.UserVocabEntryDTO;
import org.devnqminh.ieltsvoca.entity.UserVocabEntry;

import java.util.List;

public interface UserVocabEntryService {
    List<UserVocabEntryDTO> getAllUserVocabEntries(Long userId);
    UserVocabEntryDTO getUserVocabEntry(Long userId, Long wordId);
    UserVocabEntryDTO addWordToUser(Long userId, Long wordId, UserVocabEntry entryDetails);
    UserVocabEntryDTO updateUserVocabEntry(Long id, UserVocabEntry entryDetails);
    void deleteUserVocabEntry(Long id);
}
