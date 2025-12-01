package org.devnqminh.ieltsvoca.controller;

import org.devnqminh.ieltsvoca.dto.UserVocabEntryDTO;
import org.devnqminh.ieltsvoca.entity.UserVocabEntry;
import org.devnqminh.ieltsvoca.service.UserVocabEntryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user-vocab")
public class UserVocabEntryController {

    @Autowired
    private UserVocabEntryService userVocabEntryService;

    @GetMapping("/user/{userId}")
    public List<UserVocabEntryDTO> getAllUserVocabEntries(@PathVariable Long userId) {
        return userVocabEntryService.getAllUserVocabEntries(userId);
    }

    @GetMapping("/user/{userId}/word/{wordId}")
    public ResponseEntity<UserVocabEntryDTO> getUserVocabEntry(@PathVariable Long userId, @PathVariable Long wordId) {
        UserVocabEntryDTO entry = userVocabEntryService.getUserVocabEntry(userId, wordId);
        if (entry != null) {
            return ResponseEntity.ok(entry);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/user/{userId}/word/{wordId}")
    public UserVocabEntryDTO addWordToUser(@PathVariable Long userId, @PathVariable Long wordId, @RequestBody UserVocabEntry entryDetails) {
        return userVocabEntryService.addWordToUser(userId, wordId, entryDetails);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserVocabEntryDTO> updateUserVocabEntry(@PathVariable Long id, @RequestBody UserVocabEntry entryDetails) {
        UserVocabEntryDTO updatedEntry = userVocabEntryService.updateUserVocabEntry(id, entryDetails);
        if (updatedEntry != null) {
            return ResponseEntity.ok(updatedEntry);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserVocabEntry(@PathVariable Long id) {
        userVocabEntryService.deleteUserVocabEntry(id);
        return ResponseEntity.noContent().build();
    }
}
