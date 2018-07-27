package com.example.project.sprintbootuploadfile.Repository;

import com.example.project.sprintbootuploadfile.FileInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileInfoRepository extends JpaRepository<FileInfo, Long> {

    FileInfo findByFileName(String fileName);
}
