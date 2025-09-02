package com.vinist.ai.codereview.services;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

/**
 * LLM客户端服务
 * 负责与LLM服务进行通信
 */
@Service
public final class LLMClientService {
    
    private final HttpClient httpClient;
    
    public LLMClientService() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .build();
    }
    
    public static LLMClientService getInstance() {
        return ApplicationManager.getApplication().getService(LLMClientService.class);
    }
    
    /**
     * 审查代码
     */
    public String reviewCode(@NotNull String code, @NotNull String fileName) throws Exception {
        LLMConfigService configService = LLMConfigService.getInstance();
        
        if (!configService.isConfigured()) {
            throw new IllegalStateException("LLM配置未完成");
        }
        
        String prompt = buildReviewPrompt(code, fileName);
        return callLLM(prompt);
    }
    
    /**
     * 异步审查代码
     */
    public CompletableFuture<String> reviewCodeAsync(@NotNull String code, @NotNull String fileName) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return reviewCode(code, fileName);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
    
    /**
     * 构建审查提示词
     */
    private String buildReviewPrompt(@NotNull String code, @NotNull String fileName) {
        ReviewSettingsService settingsService = ReviewSettingsService.getInstance();
        String language = settingsService.getReviewLanguage();
        String focus = settingsService.getReviewFocus();
        
        StringBuilder prompt = new StringBuilder();
        
        if ("Chinese".equals(language)) {
            prompt.append("请对以下代码进行详细的代码审查，文件名：").append(fileName).append("\n\n");
            prompt.append("审查重点：");
            
            switch (focus) {
                case "security":
                    prompt.append("安全性问题");
                    break;
                case "performance":
                    prompt.append("性能优化");
                    break;
                case "maintainability":
                    prompt.append("可维护性");
                    break;
                case "bugs":
                    prompt.append("潜在错误");
                    break;
                default:
                    prompt.append("全面审查（包括安全性、性能、可维护性、潜在错误等）");
            }
            
            prompt.append("\n\n请按以下格式返回结果：\n");
            prompt.append("1. 对于发现的问题，请以'ERROR:'、'WARNING:'、'INFO:'或'CRITICAL:'开头\n");
            prompt.append("2. 每个问题单独一行\n");
            prompt.append("3. 在最后提供总体评价\n\n");
        } else {
            prompt.append("Please conduct a detailed code review for the following code, file name: ").append(fileName).append("\n\n");
            prompt.append("Review focus: ");
            
            switch (focus) {
                case "security":
                    prompt.append("Security issues");
                    break;
                case "performance":
                    prompt.append("Performance optimization");
                    break;
                case "maintainability":
                    prompt.append("Maintainability");
                    break;
                case "bugs":
                    prompt.append("Potential bugs");
                    break;
                default:
                    prompt.append("Comprehensive review (including security, performance, maintainability, potential bugs, etc.)");
            }
            
            prompt.append("\n\nPlease return results in the following format:\n");
            prompt.append("1. For issues found, please start with 'ERROR:', 'WARNING:', 'INFO:', or 'CRITICAL:'\n");
            prompt.append("2. Each issue on a separate line\n");
            prompt.append("3. Provide an overall assessment at the end\n\n");
        }
        
        prompt.append("代码内容/Code content:\n");
        prompt.append("```\n");
        prompt.append(code);
        prompt.append("\n```");
        
        return prompt.toString();
    }
    
    /**
     * 调用LLM服务
     */
    private String callLLM(@NotNull String prompt) throws Exception {
        LLMConfigService configService = LLMConfigService.getInstance();
        
        String requestBody = buildRequestBody(prompt, configService);
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(configService.getFullApiUrl()))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + configService.getApiKey())
                .timeout(Duration.ofSeconds(configService.getTimeout()))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() != 200) {
            throw new IOException("LLM API调用失败，状态码: " + response.statusCode() + ", 响应: " + response.body());
        }
        
        return parseResponse(response.body());
    }
    
    /**
     * 构建请求体
     */
    private String buildRequestBody(@NotNull String prompt, @NotNull LLMConfigService configService) {
        // 简化的JSON构建，实际应该使用JSON库
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"model\":\"").append(configService.getModel()).append("\",");
        json.append("\"messages\":[{");
        json.append("\"role\":\"user\",");
        json.append("\"content\":\"").append(escapeJson(prompt)).append("\"");
        json.append("}],");
        json.append("\"max_tokens\":").append(configService.getMaxTokens()).append(",");
        json.append("\"temperature\":").append(configService.getTemperature());
        json.append("}");
        
        return json.toString();
    }
    
    /**
     * 解析响应
     */
    private String parseResponse(@NotNull String responseBody) {
        // 简化的JSON解析，实际应该使用JSON库
        try {
            // 查找content字段
            String contentMarker = "\"content\":\"";
            int contentStart = responseBody.indexOf(contentMarker);
            if (contentStart == -1) {
                return "解析响应失败：未找到content字段";
            }
            
            contentStart += contentMarker.length();
            int contentEnd = responseBody.indexOf("\"", contentStart);
            if (contentEnd == -1) {
                return "解析响应失败：content字段格式错误";
            }
            
            String content = responseBody.substring(contentStart, contentEnd);
            return unescapeJson(content);
            
        } catch (Exception e) {
            return "解析响应时发生错误: " + e.getMessage();
        }
    }
    
    /**
     * JSON字符串转义
     */
    private String escapeJson(@NotNull String text) {
        return text.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }
    
    /**
     * JSON字符串反转义
     */
    private String unescapeJson(@NotNull String text) {
        return text.replace("\\n", "\n")
                  .replace("\\r", "\r")
                  .replace("\\t", "\t")
                  .replace("\\\"", "\"")
                  .replace("\\\\", "\\");
    }
    
    /**
     * 测试连接
     */
    public boolean testConnection() {
        try {
            String testCode = "public class Test { public static void main(String[] args) { System.out.println(\"Hello\"); } }";
            String result = reviewCode(testCode, "Test.java");
            return result != null && !result.trim().isEmpty();
        } catch (Exception e) {
            return false;
        }
    }
}