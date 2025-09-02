package com.vinist.ai.codereview.services;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.vinist.ai.codereview.models.ReviewReport;
import com.vinist.ai.codereview.models.CodeIssue;
import com.vinist.ai.codereview.models.IssueSeverity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 报告生成服务
 * 负责生成和显示审查报告
 */
@Service
public final class ReportGenerationService {
    
    public static ReportGenerationService getInstance() {
        return ApplicationManager.getApplication().getService(ReportGenerationService.class);
    }
    
    /**
     * 显示审查报告
     */
    public void showReport(@NotNull Project project, @NotNull ReviewReport report) {
        ReviewSettingsService settingsService = ReviewSettingsService.getInstance();
        
        if (settingsService.isShowReviewDialog()) {
            showReportDialog(project, report);
        }
        
        if (settingsService.isOutputToConsole()) {
            outputToConsole(report);
        }
        
        if (settingsService.isSaveReports()) {
            saveReportToFile(report);
        }
    }
    
    /**
     * 显示报告对话框
     */
    private void showReportDialog(@NotNull Project project, @NotNull ReviewReport report) {
        SwingUtilities.invokeLater(() -> {
            JDialog dialog = createReportDialog(project, report);
            dialog.setVisible(true);
        });
    }
    
    /**
     * 创建报告对话框
     */
    private JDialog createReportDialog(@NotNull Project project, @NotNull ReviewReport report) {
        JDialog dialog = new JDialog();
        dialog.setTitle("代码审查报告 - " + report.getFileName());
        dialog.setSize(800, 600);
        dialog.setLocationRelativeTo(null);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        
        // 主面板
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // 顶部信息面板
        JPanel infoPanel = createInfoPanel(report);
        mainPanel.add(infoPanel, BorderLayout.NORTH);
        
        // 中间问题列表
        JScrollPane issuesScrollPane = createIssuesPanel(report);
        mainPanel.add(issuesScrollPane, BorderLayout.CENTER);
        
        // 底部按钮面板
        JPanel buttonPanel = createButtonPanel(dialog, report);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.add(mainPanel);
        return dialog;
    }
    
    /**
     * 创建信息面板
     */
    private JPanel createInfoPanel(@NotNull ReviewReport report) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("审查信息"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // 文件名
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("文件名:"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel(report.getFileName()), gbc);
        
        // 审查时间
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("审查时间:"), gbc);
        gbc.gridx = 1;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        panel.add(new JLabel(sdf.format(new Date(report.getTimestamp()))), gbc);
        
        // 问题统计
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("问题统计:"), gbc);
        gbc.gridx = 1;
        String stats = String.format("严重: %d, 错误: %d, 警告: %d, 信息: %d",
                report.getIssueCountBySeverity(IssueSeverity.CRITICAL),
                report.getIssueCountBySeverity(IssueSeverity.ERROR),
                report.getIssueCountBySeverity(IssueSeverity.WARNING),
                report.getIssueCountBySeverity(IssueSeverity.INFO));
        panel.add(new JLabel(stats), gbc);
        
        // 总结
        if (report.getSummary() != null && !report.getSummary().trim().isEmpty()) {
            gbc.gridx = 0; gbc.gridy = 3;
            panel.add(new JLabel("总结:"), gbc);
            gbc.gridx = 1; gbc.gridwidth = 2;
            JTextArea summaryArea = new JTextArea(report.getSummary());
            summaryArea.setEditable(false);
            summaryArea.setBackground(panel.getBackground());
            summaryArea.setLineWrap(true);
            summaryArea.setWrapStyleWord(true);
            panel.add(summaryArea, gbc);
        }
        
        return panel;
    }
    
    /**
     * 创建问题列表面板
     */
    private JScrollPane createIssuesPanel(@NotNull ReviewReport report) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        List<CodeIssue> issues = report.getIssues();
        if (issues.isEmpty()) {
            JLabel noIssuesLabel = new JLabel("未发现问题");
            noIssuesLabel.setHorizontalAlignment(SwingConstants.CENTER);
            panel.add(noIssuesLabel);
        } else {
            for (CodeIssue issue : issues) {
                JPanel issuePanel = createIssuePanel(issue);
                panel.add(issuePanel);
                panel.add(Box.createVerticalStrut(5));
            }
        }
        
        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setBorder(BorderFactory.createTitledBorder("发现的问题"));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
        return scrollPane;
    }
    
    /**
     * 创建单个问题面板
     */
    private JPanel createIssuePanel(@NotNull CodeIssue issue) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.getColor(issue.getSeverity().getColor()), 2),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        
        // 左侧严重程度标签
        JLabel severityLabel = new JLabel(issue.getSeverity().getDisplayName());
        severityLabel.setForeground(Color.getColor(issue.getSeverity().getColor()));
        severityLabel.setFont(severityLabel.getFont().deriveFont(Font.BOLD));
        panel.add(severityLabel, BorderLayout.WEST);
        
        // 中间问题信息
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        
        // 问题消息
        JTextArea messageArea = new JTextArea(issue.getMessage());
        messageArea.setEditable(false);
        messageArea.setBackground(panel.getBackground());
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        infoPanel.add(messageArea);
        
        // 行号和类别
        if (issue.getLineNumber() > 0 || (issue.getCategory() != null && !issue.getCategory().trim().isEmpty())) {
            StringBuilder details = new StringBuilder();
            if (issue.getLineNumber() > 0) {
                details.append("行号: ").append(issue.getLineNumber());
            }
            if (issue.getCategory() != null && !issue.getCategory().trim().isEmpty()) {
                if (details.length() > 0) details.append(", ");
                details.append("类别: ").append(issue.getCategory());
            }
            
            JLabel detailsLabel = new JLabel(details.toString());
            detailsLabel.setFont(detailsLabel.getFont().deriveFont(Font.ITALIC, 11f));
            detailsLabel.setForeground(Color.GRAY);
            infoPanel.add(detailsLabel);
        }
        
        // 建议
        if (issue.getSuggestion() != null && !issue.getSuggestion().trim().isEmpty()) {
            JTextArea suggestionArea = new JTextArea("建议: " + issue.getSuggestion());
            suggestionArea.setEditable(false);
            suggestionArea.setBackground(new Color(240, 255, 240));
            suggestionArea.setLineWrap(true);
            suggestionArea.setWrapStyleWord(true);
            suggestionArea.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
            infoPanel.add(suggestionArea);
        }
        
        panel.add(infoPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * 创建按钮面板
     */
    private JPanel createButtonPanel(@NotNull JDialog dialog, @NotNull ReviewReport report) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        // 保存报告按钮
        JButton saveButton = new JButton("保存报告");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveReportToFile(report);
            }
        });
        panel.add(saveButton);
        
        // 关闭按钮
        JButton closeButton = new JButton("关闭");
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });
        panel.add(closeButton);
        
        return panel;
    }
    
    /**
     * 输出到控制台
     */
    private void outputToConsole(@NotNull ReviewReport report) {
        StringBuilder output = new StringBuilder();
        output.append("\n=== 代码审查报告 ===").append("\n");
        output.append("文件: ").append(report.getFileName()).append("\n");
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        output.append("时间: ").append(sdf.format(new Date(report.getTimestamp()))).append("\n");
        
        if (report.getSummary() != null && !report.getSummary().trim().isEmpty()) {
            output.append("总结: ").append(report.getSummary()).append("\n");
        }
        
        output.append("\n问题列表:\n");
        List<CodeIssue> issues = report.getIssues();
        if (issues.isEmpty()) {
            output.append("未发现问题\n");
        } else {
            for (int i = 0; i < issues.size(); i++) {
                CodeIssue issue = issues.get(i);
                output.append(String.format("%d. [%s] %s", 
                        i + 1, issue.getSeverity().getDisplayName(), issue.getMessage()));
                
                if (issue.getLineNumber() > 0) {
                    output.append(" (行号: ").append(issue.getLineNumber()).append(")");
                }
                
                if (issue.getCategory() != null && !issue.getCategory().trim().isEmpty()) {
                    output.append(" [类别: ").append(issue.getCategory()).append("]");
                }
                
                output.append("\n");
                
                if (issue.getSuggestion() != null && !issue.getSuggestion().trim().isEmpty()) {
                    output.append("   建议: ").append(issue.getSuggestion()).append("\n");
                }
            }
        }
        
        output.append("\n问题统计:\n");
        output.append(String.format("严重: %d, 错误: %d, 警告: %d, 信息: %d\n",
                report.getIssueCountBySeverity(IssueSeverity.CRITICAL),
                report.getIssueCountBySeverity(IssueSeverity.ERROR),
                report.getIssueCountBySeverity(IssueSeverity.WARNING),
                report.getIssueCountBySeverity(IssueSeverity.INFO)));
        
        output.append("===================\n");
        
        System.out.println(output.toString());
    }
    
    /**
     * 保存报告到文件
     */
    private void saveReportToFile(@NotNull ReviewReport report) {
        try {
            String fileName = generateReportFileName(report);
            File file = new File(fileName);
            
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(generateHtmlReport(report));
            }
            
            SwingUtilities.invokeLater(() -> {
                Messages.showInfoMessage(
                        "报告已保存到: " + file.getAbsolutePath(),
                        "保存成功"
                );
            });
            
        } catch (IOException e) {
            SwingUtilities.invokeLater(() -> {
                Messages.showErrorDialog(
                        "保存报告失败: " + e.getMessage(),
                        "保存失败"
                );
            });
        }
    }
    
    /**
     * 生成报告文件名
     */
    private String generateReportFileName(@NotNull ReviewReport report) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String timestamp = sdf.format(new Date(report.getTimestamp()));
        String baseName = report.getFileName().replaceAll("[^a-zA-Z0-9._-]", "_");
        return String.format("review_report_%s_%s.html", baseName, timestamp);
    }
    
    /**
     * 生成HTML报告
     */
    private String generateHtmlReport(@NotNull ReviewReport report) {
        StringBuilder html = new StringBuilder();
        
        html.append("<!DOCTYPE html>\n");
        html.append("<html lang='zh-CN'>\n");
        html.append("<head>\n");
        html.append("<meta charset='UTF-8'>\n");
        html.append("<title>代码审查报告 - ").append(escapeHtml(report.getFileName())).append("</title>\n");
        html.append("<style>\n");
        html.append(getReportCss());
        html.append("</style>\n");
        html.append("</head>\n");
        html.append("<body>\n");
        
        // 标题
        html.append("<h1>代码审查报告</h1>\n");
        
        // 基本信息
        html.append("<div class='info-section'>\n");
        html.append("<h2>基本信息</h2>\n");
        html.append("<p><strong>文件名:</strong> ").append(escapeHtml(report.getFileName())).append("</p>\n");
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        html.append("<p><strong>审查时间:</strong> ").append(sdf.format(new Date(report.getTimestamp()))).append("</p>\n");
        
        if (report.getReviewDurationMs() > 0) {
            html.append("<p><strong>审查耗时:</strong> ").append(report.getReviewDurationMs()).append(" 毫秒</p>\n");
        }
        
        if (report.getSummary() != null && !report.getSummary().trim().isEmpty()) {
            html.append("<p><strong>总结:</strong></p>\n");
            html.append("<div class='summary'>").append(escapeHtml(report.getSummary())).append("</div>\n");
        }
        html.append("</div>\n");
        
        // 问题统计
        html.append("<div class='stats-section'>\n");
        html.append("<h2>问题统计</h2>\n");
        html.append("<table class='stats-table'>\n");
        html.append("<tr><th>严重程度</th><th>数量</th></tr>\n");
        
        for (IssueSeverity severity : IssueSeverity.values()) {
            int count = report.getIssueCountBySeverity(severity);
            html.append("<tr class='severity-").append(severity.name().toLowerCase()).append("'>\n");
            html.append("<td>").append(severity.getDisplayName()).append("</td>\n");
            html.append("<td>").append(count).append("</td>\n");
            html.append("</tr>\n");
        }
        
        html.append("</table>\n");
        html.append("</div>\n");
        
        // 问题列表
        html.append("<div class='issues-section'>\n");
        html.append("<h2>发现的问题</h2>\n");
        
        List<CodeIssue> issues = report.getIssues();
        if (issues.isEmpty()) {
            html.append("<p class='no-issues'>未发现问题</p>\n");
        } else {
            for (int i = 0; i < issues.size(); i++) {
                CodeIssue issue = issues.get(i);
                html.append("<div class='issue severity-").append(issue.getSeverity().name().toLowerCase()).append("'>\n");
                html.append("<div class='issue-header'>\n");
                html.append("<span class='issue-number'>#").append(i + 1).append("</span>\n");
                html.append("<span class='issue-severity'>").append(issue.getSeverity().getDisplayName()).append("</span>\n");
                if (issue.getLineNumber() > 0) {
                    html.append("<span class='issue-line'>行号: ").append(issue.getLineNumber()).append("</span>\n");
                }
                if (issue.getCategory() != null && !issue.getCategory().trim().isEmpty()) {
                    html.append("<span class='issue-category'>").append(escapeHtml(issue.getCategory())).append("</span>\n");
                }
                html.append("</div>\n");
                
                html.append("<div class='issue-message'>").append(escapeHtml(issue.getMessage())).append("</div>\n");
                
                if (issue.getSuggestion() != null && !issue.getSuggestion().trim().isEmpty()) {
                    html.append("<div class='issue-suggestion'>\n");
                    html.append("<strong>建议:</strong> ").append(escapeHtml(issue.getSuggestion()));
                    html.append("</div>\n");
                }
                
                if (issue.getCodeSnippet() != null && !issue.getCodeSnippet().trim().isEmpty()) {
                    html.append("<div class='issue-code'>\n");
                    html.append("<strong>相关代码:</strong>\n");
                    html.append("<pre><code>").append(escapeHtml(issue.getCodeSnippet())).append("</code></pre>\n");
                    html.append("</div>\n");
                }
                
                html.append("</div>\n");
            }
        }
        
        html.append("</div>\n");
        html.append("</body>\n");
        html.append("</html>\n");
        
        return html.toString();
    }
    
    /**
     * 获取报告CSS样式
     */
    private String getReportCss() {
        return "body { font-family: Arial, sans-serif; margin: 20px; line-height: 1.6; }\n" +
               "h1 { color: #333; border-bottom: 2px solid #007acc; padding-bottom: 10px; }\n" +
               "h2 { color: #555; margin-top: 30px; }\n" +
               ".info-section, .stats-section, .issues-section { margin-bottom: 30px; }\n" +
               ".summary { background: #f9f9f9; padding: 10px; border-left: 4px solid #007acc; margin: 10px 0; }\n" +
               ".stats-table { border-collapse: collapse; width: 100%; max-width: 400px; }\n" +
               ".stats-table th, .stats-table td { border: 1px solid #ddd; padding: 8px; text-align: left; }\n" +
               ".stats-table th { background-color: #f2f2f2; }\n" +
               ".severity-critical { background-color: #ffebee; }\n" +
               ".severity-error { background-color: #fff3e0; }\n" +
               ".severity-warning { background-color: #fffde7; }\n" +
               ".severity-info { background-color: #e8f5e8; }\n" +
               ".issue { border: 1px solid #ddd; margin: 10px 0; padding: 15px; border-radius: 5px; }\n" +
               ".issue.severity-critical { border-left: 5px solid #f44336; }\n" +
               ".issue.severity-error { border-left: 5px solid #ff9800; }\n" +
               ".issue.severity-warning { border-left: 5px solid #ffeb3b; }\n" +
               ".issue.severity-info { border-left: 5px solid #4caf50; }\n" +
               ".issue-header { margin-bottom: 10px; }\n" +
               ".issue-number { font-weight: bold; margin-right: 10px; }\n" +
               ".issue-severity { background: #007acc; color: white; padding: 2px 8px; border-radius: 3px; margin-right: 10px; }\n" +
               ".issue-line, .issue-category { background: #f0f0f0; padding: 2px 6px; border-radius: 3px; margin-right: 5px; font-size: 0.9em; }\n" +
               ".issue-message { margin: 10px 0; font-weight: 500; }\n" +
               ".issue-suggestion { background: #e8f5e8; padding: 10px; border-radius: 3px; margin: 10px 0; }\n" +
               ".issue-code { background: #f5f5f5; padding: 10px; border-radius: 3px; margin: 10px 0; }\n" +
               ".issue-code pre { margin: 5px 0; overflow-x: auto; }\n" +
               ".no-issues { color: #4caf50; font-weight: bold; text-align: center; padding: 20px; }\n";
    }
    
    /**
     * HTML转义
     */
    private String escapeHtml(@Nullable String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                  .replace("<", "&lt;")
                  .replace(">", "&gt;")
                  .replace("\"", "&quot;")
                  .replace("'", "&#39;");
    }
}