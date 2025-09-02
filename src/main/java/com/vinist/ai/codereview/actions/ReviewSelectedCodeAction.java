package com.vinist.ai.codereview.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.vinist.ai.codereview.services.CodeAnalysisService;
import com.vinist.ai.codereview.services.LLMConfigService;
import com.vinist.ai.codereview.services.ReportGenerationService;
import com.vinist.ai.codereview.models.ReviewReport;
import org.jetbrains.annotations.NotNull;

/**
 * 审查选中代码动作
 * 用户可以选中一段代码，然后通过右键菜单触发此动作来审查选中的代码
 */
public class ReviewSelectedCodeAction extends AnAction {
    
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) {
            return;
        }
        
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        VirtualFile file = e.getData(CommonDataKeys.VIRTUAL_FILE);
        
        if (editor == null || file == null) {
            return;
        }
        
        // 获取选中的代码
        SelectionModel selectionModel = editor.getSelectionModel();
        String selectedText = selectionModel.getSelectedText();
        
        if (selectedText == null || selectedText.trim().isEmpty()) {
            return;
        }
        
        // 检查LLM配置
        LLMConfigService configService = LLMConfigService.getInstance();
        if (!configService.isConfigured()) {
            // 显示配置提示
            return;
        }
        
        // 执行代码分析
        CodeAnalysisService analysisService = CodeAnalysisService.getInstance();
        
        // 异步执行代码审查
        analysisService.analyzeCodeAsync(file.getName(), selectedText, new CodeAnalysisService.AnalysisCallback() {
            @Override
            public void onSuccess(ReviewReport report) {
                // 显示审查结果
                ReportGenerationService reportService = ReportGenerationService.getInstance();
                reportService.showReport(project, report);
            }
            
            @Override
            public void onError(String error) {
                // 显示错误信息
            }
        });
    }
    
    @Override
    public void update(@NotNull AnActionEvent e) {
        // 只有在有选中文本时才启用此动作
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        Project project = e.getProject();
        
        boolean enabled = false;
        if (project != null && editor != null) {
            SelectionModel selectionModel = editor.getSelectionModel();
            String selectedText = selectionModel.getSelectedText();
            enabled = selectedText != null && !selectedText.trim().isEmpty();
        }
        
        e.getPresentation().setEnabled(enabled);
    }
}