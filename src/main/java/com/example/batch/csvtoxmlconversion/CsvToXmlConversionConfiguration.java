package com.example.batch.csvtoxmlconversion;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.batch.item.xml.builder.StaxEventItemWriterBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class CsvToXmlConversionConfiguration {

    @Bean(name="csvToXmlItemReader")
    @StepScope
    public FlatFileItemReader<CsvToXmlConversion> csvToXmlItemReader(@Value("#{jobParameters.fileFullPath}")String filePath){
        FlatFileItemReader<CsvToXmlConversion> flatFileItemReader = new FlatFileItemReader<>();
        flatFileItemReader.setResource(new FileSystemResource(filePath));
        flatFileItemReader.setStrict(false);
        flatFileItemReader.setName("csvToXmlConversion");
        flatFileItemReader.setLinesToSkip(1);
        flatFileItemReader.setLineMapper(lineMapper());
        return flatFileItemReader;
    }

    private LineMapper<CsvToXmlConversion> lineMapper() {

        DefaultLineMapper<CsvToXmlConversion> defaultLineMapper = new DefaultLineMapper<>();
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setDelimiter(",");
        tokenizer.setStrict(false);
        tokenizer.setNames("id","firstName","lastName","email","gender","contactNo","country","dob");

        BeanWrapperFieldSetMapper fieldSetMapper = new BeanWrapperFieldSetMapper();
        fieldSetMapper.setTargetType(CsvToXmlConversion.class);

        defaultLineMapper.setLineTokenizer(tokenizer);
        defaultLineMapper.setFieldSetMapper(fieldSetMapper);

        return defaultLineMapper;
    }

    @Bean
    public ItemProcessor csvItemProcessor(){
        return new CsvToXmlConversionProcessor();
    }

   @Bean
    public ItemWriter csvItemWriter(){
       Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
       marshaller.setClassesToBeBound(CsvToXmlConversion.class);

        return new StaxEventItemWriterBuilder<CsvToXmlConversion>()
                .name("csvToXmlConversion")
                .rootTagName("recordList")
                .resource(new FileSystemResource("src/main/resources/customer.xml"))
                .marshaller(marshaller)
                .build();
   }

   @Bean
    public Step csvToXmlstep(FlatFileItemReader<CsvToXmlConversion> csvToXmlItemReader, JobRepository jobRepository, PlatformTransactionManager txnManager){
       return new StepBuilder("csvToXmlStepBuilder",jobRepository)
               .<CsvToXmlConversion,CsvToXmlConversion>chunk(10,txnManager)
               .reader(csvToXmlItemReader)
               .processor(csvItemProcessor())
               .writer(csvItemWriter())
               .build();
   }

   @Bean(name ="csvToXmlConversionJob")
    public Job getJob(FlatFileItemReader<CsvToXmlConversion> csvToXmlItemReader
           ,JobRepository jobRepository, PlatformTransactionManager txnManager){
       return new JobBuilder("csvToXmlConversion",jobRepository)
               .flow(csvToXmlstep(csvToXmlItemReader,jobRepository,txnManager))
               .end().build();
   }
}
