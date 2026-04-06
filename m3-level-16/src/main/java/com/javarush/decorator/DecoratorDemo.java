package com.javarush.decorator;

// Интерфейс компонента
interface DataSource {
    void writeData(String data);
    String readData();
}

// Конкретный компонент
class FileDataSource implements DataSource {

    private String filename;

    public FileDataSource(String filename) {
        this.filename = filename;
    }

    @Override
    public void writeData(String data) {
        System.out.println("Записываем данные в файл " + filename + ": " + data);
    }

    @Override
    public String readData() {
        System.out.println("Читаем данные из файла " + filename);
        return "данные из файла: [1, 2, 3, 4, ...]";
    }
}

// Базовый декоратор
abstract class DataSourceDecorator implements DataSource {
    protected DataSource wrappee; // ссылка на оборачиваемый компонент

    public DataSourceDecorator(DataSource source) {
        this.wrappee = source;
    }

    // Делегируем вызовы обернутому объекту
    @Override
    public void writeData(String data) {
        wrappee.writeData(data);
    }

    @Override
    public String readData() {
        return wrappee.readData();
    }

}

// Декоратор шифрования
class EncryptionDecorator extends DataSourceDecorator {

    public EncryptionDecorator(DataSource source) {
        super(source);
    }

    @Override
    public void writeData(String data) {
        // Новое поведение до (шифруем)
        String encryptedData = "Зашифровано(" + data + ")";
        System.out.println(" [Шифрование] -> " + encryptedData);
        // Передаем далее по цепочке
        super.writeData(encryptedData);
    };

    @Override
    public String readData() {

        // Получаем данные от обернутого объекта
        String data = super.readData();
        // Добавляем свое поведение ПОСЛЕ (расшифровка)
        System.out.println(" [Расшифровка] данных");

        return data.replace("Зашифровано(", "").replace(")",
                "");
    };



}

// Декоратор сжатия
class CompressionDecorator extends DataSourceDecorator {


    public CompressionDecorator(DataSource source) {
        super(source);
    }

    @Override
    public void writeData(String data) {
        // Сжатие данных перед записью
        String compressedData = "Сжато [" + data + "]";
        System.out.println(" [Сжатие] -> " + compressedData);
        super.writeData(compressedData);
    };

    @Override
    public String readData() {
        String data = super.readData();
        System.out.println(" [Распаковка] данных");
        return data.replace("Сжато [", "")
                .replace("]", "");
    }

}

// Клиентский код
public class DecoratorDemo {

    public static void main(String[] args) {

        // Простая запись в файл
        DataSource file = new FileDataSource("data.txt");
        file.writeData("Секретные данные 123");
        System.out.println("Прочитали: " + file.readData());

        // Файл с шифрованием
        DataSource encryptedFile = new EncryptionDecorator(new FileDataSource("encrypted.txt") );
        encryptedFile.writeData("Секретные данные 123");
        System.out.println("Прочитали: " + encryptedFile.readData());

        // Файл со сжатием и шифрованием (в разном порядке)
        DataSource superFile = new CompressionDecorator(
                new EncryptionDecorator(
                        new FileDataSource("super.txt")));
        superFile.writeData("Секретные данные 123");
        System.out.println("Прочитали: " + superFile.readData());

        // Файл с шифрованием и после сжимаем
        DataSource anotherCombo = new EncryptionDecorator(
                new CompressionDecorator(
                        new FileDataSource("another.txt")));

        anotherCombo.writeData("Секретные данные 123");
        System.out.println("Прочитали: " + anotherCombo.readData());

    }

}
