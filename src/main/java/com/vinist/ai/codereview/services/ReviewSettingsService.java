package com.vinist.ai.codereview.services;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.vinist.ai.codereview.models.IssueSeverity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 审查设置服务
 * 管理代码审查相关的设置
 */
@Service
@State(name = "ReviewSettingsService", storages = @Storage("ai-code-review-settings.xml"))
public final class ReviewSettingsService implements PersistentStateComponent<ReviewSettingsService.State> {
    
    public static class State {
        public boolean enableAutoReview = false;
        public boolean showReviewDialog = true;
        public boolean outputToConsole = true;
        public String reviewLanguage = "Chinese";
        public String reviewFocus = "all";
        public String minSeverityLevel = "INFO";
        public boolean enableLineNumbers = true;
        public boolean enableCodeSuggestions = true;
        public boolean enableCategoryFiltering = false;
        public String excludedCategories = "";
        public int maxIssuesPerFile = 50;
        public boolean enableSoundNotification = false;
        public boolean autoSaveReports = false;
        public String reportSavePath = "";
    }
    
    private State state = new State();
    
    public static ReviewSettingsService getInstance() {
        return ApplicationManager.getApplication().getService(ReviewSettingsService.class);
    }
    
    @Override
    public @Nullable State getState() {
        return state;
    }
    
    @Override
    public void loadState(@NotNull State state) {
        XmlSerializerUtil.copyBean(state, this.state);
    }
    
    // Getters and Setters
    
    public boolean isEnableAutoReview() {
        return state.enableAutoReview;
    }
    
    public void setEnableAutoReview(boolean enableAutoReview) {
        state.enableAutoReview = enableAutoReview;
    }
    
    public boolean isShowReviewDialog() {
        return state.showReviewDialog;
    }
    
    public void setShowReviewDialog(boolean showReviewDialog) {
        state.showReviewDialog = showReviewDialog;
    }
    
    public boolean isOutputToConsole() {
        return state.outputToConsole;
    }
    
    public void setOutputToConsole(boolean outputToConsole) {
        state.outputToConsole = outputToConsole;
    }
    
    public String getReviewLanguage() {
        return state.reviewLanguage;
    }
    
    public void setReviewLanguage(String reviewLanguage) {
        state.reviewLanguage = reviewLanguage;
    }
    
    public String getReviewFocus() {
        return state.reviewFocus;
    }
    
    public void setReviewFocus(String reviewFocus) {
        state.reviewFocus = reviewFocus;
    }
    
    public String getMinSeverityLevel() {
        return state.minSeverityLevel;
    }
    
    public void setMinSeverityLevel(String minSeverityLevel) {
        state.minSeverityLevel = minSeverityLevel;
    }
    
    public boolean isEnableLineNumbers() {
        return state.enableLineNumbers;
    }
    
    public void setEnableLineNumbers(boolean enableLineNumbers) {
        state.enableLineNumbers = enableLineNumbers;
    }
    
    public boolean isEnableCodeSuggestions() {
        return state.enableCodeSuggestions;
    }
    
    public void setEnableCodeSuggestions(boolean enableCodeSuggestions) {
        state.enableCodeSuggestions = enableCodeSuggestions;
    }
    
    public boolean isEnableCategoryFiltering() {
        return state.enableCategoryFiltering;
    }
    
    public void setEnableCategoryFiltering(boolean enableCategoryFiltering) {
        state.enableCategoryFiltering = enableCategoryFiltering;
    }
    
    public String getExcludedCategories() {
        return state.excludedCategories;
    }
    
    public void setExcludedCategories(String excludedCategories) {
        state.excludedCategories = excludedCategories;
    }
    
    public int getMaxIssuesPerFile() {
        return state.maxIssuesPerFile;
    }
    
    public void setMaxIssuesPerFile(int maxIssuesPerFile) {
        state.maxIssuesPerFile = maxIssuesPerFile;
    }
    
    public boolean isEnableSoundNotification() {
        return state.enableSoundNotification;
    }
    
    public void setEnableSoundNotification(boolean enableSoundNotification) {
        state.enableSoundNotification = enableSoundNotification;
    }
    
    public boolean isAutoSaveReports() {
        return state.autoSaveReports;
    }
    
    public void setAutoSaveReports(boolean autoSaveReports) {
        state.autoSaveReports = autoSaveReports;
    }
    
    public String getReportSavePath() {
        return state.reportSavePath;
    }
    
    public void setReportSavePath(String reportSavePath) {
        state.reportSavePath = reportSavePath;
    }
    
    // 添加缺失的方法
    public boolean isShowLineNumbers() {
        return state.enableLineNumbers;
    }
    
    public void setShowLineNumbers(boolean showLineNumbers) {
        state.enableLineNumbers = showLineNumbers;
    }
    
    public boolean isSaveReports() {
        return state.autoSaveReports;
    }
    
    public void setSaveReports(boolean saveReports) {
        state.autoSaveReports = saveReports;
    }
    
    // 便利方法
    
    /**
     * 获取最小严重级别枚举
     */
    public IssueSeverity getMinSeverityLevelEnum() {
        return IssueSeverity.fromName(state.minSeverityLevel);
    }
    
    /**
     * 设置最小严重级别枚举
     */
    public void setMinSeverityLevelEnum(IssueSeverity severity) {
        state.minSeverityLevel = severity.name();
    }
    
    /**
     * 检查是否应该显示指定严重级别的问题
     */
    public boolean shouldShowSeverity(IssueSeverity severity) {
        IssueSeverity minSeverity = getMinSeverityLevelEnum();
        return severity.meetsMinimumLevel(minSeverity);
    }
    
    /**
     * 获取排除的类别列表
     */
    public String[] getExcludedCategoriesArray() {
        if (state.excludedCategories == null || state.excludedCategories.trim().isEmpty()) {
            return new String[0];
        }
        return state.excludedCategories.split(",");
    }
    
    /**
     * 检查类别是否被排除
     */
    public boolean isCategoryExcluded(String category) {
        if (!state.enableCategoryFiltering || category == null) {
            return false;
        }
        
        String[] excludedCategories = getExcludedCategoriesArray();
        for (String excluded : excludedCategories) {
            if (category.trim().equalsIgnoreCase(excluded.trim())) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 重置为默认设置
     */
    public void resetToDefaults() {
        state.enableAutoReview = false;
        state.showReviewDialog = true;
        state.outputToConsole = true;
        state.reviewLanguage = "Chinese";
        state.reviewFocus = "all";
        state.minSeverityLevel = "INFO";
        state.enableLineNumbers = true;
        state.enableCodeSuggestions = true;
        state.enableCategoryFiltering = false;
        state.excludedCategories = "";
        state.maxIssuesPerFile = 50;
        state.enableSoundNotification = false;
        state.autoSaveReports = false;
        state.reportSavePath = "";
    }
    
    /**
     * 验证设置
     */
    public String validateSettings() {
        if (state.maxIssuesPerFile <= 0 || state.maxIssuesPerFile > 1000) {
            return "每个文件的最大问题数量必须在1-1000之间";
        }
        
        if (state.autoSaveReports && (state.reportSavePath == null || state.reportSavePath.trim().isEmpty())) {
            return "启用自动保存报告时，必须指定保存路径";
        }
        
        return null; // 设置有效
    }
}