package com.fam.knightfam.photo_logic.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PhotoMetadata {
    private String photoId;
    private String userId;
    private String s3Url;
    private Instant uploadTime;
}
