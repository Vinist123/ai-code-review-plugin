package com.vinist.ai.codereview.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.JBTable;
import com.vinist.ai.codereview.models.IssueSeverity;
import com.vinist.ai.codereview.models.CodeIssue;
import com.vinist.ai.codereview.models.ReviewReport;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 代码审查报告对话框
 * 显示详细的代码审查结果
 */
public class ReviewReportDialog extends DialogWrapper {
    
    private final Project project;
    private final ReviewReport report;
    private JBTable issuesTable;
    private IssueTableModel tableModel;
    private JTextArea summaryArea;
    private JLabel statisticsLabel;
    private JComboBox<SeverityFilter> severityFilterCombo;
    
    public ReviewReportDialog(@NotNull Project project, @NotNull ReviewReport report) {
        super(project);
        this.project = project;
        this.report = report;
        
        setTitle("AI Code Review Report");
        setModal(true);
        setResizable(true);
        
        init();
        updateStatistics();
    }
    
    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setPreferredSize(new Dimension(800, 600));
        
        // 创建顶部信息面板
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // 创建中间的问题列表面板
        JPanel issuesPanel = createIssuesPanel();
        mainPanel.add(issuesPanel, BorderLayout.CENTER);
        
        // 创建底部统计面板
        JPanel statisticsPanel = createStatisticsPanel();
        mainPanel.add(statisticsPanel, BorderLayout.SOUTH);
        
        return mainPanel;
    }
    
    /**
     * 创建头部信息面板
     */
    @NotNull
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Report Information"));
        
        // 基本信息
        JPanel infoPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // 文件名
        if (report.getFileName() != null) {
            gbc.gridx = 0; gbc.gridy = 0;
            infoPanel.add(new JLabel("File:"), gbc);
            gbc.gridx = 1;
            infoPanel.add(new JLabel(report.getFileName()), gbc);
        }
        
        // 时间戳
        gbc.gridx = 0; gbc.gridy = 1;
        infoPanel.add(new JLabel("Generated:"), gbc);
        gbc.gridx = 1;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        infoPanel.add(new JLabel(dateFormat.format(new Date(report.getTimestamp()))), gbc);
        
        // 审查者
        if (report.getReviewerId() != null) {
            gbc.gridx = 0; gbc.gridy = 2;
            infoPanel.add(new JLabel("Reviewer:"), gbc);
            gbc.gridx = 1;
            infoPanel.add(new JLabel(report.getReviewerId()), gbc);
        }
        
        // 耗时
        if (report.getReviewDurationMs() > 0) {
            gbc.gridx = 0; gbc.gridy = 3;
            infoPanel.add(new JLabel("Duration:"), gbc);
            gbc.gridx = 1;
            double durationSeconds = report.getReviewDurationMs() / 1000.0;
            infoPanel.add(new JLabel(String.format("%.2f seconds", durationSeconds)), gbc);
        }
        
        panel.add(infoPanel, BorderLayout.WEST);
        
        // 摘要
        if (report.getSummary() != null && !report.getSummary().trim().isEmpty()) {
            summaryArea = new JTextArea(3, 40);
            summaryArea.setText(report.getSummary());
            summaryArea.setEditable(false);
            summaryArea.setLineWrap(true);
            summaryArea.setWrapStyleWord(true);
            summaryArea.setBackground(panel.getBackground());
            
            JScrollPane summaryScroll = new JBScrollPane(summaryArea);
            summaryScroll.setBorder(BorderFactory.createTitledBorder("Summary"));
            panel.add(summaryScroll, BorderLayout.CENTER);
        }
        
        return panel;
    }
    
    /**
     * 创建问题列表面板
     */
    @NotNull
    private JPanel createIssuesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Issues Found"));
        
        // 创建过滤器面板
        JPanel filterPanel = createFilterPanel();
        panel.add(filterPanel, BorderLayout.NORTH);
        
        // 创建问题表格
        tableModel = new IssueTableModel(report.getIssues());
        issuesTable = new JBTable(tableModel);
        
        // 设置表格属性
        issuesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        issuesTable.setRowHeight(25);
        issuesTable.getTableHeader().setReorderingAllowed(false);
        
        // 设置列宽
        issuesTable.getColumnModel().getColumn(0).setPreferredWidth(80);  // Severity
        issuesTable.getColumnModel().getColumn(1).setPreferredWidth(60);  // Line
        issuesTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Category
        issuesTable.getColumnModel().getColumn(3).setPreferredWidth(400); // Message
        issuesTable.getColumnModel().getColumn(4).setPreferredWidth(100); // Rule
        
        // 设置严重级别列的渲染器
        issuesTable.getColumnModel().getColumn(0).setCellRenderer(new SeverityCellRenderer());
        
        // 添加选择监听器
        issuesTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                showIssueDetails();
            }
        });
        
        JScrollPane scrollPane = new JBScrollPane(issuesTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // 创建问题详情面板
        JPanel detailsPanel = createIssueDetailsPanel();
        panel.add(detailsPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * 创建过滤器面板
     */
    @NotNull
    private JPanel createFilterPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        panel.add(new JLabel("Filter by severity:"));
        
        severityFilterCombo = new JComboBox<>(SeverityFilter.values());
        severityFilterCombo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                applyFilter();
            }
        });
        panel.add(severityFilterCombo);
        
        return panel;
    }
    
    /**
     * 创建问题详情面板
     */
    @NotNull
    private JPanel createIssueDetailsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Issue Details"));
        panel.setPreferredSize(new Dimension(0, 150));
        
        JTextArea detailsArea = new JTextArea();
        detailsArea.setEditable(false);
        detailsArea.setLineWrap(true);
        detailsArea.setWrapStyleWord(true);
        detailsArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JBScrollPane(detailsArea);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // 保存引用以便后续更新
        panel.putClientProperty("detailsArea", detailsArea);
        
        return panel;
    }
    
    /**
     * 创建统计面板
     */
    @NotNull
    private JPanel createStatisticsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createTitledBorder("Statistics"));
        
        statisticsLabel = new JLabel();
        panel.add(statisticsLabel);
        
        return panel;
    }
    
    /**
     * 应用过滤器
     */
    private void applyFilter() {
        SeverityFilter filter = (SeverityFilter) severityFilterCombo.getSelectedItem();
        if (filter != null) {
            List<CodeIssue> filteredIssues = new ArrayList<>();
            
            for (CodeIssue issue : report.getIssues()) {
                if (filter.accepts(issue.getSeverity())) {
                    filteredIssues.add(issue);
                }
            }
            
            tableModel.setIssues(filteredIssues);
            updateStatistics();
        }
    }
    
    /**
     * 显示问题详情
     */
    private void showIssueDetails() {
        int selectedRow = issuesTable.getSelectedRow();
        if (selectedRow >= 0) {
            CodeIssue issue = tableModel.getIssueAt(selectedRow);
            
            // 查找详情面板
            JComponent centerPanel = (JComponent) getContentPanel().getComponent(0);
            JPanel issuesPanel = (JPanel) centerPanel.getComponent(1);
            JPanel detailsPanel = (JPanel) issuesPanel.getComponent(2);
            JTextArea detailsArea = (JTextArea) detailsPanel.getClientProperty("detailsArea");
            
            if (detailsArea != null) {
                StringBuilder details = new StringBuilder();
                details.append("Message: ").append(issue.getMessage()).append("\n\n");
                
                if (issue.hasCodeSnippet()) {
                    details.append("Code Snippet:\n");
                    details.append(issue.getCodeSnippet()).append("\n\n");
                }
                
                if (issue.hasSuggestion()) {
                    details.append("Suggestion:\n");
                    details.append(issue.getSuggestion()).append("\n\n");
                }
                
                if (issue.hasRuleId()) {
                    details.append("Rule ID: ").append(issue.getRuleId()).append("\n");
                }
                
                detailsArea.setText(details.toString());
                detailsArea.setCaretPosition(0);
            }
        }
    }
    
    /**
     * 更新统计信息
     */
    private void updateStatistics() {
        List<CodeIssue> currentIssues = tableModel.getIssues();
        
        int totalIssues = currentIssues.size();
        int criticalCount = (int) currentIssues.stream().filter(i -> i.getSeverity() == IssueSeverity.CRITICAL).count();
        int errorCount = (int) currentIssues.stream().filter(i -> i.getSeverity() == IssueSeverity.ERROR).count();
        int warningCount = (int) currentIssues.stream().filter(i -> i.getSeverity() == IssueSeverity.WARNING).count();
        int infoCount = (int) currentIssues.stream().filter(i -> i.getSeverity() == IssueSeverity.INFO).count();
        
        StringBuilder stats = new StringBuilder();
        stats.append("Total: ").append(totalIssues);
        
        if (criticalCount > 0) {
            stats.append(", Critical: ").append(criticalCount);
        }
        if (errorCount > 0) {
            stats.append(", Error: ").append(errorCount);
        }
        if (warningCount > 0) {
            stats.append(", Warning: ").append(warningCount);
        }
        if (infoCount > 0) {
            stats.append(", Info: ").append(infoCount);
        }
        
        statisticsLabel.setText(stats.toString());
    }
    
    @NotNull
    @Override
    protected Action[] createActions() {
        return new Action[]{getOKAction()};
    }
    
    /**
     * 问题表格模型
     */
    private static class IssueTableModel extends AbstractTableModel {
        
        private static final String[] COLUMN_NAMES = {
            "Severity", "Line", "Category", "Message", "Rule"
        };
        
        private List<CodeIssue> issues;
        
        public IssueTableModel(@NotNull List<CodeIssue> issues) {
            this.issues = new ArrayList<>(issues);
        }
        
        public void setIssues(@NotNull List<CodeIssue> issues) {
            this.issues = new ArrayList<>(issues);
            fireTableDataChanged();
        }
        
        public List<CodeIssue> getIssues() {
            return issues;
        }
        
        public CodeIssue getIssueAt(int row) {
            return issues.get(row);
        }
        
        @Override
        public int getRowCount() {
            return issues.size();
        }
        
        @Override
        public int getColumnCount() {
            return COLUMN_NAMES.length;
        }
        
        @Override
        public String getColumnName(int column) {
            return COLUMN_NAMES[column];
        }
        
        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            CodeIssue issue = issues.get(rowIndex);
            
            switch (columnIndex) {
                case 0: return issue.getSeverity();
                case 1: return issue.hasLineNumber() ? String.valueOf(issue.getLineNumber()) : "";
                case 2: return issue.hasCategory() ? issue.getCategory() : "";
                case 3: return issue.getMessage();
                case 4: return issue.hasRuleId() ? issue.getRuleId() : "";
                default: return "";
            }
        }
        
        @Override
        public Class<?> getColumnClass(int columnIndex) {
            switch (columnIndex) {
                case 0: return IssueSeverity.class;
                default: return String.class;
            }
        }
    }
    
    /**
     * 严重级别单元格渲染器
     */
    private static class SeverityCellRenderer extends DefaultTableCellRenderer {
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                     boolean isSelected, boolean hasFocus,
                                                     int row, int column) {
            Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            if (value instanceof IssueSeverity) {
                IssueSeverity severity = (IssueSeverity) value;
                setText(severity.getDisplayName());
                
                if (!isSelected) {
                    setBackground(Color.decode(severity.getColor()));
                    setForeground(Color.WHITE);
                }
            }
            
            return component;
        }
    }
    
    /**
     * 严重级别过滤器
     */
    private enum SeverityFilter {
        ALL("All") {
            @Override
            public boolean accepts(IssueSeverity severity) {
                return true;
            }
        },
        CRITICAL_AND_ERROR("Critical & Error") {
            @Override
            public boolean accepts(IssueSeverity severity) {
                return severity.getLevel() >= IssueSeverity.ERROR.getLevel();
            }
        },
        WARNING_AND_ABOVE("Warning & Above") {
            @Override
            public boolean accepts(IssueSeverity severity) {
                return severity.getLevel() >= IssueSeverity.WARNING.getLevel();
            }
        },
        CRITICAL_ONLY("Critical Only") {
            @Override
            public boolean accepts(IssueSeverity severity) {
                return severity == IssueSeverity.CRITICAL;
            }
        },
        ERROR_ONLY("Error Only") {
            @Override
            public boolean accepts(IssueSeverity severity) {
                return severity == IssueSeverity.ERROR;
            }
        },
        WARNING_ONLY("Warning Only") {
            @Override
            public boolean accepts(IssueSeverity severity) {
                return severity == IssueSeverity.WARNING;
            }
        },
        INFO_ONLY("Info Only") {
            @Override
            public boolean accepts(IssueSeverity severity) {
                return severity == IssueSeverity.INFO;
            }
        };
        
        private final String displayName;
        
        SeverityFilter(String displayName) {
            this.displayName = displayName;
        }
        
        public abstract boolean accepts(IssueSeverity severity);
        
        @Override
        public String toString() {
            return displayName;
        }
    }
}