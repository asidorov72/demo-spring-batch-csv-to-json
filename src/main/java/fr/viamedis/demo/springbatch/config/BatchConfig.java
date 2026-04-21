package fr.viamedis.demo.springbatch.config;

import fr.viamedis.demo.springbatch.model.Animal;
import fr.viamedis.demo.springbatch.processor.AnimalProcessor;

import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;

import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;

import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.infrastructure.item.file.FlatFileItemWriter;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemWriterBuilder;

import org.springframework.batch.infrastructure.item.json.JsonFileItemWriter;
import org.springframework.batch.infrastructure.item.json.builder.JsonFileItemWriterBuilder;
import org.springframework.batch.infrastructure.item.json.JacksonJsonObjectMarshaller;
import org.springframework.core.io.FileSystemResource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

    @Bean
    public FlatFileItemReader<Animal> reader() {
        return new FlatFileItemReaderBuilder<Animal>()
                .name("animalItemReader")
                .resource(new ClassPathResource("input.csv"))
                .delimited()
                .names("id", "name", "age")
                .targetType(Animal.class)
                .build();
    }

    @Bean
    public JsonFileItemWriter<Animal> writer() {
        return new JsonFileItemWriterBuilder<Animal>()
                .name("animalJsonItemWriter")
                .resource(new FileSystemResource("output.json"))
                .jsonObjectMarshaller(
                        new JacksonJsonObjectMarshaller<>(
                                JsonMapper.builder()
                                        .enable(SerializationFeature.INDENT_OUTPUT)
                                        .build()
                        )
                )
                .build();
    }

    @Bean
    public Step step(JobRepository jobRepository,
                     PlatformTransactionManager transactionManager,
                     AnimalProcessor processor) {

        return new StepBuilder("step", jobRepository)
                .<Animal, Animal>chunk(3)
                .reader(reader())
                .processor(processor)
                .writer(writer())
                .transactionManager(transactionManager)
                .build();
    }

    @Bean
    public Job job(JobRepository jobRepository, Step step) {
        return new JobBuilder("job", jobRepository)
                .start(step)
                .build();
    }
}