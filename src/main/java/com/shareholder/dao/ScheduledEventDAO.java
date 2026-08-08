package com.shareholder.dao;

import com.shareholder.model.ScheduledEvent;
import com.shareholder.model.enums.EventStatus;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface ScheduledEventDAO {
    List<ScheduledEvent> findDuePending() throws SQLException;
    int insert(Connection conn, ScheduledEvent event) throws SQLException;

    /** Chuyen PENDING -> PROCESSING truoc khi xu ly, dieu kien status = PENDING de tranh 2 scheduler dam vao nhau. */
    boolean claimForProcessing(Connection conn, int eventId) throws SQLException;

    boolean markStatus(Connection conn, int eventId, EventStatus status) throws SQLException;
}
