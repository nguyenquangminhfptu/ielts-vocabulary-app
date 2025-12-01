package org.devnqminh.ieltsvoca.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "words")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WordEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String word;

    @Column(columnDefinition = "TEXT")
    private String definition;

    @Column(name = "example_sentences_json", columnDefinition = "TEXT")
    private String exampleSentencesJson;

    @Column(name = "synonyms_json", columnDefinition = "TEXT")
    private String synonymsJson;

    @Column(name = "antonyms_json", columnDefinition = "TEXT")
    private String antonymsJson;

    @Column(name = "ielts_usage", columnDefinition = "TEXT")
    private String ieltsUsage;

    @Column(name = "band7_sample_sentence", columnDefinition = "TEXT")
    private String band7SampleSentence;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

