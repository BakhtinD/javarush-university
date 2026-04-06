package com.javarush.adapter;

// Целевой интерфейс который ожидает наш клент
interface Printer {
    void print(String text);
}

// Adaptee - новая несовместимая сущность
class EpsonPrinter {
    public void printEpson(String text) {
        System.out.printf("Epson печатает: " + text);
    }
}

// Адаптер
class EpsonPrinterAdapter implements Printer {

    private EpsonPrinter epsonPrinter;

    public EpsonPrinterAdapter(EpsonPrinter epsonPrinter) {
        this.epsonPrinter = epsonPrinter;
    }

    @Override
    public void print(String text) {

        // Преобразовать вызов target-метода print() в вызов printEpson()
        epsonPrinter.printEpson("[Адаптировано] " + text);

    }
}

// Клиентский код, который работает только с интерфейсом Printer
class ReportGenerator {
    private Printer printer;

    public ReportGenerator(Printer printer) {
        this.printer = printer;
    }

    public void generateReport() {
        System.out.println("Генерация отчета...");
        printer.print("Ваш ежемесячный отчет");
    }

}

public class AdapterDemo {

    public static void main(String[] args) {

        Printer oldCanon = new Printer() {
            @Override
            public void print(String text) {
                System.out.println("Canon печатает: " + text);
            }
        };

        ReportGenerator report1 = new ReportGenerator(oldCanon);
        report1.generateReport();

        EpsonPrinter newEpson = new EpsonPrinter();
        Printer epsonAdapter = new EpsonPrinterAdapter(newEpson);

        ReportGenerator report2 = new ReportGenerator(epsonAdapter);
        report2.generateReport();

    }

}
