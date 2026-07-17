package com.shareholder.dao;

import com.shareholder.model.Vote;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface VoteDAO {
    Optional<Vote> findByResolutionAndShareholder(int resolutionId, int shareholderId) throws SQLException;
    List<Vote> findByResolutionId(int resolutionId) throws SQLException;
    boolean hasVoted(int resolutionId, int shareholderId) throws SQLException;

    /** Insert duy nhat 1 lan nho UQ_Votes(resolution_id, shareholder_id) - khong cho sua sau khi vote. */
    int insert(Vote vote) throws SQLException;

    /** Dem so phieu theo tung vote_value cho 1 nghi quyet (dung cho ket qua bieu quyet). */
    java.util.Map<String, Integer> countByResolutionId(int resolutionId) throws SQLException;
}
