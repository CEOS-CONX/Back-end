package com.conx.server.user.controller.crew;

import com.conx.server.domain.file.service.FileService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.net.URI;

@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class ThumbnailService {
    private final FileService fileService;

    public String createThumbnail(String pdfUrl){
        return null;
    }

}
