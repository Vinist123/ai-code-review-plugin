package com.vinist.ai.codereview.handlers;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vcs.CheckinProjectPanel;
import com.intellij.openapi.vcs.changes.CommitContext;
import com.intellij.openapi.vcs.checkin.CheckinHandler;
import com.intellij.openapi.vcs.checkin.CheckinHandlerFactory;
import com.intellij.openapi.vcs.ui.RefreshableOnComponent;
import com.intellij.util.PairConsumer;
import com.vinist.ai.codereview.models.IssueSeverity;
import com.vinist.ai.codereview.models.ReviewReport;
import com.vinist.ai.codereview.services.CodeAnalysisService;
import com.vinist.ai.codereview.services.LLMConfigService;
import com.vinist.ai.codereview.services.ReviewSettingsService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * AI代码审查提交处理器
 * 在代码提交前自动进行AI代码审查
 */
public class AIReviewCheckinHandler extends CheckinHandler {
    
    private final Project project;
    private final CheckinProjectPanel checkinPanel;
    private final LLMConfigService llmConfigService;
    private final ReviewSettingsService reviewSettingsService;
    private final CodeAnalysisService codeAnalysisService;
    private final AtomicBoolean reviewInProgress = new AtomicBoolean(false);
    
    public AIReviewCheckinHandler(@NotNull Project project, 
                                 @NotNull CheckinProjectPanel checkinPanel) {
        this.project = project;
        this.checkinPanel = checkinPanel;
        this.llmConfigService = LLMConfigService.getInstance();
        this.reviewSettingsService = ReviewSettingsService.getInstance();
        this.codeAnalysisService = CodeAnalysisService.getInstance();
    }
    
    @Override
    public ReturnResult beforeCheckin() {
        // 检查是否启用了自动审查
        if (!reviewSettingsService.isEnableAutoReview()) {
            return ReturnResult.COMMIT;
        }
        
        // 检查LLM配置
        if (!llmConfigService.isConfigured()) {
            int result = Messages.showYesNoDialog(
                project,
                "LLM configuration is not complete. Do you want to proceed with commit without review?",
                "AI Code Review",
                "Proceed",
                "Cancel",
                Messages.getQuestionIcon()
            );
            return result == Messages.YES ? ReturnResult.COMMIT : ReturnResult.CANCEL;
        }
        
        // 如果正在审查中，询问用户是否等待
        if (reviewInProgress.get()) {
            int result = Messages.showYesNoDialog(
                project,
                "Code review is already in progress. Do you want to wait for it to complete?",
                "AI Code Review",
                "Wait",
                "Proceed",
                Messages.getQuestionIcon()
            );
            if (result == Messages.NO) {
                return ReturnResult.COMMIT;
            }
        }
        
        // 执行代码审查
        return performCodeReview();
    }
    
    /**
     * 执行代码审查
     */
    @NotNull
    private ReturnResult performCodeReview() {
        if (!reviewInProgress.compareAndSet(false, true)) {
            return ReturnResult.COMMIT; // 已经在审查中
        }
        
        try {
            // 获取要提交的文件
            String changedFiles = getChangedFilesContent();
            if (changedFiles.trim().isEmpty()) {
                return ReturnResult.COMMIT; // 没有需要审查的文件
            }
            
            // 显示进度对话框
            ProgressDialog progressDialog = new ProgressDialog(project);
            progressDialog.setVisible(true);
            
            CompletableFuture<ReviewReport> reviewFuture = CompletableFuture.supplyAsync(() -> {
                try {
                    return codeAnalysisService.analyzeCode(changedFiles, "commit-review");
                } catch (Exception e) {
                    throw new RuntimeException("Code review failed: " + e.getMessage(), e);
                }
            });
            
            try {
                // 等待审查完成（最多30秒）
                ReviewReport report = reviewFuture.get(30, java.util.concurrent.TimeUnit.SECONDS);
                progressDialog.dispose();
                
                return handleReviewResult(report);
                
            } catch (java.util.concurrent.TimeoutException e) {
                progressDialog.dispose();
                reviewFuture.cancel(true);
                
                int result = Messages.showYesNoDialog(
                    project,
                    "Code review is taking too long. Do you want to proceed with commit?",
                    "AI Code Review Timeout",
                    "Proceed",
                    "Cancel",
                    Messages.getWarningIcon()
                );
                return result == Messages.YES ? ReturnResult.COMMIT : ReturnResult.CANCEL;
                
            } catch (Exception e) {
                progressDialog.dispose();
                
                int result = Messages.showYesNoDialog(
                    project,
                    "Code review failed: " + e.getMessage() + "\nDo you want to proceed with commit?",
                    "AI Code Review Error",
                    "Proceed",
                    "Cancel",
                    Messages.getErrorIcon()
                );
                return result == Messages.YES ? ReturnResult.COMMIT : ReturnResult.CANCEL;
            }
            
        } finally {
            reviewInProgress.set(false);
        }
    }
    
    /**
     * 处理审查结果
     */
    @NotNull
    private ReturnResult handleReviewResult(@NotNull ReviewReport report) {
        if (!report.hasIssues()) {
            // 没有发现问题，直接提交
            if (reviewSettingsService.isShowReviewDialog()) {
                Messages.showInfoMessage(
                    project,
                    "No issues found. Code looks good!",
                    "AI Code Review"
                );
            }
            return ReturnResult.COMMIT;
        }
        
        // 检查是否有严重问题
        boolean hasCriticalIssues = report.getIssues().stream()
            .anyMatch(issue -> issue.getSeverity().getLevel() >= IssueSeverity.ERROR.getLevel());
        
        if (hasCriticalIssues) {
            // 有严重问题，显示详细信息并询问用户
            return showCriticalIssuesDialog(report);
        } else {
            // 只有警告或信息级别的问题
            return showWarningIssuesDialog(report);
        }
    }
    
    /**
     * 显示严重问题对话框
     */
    @NotNull
    private ReturnResult showCriticalIssuesDialog(@NotNull ReviewReport report) {
        StringBuilder message = new StringBuilder();
        message.append("Critical issues found in your code:\n\n");
        
        report.getIssues().stream()
            .filter(issue -> issue.getSeverity().getLevel() >= IssueSeverity.ERROR.getLevel())
            .limit(5) // 最多显示5个问题
            .forEach(issue -> {
                message.append("• ").append(issue.getMessage());
                if (issue.hasLineNumber()) {
                    message.append(" (Line ").append(issue.getLineNumber()).append(")");
                }
                message.append("\n");
            });
        
        if (report.getIssueCountBySeverity(IssueSeverity.CRITICAL) + 
            report.getIssueCountBySeverity(IssueSeverity.ERROR) > 5) {
            message.append("\n... and more issues");
        }
        
        message.append("\nDo you want to proceed with commit?");
        
        String[] options = {"Fix Issues", "Proceed Anyway", "Cancel"};
        int result = Messages.showDialog(
            project,
            message.toString(),
            "Critical Issues Found",
            options,
            0, // 默认选择"Fix Issues"
            Messages.getErrorIcon()
        );
        
        switch (result) {
            case 0: // Fix Issues
                // 可以在这里打开问题详情对话框
                return ReturnResult.CANCEL;
            case 1: // Proceed Anyway
                return ReturnResult.COMMIT;
            case 2: // Cancel
            default:
                return ReturnResult.CANCEL;
        }
    }
    
    /**
     * 显示警告问题对话框
     */
    @NotNull
    private ReturnResult showWarningIssuesDialog(@NotNull ReviewReport report) {
        if (!reviewSettingsService.isShowReviewDialog()) {
            return ReturnResult.COMMIT; // 不显示对话框，直接提交
        }
        
        StringBuilder message = new StringBuilder();
        message.append("Found ").append(report.getIssues().size()).append(" warning(s) in your code:\n\n");
        
        report.getIssues().stream()
            .limit(3) // 最多显示3个警告
            .forEach(issue -> {
                message.append("• ").append(issue.getMessage());
                if (issue.hasLineNumber()) {
                    message.append(" (Line ").append(issue.getLineNumber()).append(")");
                }
                message.append("\n");
            });
        
        if (report.getIssues().size() > 3) {
            message.append("\n... and ").append(report.getIssues().size() - 3).append(" more");
        }
        
        message.append("\nDo you want to proceed with commit?");
        
        int result = Messages.showYesNoDialog(
            project,
            message.toString(),
            "Code Review Warnings",
            "Proceed",
            "Cancel",
            Messages.getWarningIcon()
        );
        
        return result == Messages.YES ? ReturnResult.COMMIT : ReturnResult.CANCEL;
    }
    
    /**
     * 获取变更文件内容
     */
    @NotNull
    private String getChangedFilesContent() {
        StringBuilder content = new StringBuilder();
        
        try {
            // 这里需要根据实际的IntelliJ IDEA API来获取变更的文件内容
            // 由于API可能因版本而异，这里提供一个基本的实现框架
            
            // 获取变更的文件列表
            // Collection<Change> changes = checkinPanel.getSelectedChanges();
            
            // 遍历变更并获取内容
            // for (Change change : changes) {
            //     VirtualFile file = change.getVirtualFile();
            //     if (file != null && codeAnalysisService.shouldReviewFile(file.getName())) {
            //         String fileContent = VfsUtil.loadText(file);
            //         content.append("File: ").append(file.getName()).append("\n");
            //         content.append(fileContent).append("\n\n");
            //     }
            // }
            
        } catch (Exception e) {
            // 静默处理异常
        }
        
        return content.toString();
    }
    
    /**
     * 进度对话框
     */
    private static class ProgressDialog extends JDialog {
        
        public ProgressDialog(@NotNull Project project) {
            super();
            setTitle("AI Code Review");
            setModal(true);
            setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
            
            JPanel panel = new JPanel(new BorderLayout());
            panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            
            JLabel label = new JLabel("Analyzing code with AI...");
            label.setHorizontalAlignment(SwingConstants.CENTER);
            panel.add(label, BorderLayout.CENTER);
            
            JProgressBar progressBar = new JProgressBar();
            progressBar.setIndeterminate(true);
            panel.add(progressBar, BorderLayout.SOUTH);
            
            setContentPane(panel);
            setSize(300, 120);
            setLocationRelativeTo(null);
        }
    }
    
    /**
     * 提交处理器工厂
     */
    public static class Factory extends CheckinHandlerFactory {
        
        @NotNull
        @Override
        public CheckinHandler createHandler(@NotNull CheckinProjectPanel panel, 
                                           @NotNull CommitContext commitContext) {
            return new AIReviewCheckinHandler(panel.getProject(), panel);
        }
    }
    
    /**
     * 获取审查设置组件
     */
    @Nullable
    public RefreshableOnComponent getBeforeCheckinConfigurationPanel() {
        return new RefreshableOnComponent() {
            private JCheckBox enableReviewCheckBox;
            
            @Override
            public JComponent getComponent() {
                JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                enableReviewCheckBox = new JCheckBox("Enable AI Code Review");
                enableReviewCheckBox.setSelected(reviewSettingsService.isEnableAutoReview());
                
                enableReviewCheckBox.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        reviewSettingsService.setEnableAutoReview(enableReviewCheckBox.isSelected());
                    }
                });
                
                panel.add(enableReviewCheckBox);
                return panel;
            }
            
            public void refreshState() {
                if (enableReviewCheckBox != null) {
                    enableReviewCheckBox.setSelected(reviewSettingsService.isEnableAutoReview());
                }
            }
            
            @Override
            public void saveState() {
                if (enableReviewCheckBox != null) {
                    reviewSettingsService.setEnableAutoReview(enableReviewCheckBox.isSelected());
                }
            }
            
            public void restoreState() {
                refreshState();
            }
        };
    }
}