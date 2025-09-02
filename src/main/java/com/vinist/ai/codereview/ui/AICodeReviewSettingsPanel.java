package com.vinist.ai.codereview.ui;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.vinist.ai.codereview.models.IssueSeverity;
import com.vinist.ai.codereview.services.LLMConfigService;
import com.vinist.ai.codereview.services.ReviewSettingsService;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;

/**
 * AI代码审查设置面板
 */
public class AICodeReviewSettingsPanel {
    
    private JPanel mainPanel;
    
    // LLM配置字段
    private JTextField apiKeyField;
    private JTextField apiUrlField;
    private JComboBox<String> providerComboBox;
    private JTextField modelField;
    private JSpinner maxTokensSpinner;
    private JSpinner temperatureSpinner;
    private JSpinner timeoutSpinner;
    private JTextField proxyField;
    
    // 审查设置字段
    private JCheckBox enableAutoReviewCheckBox;
    private JCheckBox showReviewDialogCheckBox;
    private JCheckBox outputToConsoleCheckBox;
    private JComboBox<String> reviewLanguageComboBox;
    private JComboBox<String> reviewFocusComboBox;
    private JComboBox<IssueSeverity> minSeverityComboBox;
    private JCheckBox showLineNumbersCheckBox;
    private JCheckBox enableCodeSuggestionsCheckBox;
    private JCheckBox enableCategoryFilteringCheckBox;
    private JSpinner maxIssuesPerFileSpinner;
    private JCheckBox enableSoundNotificationCheckBox;
    private JCheckBox saveReportsCheckBox;
    
    // 测试按钮
    private JButton testConnectionButton;
    private JButton resetToDefaultsButton;
    
    private final LLMConfigService llmConfigService;
    private final ReviewSettingsService reviewSettingsService;
    private final Project project;
    
    public AICodeReviewSettingsPanel(Project project) {
        this.project = project;
        this.llmConfigService = LLMConfigService.getInstance();
        this.reviewSettingsService = ReviewSettingsService.getInstance();
        createUIComponents();
        loadSettings();
        setupEventHandlers();
    }
    
    /**
     * 创建UI组件
     */
    private void createUIComponents() {
        mainPanel = new JPanel(new BorderLayout());
        
        // 创建选项卡面板
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // LLM配置选项卡
        JPanel llmPanel = createLLMConfigPanel();
        tabbedPane.addTab("LLM配置", llmPanel);
        
        // 审查设置选项卡
        JPanel reviewPanel = createReviewSettingsPanel();
        tabbedPane.addTab("审查设置", reviewPanel);
        
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        
        // 底部按钮面板
        JPanel buttonPanel = createButtonPanel();
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
    }
    
    /**
     * 创建LLM配置面板
     */
    private JPanel createLLMConfigPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // API提供商
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("API提供商:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        providerComboBox = new ComboBox<>(new String[]{"OpenAI", "Claude", "Custom"});
        panel.add(providerComboBox, gbc);
        
        // API密钥
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("API密钥:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        apiKeyField = new JPasswordField(30);
        panel.add(apiKeyField, gbc);
        
        // API URL
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("API URL:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        apiUrlField = new JTextField(30);
        panel.add(apiUrlField, gbc);
        
        // 模型
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("模型:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        modelField = new JTextField(30);
        panel.add(modelField, gbc);
        
        // 最大令牌数
        gbc.gridx = 0; gbc.gridy = 4; gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("最大令牌数:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        maxTokensSpinner = new JSpinner(new SpinnerNumberModel(2000, 100, 10000, 100));
        panel.add(maxTokensSpinner, gbc);
        
        // 温度
        gbc.gridx = 0; gbc.gridy = 5; gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("温度 (0.0-1.0):"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        temperatureSpinner = new JSpinner(new SpinnerNumberModel(0.3, 0.0, 1.0, 0.1));
        panel.add(temperatureSpinner, gbc);
        
        // 超时时间
        gbc.gridx = 0; gbc.gridy = 6; gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("超时时间(秒):"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        timeoutSpinner = new JSpinner(new SpinnerNumberModel(60, 10, 300, 10));
        panel.add(timeoutSpinner, gbc);
        
        // 代理设置
        gbc.gridx = 0; gbc.gridy = 7; gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("代理 (可选):"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        proxyField = new JTextField(30);
        panel.add(proxyField, gbc);
        
        return panel;
    }
    
    /**
     * 创建审查设置面板
     */
    private JPanel createReviewSettingsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // 启用自动审查
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        enableAutoReviewCheckBox = new JCheckBox("启用自动审查");
        panel.add(enableAutoReviewCheckBox, gbc);
        
        // 显示审查对话框
        gbc.gridy = 1;
        showReviewDialogCheckBox = new JCheckBox("显示审查对话框");
        panel.add(showReviewDialogCheckBox, gbc);
        
        // 输出到控制台
        gbc.gridy = 2;
        outputToConsoleCheckBox = new JCheckBox("输出到控制台");
        panel.add(outputToConsoleCheckBox, gbc);
        
        // 显示行号
        gbc.gridy = 3;
        showLineNumbersCheckBox = new JCheckBox("显示行号");
        panel.add(showLineNumbersCheckBox, gbc);
        
        // 启用代码建议
        gbc.gridy = 4;
        enableCodeSuggestionsCheckBox = new JCheckBox("启用代码建议");
        panel.add(enableCodeSuggestionsCheckBox, gbc);
        
        // 启用类别过滤
        gbc.gridy = 5;
        enableCategoryFilteringCheckBox = new JCheckBox("启用类别过滤");
        panel.add(enableCategoryFilteringCheckBox, gbc);
        
        // 启用声音通知
        gbc.gridy = 6;
        enableSoundNotificationCheckBox = new JCheckBox("启用声音通知");
        panel.add(enableSoundNotificationCheckBox, gbc);
        
        // 保存报告
        gbc.gridy = 7;
        saveReportsCheckBox = new JCheckBox("保存报告到文件");
        panel.add(saveReportsCheckBox, gbc);
        
        // 审查语言
        gbc.gridx = 0; gbc.gridy = 8; gbc.gridwidth = 1;
        panel.add(new JLabel("审查语言:"), gbc);
        gbc.gridx = 1;
        reviewLanguageComboBox = new ComboBox<>(new String[]{"Chinese", "English"});
        panel.add(reviewLanguageComboBox, gbc);
        
        // 审查重点
        gbc.gridx = 0; gbc.gridy = 9;
        panel.add(new JLabel("审查重点:"), gbc);
        gbc.gridx = 1;
        reviewFocusComboBox = new ComboBox<>(new String[]{
            "comprehensive", "security", "performance", "maintainability", "bugs"
        });
        panel.add(reviewFocusComboBox, gbc);
        
        // 最小严重程度
        gbc.gridx = 0; gbc.gridy = 10;
        panel.add(new JLabel("最小严重程度:"), gbc);
        gbc.gridx = 1;
        minSeverityComboBox = new ComboBox<>(IssueSeverity.values());
        panel.add(minSeverityComboBox, gbc);
        
        // 每个文件最大问题数
        gbc.gridx = 0; gbc.gridy = 11;
        panel.add(new JLabel("每个文件最大问题数:"), gbc);
        gbc.gridx = 1;
        maxIssuesPerFileSpinner = new JSpinner(new SpinnerNumberModel(50, 1, 200, 5));
        panel.add(maxIssuesPerFileSpinner, gbc);
        
        return panel;
    }
    
    /**
     * 创建按钮面板
     */
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        testConnectionButton = new JButton("测试连接");
        resetToDefaultsButton = new JButton("重置为默认值");
        
        panel.add(testConnectionButton);
        panel.add(resetToDefaultsButton);
        
        return panel;
    }
    
    /**
     * 设置事件处理器
     */
    private void setupEventHandlers() {
        // 提供商变更事件
        providerComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateFieldsForProvider();
            }
        });
        
        // 测试连接按钮
        testConnectionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                testConnection();
            }
        });
        
        // 重置按钮
        resetToDefaultsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetToDefaults();
            }
        });
    }
    
    /**
     * 根据提供商更新字段
     */
    private void updateFieldsForProvider() {
        String provider = (String) providerComboBox.getSelectedItem();
        if (provider == null) return;
        
        switch (provider) {
            case "OpenAI":
                apiUrlField.setText("https://api.openai.com/v1/chat/completions");
                modelField.setText("gpt-3.5-turbo");
                break;
            case "Claude":
                apiUrlField.setText("https://api.anthropic.com/v1/messages");
                modelField.setText("claude-3-sonnet-20240229");
                break;
            case "Custom":
                // 保持用户自定义的值
                break;
        }
    }
    
    /**
     * 测试连接
     */
    private void testConnection() {
        try {
            // 先应用当前设置
            applySettings();
            
            // 测试连接
            boolean success = llmConfigService.testConnection();
            
            if (success) {
                JOptionPane.showMessageDialog(mainPanel, "连接测试成功！", "测试结果", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(mainPanel, "连接测试失败，请检查配置。", "测试结果", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(mainPanel, "测试连接时发生错误: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * 重置为默认值
     */
    private void resetToDefaults() {
        int result = JOptionPane.showConfirmDialog(
                mainPanel,
                "确定要重置所有设置为默认值吗？",
                "确认重置",
                JOptionPane.YES_NO_OPTION
        );
        
        if (result == JOptionPane.YES_OPTION) {
            llmConfigService.resetToDefaults();
            reviewSettingsService.resetToDefaults();
            loadSettings();
        }
    }
    
    /**
     * 加载设置
     */
    public void loadSettings() {
        // 加载LLM配置
        providerComboBox.setSelectedItem(llmConfigService.getProvider());
        apiKeyField.setText(llmConfigService.getApiKey());
        apiUrlField.setText(llmConfigService.getApiUrl());
        modelField.setText(llmConfigService.getModel());
        maxTokensSpinner.setValue(llmConfigService.getMaxTokens());
        temperatureSpinner.setValue(llmConfigService.getTemperature());
        timeoutSpinner.setValue(llmConfigService.getTimeout());
        proxyField.setText(llmConfigService.getProxy());
        
        // 加载审查设置
        enableAutoReviewCheckBox.setSelected(reviewSettingsService.isEnableAutoReview());
        showReviewDialogCheckBox.setSelected(reviewSettingsService.isShowReviewDialog());
        outputToConsoleCheckBox.setSelected(reviewSettingsService.isOutputToConsole());
        reviewLanguageComboBox.setSelectedItem(reviewSettingsService.getReviewLanguage());
        reviewFocusComboBox.setSelectedItem(reviewSettingsService.getReviewFocus());
        minSeverityComboBox.setSelectedItem(reviewSettingsService.getMinSeverityLevel());
        showLineNumbersCheckBox.setSelected(reviewSettingsService.isShowLineNumbers());
        enableCodeSuggestionsCheckBox.setSelected(reviewSettingsService.isEnableCodeSuggestions());
        enableCategoryFilteringCheckBox.setSelected(reviewSettingsService.isEnableCategoryFiltering());
        maxIssuesPerFileSpinner.setValue(reviewSettingsService.getMaxIssuesPerFile());
        enableSoundNotificationCheckBox.setSelected(reviewSettingsService.isEnableSoundNotification());
        saveReportsCheckBox.setSelected(reviewSettingsService.isSaveReports());
    }
    
    /**
     * 应用设置
     */
    public void applySettings() throws ConfigurationException {
        try {
            // 应用LLM配置
            llmConfigService.setProvider((String) providerComboBox.getSelectedItem());
            llmConfigService.setApiKey(apiKeyField.getText());
            llmConfigService.setApiUrl(apiUrlField.getText());
            llmConfigService.setModel(modelField.getText());
            llmConfigService.setMaxTokens((Integer) maxTokensSpinner.getValue());
            llmConfigService.setTemperature((Double) temperatureSpinner.getValue());
            llmConfigService.setTimeout((Integer) timeoutSpinner.getValue());
            llmConfigService.setProxy(proxyField.getText());
            
            // 应用审查设置
            reviewSettingsService.setEnableAutoReview(enableAutoReviewCheckBox.isSelected());
            reviewSettingsService.setShowReviewDialog(showReviewDialogCheckBox.isSelected());
            reviewSettingsService.setOutputToConsole(outputToConsoleCheckBox.isSelected());
            reviewSettingsService.setReviewLanguage((String) reviewLanguageComboBox.getSelectedItem());
            reviewSettingsService.setReviewFocus((String) reviewFocusComboBox.getSelectedItem());
            reviewSettingsService.setMinSeverityLevel(((IssueSeverity) minSeverityComboBox.getSelectedItem()).name());
            reviewSettingsService.setShowLineNumbers(showLineNumbersCheckBox.isSelected());
            reviewSettingsService.setEnableCodeSuggestions(enableCodeSuggestionsCheckBox.isSelected());
            reviewSettingsService.setEnableCategoryFiltering(enableCategoryFilteringCheckBox.isSelected());
            reviewSettingsService.setMaxIssuesPerFile((Integer) maxIssuesPerFileSpinner.getValue());
            reviewSettingsService.setEnableSoundNotification(enableSoundNotificationCheckBox.isSelected());
            reviewSettingsService.setSaveReports(saveReportsCheckBox.isSelected());
            
            // 验证配置
            if (!llmConfigService.isConfigured()) {
                throw new ConfigurationException("LLM配置不完整，请检查API密钥和URL设置。");
            }
            
        } catch (Exception e) {
            throw new ConfigurationException("保存配置时发生错误: " + e.getMessage());
        }
    }
    
    /**
     * 检查设置是否已修改
     */
    public boolean isModified() {
        // 检查LLM配置是否修改
        if (!llmConfigService.getProvider().equals(providerComboBox.getSelectedItem()) ||
            !llmConfigService.getApiKey().equals(apiKeyField.getText()) ||
            !llmConfigService.getApiUrl().equals(apiUrlField.getText()) ||
            !llmConfigService.getModel().equals(modelField.getText()) ||
            !Objects.equals(llmConfigService.getMaxTokens(), (Integer) maxTokensSpinner.getValue()) ||
            !Objects.equals(llmConfigService.getTemperature(), (Double) temperatureSpinner.getValue()) ||
            !Objects.equals(llmConfigService.getTimeout(), (Integer) timeoutSpinner.getValue()) ||
            !llmConfigService.getProxy().equals(proxyField.getText())) {
            return true;
        }
        
        // 检查审查设置是否修改
        if (reviewSettingsService.isEnableAutoReview() != enableAutoReviewCheckBox.isSelected() ||
            reviewSettingsService.isShowReviewDialog() != showReviewDialogCheckBox.isSelected() ||
            reviewSettingsService.isOutputToConsole() != outputToConsoleCheckBox.isSelected() ||
            !reviewSettingsService.getReviewLanguage().equals(reviewLanguageComboBox.getSelectedItem()) ||
            !reviewSettingsService.getReviewFocus().equals(reviewFocusComboBox.getSelectedItem()) ||
            !reviewSettingsService.getMinSeverityLevel().equals(((IssueSeverity) minSeverityComboBox.getSelectedItem()).name()) ||
            reviewSettingsService.isShowLineNumbers() != showLineNumbersCheckBox.isSelected() ||
            reviewSettingsService.isEnableCodeSuggestions() != enableCodeSuggestionsCheckBox.isSelected() ||
            reviewSettingsService.isEnableCategoryFiltering() != enableCategoryFilteringCheckBox.isSelected() ||
            !Objects.equals(reviewSettingsService.getMaxIssuesPerFile(), (Integer) maxIssuesPerFileSpinner.getValue()) ||
            reviewSettingsService.isEnableSoundNotification() != enableSoundNotificationCheckBox.isSelected() ||
            reviewSettingsService.isSaveReports() != saveReportsCheckBox.isSelected()) {
            return true;
        }
        
        return false;
    }
    
    /**
     * 获取主面板
     */
    @NotNull
    public JPanel getMainPanel() {
        return mainPanel;
    }
    
    /**
     * 重置设置
     */
    public void reset() {
        loadSettings();
    }
    
    /**
     * 应用设置
     */
    public void apply() throws ConfigurationException {
        applySettings();
    }
    
    /**
     * 释放资源
     */
    public void dispose() {
        // 清理资源
        mainPanel = null;
    }
}