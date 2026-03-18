package com.changhong.sqlstudio.core.event.notify;


import com.changhong.sqlstudio.core.common.DBType;
import com.changhong.sqlstudio.core.event.Event;
import org.eclipse.swt.events.SelectionEvent;

/**
 * 创建 MySQL 连接
 *
 * @author Luo Tiansheng
 * @since 2026/3/17
 */
public record OpenDBCreateUIEvent(DBType dbType, SelectionEvent selectionEvent) implements Event
{

}
