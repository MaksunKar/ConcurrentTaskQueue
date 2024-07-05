import java.util.LinkedList;
import java.util.Queue;

public class ConcurrentTaskQueue {

    private Queue<Runnable> tasks; // Объявление очереди задач типа Runnable
    private int capacity; // Объявление переменной capacity для хранения максимальной вместимости очереди

    public ConcurrentTaskQueue(int capacity) {
        tasks = new LinkedList<>(); // Инициализация очереди задач типа LinkedList
        this.capacity = capacity; // Присвоение переданного значения capacity переменной класса
    }

    public synchronized void add(Runnable task) throws InterruptedException {
        while (tasks.size() >= capacity) { // Проверка, достигла ли очередь максимальной вместимости
            wait(); // Приостановка выполнения потока, ожидание освобождения места в очереди
        }
        tasks.add(task); // Добавление задачи в очередь
        notifyAll(); // Уведомление всех потоков, что место в очереди освободилось
    }

    public synchronized Runnable remove() throws InterruptedException {
        while (tasks.isEmpty()) { // Проверка, пустая ли очередь
            wait(); // Приостановка выполнения потока, ожидание появления новой задачи в очереди
        }
        Runnable task = tasks.poll(); // Извлечение задачи из очереди
        notifyAll(); // Уведомление всех потоков, что место в очереди освободилось
        return task; // Возвращение извлеченной задачи
    }

    public static void main(String[] args) {
        ConcurrentTaskQueue queue = new ConcurrentTaskQueue(10); // Создание экземпляра класса со значением capacity 10

        Thread producerThread = new Thread(() -> { // Создание потока-производителя
            try {
                for (int i = 0; i < 20; i++) { // Цикл добавления задач в очередь
                    Runnable task = createTask(i); // Создание новой задачи
                    queue.add(task); // Добавление задачи в очередь
                    System.out.println("Producer added task: " + i); // Вывод сообщения о добавлении задачи
                    Thread.sleep(100); // Приостановка выполнения потока на 100 миллисекунд
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        Thread consumerThread = new Thread(() -> { // Создание потока-потребителя
            try {
                for (int i = 0; i < 20; i++) { // Цикл извлечения и выполнения задач из очереди
                    Runnable task = queue.remove(); // Извлечение задачи из очереди
                    System.out.println("Consumer removed task: " + task); // Вывод сообщения о извлечении задачи
                    task.run(); // Выполнение задачи
                    Thread.sleep(200); // Приостановка выполнения потока на 200 миллисекунд
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        producerThread.start(); // Запуск потока-производителя
        consumerThread.start(); // Запуск потока-потребителя
    }

    private static Runnable createTask(int taskId) { // Создание задачи
        return () -> {
            System.out.println("Executing task: " + taskId); // Вывод сообщения о выполнении задачи
            // Выполнение задачи
        };
    }
}
