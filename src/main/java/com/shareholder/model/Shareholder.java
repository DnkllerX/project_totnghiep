package com.shareholder.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Shareholder {
    private int shareholderId;
    private int userId;
    private String fullName;
    private String citizenId;
    private String phone;
    private String address;
    private LocalDate birthDate;
    private String nationality;
    private LocalDateTime createdAt;

    public Shareholder() {}

    public Shareholder(int shareholderId, int userId, String fullName, String citizenId,
                        String phone, String address, LocalDate birthDate,
                        String nationality, LocalDateTime createdAt) {
        this.shareholderId = shareholderId;
        this.userId = userId;
        this.fullName = fullName;
        this.citizenId = citizenId;
        this.phone = phone;
        this.address = address;
        this.birthDate = birthDate;
        this.nationality = nationality;
        this.createdAt = createdAt;
    }

    public int getShareholderId() { return shareholderId; }
    public void setShareholderId(int shareholderId) { this.shareholderId = shareholderId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getCitizenId() { return citizenId; }
    public void setCitizenId(String citizenId) { this.citizenId = citizenId; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }

    public String getNationality() { return nationality; }
    public void setNationality(String nationality) { this.nationality = nationality; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
