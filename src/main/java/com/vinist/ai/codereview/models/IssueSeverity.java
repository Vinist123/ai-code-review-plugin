package com.vinist.ai.codereview.models;

/**
 * 问题严重级别枚举
 */
public enum IssueSeverity {
    
    /**
     * 信息级别 - 一般性建议或提示
     */
    INFO(1, "信息", "#2196F3"),
    
    /**
     * 警告级别 - 可能的问题，建议修复
     */
    WARNING(2, "警告", "#FF9800"),
    
    /**
     * 错误级别 - 明确的问题，需要修复
     */
    ERROR(3, "错误", "#F44336"),
    
    /**
     * 严重级别 - 严重问题，必须修复
     */
    CRITICAL(4, "严重", "#9C27B0");
    
    private final int level;
    private final String displayName;
    private final String color;
    
    IssueSeverity(int level, String displayName, String color) {
        this.level = level;
        this.displayName = displayName;
        this.color = color;
    }
    
    public int getLevel() {
        return level;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getColor() {
        return color;
    }
    
    /**
     * 根据级别数值获取严重级别
     */
    public static IssueSeverity fromLevel(int level) {
        for (IssueSeverity severity : values()) {
            if (severity.level == level) {
                return severity;
            }
        }
        return INFO; // 默认返回INFO级别
    }
    
    /**
     * 根据名称获取严重级别
     */
    public static IssueSeverity fromName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return INFO;
        }
        
        String upperName = name.trim().toUpperCase();
        for (IssueSeverity severity : values()) {
            if (severity.name().equals(upperName) || 
                severity.displayName.equals(name.trim())) {
                return severity;
            }
        }
        return INFO; // 默认返回INFO级别
    }
    
    /**
     * 检查是否比指定级别更严重
     */
    public boolean isMoreSevereThan(IssueSeverity other) {
        return this.level > other.level;
    }
    
    /**
     * 检查是否比指定级别更轻微
     */
    public boolean isLessSevereThan(IssueSeverity other) {
        return this.level < other.level;
    }
    
    /**
     * 检查是否达到指定的最低严重级别
     */
    public boolean meetsMinimumLevel(IssueSeverity minimumLevel) {
        return this.level >= minimumLevel.level;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}