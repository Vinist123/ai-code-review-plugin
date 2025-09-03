package com.vinist.ai.codereview.handlers;

import com.intellij.openapi.vcs.CheckinProjectPanel;
import com.intellij.openapi.vcs.checkin.CheckinHandler;

import com.intellij.openapi.vcs.ui.RefreshableOnComponent;
import com.intellij.openapi.project.Project;
import com.vinist.ai.codereview.ui.CommitReviewIconComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;

/**
 * Git提交窗口AI评审图标处理器
 * 负责在Git提交窗口中添加AI评审图标组件
 */
public class CommitReviewIconHandler extends CheckinHandler {
    
    private final Project project;
    private final CommitReviewIconComponent reviewIconComponent;
    
    public CommitReviewIconHandler(@NotNull Project project, @NotNull CheckinProjectPanel checkinPanel) {
        this.project = project;
        this.reviewIconComponent = new CommitReviewIconComponent(project, checkinPanel);
    }
    
    @Nullable
    public RefreshableOnComponent getBeforeCheckinConfigurationPanel() {
        return new RefreshableOnComponent() {
            @Override
            public JComponent getComponent() {
                return reviewIconComponent.getComponent();
            }
            
            public void refreshState() {
                reviewIconComponent.refreshState();
            }

            @Override
            public void saveState() {
                reviewIconComponent.saveState();
            }

            @Override
            public void restoreState() {
                reviewIconComponent.restoreState();
            }
        };
    }
    
    public @NotNull List<JComponent> getAdditionalComponents() {
        return List.of(reviewIconComponent.getComponent());
    }
    
    @Override
    public ReturnResult beforeCheckin() {
        // 如果AI评审正在进行中，阻止提交
        if (reviewIconComponent.isReviewInProgress()) {
            return ReturnResult.CANCEL;
        }
        return ReturnResult.COMMIT;
    }
    
    @Override
    public void checkinSuccessful() {
        // 提交成功后重置状态
        reviewIconComponent.resetState();
    }
    
//    @Override
//    public void checkinFailed(@NotNull List<? extends Exception> exceptions) {
//        // 提交失败时不需要特殊处理
//    }

}