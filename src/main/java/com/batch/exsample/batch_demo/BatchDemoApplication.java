package com.batch.exsample.batch_demo;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

@SpringBootApplication
public class BatchDemoApplication {

	@Bean
	protected FlatFileItemReader<String> reader(){
		return new FlatFileItemReaderBuilder<String>()
				.resource(new ClassPathResource("data-file.txt"))
				.name("text-reader")
				.lineMapper((line,lineNumber) ->line).build();
	}
@Bean
	protected FlatFileItemWriter<String> writer(){
		String fileLocation = "src/main/resources/masked-data-file.txt";
		return new FlatFileItemWriterBuilder<String>()
				.name("text-writer")
				.resource(new FileSystemResource(fileLocation))
				.lineAggregator(item -> item)
				.build();
	}
@Bean
	protected Step maskingStep(JobRepository jobRepo, PlatformTransactionManager manager,
							   FlatFileItemReader<String> reader, TextItemProcessor processor,
							   FlatFileItemWriter writer){
		return new StepBuilder("text-step", jobRepo)
				.<String,String> chunk(2,manager)
				.reader(reader)
				.processor(processor)
				.writer(writer)
				.build();
	}
@Bean
	protected Job makingJob(JobRepository jobRepository, Step maskingStep,  BatchJobCompletedListener batchJobCompletedListener){
		return new JobBuilder("text-job", jobRepository)
				.start(maskingStep)
				.listener(batchJobCompletedListener)
				.build();
	}

	public static void main(String[] args) {
		SpringApplication.run(BatchDemoApplication.class, args);
	}



}
