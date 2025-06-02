package com.camunda.engine.api;


import com.camunda.engine.service.TaskService;
import io.camunda.tasklist.dto.Task;
import io.camunda.tasklist.dto.TaskState;
import io.camunda.tasklist.exception.TaskListException;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@AllArgsConstructor
@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    // 1. Find all tasks for a process instance ID
    @GetMapping("/by-process-instance/{processInstanceId}")
    public ResponseEntity<List<Task>> findTasksForProcessInstanceId(@PathVariable String processInstanceId) throws TaskListException {
        return ResponseEntity.ok(taskService.findTasksForProcessInstanceId(processInstanceId));
    }

    // 2. Find all tasks for a process instance ID, with state and variable control
    @GetMapping("/by-process-instance/{processInstanceId}/advanced")
    public ResponseEntity<List<Task>> findTasksForProcessInstanceIdAdvanced(
            @PathVariable String processInstanceId,
            @RequestParam(required = false) TaskState state,
            @RequestParam(defaultValue = "true") boolean withVariables) throws TaskListException {
        return ResponseEntity.ok(taskService.findTasksForProcessInstanceId(processInstanceId, state, withVariables));
    }

    // 3. Find active tasks for a variable name/value pair
    @GetMapping("/active/by-variable")
    public ResponseEntity<List<Task>> findActiveTasksForVariable(
            @RequestParam String name,
            @RequestParam String value) throws TaskListException {
        return ResponseEntity.ok(taskService.findActiveTasksForVariable(name, value));
    }

    // 4. Find task by ID (Optional)
    @GetMapping("/{taskId}")
    public ResponseEntity<Task> findTaskById(@PathVariable String taskId) throws TaskListException {
        return taskService.findTaskById(taskId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 5. Find all by assignee and state
    @GetMapping("/by-assignee-and-state")
    public ResponseEntity<List<Task>> findAllByAssigneeAndState(
            @RequestParam String assignee,
            @RequestParam TaskState state) throws TaskListException {
        return ResponseEntity.ok(taskService.findAllByAssigneeAndState(assignee, state));
    }

    // 6. Claim a task
    @PostMapping("/{taskId}/claim")
    public ResponseEntity<Task> claimTask(
            @PathVariable String taskId,
            @RequestParam String assignee) throws TaskListException {
        return ResponseEntity.ok(taskService.claimTask(taskId, assignee));
    }

    // 7. Claim a task with allowOverrideAssignment
    @PostMapping("/{taskId}/claim-override")
    public ResponseEntity<Task> claimTaskWithOverride(
            @PathVariable String taskId,
            @RequestParam String assignee,
            @RequestParam(defaultValue = "false") boolean allowOverrideAssignment) throws TaskListException {
        return ResponseEntity.ok(taskService.claimTask(taskId, assignee, allowOverrideAssignment));
    }

    // 8. Claim multiple tasks
    @PostMapping("/claim-batch")
    public ResponseEntity<Map<String, Task>> claimTasks(
            @RequestBody List<String> taskIds,
            @RequestParam String assignee) {
        return ResponseEntity.ok(taskService.claimTasks(taskIds, assignee));
    }


    // 9. Find active tasks by assignee
    @GetMapping("/active/by-assignee/{assignee}")
    public ResponseEntity<List<Task>> findActiveTasksByAssignee(@PathVariable String assignee) throws TaskListException {
        return ResponseEntity.ok(taskService.findActiveTasksByAssignee(assignee));
    }

    // 10. Find completed tasks by assignee
    @GetMapping("/completed/by-assignee/{assignee}")
    public ResponseEntity<List<Task>> findCompletedTasksByAssignee(@PathVariable String assignee) throws TaskListException {
        return ResponseEntity.ok(taskService.findCompletedTasksByAssignee(assignee));
    }

    // 11. Find all tasks by multiple assignees and state
    @GetMapping("/by-multi-assignees-and-state")
    public ResponseEntity<Map<String, List<Task>>> findAllByAssigneesAndState(
            @RequestParam List<String> assignees,
            @RequestParam TaskState state) throws TaskListException {
        return ResponseEntity.ok(taskService.findAllByAssigneesAndState(assignees, state));
    }

    // 12. Count tasks by assignee and state
    @GetMapping("/count/by-assignee-and-state")
    public ResponseEntity<Long> countTasksByAssigneeAndState(
            @RequestParam String assignee,
            @RequestParam TaskState state) {
        return ResponseEntity.ok(taskService.countTasksByAssigneeAndState(assignee, state));
    }

    // 13. Can claim task
    @GetMapping("/{taskId}/can-claim")
    public ResponseEntity<Boolean> canClaimTask(
            @PathVariable String taskId,
            @RequestParam String assignee) {
        return ResponseEntity.ok(taskService.canClaimTask(taskId, assignee));
    }

    // 14. Task exists
    @GetMapping("/{taskId}/exists")
    public ResponseEntity<Boolean> taskExists(@PathVariable String taskId) {
        return ResponseEntity.ok(taskService.taskExists(taskId));
    }

    // 15. Find task by ID and include variables flag
    @GetMapping("/{taskId}/with-variables")
    public ResponseEntity<Task> findTaskByIdWithVariables(
            @PathVariable String taskId,
            @RequestParam(defaultValue = "true") boolean includeVariables) throws TaskListException {
        return taskService.findTaskById(taskId, includeVariables)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


}
