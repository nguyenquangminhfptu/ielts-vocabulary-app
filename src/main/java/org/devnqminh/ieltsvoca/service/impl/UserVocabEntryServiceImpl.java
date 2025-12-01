package org.devnqminh.ieltsvoca.service.impl;

import org.devnqminh.ieltsvoca.dto.UserVocabEntryDTO;
import org.devnqminh.ieltsvoca.entity.UserAccount;
import org.devnqminh.ieltsvoca.entity.UserVocabEntry;
import org.devnqminh.ieltsvoca.entity.WordEntry;
import org.devnqminh.ieltsvoca.repository.UserAccountRepository;
import org.devnqminh.ieltsvoca.repository.UserVocabEntryRepository;
import org.devnqminh.ieltsvoca.repository.WordEntryRepository;
import org.devnqminh.ieltsvoca.service.UserVocabEntryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserVocabEntryServiceImpl implements UserVocabEntryService {

    @Autowired
    private UserVocabEntryRepository userVocabEntryRepository;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private WordEntryRepository wordEntryRepository;

    @Override
    public List<UserVocabEntryDTO> getAllUserVocabEntries(Long userId) {
        return userVocabEntryRepository.findByUserId(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UserVocabEntryDTO getUserVocabEntry(Long userId, Long wordId) {
        return userVocabEntryRepository.findByUserIdAndWordId(userId, wordId)
                .map(this::convertToDTO)
                .orElse(null);
    }

    @Override
    public UserVocabEntryDTO addWordToUser(Long userId, Long wordId, UserVocabEntry entryDetails) {
        // Check if exists
        return userVocabEntryRepository.findByUserIdAndWordId(userId, wordId)
                .map(this::convertToDTO)
                .orElseGet(() -> {
                    UserAccount user = userAccountRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
                    WordEntry word = wordEntryRepository.findById(wordId).orElseThrow(() -> new RuntimeException("Word not found"));

                    UserVocabEntry entry = new UserVocabEntry();
                    entry.setUser(user);
                    entry.setWord(word);
                    entry.setUserNote(entryDetails.getUserNote());
                    entry.setUserExample(entryDetails.getUserExample());
                    entry.setFavorite(entryDetails.getFavorite());

                    return convertToDTO(userVocabEntryRepository.save(entry));
                });
    }

    @Override
    public UserVocabEntryDTO updateUserVocabEntry(Long id, UserVocabEntry entryDetails) {
        return userVocabEntryRepository.findById(id).map(entry -> {
            entry.setUserNote(entryDetails.getUserNote());
            entry.setUserExample(entryDetails.getUserExample());
            entry.setFavorite(entryDetails.getFavorite());
            return convertToDTO(userVocabEntryRepository.save(entry));
        }).orElse(null);
    }

    @Override
    public void deleteUserVocabEntry(Long id) {
        userVocabEntryRepository.deleteById(id);
    }

    private UserVocabEntryDTO convertToDTO(UserVocabEntry entry) {
        return UserVocabEntryDTO.builder()
                .id(entry.getId())
                .userId(entry.getUser().getId())
                .wordId(entry.getWord().getId())
                .word(entry.getWord().getWord())
                .userNote(entry.getUserNote())
                .userExample(entry.getUserExample())
                .favorite(entry.getFavorite())
                .createdAt(entry.getCreatedAt())
                .updatedAt(entry.getUpdatedAt())
                .build();
    }
}
