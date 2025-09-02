package com.vinist.ai.codereview.settings;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.vinist.ai.codereview.services.LLMConfigService;
import com.vinist.ai.codereview.services.ReviewSettingsService;
import com.vinist.ai.codereview.ui.AICodeReviewSettingsPanel;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * AI代码评审插件的配置页面
 * 在IntelliJ IDEA的设置中显示
 */
public class AICodeReviewConfigurable implements Configurable {
    private AICodeReviewSettingsPanel settingsPanel;
    private final LLMConfigService llmConfigService;
    private final ReviewSettingsService reviewSettingsService;

    public AICodeReviewConfigurable() {
        this.llmConfigService = LLMConfigService.getInstance();
        this.reviewSettingsService = ReviewSettingsService.getInstance();
    }

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "AI Code Review";
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return "ai.codereview.settings";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        if (settingsPanel == null) {
            // 获取当前项目，如果没有则使用默认项目
            Project project = getCurrentProject();
            settingsPanel = new AICodeReviewSettingsPanel(project);
        }
        return settingsPanel.getMainPanel();
    }

    @Override
    public boolean isModified() {
        return settingsPanel != null && settingsPanel.isModified();
    }

    @Override
    public void apply() throws ConfigurationException {
        if (settingsPanel != null) {
            try {
                settingsPanel.apply();
            } catch (Exception e) {
                throw new ConfigurationException("Failed to apply settings: " + e.getMessage());
            }
        }
    }

    @Override
    public void reset() {
        if (settingsPanel != null) {
            settingsPanel.reset();
        }
    }

    @Override
    public void disposeUIResources() {
        if (settingsPanel != null) {
            settingsPanel.dispose();
            settingsPanel = null;
        }
    }

    /**
     * 获取当前项目
     * 如果没有打开的项目，返回默认项目
     */
    private Project getCurrentProject() {
        Project[] openProjects = ProjectManager.getInstance().getOpenProjects();
        if (openProjects.length > 0) {
            return openProjects[0];
        }
        return ProjectManager.getInstance().getDefaultProject();
    }

    /**
     * 获取配置页面的唯一ID
     */
//    @Override
//    public String getId() {
//        return "com.vinist.ai.codereview.settings.AICodeReviewConfigurable";
//    }

    /**
     * 检查配置是否有效
     */
    public boolean isConfigurationValid() {
        return llmConfigService.isConfigured() && 
               llmConfigService.isApiKeyValid();
    }

    /**
     * 获取配置状态描述
     */
    public String getConfigurationStatus() {
        if (!llmConfigService.isConfigured()) {
            return "LLM configuration is incomplete";
        }
        if (!llmConfigService.isApiKeyValid()) {
            return "API key is invalid";
        }
        return "Configuration is valid";
    }
}