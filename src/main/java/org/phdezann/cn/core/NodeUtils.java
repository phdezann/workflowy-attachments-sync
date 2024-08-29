package org.phdezann.cn.core;

import org.apache.commons.lang3.StringUtils;

public class NodeUtils {

    public static String getShortId(String uuid) {
        return StringUtils.substringAfterLast(uuid, "-");
    }

}
