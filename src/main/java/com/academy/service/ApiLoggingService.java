package com.academy.service;

import com.academy.dto.ApiLogEntry;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Service to log API requests and responses to file in JSON format
 * Uses async queue-based logging for better performance
 */
@Service
@Slf4j
public class ApiLoggingService {
    
    @Value("${api.logging.directory:./logs}")
    private String logDirectory;
    
    @Value("${api.logging.filename:api-requests.log}")
    private String logFilename;
    
    @Value("${api.logging.enabled:true}")
    private Boolean enabled;
    
    private final BlockingQueue<ApiLogEntry> logQueue = new LinkedBlockingQueue<>();
    private final AtomicBoolean running = new AtomicBoolean(false);
    private Thread loggingThread;
    private BufferedWriter writer;
    private final ObjectMapper objectMapper;
    
    public ApiLoggingService() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }
    
    @PostConstruct
    public void init() {
        if (enabled == null || !enabled) {
            log.info("API logging is disabled");
            return;
        }
        
        try {
            // Create log directory if it doesn't exist
            Path logDir = Paths.get(logDirectory);
            if (!Files.exists(logDir)) {
                Files.createDirectories(logDir);
                log.info("Created log directory: {}", logDirectory);
            }
            
            // Create log file with timestamp
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
            String filename = logFilename.replace(".log", "_" + timestamp + ".log");
            Path logFile = logDir.resolve(filename);
            
            writer = new BufferedWriter(new FileWriter(logFile.toFile(), true));
            log.info("API logging initialized. Log file: {}", logFile.toAbsolutePath());
            
            // Start background thread for async logging
            running.set(true);
            loggingThread = new Thread(this::processLogQueue, "api-logging-thread");
            loggingThread.setDaemon(true);
            loggingThread.start();
            
        } catch (IOException e) {
            log.error("Failed to initialize API logging", e);
            enabled = false;
        }
    }
    
    /**
     * Log API request/response entry
     */
    public void logApiCall(ApiLogEntry entry) {
        if (enabled == null || !enabled || entry == null) {
            return;
        }
        
        // Generate unique ID if not present
        if (entry.getId() == null) {
            entry.setId(UUID.randomUUID().toString());
        }
        
        // Set timestamp if not present
        if (entry.getTimestamp() == null) {
            entry.setTimestamp(LocalDateTime.now());
        }
        
        try {
            logQueue.put(entry);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Interrupted while adding log entry to queue", e);
        }
    }
    
    /**
     * Background thread to process log queue
     */
    private void processLogQueue() {
        while (running.get() || !logQueue.isEmpty()) {
            try {
                ApiLogEntry entry = logQueue.take();
                writeLogEntry(entry);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.warn("Logging thread interrupted", e);
                break;
            } catch (Exception e) {
                log.error("Error processing log entry", e);
            }
        }
    }
    
    /**
     * Write log entry to file in JSON format
     */
    private void writeLogEntry(ApiLogEntry entry) {
        if (writer == null) {
            return;
        }
        
        try {
            String json = objectMapper.writeValueAsString(entry);
            writer.write(json);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            log.error("Failed to write log entry to file", e);
        }
    }
    
    @PreDestroy
    public void shutdown() {
        log.info("Shutting down API logging service");
        running.set(false);
        
        if (loggingThread != null) {
            loggingThread.interrupt();
            try {
                loggingThread.join(5000); // Wait up to 5 seconds
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        if (writer != null) {
            try {
                writer.close();
            } catch (IOException e) {
                log.error("Error closing log file writer", e);
            }
        }
    }
}

