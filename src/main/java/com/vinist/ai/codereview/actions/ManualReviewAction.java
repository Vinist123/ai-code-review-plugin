package com.vinist.ai.codereview.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.vinist.ai.codereview.services.CodeAnalysisService;
import com.vinist.ai.codereview.services.LLMConfigService;
import com.vinist.ai.codereview.services.ReportGenerationService;
import com.vinist.ai.codereview.models.ReviewReport;
import org.jetbrains.annotations.NotNull;

/**
 * 手动代码审查动作
 * 用户可以通过菜单或快捷键触发此动作来对当前文件进行代码审查
 */
public class ManualReviewAction extends AnAction {
    
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) {
            return;
        }
        
        // 获取当前编辑器和文件
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        VirtualFile file = e.getData(CommonDataKeys.VIRTUAL_FILE);
        
        if (file == null) {
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
        String fileContent = "";
        
        try {
            // 读取文件内容
            fileContent = new String(file.contentsToByteArray(), file.getCharset());
        } catch (Exception ex) {
            // 处理文件读取异常
            return;
        }
        
        // 异步执行代码审查
        analysisService.analyzeCodeAsync(file.getName(), fileContent, new CodeAnalysisService.AnalysisCallback() {
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
        // 只有在有项目和文件时才启用此动作
        Project project = e.getProject();
        VirtualFile file = e.getData(CommonDataKeys.VIRTUAL_FILE);
        
        boolean enabled = project != null && file != null && !file.isDirectory();
        e.getPresentation().setEnabled(enabled);
    }
}