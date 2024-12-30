package cn.cpoet.ideas.actions.patch.constant;

import org.apache.commons.io.FilenameUtils;

/**
 * 补丁包常量
 *
 * @author CPoet
 */
public interface GenPatchConst {
    /** 生成的补丁包后缀名 */
    String PATCH_FILE_EXT = "zip";

    /** 生成的补丁包后缀名 */
    String PATCH_FULL_FILE_EXT = FilenameUtils.EXTENSION_SEPARATOR + PATCH_FILE_EXT;
}
