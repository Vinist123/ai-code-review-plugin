package com.vinist.ai.codereview.models;

import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 代码评审报告模型类
 * 包含评审的所有信息和统计数据
 */
public class ReviewReport {
    private String fileName;
    private String filePath;
    private List<CodeIssue> issues;
    private LocalDateTime reviewTime;
    private String reviewLanguage;
    private String reviewFocus;
    private long reviewDuration; // 评审耗时（毫秒）
    private String llmModel;
    private String llmProvider;
    private Map<String, Object> metadata;
    private String reviewerId; // 审查者ID
    private String summary; // 审查摘要

    public ReviewReport() {
        this.issues = new ArrayList<>();
        this.reviewTime = LocalDateTime.now();
        this.metadata = new HashMap<>();
    }

    public ReviewReport(@NotNull String fileName, @NotNull String filePath) {
        this();
        this.fileName = fileName;
        this.filePath = filePath;
    }

    // Getters and Setters
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public List<CodeIssue> getIssues() {
        return issues;
    }

    public void setIssues(List<CodeIssue> issues) {
        this.issues = issues != null ? issues : new ArrayList<>();
    }

    public LocalDateTime getReviewTime() {
        return reviewTime;
    }

    public void setReviewTime(LocalDateTime reviewTime) {
        this.reviewTime = reviewTime;
    }

    public String getReviewLanguage() {
        return reviewLanguage;
    }

    public void setReviewLanguage(String reviewLanguage) {
        this.reviewLanguage = reviewLanguage;
    }

    public String getReviewFocus() {
        return reviewFocus;
    }

    public void setReviewFocus(String reviewFocus) {
        this.reviewFocus = reviewFocus;
    }

    public long getReviewDuration() {
        return reviewDuration;
    }

    public void setReviewDuration(long reviewDuration) {
        this.reviewDuration = reviewDuration;
    }

    public String getLlmModel() {
        return llmModel;
    }

    public void setLlmModel(String llmModel) {
        this.llmModel = llmModel;
    }

    public String getLlmProvider() {
        return llmProvider;
    }

    public void setLlmProvider(String llmProvider) {
        this.llmProvider = llmProvider;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata != null ? metadata : new HashMap<>();
    }

    public String getReviewerId() {
        return reviewerId;
    }

    public void setReviewerId(String reviewerId) {
        this.reviewerId = reviewerId;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    /**
     * 获取时间戳（毫秒）
     * 兼容UI层的调用
     */
    public long getTimestamp() {
        if (reviewTime == null) {
            return System.currentTimeMillis();
        }
        return reviewTime.atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    /**
     * 获取审查耗时（毫秒）
     * 兼容UI层的调用
     */
    public long getReviewDurationMs() {
        return reviewDuration;
    }

    // 便利方法
    public void addIssue(@NotNull CodeIssue issue) {
        if (this.issues == null) {
            this.issues = new ArrayList<>();
        }
        this.issues.add(issue);
    }

    public void addIssues(@NotNull Collection<CodeIssue> issues) {
        if (this.issues == null) {
            this.issues = new ArrayList<>();
        }
        this.issues.addAll(issues);
    }

    public void addMetadata(@NotNull String key, @Nullable Object value) {
        if (this.metadata == null) {
            this.metadata = new HashMap<>();
        }
        this.metadata.put(key, value);
    }

    // 统计方法
    public int getTotalIssueCount() {
        return issues != null ? issues.size() : 0;
    }

    public int getIssueCountBySeverity(@NotNull IssueSeverity severity) {
        if (issues == null) return 0;
        return (int) issues.stream()
                .filter(issue -> severity.equals(issue.getSeverity()))
                .count();
    }

    public int getErrorCount() {
        return getIssueCountBySeverity(IssueSeverity.ERROR);
    }

    public int getWarningCount() {
        return getIssueCountBySeverity(IssueSeverity.WARNING);
    }

    public int getInfoCount() {
        return getIssueCountBySeverity(IssueSeverity.INFO);
    }

    public Map<IssueSeverity, Integer> getSeverityStatistics() {
        Map<IssueSeverity, Integer> stats = new EnumMap<>(IssueSeverity.class);
        for (IssueSeverity severity : IssueSeverity.values()) {
            stats.put(severity, getIssueCountBySeverity(severity));
        }
        return stats;
    }

    public Map<String, Integer> getCategoryStatistics() {
        if (issues == null) return new HashMap<>();
        return issues.stream()
                .filter(issue -> !StringUtil.isEmpty(issue.getCategory()))
                .collect(Collectors.groupingBy(
                        CodeIssue::getCategory,
                        Collectors.collectingAndThen(Collectors.counting(), Math::toIntExact)
                ));
    }

    public List<CodeIssue> getIssuesBySeverity(@NotNull IssueSeverity severity) {
        if (issues == null) return new ArrayList<>();
        return issues.stream()
                .filter(issue -> severity.equals(issue.getSeverity()))
                .collect(Collectors.toList());
    }

    public List<CodeIssue> getIssuesByCategory(@NotNull String category) {
        if (issues == null) return new ArrayList<>();
        return issues.stream()
                .filter(issue -> category.equals(issue.getCategory()))
                .collect(Collectors.toList());
    }

    // 检查方法
    public boolean hasIssues() {
        return issues != null && !issues.isEmpty();
    }

    public boolean hasErrors() {
        return getErrorCount() > 0;
    }

    public boolean hasWarnings() {
        return getWarningCount() > 0;
    }

    public boolean hasCriticalIssues() {
        return hasErrors();
    }

    public boolean isEmpty() {
        return !hasIssues();
    }

    // 格式化方法
    public String getFormattedReviewTime() {
        if (reviewTime == null) return "Unknown";
        return reviewTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public String getFormattedDuration() {
        if (reviewDuration <= 0) return "Unknown";
        if (reviewDuration < 1000) {
            return reviewDuration + "ms";
        } else if (reviewDuration < 60000) {
            return String.format("%.1fs", reviewDuration / 1000.0);
        } else {
            return String.format("%.1fm", reviewDuration / 60000.0);
        }
    }

    public String getSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("File: ").append(fileName != null ? fileName : "Unknown");
        summary.append(", Issues: ").append(getTotalIssueCount());
        if (hasErrors()) {
            summary.append(" (Errors: ").append(getErrorCount()).append(")");
        }
        if (hasWarnings()) {
            summary.append(" (Warnings: ").append(getWarningCount()).append(")");
        }
        return summary.toString();
    }

    // 复制方法
    public ReviewReport copy() {
        ReviewReport copy = new ReviewReport();
        copy.fileName = this.fileName;
        copy.filePath = this.filePath;
        copy.issues = this.issues != null ? new ArrayList<>(this.issues) : new ArrayList<>();
        copy.reviewTime = this.reviewTime;
        copy.reviewLanguage = this.reviewLanguage;
        copy.reviewFocus = this.reviewFocus;
        copy.reviewDuration = this.reviewDuration;
        copy.llmModel = this.llmModel;
        copy.llmProvider = this.llmProvider;
        copy.metadata = this.metadata != null ? new HashMap<>(this.metadata) : new HashMap<>();
        copy.reviewerId = this.reviewerId;
        copy.summary = this.summary;
        return copy;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        ReviewReport that = (ReviewReport) obj;
        return reviewDuration == that.reviewDuration &&
               Objects.equals(fileName, that.fileName) &&
               Objects.equals(filePath, that.filePath) &&
               Objects.equals(issues, that.issues) &&
               Objects.equals(reviewTime, that.reviewTime) &&
               Objects.equals(reviewLanguage, that.reviewLanguage) &&
               Objects.equals(reviewFocus, that.reviewFocus) &&
               Objects.equals(llmModel, that.llmModel) &&
               Objects.equals(llmProvider, that.llmProvider) &&
               Objects.equals(metadata, that.metadata) &&
               Objects.equals(reviewerId, that.reviewerId) &&
               Objects.equals(summary, that.summary);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fileName, filePath, issues, reviewTime, reviewLanguage, 
                           reviewFocus, reviewDuration, llmModel, llmProvider, metadata,
                           reviewerId, summary);
    }

    @Override
    public String toString() {
        return "ReviewReport{" +
                "fileName='" + fileName + '\'' +
                ", filePath='" + filePath + '\'' +
                ", issueCount=" + getTotalIssueCount() +
                ", errorCount=" + getErrorCount() +
                ", warningCount=" + getWarningCount() +
                ", reviewTime=" + getFormattedReviewTime() +
                ", duration=" + getFormattedDuration() +
                ", llmModel='" + llmModel + '\'' +
                ", llmProvider='" + llmProvider + '\'' +
                '}';
    }

    /**
     * Builder类用于构建ReviewReport对象
     */
    public static class Builder {
        private final ReviewReport report;

        public Builder() {
            this.report = new ReviewReport();
        }

        public Builder(@NotNull String fileName, @NotNull String filePath) {
            this.report = new ReviewReport(fileName, filePath);
        }

        public Builder fileName(String fileName) {
            report.setFileName(fileName);
            return this;
        }

        public Builder filePath(String filePath) {
            report.setFilePath(filePath);
            return this;
        }

        public Builder issues(List<CodeIssue> issues) {
            report.setIssues(issues);
            return this;
        }

        public Builder addIssue(CodeIssue issue) {
            report.addIssue(issue);
            return this;
        }

        public Builder reviewTime(LocalDateTime reviewTime) {
            report.setReviewTime(reviewTime);
            return this;
        }

        public Builder reviewLanguage(String reviewLanguage) {
            report.setReviewLanguage(reviewLanguage);
            return this;
        }

        public Builder reviewFocus(String reviewFocus) {
            report.setReviewFocus(reviewFocus);
            return this;
        }

        public Builder reviewDuration(long reviewDuration) {
            report.setReviewDuration(reviewDuration);
            return this;
        }

        public Builder llmModel(String llmModel) {
            report.setLlmModel(llmModel);
            return this;
        }

        public Builder llmProvider(String llmProvider) {
            report.setLlmProvider(llmProvider);
            return this;
        }

        public Builder metadata(Map<String, Object> metadata) {
            report.setMetadata(metadata);
            return this;
        }

        public Builder addMetadata(String key, Object value) {
            report.addMetadata(key, value);
            return this;
        }

        public Builder reviewerId(String reviewerId) {
            report.setReviewerId(reviewerId);
            return this;
        }

        public Builder summary(String summary) {
            report.setSummary(summary);
            return this;
        }

        public ReviewReport build() {
            return report;
        }
    }
}