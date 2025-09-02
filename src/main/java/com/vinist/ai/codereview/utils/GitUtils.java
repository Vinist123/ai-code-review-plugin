package com.vinist.ai.codereview.utils;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vcs.changes.Change;
import com.intellij.openapi.vcs.changes.ChangeListManager;
import com.intellij.openapi.vcs.changes.ContentRevision;
import com.intellij.openapi.vfs.VirtualFile;
import git4idea.GitUtil;
import git4idea.GitLocalBranch;
import git4idea.commands.Git;
import git4idea.commands.GitCommand;
import git4idea.commands.GitLineHandler;
import git4idea.repo.GitRepository;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Git工具类
 */
public class GitUtils {
    
    /**
     * 获取项目的Git仓库
     */
    @Nullable
    public static GitRepository getGitRepository(@NotNull Project project) {
        try {
            Collection<GitRepository> repositories = GitUtil.getRepositories(project);
            return repositories.isEmpty() ? null : repositories.iterator().next();
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * 检查项目是否是Git仓库
     */
    public static boolean isGitRepository(@NotNull Project project) {
        return getGitRepository(project) != null;
    }
    
    /**
     * 获取当前分支名称
     */
    @Nullable
    public static String getCurrentBranch(@NotNull Project project) {
        GitRepository repository = getGitRepository(project);
        if (repository == null) {
            return null;
        }
        
        try {
            GitLocalBranch currentBranch = repository.getCurrentBranch();
            return currentBranch != null ? currentBranch.getName() : null;
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * 获取所有本地分支
     */
    @NotNull
    public static List<String> getLocalBranches(@NotNull Project project) {
        List<String> branches = new ArrayList<>();
        GitRepository repository = getGitRepository(project);
        
        if (repository != null) {
            try {
                repository.getBranches().getLocalBranches().forEach(branch -> 
                    branches.add(branch.getName())
                );
            } catch (Exception e) {
                // 忽略异常
            }
        }
        
        return branches;
    }
    
    /**
     * 获取所有远程分支
     */
    @NotNull
    public static List<String> getRemoteBranches(@NotNull Project project) {
        List<String> branches = new ArrayList<>();
        GitRepository repository = getGitRepository(project);
        
        if (repository != null) {
            try {
                repository.getBranches().getRemoteBranches().forEach(branch -> 
                    branches.add(branch.getName())
                );
            } catch (Exception e) {
                // 忽略异常
            }
        }
        
        return branches;
    }
    
    /**
     * 获取未提交的更改
     */
    @NotNull
    public static List<Change> getUncommittedChanges(@NotNull Project project) {
        List<Change> changes = new ArrayList<>();
        
        try {
            ChangeListManager changeListManager = ChangeListManager.getInstance(project);
            changes.addAll(changeListManager.getAllChanges());
        } catch (Exception e) {
            // 忽略异常
        }
        
        return changes;
    }
    
    /**
     * 获取已暂存的更改
     */
    @NotNull
    public static List<Change> getStagedChanges(@NotNull Project project) {
        List<Change> changes = new ArrayList<>();
        
        try {
            ChangeListManager changeListManager = ChangeListManager.getInstance(project);
            // 获取默认变更列表中的更改（通常是已暂存的）
            changes.addAll(changeListManager.getDefaultChangeList().getChanges());
        } catch (Exception e) {
            // 忽略异常
        }
        
        return changes;
    }
    
    /**
     * 获取更改的文件路径
     */
    @Nullable
    public static String getChangeFilePath(@NotNull Change change) {
        try {
            ContentRevision afterRevision = change.getAfterRevision();
            if (afterRevision != null) {
                return afterRevision.getFile().getPath();
            }
            
            ContentRevision beforeRevision = change.getBeforeRevision();
            if (beforeRevision != null) {
                return beforeRevision.getFile().getPath();
            }
        } catch (Exception e) {
            // 忽略异常
        }
        
        return null;
    }
    
    /**
     * 获取更改的文件内容（新版本）
     */
    @Nullable
    public static String getChangeContent(@NotNull Change change) {
        try {
            ContentRevision afterRevision = change.getAfterRevision();
            if (afterRevision != null) {
                return afterRevision.getContent();
            }
        } catch (VcsException e) {
            // 忽略异常
        }
        
        return null;
    }
    
    /**
     * 获取更改的文件内容（旧版本）
     */
    @Nullable
    public static String getChangeBeforeContent(@NotNull Change change) {
        try {
            ContentRevision beforeRevision = change.getBeforeRevision();
            if (beforeRevision != null) {
                return beforeRevision.getContent();
            }
        } catch (VcsException e) {
            // 忽略异常
        }
        
        return null;
    }
    
    /**
     * 检查文件是否被修改
     */
    public static boolean isFileModified(@NotNull Project project, @NotNull VirtualFile file) {
        try {
            ChangeListManager changeListManager = ChangeListManager.getInstance(project);
            return changeListManager.isFileAffected(file);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 检查文件是否是新文件
     */
    public static boolean isNewFile(@NotNull Change change) {
        return change.getBeforeRevision() == null && change.getAfterRevision() != null;
    }
    
    /**
     * 检查文件是否被删除
     */
    public static boolean isDeletedFile(@NotNull Change change) {
        return change.getBeforeRevision() != null && change.getAfterRevision() == null;
    }
    
    /**
     * 检查文件是否被修改
     */
    public static boolean isModifiedFile(@NotNull Change change) {
        return change.getBeforeRevision() != null && change.getAfterRevision() != null;
    }
    
    /**
     * 获取最近的提交哈希
     */
    @Nullable
    public static String getLastCommitHash(@NotNull Project project) {
        GitRepository repository = getGitRepository(project);
        if (repository == null) {
            return null;
        }
        
        try {
            GitLineHandler handler = new GitLineHandler(project, repository.getRoot(), GitCommand.REV_PARSE);
            handler.addParameters("HEAD");
            
            String result = Git.getInstance().runCommand(handler).getOutputOrThrow();
            return result.trim();
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * 获取最近的提交信息
     */
    @Nullable
    public static String getLastCommitMessage(@NotNull Project project) {
        GitRepository repository = getGitRepository(project);
        if (repository == null) {
            return null;
        }
        
        try {
            GitLineHandler handler = new GitLineHandler(project, repository.getRoot(), GitCommand.LOG);
            handler.addParameters("-1", "--pretty=format:%s");
            
            String result = Git.getInstance().runCommand(handler).getOutputOrThrow();
            return result.trim();
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * 获取文件的Git状态
     */
    @Nullable
    public static String getFileStatus(@NotNull Project project, @NotNull VirtualFile file) {
        try {
            ChangeListManager changeListManager = ChangeListManager.getInstance(project);
            
            if (changeListManager.isUnversioned(file)) {
                return "UNVERSIONED";
            }
            
            if (changeListManager.isIgnoredFile(file)) {
                return "IGNORED";
            }
            
            Change change = changeListManager.getChange(file);
            if (change != null) {
                if (isNewFile(change)) {
                    return "ADDED";
                } else if (isDeletedFile(change)) {
                    return "DELETED";
                } else if (isModifiedFile(change)) {
                    return "MODIFIED";
                }
            }
            
            return "UNCHANGED";
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * 检查是否有未提交的更改
     */
    public static boolean hasUncommittedChanges(@NotNull Project project) {
        return !getUncommittedChanges(project).isEmpty();
    }
    
    /**
     * 检查是否有已暂存的更改
     */
    public static boolean hasStagedChanges(@NotNull Project project) {
        return !getStagedChanges(project).isEmpty();
    }
    
    /**
     * 获取远程仓库URL
     */
    @Nullable
    public static String getRemoteUrl(@NotNull Project project) {
        GitRepository repository = getGitRepository(project);
        if (repository == null) {
            return null;
        }
        
        try {
            GitLineHandler handler = new GitLineHandler(project, repository.getRoot(), GitCommand.CONFIG);
            handler.addParameters("--get", "remote.origin.url");
            
            String result = Git.getInstance().runCommand(handler).getOutputOrThrow();
            return result.trim();
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * 获取仓库根目录
     */
    @Nullable
    public static VirtualFile getRepositoryRoot(@NotNull Project project) {
        GitRepository repository = getGitRepository(project);
        return repository != null ? repository.getRoot() : null;
    }
    
    /**
     * 检查文件是否在Git仓库中
     */
    public static boolean isFileInRepository(@NotNull Project project, @NotNull VirtualFile file) {
        VirtualFile repositoryRoot = getRepositoryRoot(project);
        if (repositoryRoot == null) {
            return false;
        }
        
        return file.getPath().startsWith(repositoryRoot.getPath());
    }
    
    /**
     * 获取相对于仓库根目录的文件路径
     */
    @Nullable
    public static String getRelativeFilePath(@NotNull Project project, @NotNull VirtualFile file) {
        VirtualFile repositoryRoot = getRepositoryRoot(project);
        if (repositoryRoot == null) {
            return null;
        }
        
        String filePath = file.getPath();
        String rootPath = repositoryRoot.getPath();
        
        if (filePath.startsWith(rootPath)) {
            String relativePath = filePath.substring(rootPath.length());
            return relativePath.startsWith("/") ? relativePath.substring(1) : relativePath;
        }
        
        return null;
    }
    
    /**
     * 获取指定文件的更改
     */
    @Nullable
    public static Change getFileChange(@NotNull Project project, @NotNull VirtualFile file) {
        try {
            ChangeListManager changeListManager = ChangeListManager.getInstance(project);
            return changeListManager.getChange(file);
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * 获取所有已修改的Java文件
     */
    @NotNull
    public static List<VirtualFile> getModifiedJavaFiles(@NotNull Project project) {
        List<VirtualFile> javaFiles = new ArrayList<>();
        
        List<Change> changes = getUncommittedChanges(project);
        for (Change change : changes) {
            String filePath = getChangeFilePath(change);
            if (filePath != null && filePath.endsWith(".java")) {
                ContentRevision afterRevision = change.getAfterRevision();
                if (afterRevision != null && afterRevision.getFile().getVirtualFile() != null) {
                    javaFiles.add(afterRevision.getFile().getVirtualFile());
                }
            }
        }
        
        return javaFiles;
    }
    
    /**
     * 获取所有已修改的代码文件
     */
    @NotNull
    public static List<VirtualFile> getModifiedCodeFiles(@NotNull Project project) {
        List<VirtualFile> codeFiles = new ArrayList<>();
        Set<String> codeExtensions = Set.of(".java", ".kt", ".scala", ".groovy", ".js", ".ts", ".py", ".go", ".rs", ".cpp", ".c", ".h", ".hpp");
        
        List<Change> changes = getUncommittedChanges(project);
        for (Change change : changes) {
            String filePath = getChangeFilePath(change);
            if (filePath != null) {
                for (String ext : codeExtensions) {
                    if (filePath.endsWith(ext)) {
                        ContentRevision afterRevision = change.getAfterRevision();
                        if (afterRevision != null && afterRevision.getFile().getVirtualFile() != null) {
                            codeFiles.add(afterRevision.getFile().getVirtualFile());
                        }
                        break;
                    }
                }
            }
        }
        
        return codeFiles;
    }
}