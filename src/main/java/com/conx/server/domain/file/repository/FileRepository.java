package com.conx.server.domain.file.repository;

import com.conx.server.domain.file.domain.File;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface FileRepository extends JpaRepository<File, Long> {
    File findByUrl(String url);

    List<File> findAllByUrlIn(Collection<String> urls);
}
