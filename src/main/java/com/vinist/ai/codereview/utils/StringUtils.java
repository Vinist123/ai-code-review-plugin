package com.vinist.ai.codereview.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串工具类
 */
public class StringUtils {
    
    /**
     * 检查字符串是否为空或null
     */
    public static boolean isEmpty(@Nullable String str) {
        return str == null || str.isEmpty();
    }
    
    /**
     * 检查字符串是否不为空
     */
    public static boolean isNotEmpty(@Nullable String str) {
        return !isEmpty(str);
    }
    
    /**
     * 检查字符串是否为空白（null、空字符串或只包含空白字符）
     */
    public static boolean isBlank(@Nullable String str) {
        return str == null || str.trim().isEmpty();
    }
    
    /**
     * 检查字符串是否不为空白
     */
    public static boolean isNotBlank(@Nullable String str) {
        return !isBlank(str);
    }
    
    /**
     * 安全地获取字符串，如果为null则返回空字符串
     */
    @NotNull
    public static String nullToEmpty(@Nullable String str) {
        return str == null ? "" : str;
    }
    
    /**
     * 安全地获取字符串，如果为空则返回默认值
     */
    @NotNull
    public static String defaultIfEmpty(@Nullable String str, @NotNull String defaultValue) {
        return isEmpty(str) ? defaultValue : str;
    }
    
    /**
     * 安全地获取字符串，如果为空白则返回默认值
     */
    @NotNull
    public static String defaultIfBlank(@Nullable String str, @NotNull String defaultValue) {
        return isBlank(str) ? defaultValue : str;
    }
    
    /**
     * 去除字符串两端的空白字符
     */
    @Nullable
    public static String trim(@Nullable String str) {
        return str == null ? null : str.trim();
    }
    
    /**
     * 安全地去除字符串两端的空白字符，如果为null则返回空字符串
     */
    @NotNull
    public static String trimToEmpty(@Nullable String str) {
        return str == null ? "" : str.trim();
    }
    
    /**
     * 截断字符串到指定长度
     */
    @NotNull
    public static String truncate(@NotNull String str, int maxLength) {
        if (str.length() <= maxLength) {
            return str;
        }
        return str.substring(0, maxLength);
    }
    
    /**
     * 截断字符串到指定长度，并添加省略号
     */
    @NotNull
    public static String truncateWithEllipsis(@NotNull String str, int maxLength) {
        if (str.length() <= maxLength) {
            return str;
        }
        
        if (maxLength <= 3) {
            return "...";
        }
        
        return str.substring(0, maxLength - 3) + "...";
    }
    
    /**
     * 重复字符串指定次数
     */
    @NotNull
    public static String repeat(@NotNull String str, int count) {
        if (count <= 0) {
            return "";
        }
        
        StringBuilder sb = new StringBuilder(str.length() * count);
        for (int i = 0; i < count; i++) {
            sb.append(str);
        }
        return sb.toString();
    }
    
    /**
     * 左填充字符串到指定长度
     */
    @NotNull
    public static String leftPad(@NotNull String str, int size, char padChar) {
        if (str.length() >= size) {
            return str;
        }
        
        int padLength = size - str.length();
        return repeat(String.valueOf(padChar), padLength) + str;
    }
    
    /**
     * 右填充字符串到指定长度
     */
    @NotNull
    public static String rightPad(@NotNull String str, int size, char padChar) {
        if (str.length() >= size) {
            return str;
        }
        
        int padLength = size - str.length();
        return str + repeat(String.valueOf(padChar), padLength);
    }
    
    /**
     * 居中填充字符串到指定长度
     */
    @NotNull
    public static String center(@NotNull String str, int size, char padChar) {
        if (str.length() >= size) {
            return str;
        }
        
        int padLength = size - str.length();
        int leftPad = padLength / 2;
        int rightPad = padLength - leftPad;
        
        return repeat(String.valueOf(padChar), leftPad) + str + repeat(String.valueOf(padChar), rightPad);
    }
    
    /**
     * 反转字符串
     */
    @NotNull
    public static String reverse(@NotNull String str) {
        return new StringBuilder(str).reverse().toString();
    }
    
    /**
     * 检查字符串是否包含指定子字符串（忽略大小写）
     */
    public static boolean containsIgnoreCase(@NotNull String str, @NotNull String searchStr) {
        return str.toLowerCase().contains(searchStr.toLowerCase());
    }
    
    /**
     * 检查字符串是否以指定前缀开始（忽略大小写）
     */
    public static boolean startsWithIgnoreCase(@NotNull String str, @NotNull String prefix) {
        return str.toLowerCase().startsWith(prefix.toLowerCase());
    }
    
    /**
     * 检查字符串是否以指定后缀结束（忽略大小写）
     */
    public static boolean endsWithIgnoreCase(@NotNull String str, @NotNull String suffix) {
        return str.toLowerCase().endsWith(suffix.toLowerCase());
    }
    
    /**
     * 比较两个字符串是否相等（忽略大小写）
     */
    public static boolean equalsIgnoreCase(@Nullable String str1, @Nullable String str2) {
        if (str1 == null && str2 == null) {
            return true;
        }
        if (str1 == null || str2 == null) {
            return false;
        }
        return str1.equalsIgnoreCase(str2);
    }
    
    /**
     * 计算字符串中指定子字符串的出现次数
     */
    public static int countOccurrences(@NotNull String str, @NotNull String searchStr) {
        if (isEmpty(str) || isEmpty(searchStr)) {
            return 0;
        }
        
        int count = 0;
        int index = 0;
        
        while ((index = str.indexOf(searchStr, index)) != -1) {
            count++;
            index += searchStr.length();
        }
        
        return count;
    }
    
    /**
     * 替换字符串中的所有指定子字符串（忽略大小写）
     */
    @NotNull
    public static String replaceIgnoreCase(@NotNull String str, @NotNull String searchStr, @NotNull String replacement) {
        if (isEmpty(str) || isEmpty(searchStr)) {
            return str;
        }
        
        Pattern pattern = Pattern.compile(Pattern.quote(searchStr), Pattern.CASE_INSENSITIVE);
        return pattern.matcher(str).replaceAll(Matcher.quoteReplacement(replacement));
    }
    
    /**
     * 分割字符串并去除空白元素
     */
    @NotNull
    public static List<String> splitAndTrim(@NotNull String str, @NotNull String delimiter) {
        List<String> result = new ArrayList<>();
        
        if (isEmpty(str)) {
            return result;
        }
        
        String[] parts = str.split(Pattern.quote(delimiter));
        for (String part : parts) {
            String trimmed = part.trim();
            if (isNotEmpty(trimmed)) {
                result.add(trimmed);
            }
        }
        
        return result;
    }
    
    /**
     * 连接字符串数组
     */
    @NotNull
    public static String join(@NotNull String delimiter, @NotNull String... elements) {
        if (elements.length == 0) {
            return "";
        }
        
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < elements.length; i++) {
            if (i > 0) {
                sb.append(delimiter);
            }
            sb.append(nullToEmpty(elements[i]));
        }
        
        return sb.toString();
    }
    
    /**
     * 连接字符串列表
     */
    @NotNull
    public static String join(@NotNull String delimiter, @NotNull List<String> elements) {
        return join(delimiter, elements.toArray(new String[0]));
    }
    
    /**
     * 首字母大写
     */
    @NotNull
    public static String capitalize(@NotNull String str) {
        if (isEmpty(str)) {
            return str;
        }
        
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
    
    /**
     * 首字母小写
     */
    @NotNull
    public static String uncapitalize(@NotNull String str) {
        if (isEmpty(str)) {
            return str;
        }
        
        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }
    
    /**
     * 转换为驼峰命名法
     */
    @NotNull
    public static String toCamelCase(@NotNull String str) {
        if (isEmpty(str)) {
            return str;
        }
        
        StringBuilder result = new StringBuilder();
        boolean capitalizeNext = false;
        
        for (char c : str.toCharArray()) {
            if (Character.isLetterOrDigit(c)) {
                if (capitalizeNext) {
                    result.append(Character.toUpperCase(c));
                    capitalizeNext = false;
                } else {
                    result.append(Character.toLowerCase(c));
                }
            } else {
                capitalizeNext = true;
            }
        }
        
        return result.toString();
    }
    
    /**
     * 转换为下划线命名法
     */
    @NotNull
    public static String toSnakeCase(@NotNull String str) {
        if (isEmpty(str)) {
            return str;
        }
        
        StringBuilder result = new StringBuilder();
        
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            
            if (Character.isUpperCase(c)) {
                if (i > 0 && Character.isLowerCase(str.charAt(i - 1))) {
                    result.append('_');
                }
                result.append(Character.toLowerCase(c));
            } else if (Character.isLetterOrDigit(c)) {
                result.append(c);
            } else {
                result.append('_');
            }
        }
        
        return result.toString();
    }
    
    /**
     * 转换为短横线命名法
     */
    @NotNull
    public static String toKebabCase(@NotNull String str) {
        return toSnakeCase(str).replace('_', '-');
    }
    
    /**
     * 移除字符串中的所有空白字符
     */
    @NotNull
    public static String removeWhitespace(@NotNull String str) {
        if (isEmpty(str)) {
            return str;
        }
        
        return str.replaceAll("\\s+", "");
    }
    
    /**
     * 规范化空白字符（将多个连续空白字符替换为单个空格）
     */
    @NotNull
    public static String normalizeWhitespace(@NotNull String str) {
        if (isEmpty(str)) {
            return str;
        }
        
        return str.replaceAll("\\s+", " ").trim();
    }
    
    /**
     * 检查字符串是否只包含字母
     */
    public static boolean isAlpha(@NotNull String str) {
        if (isEmpty(str)) {
            return false;
        }
        
        for (char c : str.toCharArray()) {
            if (!Character.isLetter(c)) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * 检查字符串是否只包含数字
     */
    public static boolean isNumeric(@NotNull String str) {
        if (isEmpty(str)) {
            return false;
        }
        
        for (char c : str.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * 检查字符串是否只包含字母和数字
     */
    public static boolean isAlphanumeric(@NotNull String str) {
        if (isEmpty(str)) {
            return false;
        }
        
        for (char c : str.toCharArray()) {
            if (!Character.isLetterOrDigit(c)) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * 获取字符串的字节长度（UTF-8编码）
     */
    public static int getByteLength(@NotNull String str) {
        return str.getBytes(java.nio.charset.StandardCharsets.UTF_8).length;
    }
    
    /**
     * 按字节长度截断字符串（UTF-8编码）
     */
    @NotNull
    public static String truncateByBytes(@NotNull String str, int maxBytes) {
        if (getByteLength(str) <= maxBytes) {
            return str;
        }
        
        StringBuilder result = new StringBuilder();
        int currentBytes = 0;
        
        for (char c : str.toCharArray()) {
            String charStr = String.valueOf(c);
            int charBytes = getByteLength(charStr);
            
            if (currentBytes + charBytes > maxBytes) {
                break;
            }
            
            result.append(c);
            currentBytes += charBytes;
        }
        
        return result.toString();
    }
    
    /**
     * 转义HTML特殊字符
     */
    @NotNull
    public static String escapeHtml(@NotNull String str) {
        if (isEmpty(str)) {
            return str;
        }
        
        return str.replace("&", "&amp;")
                  .replace("<", "&lt;")
                  .replace(">", "&gt;")
                  .replace("\"", "&quot;")
                  .replace("'", "&#39;");
    }
    
    /**
     * 转义JSON特殊字符
     */
    @NotNull
    public static String escapeJson(@NotNull String str) {
        if (isEmpty(str)) {
            return str;
        }
        
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t")
                  .replace("\b", "\\b")
                  .replace("\f", "\\f");
    }
    
    /**
     * 生成随机字符串
     */
    @NotNull
    public static String generateRandomString(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder result = new StringBuilder();
        
        for (int i = 0; i < length; i++) {
            int index = (int) (Math.random() * chars.length());
            result.append(chars.charAt(index));
        }
        
        return result.toString();
    }
}