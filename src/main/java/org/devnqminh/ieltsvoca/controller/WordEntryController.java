package org.devnqminh.ieltsvoca.controller;

import org.devnqminh.ieltsvoca.dto.WordEntryDTO;
import org.devnqminh.ieltsvoca.entity.WordEntry;
import org.devnqminh.ieltsvoca.service.WordEntryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/words")
public class WordEntryController {

    @Autowired
    private WordEntryService wordEntryService;

    @GetMapping
    public List<WordEntryDTO> getAllWords() {
        return wordEntryService.getAllWords();
    }

    @GetMapping("/lookup")
    public ResponseEntity<WordEntryDTO> lookupWord(@RequestParam String word) {
        WordEntryDTO result = wordEntryService.lookupWord(word);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WordEntryDTO> getWordById(@PathVariable Long id) {
        WordEntryDTO word = wordEntryService.getWordById(id);
        if (word != null) {
            return ResponseEntity.ok(word);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public WordEntryDTO createWord(@RequestBody WordEntry word) {
        return wordEntryService.createWord(word);
    }

    @PutMapping("/{id}")
    public ResponseEntity<WordEntryDTO> updateWord(@PathVariable Long id, @RequestBody WordEntry wordDetails) {
        WordEntryDTO updatedWord = wordEntryService.updateWord(id, wordDetails);
        if (updatedWord != null) {
            return ResponseEntity.ok(updatedWord);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWord(@PathVariable Long id) {
        wordEntryService.deleteWord(id);
        return ResponseEntity.noContent().build();
    }
}
