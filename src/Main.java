public class Main {
    public static void main(String[] args) {
        TaskManager manager = new TaskManager();

        Task oldTask = new Task("задача1", "описание", "new");
        int task1Id = manager.createNewTask(oldTask);
        System.out.println(oldTask);
        Task updatedTask = new Task(task1Id, "задача1", "описание", "in_progress");
        System.out.println(updatedTask);
    }
}
