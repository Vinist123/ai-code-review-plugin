package com.vinist.ai.codereview.services;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * LLM配置服务
 * 管理LLM相关的配置信息
 */
@Service
@State(name = "LLMConfigService", storages = @Storage("ai-code-review-llm.xml"))
public final class LLMConfigService implements PersistentStateComponent<LLMConfigService.State> {
    
    public static class State {
        public String llmProvider = "openai";
        public String apiKey = "";
        public String apiUrl = "https://api.openai.com/v1";
        public String model = "gpt-3.5-turbo";
        public int maxTokens = 2048;
        public double temperature = 0.3;
        public int timeout = 30;
        public boolean enableProxy = false;
        public String proxyHost = "";
        public int proxyPort = 8080;
    }
    
    private State state = new State();
    
    public static LLMConfigService getInstance() {
        return ApplicationManager.getApplication().getService(LLMConfigService.class);
    }
    
    @Override
    public @Nullable State getState() {
        return state;
    }
    
    @Override
    public void loadState(@NotNull State state) {
        XmlSerializerUtil.copyBean(state, this.state);
    }
    
    // Getters and Setters
    
    public String getLlmProvider() {
        return state.llmProvider;
    }
    
    public void setLlmProvider(String llmProvider) {
        state.llmProvider = llmProvider;
    }
    
    public String getApiKey() {
        return state.apiKey;
    }
    
    public void setApiKey(String apiKey) {
        state.apiKey = apiKey;
    }
    
    public String getApiUrl() {
        return state.apiUrl;
    }
    
    public void setApiUrl(String apiUrl) {
        state.apiUrl = apiUrl;
    }
    
    public String getModel() {
        return state.model;
    }
    
    public void setModel(String model) {
        state.model = model;
    }
    
    public int getMaxTokens() {
        return state.maxTokens;
    }
    
    public void setMaxTokens(int maxTokens) {
        state.maxTokens = maxTokens;
    }
    
    public double getTemperature() {
        return state.temperature;
    }
    
    public void setTemperature(double temperature) {
        state.temperature = temperature;
    }
    
    public int getTimeout() {
        return state.timeout;
    }
    
    public void setTimeout(int timeout) {
        state.timeout = timeout;
    }
    
    public boolean isEnableProxy() {
        return state.enableProxy;
    }
    
    public void setEnableProxy(boolean enableProxy) {
        state.enableProxy = enableProxy;
    }
    
    public String getProxyHost() {
        return state.proxyHost;
    }
    
    public void setProxyHost(String proxyHost) {
        state.proxyHost = proxyHost;
    }
    
    public int getProxyPort() {
        return state.proxyPort;
    }
    
    public void setProxyPort(int proxyPort) {
        state.proxyPort = proxyPort;
    }
    
    // 便利方法
    
    /**
     * 检查配置是否完整
     */
    public boolean isConfigured() {
        return state.apiKey != null && !state.apiKey.trim().isEmpty() &&
               state.apiUrl != null && !state.apiUrl.trim().isEmpty() &&
               state.model != null && !state.model.trim().isEmpty();
    }
    
    /**
     * 检查API密钥是否有效
     */
    public boolean isApiKeyValid() {
        return state.apiKey != null && !state.apiKey.trim().isEmpty() && 
               state.apiKey.length() > 10; // 简单的长度检查
    }
    
    /**
     * 获取完整的API URL
     */
    public String getFullApiUrl() {
        String baseUrl = state.apiUrl;
        if (baseUrl == null || baseUrl.trim().isEmpty()) {
            return "";
        }
        
        // 确保URL以正确的端点结尾
//        if (!baseUrl.endsWith("/")) {
//            baseUrl += "/";
//        }
        
        if ("openai".equals(state.llmProvider)) {
            if (!baseUrl.endsWith("chat/completions")) {
                baseUrl += "chat/completions";
            }
        }
        
        return baseUrl;
    }
    
    /**
     * 重置为默认配置
     */
    public void resetToDefaults() {
        state.llmProvider = "openai";
        state.apiUrl = "https://api.openai.com/v1";
        state.model = "gpt-3.5-turbo";
        state.maxTokens = 2048;
        state.temperature = 0.3;
        state.timeout = 30;
        state.enableProxy = false;
        state.proxyHost = "";
        state.proxyPort = 8080;
        // 保留API密钥
    }
    
    /**
     * 验证配置
     */
    public String validateConfig() {
        if (state.apiKey == null || state.apiKey.trim().isEmpty()) {
            return "API密钥不能为空";
        }
        
        if (state.apiUrl == null || state.apiUrl.trim().isEmpty()) {
            return "API URL不能为空";
        }
        
        if (state.model == null || state.model.trim().isEmpty()) {
            return "模型名称不能为空";
        }
        
        if (state.maxTokens <= 0 || state.maxTokens > 32000) {
            return "最大Token数量必须在1-32000之间";
        }
        
        if (state.temperature < 0 || state.temperature > 2) {
            return "温度值必须在0-2之间";
        }
        
        if (state.timeout <= 0 || state.timeout > 300) {
            return "超时时间必须在1-300秒之间";
        }
        
        return null; // 配置有效
    }
    
    /**
     * 测试连接
     */
    public boolean testConnection() {
        // 简单的配置验证，实际项目中可以发送测试请求
        return isConfigured() && isApiKeyValid();
    }
    
    /**
     * 获取提供商
     */
    public String getProvider() {
        return getLlmProvider();
    }
    
    /**
     * 获取代理配置
     */
    public String getProxy() {
        if (!isEnableProxy() || getProxyHost() == null || getProxyHost().trim().isEmpty()) {
            return "";
        }
        return getProxyHost() + ":" + getProxyPort();
    }
    
    /**
     * 设置提供商
     */
    public void setProvider(String provider) {
        setLlmProvider(provider);
    }
    
    /**
     * 设置代理
     */
    public void setProxy(String proxy) {
        if (proxy == null || proxy.trim().isEmpty()) {
            setEnableProxy(false);
            setProxyHost("");
            setProxyPort(8080);
        } else {
            String[] parts = proxy.split(":");
            if (parts.length >= 1) {
                setProxyHost(parts[0].trim());
                setEnableProxy(true);
            }
            if (parts.length >= 2) {
                try {
                    setProxyPort(Integer.parseInt(parts[1].trim()));
                } catch (NumberFormatException e) {
                    setProxyPort(8080); // 默认端口
                }
            }
        }
    }
}