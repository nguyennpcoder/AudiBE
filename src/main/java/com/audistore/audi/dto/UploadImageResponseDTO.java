package com.audistore.audi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UploadImageResponseDTO {
    private String fileName;
    private String fileDownloadUri;
    private String fileType;
    private long size;
}