package cn.cpoet.ideas.ic.util;

import cn.cpoet.ideas.ic.constant.FileBuildTypeExtEnum;
import cn.cpoet.ideas.ic.constant.OSExplorerConst;
import com.intellij.openapi.vfs.VirtualFile;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.jdesktop.swingx.util.OS;

/**
 * 文件操作工具
 *
 * @author CPoet
 */
public abstract class FileUtil {

    private FileUtil() {
    }

    /**
     * 在资源管理器中打开目录
     *
     * @param folder 目录
     */
    public static void openFolder(VirtualFile folder) {
        String folderPath = FilenameUtils.separatorsToSystem(folder.getPath());
        openFolder(folderPath);
    }

    /**
     * 在资源管理器中打开目录
     *
     * @param folderPath 目录路径
     */
    public static void openFolder(String folderPath) {

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
        String filePath = file.getPath().substring(root.getPath().length());
        String ext = FileBuildTypeExtEnum.findBuildExt(FilenameUtils.getExtension(filePath));
        if (StringUtils.isNotEmpty(ext)) {
            filePath = FilenameUtils.removeExtension(filePath) + FilenameUtils.EXTENSION_SEPARATOR + ext;
        }
        return filePath;
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
}
