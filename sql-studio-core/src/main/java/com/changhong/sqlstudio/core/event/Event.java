package com.changhong.sqlstudio.core.event;

/**
 * 事件接口
 *
 * @author Luo Tiansheng
 */
public interface Event
{
        @SuppressWarnings("unchecked")
        default <T extends Event> T getEventInstance()
        {
                return (T) this;
        }

}
