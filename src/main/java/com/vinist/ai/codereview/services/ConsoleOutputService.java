package com.vinist.ai.codereview.services;

import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.vinist.ai.codereview.models.IssueSeverity;
import com.vinist.ai.codereview.models.CodeIssue;
import com.vinist.ai.codereview.models.ReviewReport;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 控制台输出服务
 * 负责将代码审查结果输出到IDE控制台
 */
public class ConsoleOutputService {
    
    private static final String TOOL_WINDOW_ID = "AI Code Review";
    private static final String CONSOLE_TITLE = "Code Review Results";
    
    private final Project project;
    private ConsoleView consoleView;
    
    public ConsoleOutputService(@NotNull Project project) {
        this.project = project;
    }
    
    /**
     * 输出审查报告到控制台
     */
    public void outputReport(@NotNull ReviewReport report) {
        ApplicationManager.getApplication().invokeLater(() -> {
            try {
                ensureConsoleExists();
                if (consoleView != null) {
                    clearConsole();
                    printReportHeader(report);
                    printReportSummary(report);
                    printIssues(report.getIssues());
                    printReportFooter(report);
                }
            } catch (Exception e) {
                // 静默处理异常，避免影响主要功能
            }
        });
    }
    
    /**
     * 输出单个问题到控制台
     */
    public void outputIssue(@NotNull CodeIssue issue) {
        ApplicationManager.getApplication().invokeLater(() -> {
            try {
                ensureConsoleExists();
                if (consoleView != null) {
                    printIssue(issue);
                }
            } catch (Exception e) {
                // 静默处理异常
            }
        });
    }
    
    /**
     * 输出消息到控制台
     */
    public void outputMessage(@NotNull String message) {
        outputMessage(message, ConsoleViewContentType.NORMAL_OUTPUT);
    }
    
    /**
     * 输出错误消息到控制台
     */
    public void outputError(@NotNull String message) {
        outputMessage(message, ConsoleViewContentType.ERROR_OUTPUT);
    }
    
    /**
     * 输出警告消息到控制台
     */
    public void outputWarning(@NotNull String message) {
        outputMessage(message, ConsoleViewContentType.LOG_WARNING_OUTPUT);
    }
    
    /**
     * 输出信息消息到控制台
     */
    public void outputInfo(@NotNull String message) {
        outputMessage(message, ConsoleViewContentType.LOG_INFO_OUTPUT);
    }
    
    /**
     * 输出消息到控制台（指定类型）
     */
    public void outputMessage(@NotNull String message, @NotNull ConsoleViewContentType contentType) {
        ApplicationManager.getApplication().invokeLater(() -> {
            try {
                ensureConsoleExists();
                if (consoleView != null) {
                    consoleView.print(message + "\n", contentType);
                }
            } catch (Exception e) {
                // 静默处理异常
            }
        });
    }
    
    /**
     * 清空控制台
     */
    public void clearConsole() {
        ApplicationManager.getApplication().invokeLater(() -> {
            try {
                if (consoleView != null) {
                    consoleView.clear();
                }
            } catch (Exception e) {
                // 静默处理异常
            }
        });
    }
    
    /**
     * 确保控制台存在
     */
    private void ensureConsoleExists() {
        if (consoleView == null) {
            createConsole();
        }
    }
    
    /**
     * 创建控制台
     */
    private void createConsole() {
        try {
            ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);
            ToolWindow toolWindow = toolWindowManager.getToolWindow(TOOL_WINDOW_ID);
            
            if (toolWindow == null) {
                // 如果工具窗口不存在，使用默认的控制台输出
                return;
            }
            
            // 创建控制台视图
            consoleView = createConsoleView();
            
            if (consoleView != null) {
                // 创建内容并添加到工具窗口
                ContentFactory contentFactory = ContentFactory.getInstance();
                Content content = contentFactory.createContent(consoleView.getComponent(), CONSOLE_TITLE, false);
                toolWindow.getContentManager().addContent(content);
                toolWindow.getContentManager().setSelectedContent(content);
            }
        } catch (Exception e) {
            // 静默处理异常
        }
    }
    
    /**
     * 创建控制台视图
     */
    @Nullable
    private ConsoleView createConsoleView() {
        try {
            // 这里需要根据实际的IntelliJ IDEA API来创建ConsoleView
            // 由于API可能因版本而异，这里提供一个基本的实现框架
            return null; // 实际实现需要根据具体的API
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * 打印报告头部
     */
    private void printReportHeader(@NotNull ReviewReport report) {
        if (consoleView == null) return;
        
        String separator = "=" + "=".repeat(60) + "=";
        consoleView.print(separator + "\n", ConsoleViewContentType.NORMAL_OUTPUT);
        consoleView.print("AI Code Review Report\n", ConsoleViewContentType.LOG_INFO_OUTPUT);
        consoleView.print(separator + "\n", ConsoleViewContentType.NORMAL_OUTPUT);
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        consoleView.print("Generated: " + dateFormat.format(new Date(report.getTimestamp())) + "\n", 
                         ConsoleViewContentType.NORMAL_OUTPUT);
        
        if (report.getFileName() != null) {
            consoleView.print("File: " + report.getFileName() + "\n", ConsoleViewContentType.NORMAL_OUTPUT);
        }
        
        if (report.getReviewerId() != null) {
            consoleView.print("Reviewer: " + report.getReviewerId() + "\n", ConsoleViewContentType.NORMAL_OUTPUT);
        }
        
        consoleView.print("\n", ConsoleViewContentType.NORMAL_OUTPUT);
    }
    
    /**
     * 打印报告摘要
     */
    private void printReportSummary(@NotNull ReviewReport report) {
        if (consoleView == null) return;
        
        consoleView.print("Summary:\n", ConsoleViewContentType.LOG_INFO_OUTPUT);
        consoleView.print("-".repeat(40) + "\n", ConsoleViewContentType.NORMAL_OUTPUT);
        
        if (report.getSummary() != null && !report.getSummary().trim().isEmpty()) {
            consoleView.print(report.getSummary() + "\n", ConsoleViewContentType.NORMAL_OUTPUT);
        }
        
        // 统计信息
        List<CodeIssue> issues = report.getIssues();
        int totalIssues = issues != null ? issues.size() : 0;
        int criticalCount = report.getIssueCountBySeverity(IssueSeverity.CRITICAL);
        int errorCount = report.getIssueCountBySeverity(IssueSeverity.ERROR);
        int warningCount = report.getIssueCountBySeverity(IssueSeverity.WARNING);
        int infoCount = report.getIssueCountBySeverity(IssueSeverity.INFO);
        
        consoleView.print("\nIssue Statistics:\n", ConsoleViewContentType.LOG_INFO_OUTPUT);
        consoleView.print(String.format("  Total Issues: %d\n", totalIssues), ConsoleViewContentType.NORMAL_OUTPUT);
        
        if (criticalCount > 0) {
            consoleView.print(String.format("  Critical: %d\n", criticalCount), ConsoleViewContentType.ERROR_OUTPUT);
        }
        if (errorCount > 0) {
            consoleView.print(String.format("  Error: %d\n", errorCount), ConsoleViewContentType.ERROR_OUTPUT);
        }
        if (warningCount > 0) {
            consoleView.print(String.format("  Warning: %d\n", warningCount), ConsoleViewContentType.LOG_WARNING_OUTPUT);
        }
        if (infoCount > 0) {
            consoleView.print(String.format("  Info: %d\n", infoCount), ConsoleViewContentType.LOG_INFO_OUTPUT);
        }
        
        consoleView.print("\n", ConsoleViewContentType.NORMAL_OUTPUT);
    }
    
    /**
     * 打印问题列表
     */
    private void printIssues(@NotNull List<CodeIssue> issues) {
        if (consoleView == null || issues.isEmpty()) return;
        
        consoleView.print("Issues Found:\n", ConsoleViewContentType.LOG_INFO_OUTPUT);
        consoleView.print("-".repeat(40) + "\n", ConsoleViewContentType.NORMAL_OUTPUT);
        
        for (int i = 0; i < issues.size(); i++) {
            CodeIssue issue = issues.get(i);
            consoleView.print(String.format("\n%d. ", i + 1), ConsoleViewContentType.NORMAL_OUTPUT);
            printIssue(issue);
        }
    }
    
    /**
     * 打印单个问题
     */
    private void printIssue(@NotNull CodeIssue issue) {
        if (consoleView == null) return;
        
        // 根据严重级别选择输出类型
        ConsoleViewContentType contentType = getContentTypeForSeverity(issue.getSeverity());
        
        // 打印问题信息
        StringBuilder issueText = new StringBuilder();
        issueText.append("[").append(issue.getSeverity().getDisplayName()).append("] ");
        
        if (issue.hasLineNumber()) {
            issueText.append("Line ").append(issue.getLineNumber()).append(": ");
        }
        
        issueText.append(issue.getMessage());
        
        if (issue.hasCategory()) {
            issueText.append(" (").append(issue.getCategory()).append(")");
        }
        
        consoleView.print(issueText.toString() + "\n", contentType);
        
        // 打印代码片段
        if (issue.hasCodeSnippet()) {
            consoleView.print("  Code: ", ConsoleViewContentType.NORMAL_OUTPUT);
            consoleView.print(issue.getCodeSnippet() + "\n", ConsoleViewContentType.LOG_DEBUG_OUTPUT);
        }
        
        // 打印建议
        if (issue.hasSuggestion()) {
            consoleView.print("  Suggestion: ", ConsoleViewContentType.NORMAL_OUTPUT);
            consoleView.print(issue.getSuggestion() + "\n", ConsoleViewContentType.LOG_INFO_OUTPUT);
        }
        
        // 打印规则ID
        if (issue.hasRuleId()) {
            consoleView.print("  Rule: ", ConsoleViewContentType.NORMAL_OUTPUT);
            consoleView.print(issue.getRuleId() + "\n", ConsoleViewContentType.NORMAL_OUTPUT);
        }
    }
    
    /**
     * 打印报告尾部
     */
    private void printReportFooter(@NotNull ReviewReport report) {
        if (consoleView == null) return;
        
        consoleView.print("\n", ConsoleViewContentType.NORMAL_OUTPUT);
        
        if (report.getReviewDurationMs() > 0) {
            double durationSeconds = report.getReviewDurationMs() / 1000.0;
            consoleView.print(String.format("Review completed in %.2f seconds\n", durationSeconds), 
                             ConsoleViewContentType.LOG_INFO_OUTPUT);
        }
        
        String separator = "=" + "=".repeat(60) + "=";
        consoleView.print(separator + "\n", ConsoleViewContentType.NORMAL_OUTPUT);
    }
    
    /**
     * 根据严重级别获取控制台内容类型
     */
    @NotNull
    private ConsoleViewContentType getContentTypeForSeverity(@NotNull IssueSeverity severity) {
        switch (severity) {
            case CRITICAL:
            case ERROR:
                return ConsoleViewContentType.ERROR_OUTPUT;
            case WARNING:
                return ConsoleViewContentType.LOG_WARNING_OUTPUT;
            case INFO:
            default:
                return ConsoleViewContentType.LOG_INFO_OUTPUT;
        }
    }
    
    /**
     * 显示工具窗口
     */
    public void showToolWindow() {
        ApplicationManager.getApplication().invokeLater(() -> {
            try {
                ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);
                ToolWindow toolWindow = toolWindowManager.getToolWindow(TOOL_WINDOW_ID);
                if (toolWindow != null) {
                    toolWindow.show();
                }
            } catch (Exception e) {
                // 静默处理异常
            }
        });
    }
    
    /**
     * 隐藏工具窗口
     */
    public void hideToolWindow() {
        ApplicationManager.getApplication().invokeLater(() -> {
            try {
                ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);
                ToolWindow toolWindow = toolWindowManager.getToolWindow(TOOL_WINDOW_ID);
                if (toolWindow != null) {
                    toolWindow.hide();
                }
            } catch (Exception e) {
                // 静默处理异常
            }
        });
    }
    
    /**
     * 检查控制台是否可用
     */
    public boolean isConsoleAvailable() {
        return consoleView != null;
    }
    
    /**
     * 释放资源
     */
    public void dispose() {
        if (consoleView != null) {
            try {
                consoleView.dispose();
            } catch (Exception e) {
                // 静默处理异常
            } finally {
                consoleView = null;
            }
        }
    }
}