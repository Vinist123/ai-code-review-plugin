package com.vinist.ai.codereview.vcs;

import com.intellij.openapi.vcs.CheckinProjectPanel;
import com.intellij.openapi.vcs.changes.CommitContext;
import com.intellij.openapi.vcs.checkin.CheckinHandler;
import com.intellij.openapi.vcs.checkin.CheckinHandlerFactory;
import com.intellij.openapi.vcs.ui.RefreshableOnComponent;
import com.vinist.ai.codereview.ui.CommitReviewIconComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Git提交窗口AI评审图标的CheckinHandlerFactory
 * 负责在提交窗口中添加AI评审小图标按钮
 */
public class CommitReviewIconHandlerFactory extends CheckinHandlerFactory {
    
    @NotNull
    @Override
    public CheckinHandler createHandler(@NotNull CheckinProjectPanel panel, @NotNull CommitContext commitContext) {
        return new CommitReviewIconHandler(panel);
    }
    
    /**
     * 内部CheckinHandler类，用于提供UI组件
     */
    private static class CommitReviewIconHandler extends CheckinHandler {
        
        private final CheckinProjectPanel panel;
        private final CommitReviewIconComponent iconComponent;
        
        public CommitReviewIconHandler(@NotNull CheckinProjectPanel panel) {
            this.panel = panel;
            this.iconComponent = new CommitReviewIconComponent(panel.getProject(), panel);
        }
        
        @Nullable
        @Override
        public RefreshableOnComponent getBeforeCheckinConfigurationPanel() {
            return iconComponent;
        }
    }
}