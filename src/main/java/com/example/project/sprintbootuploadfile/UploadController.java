package com.example.project.sprintbootuploadfile;

import com.example.project.sprintbootuploadfile.Repository.FileInfoRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

@Controller
public class UploadController {

    @Autowired
    FileInfoRepository fileInfoRepository;

    public static final int chunkSize = 5000;   // Change should be made on both client side and server side

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(UploadController.class);

    @RequestMapping(value = "/getInfo", method = RequestMethod.POST,
    produces = {"Application/json"})
    @ResponseBody
        public String getInfo(@RequestBody String json) throws IOException {

        ObjectMapper mapper = new ObjectMapper();

        FileInfo file = mapper.readValue(json,FileInfo.class);

        file.setChunksDownloaded(getCurrentChunks(file.getFileName()));

        json = mapper.writeValueAsString(file);

        return json;
    }

    private long getCurrentChunks(String name) {

        if(fileInfoRepository.findByFileName(name)!= null)
            return fileInfoRepository.findByFileName(name).getChunksDownloaded();
        else return 0;

    }


    @RequestMapping(value = "/postChunk", method = RequestMethod.POST)
    @ResponseBody
    public String uploadChunk(@RequestBody byte[] nextChunk, @RequestHeader("file_name") String fileName, @RequestHeader("fileSize") String originalSize) throws IOException {

        long filesize = Long.parseLong(originalSize);
        long originalChunks;
        if(filesize%chunkSize == 0) 	originalChunks = filesize/chunkSize;
        else					originalChunks = filesize/chunkSize + 1;

        // Create File Object
        File newFile = new File("src/" + fileName);

        // If file does not exist, create new file and update repository
        if(!newFile.exists()){
            newFile.createNewFile();

            FileInfo fileInfo = new FileInfo(fileName,newFile.length(),originalChunks,0);

            fileInfoRepository.save(fileInfo);
        }

        // Append received chunk
        Files.write(newFile.toPath(), nextChunk, StandardOpenOption.APPEND);

        // Update Repository and send response
        if(newFile.length() != filesize) {
            FileInfo updateFile = fileInfoRepository.findByFileName(fileName);
            updateFile.setChunksDownloaded(newFile.length()/chunkSize);
            fileInfoRepository.save(updateFile);
            return "Chunks Added";
        }else {
            FileInfo updateFile = fileInfoRepository.findByFileName(fileName);
            updateFile.setChunksDownloaded(newFile.length()/chunkSize+1);
            fileInfoRepository.save(updateFile);
            return "File Completely Uploaded";
        }
    }

}
