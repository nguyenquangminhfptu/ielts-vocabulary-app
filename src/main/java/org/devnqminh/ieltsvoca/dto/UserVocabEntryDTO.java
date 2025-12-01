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
public class UserVocabEntryDTO {
    private Long id;
    private Long userId;
    private Long wordId;
    private String word; // Include word string for convenience
    private String userNote;
    private String userExample;
    private Boolean favorite;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
