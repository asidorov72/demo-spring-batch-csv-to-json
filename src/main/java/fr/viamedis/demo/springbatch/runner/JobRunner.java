package fr.viamedis.demo.springbatch.runner;

import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/*
* JobOperator.start(Job, JobParameters) — это новый рекомендованный метод запуска job в Spring Batch 6.
* Старый start(String, Properties) тоже deprecated.
* Для следующего экземпляра job есть ещё startNextInstance(Job), но для твоего демо сейчас нужен именно start(job, params).
* */
@Component
public class JobRunner implements CommandLineRunner {

    private final JobOperator jobOperator;
    private final Job job;

    public JobRunner(JobOperator jobOperator, Job job) {
        this.jobOperator = jobOperator;
        this.job = job;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("JOB STARTED!!!");

        JobParameters params = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();

        jobOperator.start(job, params);

        System.out.println("JOB FINISHED!!!");
    }
}