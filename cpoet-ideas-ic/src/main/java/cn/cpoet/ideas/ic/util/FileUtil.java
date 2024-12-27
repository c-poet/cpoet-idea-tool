package cn.cpoet.ideas.ic.util;

import cn.cpoet.ideas.ic.constant.FileBuildTypeExtEnum;
import com.intellij.openapi.vfs.VirtualFile;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * 文件操作工具
 *
 * @author CPoet
 */
public abstract class FileUtil {

    private FileUtil() {
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
     *
     * @param root
     * @param file
     * @return
     */
    public static String getOutputFilePath(VirtualFile root, VirtualFile file) {
        String filePath = file.getPath().substring(root.getPath().length());
        String ext = FileBuildTypeExtEnum.findBuildExt(FilenameUtils.getExtension(filePath));
        if (StringUtils.isNotEmpty(ext)) {
            filePath = FilenameUtils.removeExtension(filePath) + FilenameUtils.EXTENSION_SEPARATOR + ext;
        }
        return filePath;
    }

    public static VirtualFile getFileInRoot(VirtualFile root, String filePath) {
        if (root != null) {
            return root.findFileByRelativePath(filePath);
        }
        return null;
    }
}
