package com.javarush.example;

public class AnnotationExamples {

    @Deprecated
    public void oldMethod() {
        System.out.println("Устаревший метод");
    }

    @SuppressWarnings("unused")
    public void newMethod() {
        String notUsed = "Эта переменная не используется";
        System.out.println("Новый метод");
    }

    @Override
    @SuppressWarnings("unused")
    public String toString() {
        String notUsed = "Эта переменная не используется";
        return "toString";
    }

    public static void main(String[] args) {
        AnnotationExamples examples = new AnnotationExamples();

        examples.oldMethod();
        examples.newMethod();

        System.out.println(examples);

        try {
            Deprecated deprecateAnnotation = AnnotationExamples.class
                    .getMethod("oldMethod")
                    .getAnnotation(Deprecated.class);

            if (deprecateAnnotation != null) {
                System.out.println("Метод oldMethod помечен как @Deprecated");
            }

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

    }

}
