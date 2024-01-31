package com.example.batch.controller;

import com.example.batch.api.CsvToXmlApi;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@RestController
public class CsvToXmlLoadController implements CsvToXmlApi {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    @Qualifier("CsvToXmlBatchConfig")
    private Job job;

    private final String fileDirectory ="E:\\CodeThali\\SpringBatch\\batch\\batch\\src\\main\\resources\\";
    @Override
    public ResponseEntity<String> csvToXml(MultipartFile file) {
        try {
            String fileName = file.getOriginalFilename();
            String originalFileName = fileDirectory+fileName;
            File file1 = new File(originalFileName);
            file.transferTo(file1);
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("startAt", System.currentTimeMillis())
                    .addString("fullPath",originalFileName)
                    .toJobParameters();
            JobExecution run = jobLauncher.run(job, jobParameters);
            if("COMPLETED".equalsIgnoreCase(run.getStatus().toString())){
                Files.deleteIfExists(Paths.get(originalFileName));
            }
            return ResponseEntity.ok(run.getStatus().toString());
        } catch (JobExecutionAlreadyRunningException e) {
            throw new RuntimeException(e);
        } catch (JobRestartException e) {
            throw new RuntimeException(e);
        } catch (JobInstanceAlreadyCompleteException e) {
            throw new RuntimeException(e);
        } catch (JobParametersInvalidException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
