package com.example.batch.csvtoxmlconversion;

import com.example.batch.api.CsvToXmlConversionApi;
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
public class CsvToXmlRestController implements CsvToXmlConversionApi {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    @Qualifier("csvToXmlConversionJob")
    private Job job;

    private final String fileFolder = "E:\\CodeThali\\SpringBatch\\batch\\batch\\src\\main\\resources\\";
    @Override
    public ResponseEntity<String> csvToXmlConversion(MultipartFile file) {
        try {
            String originalFilename = file.getOriginalFilename();
            String filePath =fileFolder+originalFilename;
            File readerFile = new File(filePath);
            file.transferTo(readerFile);
            System.out.println("entire File path is :"+filePath);
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("startAt",System.currentTimeMillis())
                    .addString("fileFullPath",filePath)
                    .toJobParameters();
            JobExecution run = jobLauncher.run(job, jobParameters);
           if("COMPLETED".equalsIgnoreCase(run.getStatus().toString())){
               Files.deleteIfExists(Paths.get(filePath));
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
            throw new RuntimeException(e);
        }
    }
}
