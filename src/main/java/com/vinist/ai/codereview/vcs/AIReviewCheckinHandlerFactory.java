package com.vinist.ai.codereview.vcs;

import com.intellij.openapi.vcs.CheckinProjectPanel;
import com.intellij.openapi.vcs.changes.CommitContext;
import com.intellij.openapi.vcs.checkin.CheckinHandler;
import com.intellij.openapi.vcs.checkin.CheckinHandlerFactory;
import com.vinist.ai.codereview.handlers.AIReviewCheckinHandler;
import org.jetbrains.annotations.NotNull;

/**
 * AI代码评审的提交处理器工厂
 * 用于在Git提交时创建AI代码评审处理器
 */
public class AIReviewCheckinHandlerFactory extends CheckinHandlerFactory {

    @NotNull
    @Override
    public CheckinHandler createHandler(@NotNull CheckinProjectPanel panel, @NotNull CommitContext commitContext) {
        return new AIReviewCheckinHandler(panel.getProject(), panel);
    }

    /**
     * 获取工厂的唯一标识
     */
//    @Override
//    public String getId() {
//        return "AIReviewCheckinHandlerFactory";
//    }
//
//    /**
//     * 获取工厂的显示名称
//     */
//    @Override
//    public String getDisplayName() {
//        return "AI Code Review";
//    }

    /**
     * 获取工厂的描述
     */
    public String getDescription() {
        return "Performs AI-powered code review before commit";
    }

    /**
     * 检查是否应该为指定的面板创建处理器
     */
    public boolean shouldCreateHandler(@NotNull CheckinProjectPanel panel) {
        // 只为有Git支持的项目创建处理器
        return panel.getProject() != null && !panel.getProject().isDisposed();
    }
}