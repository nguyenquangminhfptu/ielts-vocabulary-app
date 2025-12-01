package org.devnqminh.ieltsvoca.service.impl;

import org.devnqminh.ieltsvoca.dto.WordEntryDTO;
import org.devnqminh.ieltsvoca.entity.WordEntry;
import org.devnqminh.ieltsvoca.repository.WordEntryRepository;
import org.devnqminh.ieltsvoca.service.WordEntryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WordEntryServiceImpl implements WordEntryService {

    @Autowired
    private WordEntryRepository wordEntryRepository;

    @Autowired
    private org.devnqminh.ieltsvoca.service.AIService aiService;

    @Override
    public WordEntryDTO lookupWord(String word) {
        return wordEntryRepository.findByWord(word)
                .map(this::convertToDTO)
                .orElseGet(() -> {
                    WordEntry newWord = aiService.generateWordInfo(word);
                    return convertToDTO(wordEntryRepository.save(newWord));
                });
    }

    @Override
    public List<WordEntryDTO> getAllWords() {
        return wordEntryRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public WordEntryDTO getWordById(Long id) {
        return wordEntryRepository.findById(id)
                .map(this::convertToDTO)
                .orElse(null);
    }

    @Override
    public WordEntryDTO createWord(WordEntry word) {
        return convertToDTO(wordEntryRepository.save(word));
    }

    @Override
    public WordEntryDTO updateWord(Long id, WordEntry wordDetails) {
        return wordEntryRepository.findById(id).map(word -> {
            word.setWord(wordDetails.getWord());
            word.setDefinition(wordDetails.getDefinition());
            word.setExampleSentencesJson(wordDetails.getExampleSentencesJson());
            word.setSynonymsJson(wordDetails.getSynonymsJson());
            word.setAntonymsJson(wordDetails.getAntonymsJson());
            word.setIeltsUsage(wordDetails.getIeltsUsage());
            word.setBand7SampleSentence(wordDetails.getBand7SampleSentence());
            return convertToDTO(wordEntryRepository.save(word));
        }).orElse(null);
    }

    @Override
    public void deleteWord(Long id) {
        wordEntryRepository.deleteById(id);
    }

    private WordEntryDTO convertToDTO(WordEntry word) {
        return WordEntryDTO.builder()
                .id(word.getId())
                .word(word.getWord())
                .definition(word.getDefinition())
                .exampleSentencesJson(word.getExampleSentencesJson())
                .synonymsJson(word.getSynonymsJson())
                .antonymsJson(word.getAntonymsJson())
                .ieltsUsage(word.getIeltsUsage())
                .band7SampleSentence(word.getBand7SampleSentence())
                .createdAt(word.getCreatedAt())
                .updatedAt(word.getUpdatedAt())
                .build();
    }
}
