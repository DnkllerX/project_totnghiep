package com.shareholder.service;

import com.shareholder.dao.ResolutionDAO;
import com.shareholder.dao.ShareIssueDAO;
import com.shareholder.dao.ShareIssueDetailDAO;
import com.shareholder.dao.VoteDAO;
import com.shareholder.dao.impl.ResolutionDAOImpl;
import com.shareholder.dao.impl.ShareIssueDAOImpl;
import com.shareholder.dao.impl.ShareIssueDetailDAOImpl;
import com.shareholder.dao.impl.VoteDAOImpl;
import com.shareholder.model.Notification;
import com.shareholder.model.Resolution;
import com.shareholder.model.ShareIssue;
import com.shareholder.model.ShareIssueDetail;
import com.shareholder.model.Vote;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Gop 2 nguon du lieu that thanh danh sach "Thong bao" hien o sidebar cua co dong (truoc muc
 * "Ky nhan Co phan"):
 *   - Tu bang VOTES: cac nghi quyet co dong DA bau -> nhan loai "Biểu quyết"
 *   - Tu bang SHARE_ISSUES (join SHARE_ISSUE_DETAILS): cac dot phat hanh co dong duoc quyen nhan
 *     -> nhan loai "Phát hành cổ tức" (he thong hien chi ho tro IssueType.DIVIDEND)
 * Khong co bang THONG_BAO rieng - toan bo noi dung duoc suy ra tu du lieu that cua 2 nghiep vu tren.
 */
public class NotificationService {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter DATE_ONLY_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final VoteDAO voteDAO = new VoteDAOImpl();
    private final ResolutionDAO resolutionDAO = new ResolutionDAOImpl();
    private final ShareIssueDetailDAO issueDetailDAO = new ShareIssueDetailDAOImpl();
    private final ShareIssueDAO issueDAO = new ShareIssueDAOImpl();

    public List<Notification> buildForShareholder(int shareholderId) throws SQLException {
        List<Notification> result = new ArrayList<>();

        // 1) Biểu quyết - chi lay nhung nghi quyet co dong DA bau (co dong trong bang VOTES)
        for (Vote vote : voteDAO.findByShareholderId(shareholderId)) {
            Optional<Resolution> resOpt = resolutionDAO.findById(vote.getResolutionId());
            if (resOpt.isEmpty()) continue;
            Resolution r = resOpt.get();

            List<String> info = new ArrayList<>();
            info.add("Bạn đã bầu: " + voteValueLabel(vote.getVoteValue()));

            String note = (r.getEndTime() != null)
                    ? "Ngày kết thúc: " + r.getEndTime().format(DATE_FMT)
                    : null;

            result.add(new Notification("Biểu quyết", r.getTitle(), info, note, vote.getVotedAt()));
        }

        // 2) Phát hành cổ tức - cac dot phat hanh ma co dong co trong SHARE_ISSUE_DETAILS
        for (ShareIssueDetail detail : issueDetailDAO.findByShareholderId(shareholderId)) {
            Optional<ShareIssue> issueOpt = issueDAO.findById(detail.getIssueId());
            if (issueOpt.isEmpty()) continue;
            ShareIssue issue = issueOpt.get();

            List<String> info = new ArrayList<>();
            info.add("Tỷ lệ: " + ratioToPercent(issue.getIssueRatio()));
            if (issue.getIssueDate() != null) {
                info.add("Ngày đăng ký cuối cùng: " + issue.getIssueDate().format(DATE_ONLY_FMT));
            }

            String note = (issue.getStartDate() != null && issue.getEndDate() != null)
                    ? "Thời gian ký nhận: " + issue.getStartDate().format(DATE_FMT)
                        + " - " + issue.getEndDate().format(DATE_FMT)
                    : null;

            LocalDateTime eventTime = issue.getCreatedAt() != null ? issue.getCreatedAt() : issue.getStartDate();
            result.add(new Notification("Phát hành cổ tức", issue.getTitle(), info, note, eventTime));
        }

        result.sort(Comparator.comparing(Notification::getEventTime,
                Comparator.nullsLast(Comparator.reverseOrder())));
        return result;
    }

    private String voteValueLabel(com.shareholder.model.enums.VoteValue value) {
        if (value == null) return "";
        return switch (value) {
            case AGREE -> "Đồng ý";
            case DISAGREE -> "Không đồng ý";
            case ABSTAIN -> "Trắng phiếu";
        };
    }

    /** issue_ratio vd 0.1000 -> "10%" */
    private String ratioToPercent(BigDecimal ratio) {
        if (ratio == null) return "Chưa cập nhật";
        BigDecimal percent = ratio.multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP)
                .stripTrailingZeros();
        // stripTrailingZeros co the tra ve dang khoa hoc (vd 1E+1) cho so nguyen -> ep lai ve plain string
        return percent.toPlainString() + "%";
    }
}
