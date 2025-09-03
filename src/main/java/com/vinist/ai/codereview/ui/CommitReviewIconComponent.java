package com.vinist.ai.codereview.ui;

import com.vinist.ai.codereview.icons.AIReviewIcons;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vcs.CheckinProjectPanel;
import com.intellij.openapi.vcs.changes.Change;
import com.intellij.openapi.vcs.changes.CommitContext;
import com.intellij.openapi.vcs.ui.RefreshableOnComponent;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.JBUI;
import com.vinist.ai.codereview.models.ReviewReport;
import com.vinist.ai.codereview.services.CodeAnalysisService;
import com.vinist.ai.codereview.services.LLMConfigService;
import com.vinist.ai.codereview.services.ReportGenerationService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Git提交窗口的AI评审小图标组件
 * 类似GitMoji插件的交互方式
 */
public class CommitReviewIconComponent implements RefreshableOnComponent {
    
    private final Project project;
    private final CheckinProjectPanel checkinPanel;
    private final LLMConfigService llmConfigService;
    private final CodeAnalysisService codeAnalysisService;
    private final ReportGenerationService reportGenerationService;
    private final AtomicBoolean reviewInProgress = new AtomicBoolean(false);
    
    private JPanel mainPanel;
    private JButton reviewButton;
    private JLabel statusLabel;
    private JProgressBar progressBar;
    
    public CommitReviewIconComponent(@NotNull Project project, @NotNull CheckinProjectPanel checkinPanel) {
        this.project = project;
        this.checkinPanel = checkinPanel;
        this.llmConfigService = LLMConfigService.getInstance();
        this.codeAnalysisService = CodeAnalysisService.getInstance();
        this.reportGenerationService = ReportGenerationService.getInstance();
        
        initializeUI();
    }
    
    private void initializeUI() {
        mainPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        mainPanel.setBorder(JBUI.Borders.empty(2, 0));
        
        // AI评审按钮 - 小图标样式
        reviewButton = new JButton();
        reviewButton.setIcon(AIReviewIcons.AI_REVIEW); // 使用自定义AI评审图标
        reviewButton.setToolTipText("AI Code Review");
        reviewButton.setPreferredSize(new Dimension(24, 24));
        reviewButton.setBorderPainted(false);
        reviewButton.setContentAreaFilled(false);
        reviewButton.setFocusPainted(false);
        reviewButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        // 鼠标悬停效果
        reviewButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                reviewButton.setContentAreaFilled(true);
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (!reviewInProgress.get()) {
                    reviewButton.setContentAreaFilled(false);
                }
            }
        });
        
        // 点击事件
        reviewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                triggerAIReview();
            }
        });
        
        // 状态标签
        statusLabel = new JBLabel("");
        statusLabel.setFont(statusLabel.getFont().deriveFont(Font.PLAIN, 11f));
        statusLabel.setForeground(JBUI.CurrentTheme.Label.disabledForeground());
        
        // 进度条
        progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setPreferredSize(new Dimension(100, 4));
        progressBar.setVisible(false);
        
        // 添加组件到主面板
        mainPanel.add(reviewButton);
        mainPanel.add(statusLabel);
        mainPanel.add(progressBar);
        
        updateButtonState();
    }
    
    /**
     * 触发AI代码评审
     */
    private void triggerAIReview() {
        if (reviewInProgress.get()) {
            return; // 已经在进行中
        }
        
        // 检查LLM配置
        if (!llmConfigService.isConfigured()) {
            Messages.showWarningDialog(
                project,
                "Please configure LLM settings first.",
                "AI Code Review"
            );
            return;
        }
        
        // 获取变更的文件
        Collection<Change> changes = checkinPanel.getSelectedChanges();
        if (changes.isEmpty()) {
            Messages.showInfoMessage(
                project,
                "No changes to review.",
                "AI Code Review"
            );
            return;
        }
        
        // 开始评审
        startReview(changes);
    }
    
    /**
     * 开始代码评审
     */
    private void startReview(@NotNull Collection<Change> changes) {
        if (!reviewInProgress.compareAndSet(false, true)) {
            return;
        }
        
        showLoadingState("Analyzing code...");
        
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
                hideLoadingState();
                reviewInProgress.set(false);
                
                if (throwable != null) {
                    showErrorState("Review failed: " + throwable.getMessage());
                    Messages.showErrorDialog(
                        project,
                        "AI code review failed: " + throwable.getMessage(),
                        "AI Code Review Error"
                    );
                } else {
                    showSuccessState("Review completed");
                    // 显示评审报告
                    reportGenerationService.showReport(project, report);
                }
            });
        });
    }
    
    /**
     * 显示加载状态
     */
    private void showLoadingState(@NotNull String message) {
        SwingUtilities.invokeLater(() -> {
            reviewButton.setEnabled(false);
            reviewButton.setContentAreaFilled(true);
            statusLabel.setText(message);
            statusLabel.setForeground(JBUI.CurrentTheme.Label.foreground());
            progressBar.setVisible(true);
            mainPanel.revalidate();
            mainPanel.repaint();
        });
    }
    
    /**
     * 隐藏加载状态
     */
    private void hideLoadingState() {
        SwingUtilities.invokeLater(() -> {
            reviewButton.setEnabled(true);
            reviewButton.setContentAreaFilled(false);
            progressBar.setVisible(false);
            mainPanel.revalidate();
            mainPanel.repaint();
        });
    }
    
    /**
     * 显示成功状态
     */
    private void showSuccessState(@NotNull String message) {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText(message);
            statusLabel.setForeground(JBUI.CurrentTheme.Banner.SUCCESS_BACKGROUND);
            
            // 3秒后清除状态
            Timer timer = new Timer(3000, e -> {
                statusLabel.setText("");
                statusLabel.setForeground(JBUI.CurrentTheme.Label.disabledForeground());
            });
            timer.setRepeats(false);
            timer.start();
        });
    }
    
    /**
     * 显示错误状态
     */
    private void showErrorState(@NotNull String message) {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText(message);
            statusLabel.setForeground(JBUI.CurrentTheme.Banner.ERROR_BACKGROUND);
            
            // 5秒后清除状态
            Timer timer = new Timer(5000, e -> {
                statusLabel.setText("");
                statusLabel.setForeground(JBUI.CurrentTheme.Label.disabledForeground());
            });
            timer.setRepeats(false);
            timer.start();
        });
    }
    
    /**
     * 更新按钮状态
     */
    private void updateButtonState() {
        boolean enabled = llmConfigService.isConfigured() && !reviewInProgress.get();
        reviewButton.setEnabled(enabled);
        
        if (!llmConfigService.isConfigured()) {
            reviewButton.setToolTipText("AI Code Review (Configure LLM settings first)");
        } else {
            reviewButton.setToolTipText("AI Code Review");
        }
    }
    
    @Override
    public JComponent getComponent() {
        return mainPanel;
    }
    
    public void refreshState() {
        updateButtonState();
    }
    
    @Override
    public void saveState() {
        // 不需要保存状态
    }
    
    @Override
    public void restoreState() {
        refreshState();
    }
    
    /**
     * 检查是否正在进行评审
     */
    public boolean isReviewInProgress() {
        return reviewInProgress.get();
    }
    
    /**
     * 重置状态
     */
    public void resetState() {
        reviewInProgress.set(false);
        hideLoadingState();
        statusLabel.setText("");
        statusLabel.setForeground(JBUI.CurrentTheme.Label.disabledForeground());
        updateButtonState();
    }
}