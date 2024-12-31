package cn.cpoet.ideas.util;

import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationGroupManager;

/**
 * 通知工具
 *
 * @author CPoet
 */
public abstract class NotificationUtil {

    private final static String GROUP_BALLOON = "CPOET_IDEA_NOTIFICATION_BALLOON";

    private NotificationUtil() {
    }

    public static NotificationGroup getBalloonGroup() {
        return NotificationGroupManager.getInstance().getNotificationGroup(GROUP_BALLOON);
    }
}
