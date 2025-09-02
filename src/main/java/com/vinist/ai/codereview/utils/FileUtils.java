package com.vinist.ai.codereview.utils;

import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 文件工具类
 */
public class FileUtils {
    
    // 支持的代码文件扩展名
    private static final Set<String> CODE_EXTENSIONS = new HashSet<>(Arrays.asList(
            "java", "kt", "scala", "groovy", "clj", "cljs", "cljc",
            "js", "ts", "jsx", "tsx", "vue", "svelte",
            "py", "pyw", "pyi", "pyx", "pxd", "pxi",
            "cpp", "cc", "cxx", "c++", "c", "h", "hpp", "hxx", "h++",
            "cs", "vb", "fs", "fsx", "fsi",
            "go", "rs", "swift", "m", "mm",
            "php", "rb", "pl", "pm", "t", "pod",
            "sh", "bash", "zsh", "fish", "ps1", "psm1", "psd1",
            "sql", "mysql", "pgsql", "sqlite",
            "xml", "html", "htm", "xhtml", "jsp", "asp", "aspx",
            "css", "scss", "sass", "less", "styl",
            "json", "yaml", "yml", "toml", "ini", "cfg", "conf",
            "md", "markdown", "rst", "txt", "log"
    ));
    
    // 需要忽略的文件和目录
    private static final Set<String> IGNORED_NAMES = new HashSet<>(Arrays.asList(
            ".git", ".svn", ".hg", ".bzr",
            "node_modules", "bower_components",
            "target", "build", "dist", "out", "bin",
            ".idea", ".vscode", ".eclipse",
            "__pycache__", ".pytest_cache", ".coverage",
            ".gradle", ".maven", ".m2",
            "vendor", "Pods",
            ".DS_Store", "Thumbs.db",
            "*.class", "*.jar", "*.war", "*.ear",
            "*.pyc", "*.pyo", "*.pyd",
            "*.o", "*.so", "*.dll", "*.dylib",
            "*.exe", "*.app", "*.deb", "*.rpm",
            "*.zip", "*.tar", "*.gz", "*.bz2", "*.xz", "*.7z",
            "*.jpg", "*.jpeg", "*.png", "*.gif", "*.bmp", "*.svg", "*.ico",
            "*.mp3", "*.mp4", "*.avi", "*.mov", "*.wmv", "*.flv",
            "*.pdf", "*.doc", "*.docx", "*.xls", "*.xlsx", "*.ppt", "*.pptx"
    ));
    
    /**
     * 检查文件是否为代码文件
     */
    public static boolean isCodeFile(@Nullable VirtualFile file) {
        if (file == null || file.isDirectory()) {
            return false;
        }
        
        String extension = file.getExtension();
        return extension != null && CODE_EXTENSIONS.contains(extension.toLowerCase());
    }
    
    /**
     * 检查文件是否应该被忽略
     */
    public static boolean shouldIgnoreFile(@Nullable VirtualFile file) {
        if (file == null) {
            return true;
        }
        
        String name = file.getName();
        
        // 检查文件名是否在忽略列表中
        if (IGNORED_NAMES.contains(name.toLowerCase())) {
            return true;
        }
        
        // 检查是否为隐藏文件（以.开头，但不是代码文件）
        if (name.startsWith(".") && !isCodeFile(file)) {
            return true;
        }
        
        // 检查文件大小（忽略过大的文件，超过1MB）
        if (file.getLength() > 1024 * 1024) {
            return true;
        }
        
        return false;
    }
    
    /**
     * 读取文件内容
     */
    @Nullable
    public static String readFileContent(@NotNull VirtualFile file) {
        try {
            byte[] content = file.contentsToByteArray();
            return new String(content, StandardCharsets.UTF_8);
        } catch (IOException e) {
            return null;
        }
    }
    
    /**
     * 读取文件内容（从路径）
     */
    @Nullable
    public static String readFileContent(@NotNull String filePath) {
        try {
            Path path = Paths.get(filePath);
            byte[] content = Files.readAllBytes(path);
            return new String(content, StandardCharsets.UTF_8);
        } catch (IOException e) {
            return null;
        }
    }
    
    /**
     * 写入文件内容
     */
    public static boolean writeFileContent(@NotNull String filePath, @NotNull String content) {
        try {
            Path path = Paths.get(filePath);
            // 确保父目录存在
            Path parent = path.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Files.write(path, content.getBytes(StandardCharsets.UTF_8));
            return true;
        } catch (IOException e) {
            return false;
        }
    }
    
    /**
     * 获取文件扩展名
     */
    @Nullable
    public static String getFileExtension(@NotNull String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < fileName.length() - 1) {
            return fileName.substring(lastDotIndex + 1).toLowerCase();
        }
        return null;
    }
    
    /**
     * 获取文件名（不包含扩展名）
     */
    @NotNull
    public static String getFileNameWithoutExtension(@NotNull String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0) {
            return fileName.substring(0, lastDotIndex);
        }
        return fileName;
    }
    
    /**
     * 检查文件是否存在
     */
    public static boolean fileExists(@NotNull String filePath) {
        return Files.exists(Paths.get(filePath));
    }
    
    /**
     * 创建目录
     */
    public static boolean createDirectories(@NotNull String dirPath) {
        try {
            Files.createDirectories(Paths.get(dirPath));
            return true;
        } catch (IOException e) {
            return false;
        }
    }
    
    /**
     * 删除文件
     */
    public static boolean deleteFile(@NotNull String filePath) {
        try {
            Files.deleteIfExists(Paths.get(filePath));
            return true;
        } catch (IOException e) {
            return false;
        }
    }
    
    /**
     * 获取文件大小（字节）
     */
    public static long getFileSize(@NotNull String filePath) {
        try {
            return Files.size(Paths.get(filePath));
        } catch (IOException e) {
            return -1;
        }
    }
    
    /**
     * 获取文件大小（字节）
     */
    public static long getFileSize(@NotNull VirtualFile file) {
        return file.getLength();
    }
    
    /**
     * 格式化文件大小
     */
    @NotNull
    public static String formatFileSize(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        }
        
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp - 1) + "";
        return String.format("%.1f %sB", bytes / Math.pow(1024, exp), pre);
    }
    
    /**
     * 检查文件是否为文本文件
     */
    public static boolean isTextFile(@NotNull VirtualFile file) {
        if (file.isDirectory()) {
            return false;
        }
        
        // 首先检查扩展名
        if (isCodeFile(file)) {
            return true;
        }
        
        // 检查文件内容（读取前1024字节）
        try {
            byte[] content = file.contentsToByteArray();
            int checkLength = Math.min(content.length, 1024);
            
            for (int i = 0; i < checkLength; i++) {
                byte b = content[i];
                // 检查是否包含非文本字符（除了常见的控制字符）
                if (b < 0x09 || (b > 0x0D && b < 0x20) || b == 0x7F) {
                    return false;
                }
            }
            
            return true;
        } catch (IOException e) {
            return false;
        }
    }
    
    /**
     * 获取相对路径
     */
    @NotNull
    public static String getRelativePath(@NotNull String basePath, @NotNull String targetPath) {
        try {
            Path base = Paths.get(basePath).normalize();
            Path target = Paths.get(targetPath).normalize();
            return base.relativize(target).toString();
        } catch (Exception e) {
            return targetPath;
        }
    }
    
    /**
     * 规范化路径
     */
    @NotNull
    public static String normalizePath(@NotNull String path) {
        return Paths.get(path).normalize().toString();
    }
    
    /**
     * 检查路径是否为绝对路径
     */
    public static boolean isAbsolutePath(@NotNull String path) {
        return Paths.get(path).isAbsolute();
    }
    
    /**
     * 连接路径
     */
    @NotNull
    public static String joinPaths(@NotNull String... paths) {
        if (paths.length == 0) {
            return "";
        }
        
        Path result = Paths.get(paths[0]);
        for (int i = 1; i < paths.length; i++) {
            result = result.resolve(paths[i]);
        }
        
        return result.toString();
    }
    
    /**
     * 获取父目录路径
     */
    @Nullable
    public static String getParentPath(@NotNull String filePath) {
        Path parent = Paths.get(filePath).getParent();
        return parent != null ? parent.toString() : null;
    }
    
    /**
     * 获取文件名
     */
    @NotNull
    public static String getFileName(@NotNull String filePath) {
        return Paths.get(filePath).getFileName().toString();
    }
    
    /**
     * 检查文件是否可读
     */
    public static boolean isReadable(@NotNull String filePath) {
        return Files.isReadable(Paths.get(filePath));
    }
    
    /**
     * 检查文件是否可写
     */
    public static boolean isWritable(@NotNull String filePath) {
        return Files.isWritable(Paths.get(filePath));
    }
    
    /**
     * 获取临时目录路径
     */
    @NotNull
    public static String getTempDirectory() {
        return System.getProperty("java.io.tmpdir");
    }
    
    /**
     * 创建临时文件
     */
    @Nullable
    public static String createTempFile(@NotNull String prefix, @NotNull String suffix) {
        try {
            Path tempFile = Files.createTempFile(prefix, suffix);
            return tempFile.toString();
        } catch (IOException e) {
            return null;
        }
    }
}