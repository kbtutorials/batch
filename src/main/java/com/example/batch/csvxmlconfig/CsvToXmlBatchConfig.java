package com.example.batch.csvxmlconfig;

import com.example.batch.entity.Customer;
import com.example.batch.entity.CustomerModel;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.batch.item.xml.builder.StaxEventItemWriterBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.transaction.PlatformTransactionManager;

import javax.xml.bind.Marshaller;
import java.io.File;

@Configuration
public class CsvToXmlBatchConfig {

    @Bean(name="itemReader")
    @StepScope
    public FlatFileItemReader<CustomerModel> itemReader(@Value("#{jobParameters.fullPath}") String fullPath){
        FlatFileItemReader<CustomerModel> flatFileItemReader = new FlatFileItemReader<>();
        flatFileItemReader.setResource(new FileSystemResource(fullPath));
        flatFileItemReader.setName("csvItemReader");
        flatFileItemReader.setLinesToSkip(1);
        flatFileItemReader.setStrict(false);
        flatFileItemReader.setLineMapper(lineMapper());

        return flatFileItemReader;
    }

    private LineMapper<CustomerModel> lineMapper() {
        DefaultLineMapper<CustomerModel> defaultLineMapper = new DefaultLineMapper<>();

        BeanWrapperFieldSetMapper fieldSetMapper = new BeanWrapperFieldSetMapper();
        fieldSetMapper.setTargetType(CustomerModel.class);

        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setStrict(false);
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setNames("id","firstName","lastName","email","gender","contactNo","country","dob");

        defaultLineMapper.setLineTokenizer(lineTokenizer);
        defaultLineMapper.setFieldSetMapper(fieldSetMapper);
        return defaultLineMapper;
    }
    @Bean
    public ItemProcessor itemProcessor(){
        CsvToXmlProcessor processor = new CsvToXmlProcessor();
        return processor;
    }

    @Bean
    public StaxEventItemWriter<CustomerModel> csvToXmlItemWriter(){
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setClassesToBeBound(CustomerModel.class);

        return new StaxEventItemWriterBuilder<CustomerModel>()
                .name("contactItemWriter")
                .version("1.0")
                .rootTagName("ContactList")
                .resource(new FileSystemResource("src/main/resources/customers.xml"))
                .marshaller(marshaller)
                .build();
    }


    @Bean
    public Step getStep(FlatFileItemReader<CustomerModel> itemReader, JobRepository jobRepository, PlatformTransactionManager txnManager){
        return new StepBuilder("csvToXmlStep",jobRepository)
                .<CustomerModel,CustomerModel>chunk(10,txnManager)
                .reader(itemReader)
                .processor(itemProcessor())
                .writer(csvToXmlItemWriter())
                .build();
    }

    @Bean(name = "CsvToXmlBatchConfig")
    public Job getJob(FlatFileItemReader<CustomerModel> itemReader, JobRepository jobRepository, PlatformTransactionManager txnManager){
        return new JobBuilder("csvToXmlJobBuilder",jobRepository)
                .flow(getStep(itemReader,jobRepository, txnManager)).end().build();
    }

}
