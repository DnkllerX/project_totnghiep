package com.shareholder.scheduler;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Khoi dong 1 ScheduledExecutorService khi app deploy, chay dinh ky ScheduledEventProcessor.
 * Dung ScheduledExecutorService thay vi java.util.Timer vi:
 *   - 1 task nem exception khong lam "chet" toan bo scheduler (Timer thi co)
 *   - ho tro nhieu thread (day khong can nhung an toan hon cho tuong lai)
 */
public class SchedulerContextListener implements ServletContextListener {

    private static final Logger LOGGER = Logger.getLogger(SchedulerContextListener.class.getName());
    private static final long INITIAL_DELAY_SECONDS = 30; // cho app khoi dong on dinh
    private static final long PERIOD_SECONDS = 60;         // kiem tra moi 60 giay

    private ScheduledExecutorService executor;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        executor = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "shareholder-scheduler");
            t.setDaemon(true);
            return t;
        });

        ScheduledEventProcessor processor = new ScheduledEventProcessor();

        executor.scheduleAtFixedRate(() -> {
            try {
                processor.processDueEvents();
            } catch (Exception e) {
                // Bat toan bo Exception (khong chi SQLException) de dam bao 1 lan chay loi
                // khong lam executor dung han - se thu lai o lan chay tiep theo.
                LOGGER.log(Level.SEVERE, "Loi khi xu ly scheduled events", e);
            }
        }, INITIAL_DELAY_SECONDS, PERIOD_SECONDS, TimeUnit.SECONDS);

        LOGGER.info("Scheduler da khoi dong, kiem tra scheduled events moi " + PERIOD_SECONDS + " giay");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (executor != null) {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
        com.shareholder.config.DBConnection.shutdown();
        LOGGER.info("Scheduler da dung");
    }
}
