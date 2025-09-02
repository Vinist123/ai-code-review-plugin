package com.vinist.ai.codereview.models;

import com.intellij.openapi.util.text.StringUtil;
import java.util.Objects;

/**
 * LLM配置模型类
 * 用于存储和管理LLM服务的配置信息
 */
public class LLMConfig {
    private String provider = "openai";
    private String apiKey = "";
    private String apiUrl = "https://api.openai.com/v1/chat/completions";
    private String model = "gpt-3.5-turbo";
    private int maxTokens = 2048;
    private double temperature = 0.3;
    private int timeout = 30;
    private String proxyHost = "";
    private int proxyPort = 0;
    private String proxyUsername = "";
    private String proxyPassword = "";

    public LLMConfig() {
    }

    public LLMConfig(String provider, String apiKey, String apiUrl, String model) {
        this.provider = provider;
        this.apiKey = apiKey;
        this.apiUrl = apiUrl;
        this.model = model;
    }

    // Getters and Setters
    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getMaxTokens() {
        return maxTokens;
    }

    public void setMaxTokens(int maxTokens) {
        this.maxTokens = maxTokens;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public String getProxyHost() {
        return proxyHost;
    }

    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
    }

    public int getProxyPort() {
        return proxyPort;
    }

    public void setProxyPort(int proxyPort) {
        this.proxyPort = proxyPort;
    }

    public String getProxyUsername() {
        return proxyUsername;
    }

    public void setProxyUsername(String proxyUsername) {
        this.proxyUsername = proxyUsername;
    }

    public String getProxyPassword() {
        return proxyPassword;
    }

    public void setProxyPassword(String proxyPassword) {
        this.proxyPassword = proxyPassword;
    }

    /**
     * 检查配置是否完整
     */
    public boolean isValid() {
        return !StringUtil.isEmpty(provider) && 
               !StringUtil.isEmpty(apiKey) && 
               !StringUtil.isEmpty(apiUrl) && 
               !StringUtil.isEmpty(model);
    }

    /**
     * 检查是否配置了代理
     */
    public boolean hasProxy() {
        return !StringUtil.isEmpty(proxyHost) && proxyPort > 0;
    }

    /**
     * 检查代理是否需要认证
     */
    public boolean hasProxyAuth() {
        return hasProxy() && !StringUtil.isEmpty(proxyUsername);
    }

    /**
     * 重置为默认配置
     */
    public void resetToDefaults() {
        this.provider = "openai";
        this.apiKey = "";
        this.apiUrl = "https://api.openai.com/v1/chat/completions";
        this.model = "gpt-3.5-turbo";
        this.maxTokens = 2048;
        this.temperature = 0.3;
        this.timeout = 30;
        this.proxyHost = "";
        this.proxyPort = 0;
        this.proxyUsername = "";
        this.proxyPassword = "";
    }

    /**
     * 复制配置
     */
    public LLMConfig copy() {
        LLMConfig copy = new LLMConfig();
        copy.provider = this.provider;
        copy.apiKey = this.apiKey;
        copy.apiUrl = this.apiUrl;
        copy.model = this.model;
        copy.maxTokens = this.maxTokens;
        copy.temperature = this.temperature;
        copy.timeout = this.timeout;
        copy.proxyHost = this.proxyHost;
        copy.proxyPort = this.proxyPort;
        copy.proxyUsername = this.proxyUsername;
        copy.proxyPassword = this.proxyPassword;
        return copy;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        LLMConfig that = (LLMConfig) obj;
        return maxTokens == that.maxTokens &&
               Double.compare(that.temperature, temperature) == 0 &&
               timeout == that.timeout &&
               proxyPort == that.proxyPort &&
               Objects.equals(provider, that.provider) &&
               Objects.equals(apiKey, that.apiKey) &&
               Objects.equals(apiUrl, that.apiUrl) &&
               Objects.equals(model, that.model) &&
               Objects.equals(proxyHost, that.proxyHost) &&
               Objects.equals(proxyUsername, that.proxyUsername) &&
               Objects.equals(proxyPassword, that.proxyPassword);
    }

    @Override
    public int hashCode() {
        return Objects.hash(provider, apiKey, apiUrl, model, maxTokens, 
                          temperature, timeout, proxyHost, proxyPort, 
                          proxyUsername, proxyPassword);
    }

    @Override
    public String toString() {
        return "LLMConfig{" +
                "provider='" + provider + '\'' +
                ", apiUrl='" + apiUrl + '\'' +
                ", model='" + model + '\'' +
                ", maxTokens=" + maxTokens +
                ", temperature=" + temperature +
                ", timeout=" + timeout +
                ", hasProxy=" + hasProxy() +
                '}';
    }
}