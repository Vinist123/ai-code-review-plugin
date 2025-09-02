package com.vinist.ai.codereview.models;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * 代码审查设置模型类
 */
public class ReviewSettings {
    
    // 基本设置
    private boolean autoReviewEnabled;
    private boolean showReviewDialog;
    private boolean outputToConsole;
    private String reviewLanguage;
    private String reviewFocus;
    
    // 问题过滤设置
    private IssueSeverity minimumSeverityLevel;
    private boolean showLineNumbers;
    private boolean showCodeSuggestions;
    private Set<String> enabledCategories;
    private int maxIssuesPerFile;
    
    // 通知设置
    private boolean soundNotificationEnabled;
    private boolean saveReportToFile;
    private String reportSaveDirectory;
    
    // 高级设置
    private boolean enableCustomRules;
    private String customRulesPath;
    private boolean enableMetrics;
    private boolean enablePerformanceAnalysis;
    
    // 默认值常量
    public static final boolean DEFAULT_AUTO_REVIEW_ENABLED = false;
    public static final boolean DEFAULT_SHOW_REVIEW_DIALOG = true;
    public static final boolean DEFAULT_OUTPUT_TO_CONSOLE = true;
    public static final String DEFAULT_REVIEW_LANGUAGE = "Chinese";
    public static final String DEFAULT_REVIEW_FOCUS = "General";
    public static final IssueSeverity DEFAULT_MINIMUM_SEVERITY_LEVEL = IssueSeverity.INFO;
    public static final boolean DEFAULT_SHOW_LINE_NUMBERS = true;
    public static final boolean DEFAULT_SHOW_CODE_SUGGESTIONS = true;
    public static final int DEFAULT_MAX_ISSUES_PER_FILE = 50;
    public static final boolean DEFAULT_SOUND_NOTIFICATION_ENABLED = false;
    public static final boolean DEFAULT_SAVE_REPORT_TO_FILE = false;
    public static final boolean DEFAULT_ENABLE_CUSTOM_RULES = false;
    public static final boolean DEFAULT_ENABLE_METRICS = true;
    public static final boolean DEFAULT_ENABLE_PERFORMANCE_ANALYSIS = false;
    
    /**
     * 默认构造函数
     */
    public ReviewSettings() {
        resetToDefaults();
    }
    
    /**
     * 复制构造函数
     */
    public ReviewSettings(@NotNull ReviewSettings other) {
        this.autoReviewEnabled = other.autoReviewEnabled;
        this.showReviewDialog = other.showReviewDialog;
        this.outputToConsole = other.outputToConsole;
        this.reviewLanguage = other.reviewLanguage;
        this.reviewFocus = other.reviewFocus;
        this.minimumSeverityLevel = other.minimumSeverityLevel;
        this.showLineNumbers = other.showLineNumbers;
        this.showCodeSuggestions = other.showCodeSuggestions;
        this.enabledCategories = new HashSet<>(other.enabledCategories);
        this.maxIssuesPerFile = other.maxIssuesPerFile;
        this.soundNotificationEnabled = other.soundNotificationEnabled;
        this.saveReportToFile = other.saveReportToFile;
        this.reportSaveDirectory = other.reportSaveDirectory;
        this.enableCustomRules = other.enableCustomRules;
        this.customRulesPath = other.customRulesPath;
        this.enableMetrics = other.enableMetrics;
        this.enablePerformanceAnalysis = other.enablePerformanceAnalysis;
    }
    
    // Getter和Setter方法
    
    public boolean isAutoReviewEnabled() {
        return autoReviewEnabled;
    }
    
    public void setAutoReviewEnabled(boolean autoReviewEnabled) {
        this.autoReviewEnabled = autoReviewEnabled;
    }
    
    public boolean isShowReviewDialog() {
        return showReviewDialog;
    }
    
    public void setShowReviewDialog(boolean showReviewDialog) {
        this.showReviewDialog = showReviewDialog;
    }
    
    public boolean isOutputToConsole() {
        return outputToConsole;
    }
    
    public void setOutputToConsole(boolean outputToConsole) {
        this.outputToConsole = outputToConsole;
    }
    
    @NotNull
    public String getReviewLanguage() {
        return reviewLanguage;
    }
    
    public void setReviewLanguage(@NotNull String reviewLanguage) {
        this.reviewLanguage = reviewLanguage;
    }
    
    @NotNull
    public String getReviewFocus() {
        return reviewFocus;
    }
    
    public void setReviewFocus(@NotNull String reviewFocus) {
        this.reviewFocus = reviewFocus;
    }
    
    @NotNull
    public IssueSeverity getMinimumSeverityLevel() {
        return minimumSeverityLevel;
    }
    
    public void setMinimumSeverityLevel(@NotNull IssueSeverity minimumSeverityLevel) {
        this.minimumSeverityLevel = minimumSeverityLevel;
    }
    
    public boolean isShowLineNumbers() {
        return showLineNumbers;
    }
    
    public void setShowLineNumbers(boolean showLineNumbers) {
        this.showLineNumbers = showLineNumbers;
    }
    
    public boolean isShowCodeSuggestions() {
        return showCodeSuggestions;
    }
    
    public void setShowCodeSuggestions(boolean showCodeSuggestions) {
        this.showCodeSuggestions = showCodeSuggestions;
    }
    
    @NotNull
    public Set<String> getEnabledCategories() {
        return enabledCategories;
    }
    
    public void setEnabledCategories(@NotNull Set<String> enabledCategories) {
        this.enabledCategories = new HashSet<>(enabledCategories);
    }
    
    public int getMaxIssuesPerFile() {
        return maxIssuesPerFile;
    }
    
    public void setMaxIssuesPerFile(int maxIssuesPerFile) {
        this.maxIssuesPerFile = Math.max(1, maxIssuesPerFile);
    }
    
    public boolean isSoundNotificationEnabled() {
        return soundNotificationEnabled;
    }
    
    public void setSoundNotificationEnabled(boolean soundNotificationEnabled) {
        this.soundNotificationEnabled = soundNotificationEnabled;
    }
    
    public boolean isSaveReportToFile() {
        return saveReportToFile;
    }
    
    public void setSaveReportToFile(boolean saveReportToFile) {
        this.saveReportToFile = saveReportToFile;
    }
    
    @Nullable
    public String getReportSaveDirectory() {
        return reportSaveDirectory;
    }
    
    public void setReportSaveDirectory(@Nullable String reportSaveDirectory) {
        this.reportSaveDirectory = reportSaveDirectory;
    }
    
    public boolean isEnableCustomRules() {
        return enableCustomRules;
    }
    
    public void setEnableCustomRules(boolean enableCustomRules) {
        this.enableCustomRules = enableCustomRules;
    }
    
    @Nullable
    public String getCustomRulesPath() {
        return customRulesPath;
    }
    
    public void setCustomRulesPath(@Nullable String customRulesPath) {
        this.customRulesPath = customRulesPath;
    }
    
    public boolean isEnableMetrics() {
        return enableMetrics;
    }
    
    public void setEnableMetrics(boolean enableMetrics) {
        this.enableMetrics = enableMetrics;
    }
    
    public boolean isEnablePerformanceAnalysis() {
        return enablePerformanceAnalysis;
    }
    
    public void setEnablePerformanceAnalysis(boolean enablePerformanceAnalysis) {
        this.enablePerformanceAnalysis = enablePerformanceAnalysis;
    }
    
    // 便利方法
    
    /**
     * 重置为默认设置
     */
    public void resetToDefaults() {
        this.autoReviewEnabled = DEFAULT_AUTO_REVIEW_ENABLED;
        this.showReviewDialog = DEFAULT_SHOW_REVIEW_DIALOG;
        this.outputToConsole = DEFAULT_OUTPUT_TO_CONSOLE;
        this.reviewLanguage = DEFAULT_REVIEW_LANGUAGE;
        this.reviewFocus = DEFAULT_REVIEW_FOCUS;
        this.minimumSeverityLevel = DEFAULT_MINIMUM_SEVERITY_LEVEL;
        this.showLineNumbers = DEFAULT_SHOW_LINE_NUMBERS;
        this.showCodeSuggestions = DEFAULT_SHOW_CODE_SUGGESTIONS;
        this.enabledCategories = getDefaultEnabledCategories();
        this.maxIssuesPerFile = DEFAULT_MAX_ISSUES_PER_FILE;
        this.soundNotificationEnabled = DEFAULT_SOUND_NOTIFICATION_ENABLED;
        this.saveReportToFile = DEFAULT_SAVE_REPORT_TO_FILE;
        this.reportSaveDirectory = null;
        this.enableCustomRules = DEFAULT_ENABLE_CUSTOM_RULES;
        this.customRulesPath = null;
        this.enableMetrics = DEFAULT_ENABLE_METRICS;
        this.enablePerformanceAnalysis = DEFAULT_ENABLE_PERFORMANCE_ANALYSIS;
    }
    
    /**
     * 获取默认启用的分类
     */
    @NotNull
    public static Set<String> getDefaultEnabledCategories() {
        Set<String> categories = new HashSet<>();
        categories.add("Code Quality");
        categories.add("Performance");
        categories.add("Security");
        categories.add("Best Practices");
        categories.add("Maintainability");
        return categories;
    }
    
    /**
     * 检查分类是否启用
     */
    public boolean isCategoryEnabled(@NotNull String category) {
        return enabledCategories.contains(category);
    }
    
    /**
     * 启用分类
     */
    public void enableCategory(@NotNull String category) {
        enabledCategories.add(category);
    }
    
    /**
     * 禁用分类
     */
    public void disableCategory(@NotNull String category) {
        enabledCategories.remove(category);
    }
    
    /**
     * 切换分类状态
     */
    public void toggleCategory(@NotNull String category) {
        if (isCategoryEnabled(category)) {
            disableCategory(category);
        } else {
            enableCategory(category);
        }
    }
    
    /**
     * 检查问题是否应该显示（基于严重级别）
     */
    public boolean shouldShowIssue(@NotNull IssueSeverity severity) {
        return severity.getLevel() >= minimumSeverityLevel.getLevel();
    }
    
    /**
     * 检查问题是否应该显示（基于分类）
     */
    public boolean shouldShowIssue(@Nullable String category) {
        return category == null || enabledCategories.isEmpty() || isCategoryEnabled(category);
    }
    
    /**
     * 检查问题是否应该显示（综合判断）
     */
    public boolean shouldShowIssue(@NotNull IssueSeverity severity, @Nullable String category) {
        return shouldShowIssue(severity) && shouldShowIssue(category);
    }
    
    /**
     * 验证设置的有效性
     */
    public boolean isValid() {
        if (reviewLanguage == null || reviewLanguage.trim().isEmpty()) {
            return false;
        }
        
        if (reviewFocus == null || reviewFocus.trim().isEmpty()) {
            return false;
        }
        
        if (minimumSeverityLevel == null) {
            return false;
        }
        
        if (maxIssuesPerFile <= 0) {
            return false;
        }
        
        if (enabledCategories == null) {
            return false;
        }
        
        return true;
    }
    
    /**
     * 获取设置摘要
     */
    @NotNull
    public String getSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("Auto Review: ").append(autoReviewEnabled ? "Enabled" : "Disabled").append("\n");
        sb.append("Language: ").append(reviewLanguage).append("\n");
        sb.append("Focus: ").append(reviewFocus).append("\n");
        sb.append("Min Severity: ").append(minimumSeverityLevel.getDisplayName()).append("\n");
        sb.append("Max Issues: ").append(maxIssuesPerFile).append("\n");
        sb.append("Enabled Categories: ").append(enabledCategories.size()).append("\n");
        return sb.toString();
    }
    
    /**
     * 创建设置的副本
     */
    @NotNull
    public ReviewSettings copy() {
        return new ReviewSettings(this);
    }
    
    /**
     * 构建器模式
     */
    public static class Builder {
        private final ReviewSettings settings;
        
        public Builder() {
            this.settings = new ReviewSettings();
        }
        
        public Builder autoReviewEnabled(boolean enabled) {
            settings.setAutoReviewEnabled(enabled);
            return this;
        }
        
        public Builder showReviewDialog(boolean show) {
            settings.setShowReviewDialog(show);
            return this;
        }
        
        public Builder outputToConsole(boolean output) {
            settings.setOutputToConsole(output);
            return this;
        }
        
        public Builder reviewLanguage(@NotNull String language) {
            settings.setReviewLanguage(language);
            return this;
        }
        
        public Builder reviewFocus(@NotNull String focus) {
            settings.setReviewFocus(focus);
            return this;
        }
        
        public Builder minimumSeverityLevel(@NotNull IssueSeverity severity) {
            settings.setMinimumSeverityLevel(severity);
            return this;
        }
        
        public Builder showLineNumbers(boolean show) {
            settings.setShowLineNumbers(show);
            return this;
        }
        
        public Builder showCodeSuggestions(boolean show) {
            settings.setShowCodeSuggestions(show);
            return this;
        }
        
        public Builder enabledCategories(@NotNull Set<String> categories) {
            settings.setEnabledCategories(categories);
            return this;
        }
        
        public Builder maxIssuesPerFile(int max) {
            settings.setMaxIssuesPerFile(max);
            return this;
        }
        
        public Builder soundNotificationEnabled(boolean enabled) {
            settings.setSoundNotificationEnabled(enabled);
            return this;
        }
        
        public Builder saveReportToFile(boolean save) {
            settings.setSaveReportToFile(save);
            return this;
        }
        
        public Builder reportSaveDirectory(@Nullable String directory) {
            settings.setReportSaveDirectory(directory);
            return this;
        }
        
        public Builder enableCustomRules(boolean enable) {
            settings.setEnableCustomRules(enable);
            return this;
        }
        
        public Builder customRulesPath(@Nullable String path) {
            settings.setCustomRulesPath(path);
            return this;
        }
        
        public Builder enableMetrics(boolean enable) {
            settings.setEnableMetrics(enable);
            return this;
        }
        
        public Builder enablePerformanceAnalysis(boolean enable) {
            settings.setEnablePerformanceAnalysis(enable);
            return this;
        }
        
        @NotNull
        public ReviewSettings build() {
            return settings;
        }
    }
    
    // Object方法重写
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        ReviewSettings that = (ReviewSettings) o;
        
        return autoReviewEnabled == that.autoReviewEnabled &&
               showReviewDialog == that.showReviewDialog &&
               outputToConsole == that.outputToConsole &&
               showLineNumbers == that.showLineNumbers &&
               showCodeSuggestions == that.showCodeSuggestions &&
               maxIssuesPerFile == that.maxIssuesPerFile &&
               soundNotificationEnabled == that.soundNotificationEnabled &&
               saveReportToFile == that.saveReportToFile &&
               enableCustomRules == that.enableCustomRules &&
               enableMetrics == that.enableMetrics &&
               enablePerformanceAnalysis == that.enablePerformanceAnalysis &&
               Objects.equals(reviewLanguage, that.reviewLanguage) &&
               Objects.equals(reviewFocus, that.reviewFocus) &&
               minimumSeverityLevel == that.minimumSeverityLevel &&
               Objects.equals(enabledCategories, that.enabledCategories) &&
               Objects.equals(reportSaveDirectory, that.reportSaveDirectory) &&
               Objects.equals(customRulesPath, that.customRulesPath);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(autoReviewEnabled, showReviewDialog, outputToConsole, reviewLanguage,
                          reviewFocus, minimumSeverityLevel, showLineNumbers, showCodeSuggestions,
                          enabledCategories, maxIssuesPerFile, soundNotificationEnabled,
                          saveReportToFile, reportSaveDirectory, enableCustomRules, customRulesPath,
                          enableMetrics, enablePerformanceAnalysis);
    }
    
    @Override
    public String toString() {
        return "ReviewSettings{" +
               "autoReviewEnabled=" + autoReviewEnabled +
               ", showReviewDialog=" + showReviewDialog +
               ", outputToConsole=" + outputToConsole +
               ", reviewLanguage='" + reviewLanguage + '\'' +
               ", reviewFocus='" + reviewFocus + '\'' +
               ", minimumSeverityLevel=" + minimumSeverityLevel +
               ", showLineNumbers=" + showLineNumbers +
               ", showCodeSuggestions=" + showCodeSuggestions +
               ", enabledCategories=" + enabledCategories +
               ", maxIssuesPerFile=" + maxIssuesPerFile +
               ", soundNotificationEnabled=" + soundNotificationEnabled +
               ", saveReportToFile=" + saveReportToFile +
               ", reportSaveDirectory='" + reportSaveDirectory + '\'' +
               ", enableCustomRules=" + enableCustomRules +
               ", customRulesPath='" + customRulesPath + '\'' +
               ", enableMetrics=" + enableMetrics +
               ", enablePerformanceAnalysis=" + enablePerformanceAnalysis +
               '}';
    }
}