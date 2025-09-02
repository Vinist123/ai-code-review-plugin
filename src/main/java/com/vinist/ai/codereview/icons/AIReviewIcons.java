package com.vinist.ai.codereview.icons;

import com.intellij.openapi.util.IconLoader;
import javax.swing.Icon;

/**
 * AI代码评审插件图标
 */
public class AIReviewIcons {
    
    /**
     * 16x16 AI评审图标（自动适配主题）
     */
    public static final Icon AI_REVIEW_16 = IconLoader.getIcon("/icons/ai-review-16.svg", AIReviewIcons.class);
    
    /**
     * 32x32 AI评审图标（自动适配主题）
     */
    public static final Icon AI_REVIEW_32 = IconLoader.getIcon("/icons/ai-review-32.svg", AIReviewIcons.class);
    
    /**
     * 16x16 AI评审图标深色主题版本
     */
    public static final Icon AI_REVIEW_16_DARK = IconLoader.getIcon("/icons/ai-review-16_dark.svg", AIReviewIcons.class);
    
    /**
     * 32x32 AI评审图标深色主题版本
     */
    public static final Icon AI_REVIEW_32_DARK = IconLoader.getIcon("/icons/ai-review-32_dark.svg", AIReviewIcons.class);
    
    /**
     * 默认AI评审图标（16x16，自动适配主题）
     */
    public static final Icon AI_REVIEW = AI_REVIEW_16;
}