package com.sample.system.reporting.service.batch;

import com.sample.system.reporting.service.application.reportbatch.model.ReportBatchContext;
import com.sample.system.reporting.service.application.reportbatch.service.ReportBatchContextFactory;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.ItemStreamReader;
import org.springframework.batch.infrastructure.item.ItemStreamWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Configuration
@EnableBatchProcessing
public class ReportBatchJobConfiguration {

    public static final String REPORT_EXECUTION_BATCH_JOB = "reportExecutionBatchJob";
    public static final String REPORT_EXECUTION_BATCH_STEP = "reportExecutionBatchStep";

    @Bean
    public Job reportExecutionBatchJob(JobRepository jobRepository, Step reportExecutionBatchStep) {
        return new JobBuilder(REPORT_EXECUTION_BATCH_JOB, jobRepository)
                .start(reportExecutionBatchStep)
                .build();
    }

    @Bean
    public Step reportExecutionBatchStep(JobRepository jobRepository,
                                         PlatformTransactionManager transactionManager,
                                         ReportBatchProperties properties,
                                         ReportBatchStepCompletionListener stepCompletionListener,
                                         ItemStreamReader<Object> reportBatchItemReader,
                                         ItemProcessor<Object, Object> reportBatchItemProcessor,
                                         ItemStreamWriter<Object> reportBatchItemWriter) {
        return new StepBuilder(REPORT_EXECUTION_BATCH_STEP, jobRepository)
                .<Object, Object>chunk(Math.max(1, properties.getChunkSize()), transactionManager)
                .reader(reportBatchItemReader)
                .processor(reportBatchItemProcessor)
                .writer(reportBatchItemWriter)
                .listener(stepCompletionListener)
                .build();
    }

    @Bean
    @StepScope
    public ReportBatchContext reportBatchContext(ReportBatchContextFactory contextFactory,
                                                 ReportBatchSharedStateRegistry sharedStateRegistry,
                                                 @Value("#{jobParameters}") Map<String, Object> jobParameters) {
        ConcurrentMap<String, Object> batchParameters = new ConcurrentHashMap<>(jobParameters);
        ReportBatchContext context = contextFactory.create(batchParameters);
        sharedStateRegistry.bind(context.getReportExecution().getId().getValue(), context.getSharedState());
        return context;
    }

    @Bean
    @StepScope
    public ItemStreamReader<Object> reportBatchItemReader(ReportBatchComponentRegistry registry, ReportBatchContext reportBatchContext) {
        return new DelegatingReportItemReader(registry.readerFor(reportBatchContext.getReportDefinition()), reportBatchContext);
    }

    @Bean
    @StepScope
    public ItemProcessor<Object, Object> reportBatchItemProcessor(ReportBatchComponentRegistry registry, ReportBatchContext reportBatchContext) {
        return new DelegatingReportItemProcessor(registry.processorFor(reportBatchContext.getReportDefinition()), reportBatchContext);
    }

    @Bean
    @StepScope
    public ItemStreamWriter<Object> reportBatchItemWriter(ReportBatchComponentRegistry registry, ReportBatchContext reportBatchContext) {
        return new DelegatingReportItemWriter(
                registry.writerFor(reportBatchContext.getReportDefinition(), reportBatchContext.getReportFormat()), reportBatchContext);
    }
}
