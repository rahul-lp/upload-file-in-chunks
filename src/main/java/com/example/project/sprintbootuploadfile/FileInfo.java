package com.example.project.sprintbootuploadfile;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "FILE_INFO")
public class FileInfo implements Serializable {

    @Id
    @JsonProperty("fileName")
    private String fileName;

    @JsonProperty("size")
    private long size;

    @JsonProperty("totalChunks")
    private long totalChunks;

    @JsonProperty("downloadedChunks")
    private long chunksDownloaded;

    public FileInfo(String fileName, long size, long totalChunks, long chunksDownloaded) {
        this.fileName = fileName;
        this.size = size;
        this.totalChunks = totalChunks;
        this.chunksDownloaded = chunksDownloaded;
    }

    public FileInfo(){

    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getTotalChunks() {
        return totalChunks;
    }

    public void setTotalChunks(long totalChunks) {
        this.totalChunks = totalChunks;
    }

    public long getChunksDownloaded() {
        return chunksDownloaded;
    }

    public void setChunksDownloaded(long chunksDownloaded) {
        this.chunksDownloaded = chunksDownloaded;
    }

    @Override
    public String toString() {
        return "FileInfo{" +
                "fileName='" + fileName + '\'' +
                ", size=" + size +
                ", totalChunks=" + totalChunks +
                ", chunksDownloaded=" + chunksDownloaded +
                '}';
    }
}