package org.devnqminh.ieltsvoca.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WordEntryDTO {
    private Long id;
    private String word;
    private String definition;
    private String exampleSentencesJson;
    private String synonymsJson;
    private String antonymsJson;
    private String ieltsUsage;
    private String band7SampleSentence;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
