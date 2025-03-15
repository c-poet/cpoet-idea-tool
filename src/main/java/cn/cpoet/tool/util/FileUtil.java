package cn.cpoet.tool.util;

import cn.cpoet.tool.constant.FileBuildTypeExtEnum;
import cn.cpoet.tool.constant.OSExplorerConst;
import cn.cpoet.tool.exception.ToolException;
import com.intellij.openapi.vfs.VirtualFile;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.jdesktop.swingx.util.OS;

import java.io.File;
import java.io.InputStream;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * 文件操作工具
 *
 * @author CPoet
 */
public abstract class FileUtil {

    /** Unix路径分隔符 */
    public final static String UNIX_SEPARATOR = "/";
    /** Windows路径分隔符 */
    public final static String WINDOWS_SEPARATOR = "\\";

    private FileUtil() {
    }

    /**
     * 在资源管理器中打开目录
     *
     * @param path 目录路径
     */
    public static void openFolder(String path) {
        if (OS.isLinux()) {
            if (!OSUtil.execCommand(OSExplorerConst.LINUX_GNOME, path)
                    && !OSUtil.execCommand(OSExplorerConst.LINUX_NAUTILUS, path)) {
                OSUtil.execCommand(OSExplorerConst.LINUX_KDE, path);
            }
        } else if (OS.isMacOSX()) {
            OSUtil.execCommand(OSExplorerConst.MACOS, path);
        } else {
            OSUtil.execCommand(OSExplorerConst.WINDOWS, path);
        }
    }

    /**
     * 在资源管理器中选中文件
     *
     * @param file 文件
     */
    public static void selectFile(VirtualFile file) {
        String filePath = FilenameUtils.separatorsToSystem(file.getPath());
        selectFile(filePath);
    }

    /**
     * 在资源管理器中选中文件
     *
     * @param filePath 文件路径
     */
    public static void selectFile(String filePath) {
        if (OS.isLinux()) {
            OSUtil.execCommand(OSExplorerConst.LINUX_GNOME, filePath);
        } else if (OS.isMacOSX()) {
            OSUtil.execCommand(OSExplorerConst.MACOS, filePath);
        } else {
            OSUtil.execCommand(OSExplorerConst.WINDOWS, "/e,/select," + filePath);
        }
    }

    /**
     * 判断是否是子路径
     *
     * @param parent 父路径
     * @param child  子路径
     * @return 是否为子路径
     */
    public static boolean isFileChild(VirtualFile parent, VirtualFile child) {
        if (parent != null && child != null) {
            while (child != null && !parent.equals(child)) {
                child = child.getParent();
            }
        }
        return parent == null || child != null;
    }

    /**
     * 获取文件的输出路径
     *
     * @param root 输出根目录
     * @param file 源文件路径
     * @return 输出路径
     */
    public static String getOutputFilePath(VirtualFile root, VirtualFile file) {
        String filePath = getRelativePath(root.getPath(), file.getPath());
        String ext = FileBuildTypeExtEnum.findBuildExt(FilenameUtils.getExtension(filePath));
        if (StringUtils.isNotEmpty(ext)) {
            filePath = FilenameUtils.removeExtension(filePath) + FilenameUtils.EXTENSION_SEPARATOR + ext;
        }
        return filePath;
    }

    /**
     * 获取相对路径
     *
     * @param rootPath 根路径
     * @param filePath 文件路径
     * @return 相对路径
     */
    public static String getRelativePath(String rootPath, String filePath) {
        return filePath.substring(rootPath.length());
    }

    /**
     * 获取文件实例
     *
     * @param root     文件根目录
     * @param filePath 文件路径
     * @return 文件实例
     */
    public static VirtualFile getFileInRoot(VirtualFile root, String filePath) {
        if (root != null) {
            return root.findFileByRelativePath(filePath);
        }
        return null;
    }

    /**
     * 移出路径开始的分隔符
     *
     * @param path 路径
     * @return 移出路径开始分隔符的路径
     */
    public static String removeStartSeparator(String path) {
        if (path != null && !path.isEmpty()) {
            if (path.startsWith(UNIX_SEPARATOR)) {
                return path.substring(UNIX_SEPARATOR.length());
            }
            if (path.startsWith(WINDOWS_SEPARATOR)) {
                return path.substring(WINDOWS_SEPARATOR.length());
            }
        }
        return path;
    }

    /**
     * 获取目录下满足条件的文件列表
     *
     * @param file   目录文件
     * @param filter 过滤
     * @return 文件列表
     */
    public static VirtualFile[] getChildren(VirtualFile file, Predicate<VirtualFile> filter) {
        if (file == null || !file.isDirectory()) {
            return new VirtualFile[0];
        }
        return Stream.of(file.getChildren())
                .filter(filter)
                .toArray(VirtualFile[]::new);
    }

    /**
     * 写入文件
     *
     * @param originFile 源文件
     * @param filePath   目标文件路径
     */
    public static void writeToFile(VirtualFile originFile, String filePath) {
        writeToFile(originFile, new File(filePath));
    }

    /**
     * 写入文件
     *
     * @param originFile 源文件
     * @param toFile     目标文件
     */
    public static void writeToFile(VirtualFile originFile, File toFile) {
        try (InputStream in = originFile.getInputStream()) {
            writeToFile(com.intellij.openapi.util.io.FileUtil.loadBytes(in), toFile);
        } catch (Exception e) {
            throw new ToolException("Read Or Write file fail", e);
        }
    }

    /**
     * 写入文件
     *
     * @param data     数据
     * @param filePath 文件路径
     */
    public static void writeToFile(byte[] data, String filePath) {
        writeToFile(data, new File(filePath));
    }

    /**
     * 写入文件
     *
     * @param data 数据
     * @param file 文件路径
     */
    public static void writeToFile(byte[] data, File file) {
        try {
            com.intellij.openapi.util.io.FileUtil.writeToFile(file, data);
        } catch (Exception e) {
            throw new ToolException("Write file fail", e);
        }
    }
}
