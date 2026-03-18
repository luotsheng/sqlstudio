package com.changhong.sqlstudio.core.event.notify;

import com.changhong.sqlstudio.core.event.Event;

/**
 * 打开数据表标签页
 *
 * @author Luo Tiansheng
 * @since 2026-03-01
 */
public class OpenDataTableTabEvent implements Event
{
        private final Object object;

        public OpenDataTableTabEvent(Object object)
        {
                this.object = object;
        }

        @SuppressWarnings("unchecked")
        public <T> T table() {
                return (T) object;
        }
}
