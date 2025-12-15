package com.mycompany.teamcode_kanbanpro.controller;

import com.mycompany.teamcode_kanbanpro.model.Column;
import com.mycompany.teamcode_kanbanpro.model.Task;

/**
 * Optional interface for controllers that want to be notified when a task is moved
 */
public interface TaskMovedHandler {
    void handleTaskMoved(Task task, Column newColumn);
}
