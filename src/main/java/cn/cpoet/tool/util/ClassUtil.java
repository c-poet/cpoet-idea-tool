package cn.cpoet.tool.util;

import com.intellij.openapi.vfs.VirtualFile;
import org.apache.commons.lang3.ClassUtils;

/**
 * 类处理工具
 *
 * @author CPoet
 */
public abstract class ClassUtil {

    public final static String CLASS_EXT = "class";

    private ClassUtil() {
    }

    /**
     * 获取所有内部类输出的文件列表
     *
     * @param classFile 类输出文件
     * @return 内部类输出文件列表
     */
    public static VirtualFile[] getInnerOutputFiles(VirtualFile classFile) {
        if (classFile == null || !CLASS_EXT.equals(classFile.getExtension())) {
            return new VirtualFile[0];
        }
        String filePrefix = classFile.getNameWithoutExtension() + ClassUtils.INNER_CLASS_SEPARATOR;
        return FileUtil.getChildren(classFile.getParent(), file -> file.getName().startsWith(filePrefix));
    }
}
