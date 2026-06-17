package com.preptrack.preptrack.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "problems")
public class Problem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    // LeetCode Difficulty
    private String difficulty;

    // Your Personal Difficulty
    private String personalDifficulty;

    private String topic;

    private String company;

    private String status;

    private String leetcodeUrl;

    // Old confidence field (keep for now)
    private String confidence;

    // New confidence percentage
    private Integer confidencePercentage;

    // Number of revisions
    private Integer revisionCount;

    private LocalDate solvedDate;

    @Column(length = 2000)
    private String notes;

    private boolean revisionNeeded;

    public Problem() {}

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getPersonalDifficulty() {
        return personalDifficulty;
    }

    public void setPersonalDifficulty(String personalDifficulty) {
        this.personalDifficulty = personalDifficulty;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLeetcodeUrl() {
        return leetcodeUrl;
    }

    public void setLeetcodeUrl(String leetcodeUrl) {
        this.leetcodeUrl = leetcodeUrl;
    }

    public String getConfidence() {
        return confidence;
    }

    public void setConfidence(String confidence) {
        this.confidence = confidence;
    }

    public Integer getConfidencePercentage() {
        return confidencePercentage;
    }

    public void setConfidencePercentage(Integer confidencePercentage) {
        this.confidencePercentage = confidencePercentage;
    }

    public Integer getRevisionCount() {
        return revisionCount;
    }

    public void setRevisionCount(Integer revisionCount) {
        this.revisionCount = revisionCount;
    }

    public LocalDate getSolvedDate() {
        return solvedDate;
    }

    public void setSolvedDate(LocalDate solvedDate) {
        this.solvedDate = solvedDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public boolean isRevisionNeeded() {
        return revisionNeeded;
    }

    public void setRevisionNeeded(boolean revisionNeeded) {
        this.revisionNeeded = revisionNeeded;
    }
}