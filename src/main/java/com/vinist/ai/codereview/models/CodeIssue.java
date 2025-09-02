package com.vinist.ai.codereview.models;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * 代码问题模型类
 * 用于表示代码审查中发现的单个问题
 */
public class CodeIssue {
    
    private String message;
    private IssueSeverity severity;
    private int lineNumber;
    private String category;
    private String suggestion;
    private String codeSnippet;
    private String ruleId;
    private String fileName;
    private int startColumn;
    private int endColumn;
    private String description;
    
    /**
     * 默认构造函数
     */
    public CodeIssue() {
        this.severity = IssueSeverity.INFO;
        this.lineNumber = -1;
        this.startColumn = -1;
        this.endColumn = -1;
    }
    
    /**
     * 构造函数
     */
    public CodeIssue(@NotNull String message, @NotNull IssueSeverity severity) {
        this();
        this.message = message;
        this.severity = severity;
    }
    
    /**
     * 构造函数
     */
    public CodeIssue(@NotNull String message, @NotNull IssueSeverity severity, int lineNumber) {
        this(message, severity);
        this.lineNumber = lineNumber;
    }
    
    /**
     * 构造函数
     */
    public CodeIssue(@NotNull String message, @NotNull IssueSeverity severity, int lineNumber, @Nullable String category) {
        this(message, severity, lineNumber);
        this.category = category;
    }
    
    /**
     * 完整构造函数
     */
    public CodeIssue(@NotNull String message, @NotNull IssueSeverity severity, int lineNumber, 
                     @Nullable String category, @Nullable String suggestion, @Nullable String codeSnippet, 
                     @Nullable String ruleId) {
        this(message, severity, lineNumber, category);
        this.suggestion = suggestion;
        this.codeSnippet = codeSnippet;
        this.ruleId = ruleId;
    }
    
    // Getter和Setter方法
    
    @Nullable
    public String getMessage() {
        return message;
    }
    
    public void setMessage(@Nullable String message) {
        this.message = message;
    }
    
    @NotNull
    public IssueSeverity getSeverity() {
        return severity;
    }
    
    public void setSeverity(@NotNull IssueSeverity severity) {
        this.severity = severity;
    }
    
    public int getLineNumber() {
        return lineNumber;
    }
    
    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }
    
    @Nullable
    public String getCategory() {
        return category;
    }
    
    public void setCategory(@Nullable String category) {
        this.category = category;
    }
    
    @Nullable
    public String getSuggestion() {
        return suggestion;
    }
    
    public void setSuggestion(@Nullable String suggestion) {
        this.suggestion = suggestion;
    }
    
    @Nullable
    public String getCodeSnippet() {
        return codeSnippet;
    }
    
    public void setCodeSnippet(@Nullable String codeSnippet) {
        this.codeSnippet = codeSnippet;
    }
    
    @Nullable
    public String getRuleId() {
        return ruleId;
    }
    
    public void setRuleId(@Nullable String ruleId) {
        this.ruleId = ruleId;
    }
    
    @Nullable
    public String getFileName() {
        return fileName;
    }
    
    public void setFileName(@Nullable String fileName) {
        this.fileName = fileName;
    }
    
    public int getStartColumn() {
        return startColumn;
    }
    
    public void setStartColumn(int startColumn) {
        this.startColumn = startColumn;
    }
    
    public int getEndColumn() {
        return endColumn;
    }
    
    public void setEndColumn(int endColumn) {
        this.endColumn = endColumn;
    }
    
    @Nullable
    public String getDescription() {
        return description;
    }
    
    public void setDescription(@Nullable String description) {
        this.description = description;
    }
    
    // 便利方法
    
    /**
     * 检查是否是错误级别的问题
     */
    public boolean isError() {
        return severity == IssueSeverity.ERROR || severity == IssueSeverity.CRITICAL;
    }
    
    /**
     * 检查是否是警告级别的问题
     */
    public boolean isWarning() {
        return severity == IssueSeverity.WARNING;
    }
    
    /**
     * 检查是否是信息级别的问题
     */
    public boolean isInfo() {
        return severity == IssueSeverity.INFO;
    }
    
    /**
     * 检查是否是严重级别的问题
     */
    public boolean isCritical() {
        return severity == IssueSeverity.CRITICAL;
    }
    
    /**
     * 检查是否有行号信息
     */
    public boolean hasLineNumber() {
        return lineNumber > 0;
    }
    
    /**
     * 检查是否有列号信息
     */
    public boolean hasColumnInfo() {
        return startColumn >= 0 && endColumn >= 0;
    }
    
    /**
     * 检查是否有建议
     */
    public boolean hasSuggestion() {
        return suggestion != null && !suggestion.trim().isEmpty();
    }
    
    /**
     * 检查是否有代码片段
     */
    public boolean hasCodeSnippet() {
        return codeSnippet != null && !codeSnippet.trim().isEmpty();
    }
    
    /**
     * 检查是否有规则ID
     */
    public boolean hasRuleId() {
        return ruleId != null && !ruleId.trim().isEmpty();
    }
    
    /**
     * 检查是否有分类信息
     */
    public boolean hasCategory() {
        return category != null && !category.trim().isEmpty();
    }
    
    /**
     * 获取问题的完整描述
     */
    @NotNull
    public String getFullDescription() {
        StringBuilder sb = new StringBuilder();
        
        if (message != null) {
            sb.append(message);
        }
        
        if (description != null && !description.trim().isEmpty()) {
            if (sb.length() > 0) {
                sb.append(" - ");
            }
            sb.append(description);
        }
        
        return sb.toString();
    }
    
    /**
     * 获取位置信息字符串
     */
    @NotNull
    public String getLocationString() {
        StringBuilder sb = new StringBuilder();
        
        if (fileName != null) {
            sb.append(fileName);
        }
        
        if (hasLineNumber()) {
            if (sb.length() > 0) {
                sb.append(":");
            }
            sb.append(lineNumber);
            
            if (hasColumnInfo()) {
                sb.append(":").append(startColumn);
                if (startColumn != endColumn) {
                    sb.append("-").append(endColumn);
                }
            }
        }
        
        return sb.toString();
    }
    
    /**
     * 获取格式化的问题字符串
     */
    @NotNull
    public String getFormattedString() {
        StringBuilder sb = new StringBuilder();
        
        // 添加严重级别
        sb.append("[").append(severity.getDisplayName()).append("] ");
        
        // 添加位置信息
        String location = getLocationString();
        if (!location.isEmpty()) {
            sb.append(location).append(": ");
        }
        
        // 添加消息
        sb.append(getFullDescription());
        
        // 添加分类
        if (hasCategory()) {
            sb.append(" (").append(category).append(")");
        }
        
        // 添加规则ID
        if (hasRuleId()) {
            sb.append(" [Rule: ").append(ruleId).append("]");
        }
        
        return sb.toString();
    }
    
    /**
     * 创建问题的副本
     */
    @NotNull
    public CodeIssue copy() {
        CodeIssue copy = new CodeIssue();
        copy.message = this.message;
        copy.severity = this.severity;
        copy.lineNumber = this.lineNumber;
        copy.category = this.category;
        copy.suggestion = this.suggestion;
        copy.codeSnippet = this.codeSnippet;
        copy.ruleId = this.ruleId;
        copy.fileName = this.fileName;
        copy.startColumn = this.startColumn;
        copy.endColumn = this.endColumn;
        copy.description = this.description;
        return copy;
    }
    
    /**
     * 构建器模式
     */
    public static class Builder {
        private final CodeIssue issue;
        
        public Builder(@NotNull String message, @NotNull IssueSeverity severity) {
            this.issue = new CodeIssue(message, severity);
        }
        
        public Builder lineNumber(int lineNumber) {
            issue.setLineNumber(lineNumber);
            return this;
        }
        
        public Builder category(@Nullable String category) {
            issue.setCategory(category);
            return this;
        }
        
        public Builder suggestion(@Nullable String suggestion) {
            issue.setSuggestion(suggestion);
            return this;
        }
        
        public Builder codeSnippet(@Nullable String codeSnippet) {
            issue.setCodeSnippet(codeSnippet);
            return this;
        }
        
        public Builder ruleId(@Nullable String ruleId) {
            issue.setRuleId(ruleId);
            return this;
        }
        
        public Builder fileName(@Nullable String fileName) {
            issue.setFileName(fileName);
            return this;
        }
        
        public Builder columnRange(int startColumn, int endColumn) {
            issue.setStartColumn(startColumn);
            issue.setEndColumn(endColumn);
            return this;
        }
        
        public Builder description(@Nullable String description) {
            issue.setDescription(description);
            return this;
        }
        
        @NotNull
        public CodeIssue build() {
            return issue;
        }
    }
    
    // Object方法重写
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        CodeIssue codeIssue = (CodeIssue) o;
        
        return lineNumber == codeIssue.lineNumber &&
               startColumn == codeIssue.startColumn &&
               endColumn == codeIssue.endColumn &&
               Objects.equals(message, codeIssue.message) &&
               severity == codeIssue.severity &&
               Objects.equals(category, codeIssue.category) &&
               Objects.equals(suggestion, codeIssue.suggestion) &&
               Objects.equals(codeSnippet, codeIssue.codeSnippet) &&
               Objects.equals(ruleId, codeIssue.ruleId) &&
               Objects.equals(fileName, codeIssue.fileName) &&
               Objects.equals(description, codeIssue.description);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(message, severity, lineNumber, category, suggestion, 
                          codeSnippet, ruleId, fileName, startColumn, endColumn, description);
    }
    
    @Override
    public String toString() {
        return getFormattedString();
    }
}