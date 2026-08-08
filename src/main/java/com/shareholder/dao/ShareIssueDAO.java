package com.shareholder.dao;

import com.shareholder.model.ShareIssue;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface ShareIssueDAO {
    Optional<ShareIssue> findById(int issueId) throws SQLException;
    List<ShareIssue> findAll() throws SQLException;

    /** Cac dot phat hanh da het end_date nhung chua duoc scheduler xu ly. */
    List<ShareIssue> findEndedNotProcessed() throws SQLException;

    int insert(Connection conn, ShareIssue issue) throws SQLException;
}
