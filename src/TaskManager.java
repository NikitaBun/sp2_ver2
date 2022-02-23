import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskManager {

    private Map<Integer, Task> tasks = new HashMap<>();
    private int idCounter = 0;

    private int generateNewId() {
        return idCounter++;
    }

    Task getTaskById(int id) {
        if (!tasks.containsKey(id)) {
            System.out.println("передана не существующая задача");
            throw new IllegalArgumentException();
        }
        return tasks.get(id);
    }

    int createNewTask(Task task) {
        if (task.getId() != null) {
            System.out.println("задача с не пустым id");
            throw new IllegalArgumentException();
        }
        if (tasks.containsKey(task.getId())) {
            System.out.println("передана существующая задача");
            throw new IllegalArgumentException();
        }
        if (task.getTaskType().equals("subtask")) {
            Subtask subtask = (Subtask) task;
            int epicId = subtask.getEpicId();
            if (!tasks.containsKey(epicId)) {
                System.out.println("epicId нет в базе");
                throw new IllegalArgumentException();
            }
            subtask.setId(generateNewId());
            tasks.put(subtask.getId(), subtask);

            Epic epic = (Epic) tasks.get(epicId);
            List<Integer> subtasksIds = epic.getSubtaskIds();
            subtasksIds.add(subtask.getId());
            updateEpicStatus(epic);
            return subtask.getId();
        } else {
            task.setId(generateNewId());
            tasks.put(task.getId(), task);
            return task.getId();
        }
    }

    private void updateEpicStatus(Epic epic) {
        List<Subtask> subtasks = getAllSubtasksOfEpicById(epic.getId());
        Map<String, Integer> statusCounter = new HashMap<>();
        int allSubtasksCount = 0;
        for (Subtask subtask : subtasks) {
            Integer statusCount = statusCounter.getOrDefault(subtask.getStatus(), 0);
            statusCounter.put(subtask.getStatus(), statusCount + 1);
            allSubtasksCount += 1;
        }
        if (allSubtasksCount == 0 || statusCounter.getOrDefault("new", 0) == allSubtasksCount) {
            epic.setStatus("new");
        } else if (statusCounter.getOrDefault("done", 0) == allSubtasksCount) {
            epic.setStatus("done");
        } else {
            epic.setStatus("in_progress");
        }
    }

    void updateTask(Task task) {
        if (task.getId() == null) {
            System.out.println("задача с  пустым id");
            return;
        }
        if (!tasks.containsKey(task.getId())) {
            System.out.println("передана не существующая задача");
            return;
        }
        if (task.getTaskType().equals("subtask")) {
            Subtask subtask = (Subtask) task;
            int epicId = subtask.getEpicId();
            if (!tasks.containsKey(epicId)) {
                System.out.println("epicId нет в базе");
                return;
            }
            tasks.put(subtask.getId(), subtask);

            Epic epic = (Epic) tasks.get(epicId);
            updateEpicStatus(epic);
        } else {
            tasks.put(task.getId(), task);
        }
    }

    void removeTaskById(int id) {
        if (!tasks.containsKey(id)) {
            System.out.println("передана не существующая задача");
            return;
        }
        Task task = tasks.get(id);
        if (task.getTaskType().equals("subtask")) {
            Subtask subtask = (Subtask) task;
            int epicId = subtask.getEpicId();
            Epic epic = (Epic) tasks.get(epicId);
            epic.getSubtaskIds().remove(subtask.getId());
            updateEpicStatus(epic);
        } else if (task.getTaskType().equals("epic")) {
            Epic epic = (Epic) task;
            if(!epic.getSubtaskIds().isEmpty()) {
                System.out.println("нельзя удалять, у эпика есть подзадачи");
                return;
            } else {
                tasks.remove(id);
            }
        } else {
            tasks.remove(id);
        }
    }

    void clearAllTasks() {
        tasks.clear();
    }

    List<Subtask> getAllSubtasksOfEpicById(int id) {
        Task task = tasks.get(id);
        if(!task.getTaskType().equals("epic")) {
            System.out.println("нельзя получить список по id задачи или подзадачи");
        }
        Epic epic = (Epic) task;
        List<Integer> subtasksIds = epic.getSubtaskIds();
        List<Subtask> subtasks = new ArrayList<>();
        for (Integer subtaskId : subtasksIds) {
            Subtask subtask = (Subtask) tasks.get(subtaskId);
            subtasks.add(subtask);
        }
        return subtasks;
    }
}
