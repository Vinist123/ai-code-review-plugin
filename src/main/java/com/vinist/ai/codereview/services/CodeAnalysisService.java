package com.vinist.ai.codereview.services;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import com.vinist.ai.codereview.models.ReviewReport;
import com.vinist.ai.codereview.models.CodeIssue;
import com.vinist.ai.codereview.models.IssueSeverity;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

/**
 * 代码分析服务
 * 负责执行代码审查和分析
 */
@Service
public final class CodeAnalysisService {
    
    public static CodeAnalysisService getInstance() {
        return ApplicationManager.getApplication().getService(CodeAnalysisService.class);
    }
    
    /**
     * 分析回调接口
     */
    public interface AnalysisCallback {
        void onSuccess(ReviewReport report);
        void onError(String error);
    }
    
    /**
     * 同步分析代码
     */
//    public ReviewReport analyzeCode(@NotNull String fileName, @NotNull String code) {
//        return analyzeCodeContext(code, fileName);
//    }
    
    /**
     * 同步分析代码（重载方法，支持context参数）
     */
    public ReviewReport analyzeCode(@NotNull String code, @NotNull String context) {
        ReviewReport report = new ReviewReport(context, code);
        report.setReviewTime(LocalDateTime.now());
        
        try {
            long startTime = System.currentTimeMillis();
            
            // 获取LLM客户端服务
            LLMClientService llmService = LLMClientService.getInstance();
            
            // 调用LLM进行代码审查
            String reviewResult = llmService.reviewCode(code, context);
            
            // 解析审查结果
            parseReviewResult(report, reviewResult);
            
            long endTime = System.currentTimeMillis();
            report.setReviewDuration(endTime - startTime);
            
        } catch (Exception e) {
            // 添加错误信息到报告
            CodeIssue errorIssue = new CodeIssue("代码分析过程中发生错误: " + e.getMessage(), IssueSeverity.ERROR);
            report.addIssue(errorIssue);
        }
        
        return report;
    }
    
    /**
     * 异步分析代码
     */
    public void analyzeCodeAsync(@NotNull String fileName, @NotNull String code, @NotNull AnalysisCallback callback) {
        CompletableFuture.supplyAsync(() -> {
            return analyzeCode(fileName, code);
        }).thenAccept(report -> {
            ApplicationManager.getApplication().invokeLater(() -> {
                callback.onSuccess(report);
            });
        }).exceptionally(throwable -> {
            ApplicationManager.getApplication().invokeLater(() -> {
                callback.onError(throwable.getMessage());
            });
            return null;
        });
    }
    
    /**
     * 解析LLM返回的审查结果
     */
    private void parseReviewResult(ReviewReport report, String reviewResult) {
        if (reviewResult == null || reviewResult.trim().isEmpty()) {
            report.setSummary("未发现明显问题");
            return;
        }
        
        try {
            // 简单的解析逻辑，实际应该根据LLM返回的格式进行解析
            String[] lines = reviewResult.split("\n");
            StringBuilder summaryBuilder = new StringBuilder();
            
            for (String line : lines) {
                line = line.trim();
                if (line.isEmpty()) continue;
                
                // 检查是否是问题行
                if (line.toLowerCase().contains("error") || line.toLowerCase().contains("错误")) {
                    report.addIssue(new CodeIssue(line, IssueSeverity.ERROR));
                } else if (line.toLowerCase().contains("warning") || line.toLowerCase().contains("警告")) {
                    report.addIssue(new CodeIssue(line, IssueSeverity.WARNING));
                } else if (line.toLowerCase().contains("critical") || line.toLowerCase().contains("严重")) {
                    report.addIssue(new CodeIssue(line, IssueSeverity.CRITICAL));
                } else if (line.toLowerCase().contains("info") || line.toLowerCase().contains("信息") || line.toLowerCase().contains("建议")) {
                    report.addIssue(new CodeIssue(line, IssueSeverity.INFO));
                } else {
                    // 作为总结的一部分
                    if (summaryBuilder.length() > 0) {
                        summaryBuilder.append("\n");
                    }
                    summaryBuilder.append(line);
                }
            }
            
            if (summaryBuilder.length() > 0) {
                report.setSummary(summaryBuilder.toString());
            } else {
                report.setSummary("代码审查完成");
            }
            
        } catch (Exception e) {
            CodeIssue parseErrorIssue = new CodeIssue("解析审查结果时发生错误: " + e.getMessage(), IssueSeverity.ERROR);
            report.addIssue(parseErrorIssue);
            report.setSummary("审查结果解析失败");
        }
    }
    
    /**
     * 检查代码是否需要审查
     */
    public boolean shouldReviewFile(String fileName) {
        if (fileName == null) {
            return false;
        }
        
        String lowerFileName = fileName.toLowerCase();
        
        // 支持的文件类型
        return lowerFileName.endsWith(".java") ||
               lowerFileName.endsWith(".kt") ||
               lowerFileName.endsWith(".scala") ||
               lowerFileName.endsWith(".groovy") ||
               lowerFileName.endsWith(".js") ||
               lowerFileName.endsWith(".ts") ||
               lowerFileName.endsWith(".py") ||
               lowerFileName.endsWith(".go") ||
               lowerFileName.endsWith(".rs") ||
               lowerFileName.endsWith(".cpp") ||
               lowerFileName.endsWith(".c") ||
               lowerFileName.endsWith(".h") ||
               lowerFileName.endsWith(".cs") ||
               lowerFileName.endsWith(".php") ||
               lowerFileName.endsWith(".rb");
    }
}