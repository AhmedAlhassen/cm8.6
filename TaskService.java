package com.camunda.engine.service;


import io.camunda.tasklist.CamundaTaskListClient;
import io.camunda.tasklist.dto.*;
import io.camunda.tasklist.exception.TaskListException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;



import java.util.*;

@AllArgsConstructor
@Service
public class TaskService {

    private final CamundaTaskListClient client;

    // 1. Find all tasks for a process instance ID
    public List<Task> findTasksForProcessInstanceId(String processInstanceId) throws TaskListException {
        TaskSearch taskSearch = new TaskSearch()
                .setProcessInstanceKey(processInstanceId)
                .setWithVariables(true);
        TaskList taskList = client.getTasks(taskSearch);
        return taskList.getItems();
    }

    // 2. Find all tasks for a process instance ID, with state and variable control
    public List<Task> findTasksForProcessInstanceId(String processInstanceId, TaskState state, boolean withVariables) throws TaskListException {
        TaskSearch taskSearch = new TaskSearch()
                .setProcessInstanceKey(processInstanceId)
                .setWithVariables(withVariables);
        if (state != null) {
            taskSearch.setState(state);
        }
        TaskList taskList = client.getTasks(taskSearch);
        return taskList.getItems();
    }


    // 3. Find active tasks for a variable name/value pair
    public List<Task> findActiveTasksForVariable(String name, String value) throws TaskListException {
        TaskSearch taskSearch = new TaskSearch()
                .setState(TaskState.CREATED)
                .setWithVariables(true)
                .addVariableFilter(name, value);
        TaskList taskList = client.getTasks(taskSearch);
        return taskList.getItems();
    }



    // 6. Find task by ID (Optional)
    public Optional<Task> findTaskById(String taskId) {
        if (taskId == null || taskId.trim().isEmpty()) return Optional.empty();
        try {
            Task task = client.getTask(taskId, true);
            return Optional.ofNullable(task);
        } catch (TaskListException e) {
            return Optional.empty();
        }
    }

    public Optional<Task> findTaskById(String taskId, boolean includeVariables) {
        if (taskId == null || taskId.trim().isEmpty()) return Optional.empty();
        try {
            Task task = client.getTask(taskId, includeVariables);
            return Optional.ofNullable(task);
        } catch (TaskListException e) {
            return Optional.empty();
        }
    }

    public Task findTaskByIdOrThrow(String taskId) throws TaskListException {
        if (taskId == null || taskId.trim().isEmpty())
            throw new TaskListException("Task ID cannot be null or empty");
        Task task = client.getTask(taskId, true);
        if (task == null) throw new TaskListException("Task not found with ID: " + taskId);
        return task;
    }

    public boolean taskExists(String taskId) {
        if (taskId == null || taskId.trim().isEmpty()) return false;
        try {
            Task task = client.getTask(taskId, false);
            return task != null;
        } catch (TaskListException e) {
            return false;
        }
    }

    // 7. Find all by assignee and state
    public List<Task> findAllByAssigneeAndState(String assignee, TaskState state) throws TaskListException {
        if (assignee == null || assignee.trim().isEmpty())
            throw new IllegalArgumentException("Assignee cannot be null or empty");
        if (state == null)
            throw new IllegalArgumentException("TaskState cannot be null");
        TaskSearch taskSearch = new TaskSearch()
                .setAssignee(assignee.trim())
                .setState(state)
                .setWithVariables(true);
        TaskList taskList = client.getTasks(taskSearch);

        return taskList.getItems() != null ? taskList.getItems() : new ArrayList<>();
    }

    public List<Task> findAllByAssigneeAndState(String assignee, TaskState state, boolean includeVariables) throws TaskListException {
        if (assignee == null || assignee.trim().isEmpty())
            throw new IllegalArgumentException("Assignee cannot be null or empty");
        if (state == null)
            throw new IllegalArgumentException("TaskState cannot be null");
        TaskSearch taskSearch = new TaskSearch()
                .setAssignee(assignee.trim())
                .setState(state)
                .setWithVariables(includeVariables);
        TaskList taskList = client.getTasks(taskSearch);
        return taskList.getItems() != null ? taskList.getItems() : new ArrayList<>();
    }

    public List<Task> findAllByAssigneeAndState(String assignee, TaskState state, Pagination pagination) throws TaskListException {
        if (assignee == null || assignee.trim().isEmpty())
            throw new IllegalArgumentException("Assignee cannot be null or empty");
        if (state == null)
            throw new IllegalArgumentException("TaskState cannot be null");
        TaskSearch taskSearch = new TaskSearch()
                .setAssignee(assignee.trim())
                .setState(state)
                .setWithVariables(true)
                .setPagination(pagination);
        TaskList taskList = client.getTasks(taskSearch);
        return taskList.getItems() != null ? taskList.getItems() : new ArrayList<>();
    }

    public List<Task> findAllByAssigneeAndStateSafe(String assignee, TaskState state) {
        if (assignee == null || assignee.trim().isEmpty() || state == null)
            return new ArrayList<>();
        try {
            return findAllByAssigneeAndState(assignee, state);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public List<Task> findActiveTasksByAssignee(String assignee) throws TaskListException {
        return findAllByAssigneeAndState(assignee, TaskState.CREATED);
    }

    public List<Task> findCompletedTasksByAssignee(String assignee) throws TaskListException {
        return findAllByAssigneeAndState(assignee, TaskState.COMPLETED);
    }

    public Map<String, List<Task>> findAllByAssigneesAndState(List<String> assignees, TaskState state) throws TaskListException {
        if (assignees == null || assignees.isEmpty()) return new HashMap<>();
        if (state == null)
            throw new IllegalArgumentException("TaskState cannot be null");
        Map<String, List<Task>> results = new HashMap<>();
        for (String assignee : assignees) {
            if (assignee != null && !assignee.trim().isEmpty()) {
                try {
                    List<Task> tasks = findAllByAssigneeAndState(assignee, state);
                    results.put(assignee, tasks);
                } catch (TaskListException e) {
                    results.put(assignee, new ArrayList<>());
                }
            }
        }
        return results;
    }

    public long countTasksByAssigneeAndState(String assignee, TaskState state) {
        try {
            List<Task> tasks = findAllByAssigneeAndState(assignee, state, false);
            return tasks.size();
        } catch (Exception e) {
            return 0;
        }
    }

    // 8. Claim task
    public Task claimTask(String taskId, String assignee) throws TaskListException {
        if (taskId == null || taskId.trim().isEmpty())
            throw new IllegalArgumentException("Task ID cannot be null or empty");
        if (assignee == null || assignee.trim().isEmpty())
            throw new IllegalArgumentException("Assignee cannot be null or empty");
        Task claimedTask = client.claim(taskId.trim(), assignee.trim());
        if (claimedTask == null)
            throw new TaskListException("Failed to claim task: " + taskId + ". Task not found or claim operation failed.");
        return claimedTask;
    }

    public Task claimTask(String taskId, String assignee, boolean allowOverrideAssignment) throws TaskListException {
        if (taskId == null || taskId.trim().isEmpty())
            throw new IllegalArgumentException("Task ID cannot be null or empty");
        if (assignee == null || assignee.trim().isEmpty())
            throw new IllegalArgumentException("Assignee cannot be null or empty");
        Optional<Task> existingTask = findTaskById(taskId);
        if (!existingTask.isPresent())
            throw new TaskListException("Task not found with ID: " + taskId);
        Task currentTask = existingTask.get();
        if (!allowOverrideAssignment && currentTask.getAssignee() != null && !currentTask.getAssignee().trim().isEmpty()) {
            throw new TaskListException("Task '" + taskId + "' is already assigned to '" +
                    currentTask.getAssignee() + "'. Use allowOverrideAssignment=true to reassign.");
        }
        Task claimedTask = client.claim(taskId.trim(), assignee.trim(), allowOverrideAssignment);
        if (claimedTask == null)
            throw new TaskListException("Failed to claim task: " + taskId + ". Claim operation failed.");
        return claimedTask;
    }

    public Map<String, Task> claimTasks(List<String> taskIds, String assignee) {
        if (assignee == null || assignee.trim().isEmpty()) throw new IllegalArgumentException("Assignee cannot be null or empty");
        if (taskIds == null || taskIds.isEmpty()) return new HashMap<>();
        Map<String, Task> results = new HashMap<>();
        for (String taskId : taskIds) {
            if (taskId != null && !taskId.trim().isEmpty()) {
                try {
                    Task claimedTask = claimTask(taskId, assignee);
                    results.put(taskId, claimedTask);
                } catch (TaskListException e) {
                    results.put(taskId, null);
                }
            }
        }
        return results;
    }

    public Task claimTaskForCurrentUser(String taskId) throws TaskListException {
        String currentUser = getCurrentUser();
        if (currentUser == null || currentUser.trim().isEmpty())
            throw new TaskListException("Cannot determine current user for task claim operation");
        return claimTask(taskId, currentUser);
    }

    public Optional<Task> tryClaimTask(String taskId, String assignee) {
        try {
            Task claimedTask = claimTask(taskId, assignee);
            return Optional.ofNullable(claimedTask);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Task claimTaskWithVariables(String taskId, String assignee) throws TaskListException {
        Task claimedTask = claimTask(taskId, assignee);
        try {
            List<Variable> variables = client.getVariables(taskId, true);
            claimedTask.setVariables(variables);
            return claimedTask;
        } catch (TaskListException e) {
            return claimedTask;
        }
    }

    String getCurrentUser() {
        // For real implementation, get from Spring SecurityContext
        return "AhmedAlhassen";
    }

    public boolean canClaimTask(String taskId, String assignee) {
        try {
            Optional<Task> taskOpt = findTaskById(taskId);
            if (!taskOpt.isPresent()) return false;
            Task task = taskOpt.get();
            if (task.getTaskState() != TaskState.CREATED) return false;
            if (assignee.equals(task.getAssignee())) return false;
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Task unclaimTask(String taskId) throws TaskListException {
        if (taskId == null || taskId.trim().isEmpty())
            throw new IllegalArgumentException("Task ID cannot be null or empty");
        Task unclaimedTask = client.unclaim(taskId.trim());
        if (unclaimedTask == null)
            throw new TaskListException("Failed to unclaim task: " + taskId + ". Task not found or unclaim operation failed.");
        return unclaimedTask;
    }

    public void completeTask(String taskId, Map<String, Object> variables) throws TaskListException {
        if (taskId == null || taskId.trim().isEmpty())
            throw new IllegalArgumentException("Task ID cannot be null or empty");
        client.completeTask(taskId.trim(), variables);
    }

    // Get tasks by candidate groups and Task state
    public List<Task> findTasksByCandidateGroups(List<String> groups, TaskState state, boolean withVariables, Pagination pagination) throws TaskListException {
        if (groups == null || groups.isEmpty())
            throw new IllegalArgumentException("Groups cannot be null or empty");
        TaskList taskList = client.getGroupsTasks(groups, state, withVariables, pagination);
        return taskList.getItems() != null ? taskList.getItems() : new ArrayList<>();
    }

    public List<Task> findTasksByCandidateGroups(List<String> groups) throws TaskListException {
        return findTasksByCandidateGroups(groups, null, false, null);
    }

    public List<Task> findTaskByCandidateGroup(String group) throws TaskListException {
        if (group == null || group.trim().isEmpty())
            throw new IllegalArgumentException("Group cannot be null or empty");
//        TaskList taskList = client.getGroupTasks(group.trim(),null,null);
        TaskSearch taskListSearch = new TaskSearch().setCandidateGroup(group);
        var taskList = client.getTasks(taskListSearch);
        return taskList.getItems() != null ? taskList.getItems() : new ArrayList<>();
    }
}
