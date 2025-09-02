package com.vinist.ai.codereview.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vcs.VcsDataKeys;
import com.intellij.openapi.vcs.changes.Change;
import com.intellij.openapi.vcs.changes.ChangeListManager;
import com.intellij.openapi.vcs.CommitMessageI;
import com.vinist.ai.codereview.icons.AIReviewIcons;
import com.vinist.ai.codereview.services.CodeAnalysisService;
import com.vinist.ai.codereview.services.LLMConfigService;
import com.vinist.ai.codereview.services.ReportGenerationService;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * AI代码评审Action，显示在提交消息输入框附近
 * 参考GitMoji插件的实现方式
 */
public class CommitMessageAIReviewAction extends AnAction {
    
    private static final AtomicBoolean reviewInProgress = new AtomicBoolean(false);
    
    public CommitMessageAIReviewAction() {
        super("AI Code Review", "Perform AI code review on selected changes", AIReviewIcons.AI_REVIEW);
    }
    
    @Override
    public void update(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) {
            e.getPresentation().setEnabledAndVisible(false);
            return;
        }
        
        // 检查是否在提交界面
        CommitMessageI commitMessage = VcsDataKeys.COMMIT_MESSAGE_CONTROL.getData(e.getDataContext());
        if (commitMessage == null) {
            e.getPresentation().setEnabledAndVisible(false);
            return;
        }
        
        // 检查LLM配置和评审状态
        LLMConfigService llmConfigService = LLMConfigService.getInstance();
        boolean enabled = llmConfigService.isConfigured() && !reviewInProgress.get();
        
        e.getPresentation().setEnabledAndVisible(true);
        e.getPresentation().setEnabled(enabled);
        
        // 更新图标状态
        if (reviewInProgress.get()) {
            e.getPresentation().setText("AI Review (In Progress...)");
        } else {
            e.getPresentation().setText("AI Code Review");
        }
    }
    
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) {
            return;
        }
        
        if (reviewInProgress.get()) {
            return; // 已经在进行中
        }
        
        // 检查LLM配置
        LLMConfigService llmConfigService = LLMConfigService.getInstance();
        if (!llmConfigService.isConfigured()) {
            Messages.showWarningDialog(
                project,
                "Please configure LLM settings first.",
                "AI Code Review"
            );
            return;
        }
        
        // 获取变更的文件
        ChangeListManager changeListManager = ChangeListManager.getInstance(project);
        Collection<Change> changes = changeListManager.getDefaultChangeList().getChanges();
        
        if (changes.isEmpty()) {
            Messages.showInfoMessage(
                project,
                "No changes to review.",
                "AI Code Review"
            );
            return;
        }
        
        // 开始评审
        startReview(project, changes);
    }
    
    /**
     * 开始代码评审
     */
    private void startReview(@NotNull Project project, @NotNull Collection<Change> changes) {
        if (!reviewInProgress.compareAndSet(false, true)) {
            return;
        }
        
        CodeAnalysisService codeAnalysisService = CodeAnalysisService.getInstance();
        ReportGenerationService reportGenerationService = ReportGenerationService.getInstance();
        
        CompletableFuture.supplyAsync(() -> {
            try {
                // 构建要分析的代码内容
                StringBuilder codeContent = new StringBuilder();
                for (Change change : changes) {
                    if (change.getAfterRevision() != null && change.getAfterRevision().getFile() != null) {
                        String fileName = change.getAfterRevision().getFile().getName();
                        if (codeAnalysisService.shouldReviewFile(fileName)) {
                            try {
                                String content = change.getAfterRevision().getContent();
                                if (content != null) {
                                    codeContent.append("File: ").append(fileName).append("\n");
                                    codeContent.append(content).append("\n\n");
                                }
                            } catch (Exception e) {
                                // 忽略单个文件的错误
                            }
                        }
                    }
                }
                
                if (codeContent.length() == 0) {
                    throw new RuntimeException("No reviewable files found");
                }
                
                return codeAnalysisService.analyzeCode(codeContent.toString(), "commit-review");
                
            } catch (Exception e) {
                throw new RuntimeException("Code analysis failed: " + e.getMessage(), e);
            }
        }).whenComplete((report, throwable) -> {
            SwingUtilities.invokeLater(() -> {
                reviewInProgress.set(false);
                
                if (throwable != null) {
                    Messages.showErrorDialog(
                        project,
                        "AI code review failed: " + throwable.getMessage(),
                        "AI Code Review Error"
                    );
                } else {
                    // 显示评审报告
                    reportGenerationService.showReport(project, report);
                }
            });
        });
    }
}