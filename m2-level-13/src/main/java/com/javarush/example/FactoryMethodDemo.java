package com.javarush.example;

import static java.lang.Thread.sleep;

// 1. Продукт - задача для потока
interface Task {
    void execute();
}

// 2. Конкретные продукты: IOTask, ComputeTask
class IOTask implements Task {
    @Override
    public void execute() {
        System.out.println("IO задача выполняется (Input/Output)");
    }
}

class  ComputeTask implements Task {
    @Override
    public void execute() {
        System.out.println("Вычислительная задача выполняется");
    }
}

// 3. Фабрика с фабричным методом
abstract class TaskFactory {
    // Фабричный метод (определяет интерфейс создания)
    public abstract Task createTask();

    // Метод, использующий фабричный метод
    public void runInThread() {
        Task task = createTask(); // создание через фабричный метод
        new Thread(task::execute).start();
    }
}

// 4. Конкретные фабрики
class IOTaskFactory extends TaskFactory {
    @Override
    public Task createTask() {
        return new IOTask(); // создается задача IO (Input/Output)
    }
}

class ComputeTaskFactory extends TaskFactory {
    @Override
    public Task createTask() {
        return new ComputeTask(); // вычисления
    }
}

public class FactoryMethodDemo {

    public static void main(String[] args) throws InterruptedException {
        TaskFactory ioFactory = new IOTaskFactory();
        ioFactory.runInThread();
        sleep(100);

        TaskFactory compFactory = new ComputeTaskFactory();
        compFactory.runInThread();
        sleep(100);
    }

}
