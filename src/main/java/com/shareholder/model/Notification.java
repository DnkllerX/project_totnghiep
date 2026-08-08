package com.shareholder.model;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Khong phai bang trong DB - day la model tong hop (aggregate) cho trang "Thong bao" cua co dong,
 * ghep tu 2 nguon du lieu that: VOTES (da bau) va SHARE_ISSUES/SHARE_ISSUE_DETAILS (phat hanh co tuc).
 *
 * type    - nhan dien nguon du lieu: "Biểu quyết" (tu bang VOTES) hoac "Phát hành cổ tức" (tu bang SHARE_ISSUES)
 * title   - tieu de: ten nghi quyet hoac ten dot phat hanh
 * infoLines - cac dong thong tin chinh (khac nhau theo type, xem NotificationService)
 * note    - ghi chu (ngay ket thuc bieu quyet, hoac thoi gian mo/dong ky nhan)
 * eventTime - moc thoi gian dung de sap xep danh sach (moi nhat len dau)
 */
public class Notification {
    private String type;
    private String title;
    private List<String> infoLines;
    private String note;
    private LocalDateTime eventTime;

    public Notification() {}

    public Notification(String type, String title, List<String> infoLines, String note, LocalDateTime eventTime) {
        this.type = type;
        this.title = title;
        this.infoLines = infoLines;
        this.note = note;
        this.eventTime = eventTime;
    }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public List<String> getInfoLines() { return infoLines; }
    public void setInfoLines(List<String> infoLines) { this.infoLines = infoLines; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public LocalDateTime getEventTime() { return eventTime; }
    public void setEventTime(LocalDateTime eventTime) { this.eventTime = eventTime; }
}
