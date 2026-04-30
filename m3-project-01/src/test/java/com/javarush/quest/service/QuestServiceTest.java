package com.javarush.quest.service;

import com.javarush.quest.model.QuestStep;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class QuestServiceTest {

    private static final String EXPECTED_STEP_START_ID = "start";

    private QuestService questService;

    @BeforeEach
    void setUp() {
        questService = new QuestService();
    }

    //   - id шага: "start", "bridge", "captain", "win", "lose1", "lose2", "lose3"

    // todo Делаем: 1 метод на 1 assert, все EXPECTED_ объявляем через константы класса
    @Test
    @DisplayName("Тест получения начального шага")
    void testGetStartStep() {
        QuestStep step = questService.getStep("start");

        assertNotNull(step, "Шаг 'start' не должен быть null");
        assertEquals(EXPECTED_STEP_START_ID, step.getId(), "ID шага должен быть `" + EXPECTED_STEP_START_ID + "`");
        // Text
        assertEquals("Ты потерял память. Принять вызов НЛО?", step.getText(), "Должна быть строка: `Ты потерял память. Принять вызов НЛО?`");
        // option1 "Принять вызов"
        assertEquals("Принять вызов", step.getOption1(), "Должен быть: `Принять вызов`");
        // option2 "Отклонить вызов"
        assertEquals("Отклонить вызов", step.getOption2(), "Должен быть: `Отклонить вызов`");
        // nextStepId1 "bridge"
        assertEquals("bridge", step.getNextStepId1(), "Должен быть: `bridge`");
        // nextStepId2 "lose1"
        assertEquals("lose1", step.getNextStepId2(), "Должен быть: `lose1`");
    }

    // Тест получения шага с победой. На этом шаге не должно быть вариантов ответа
    @Test
    @DisplayName("Тест получения шага с победой")
    void testGetWinStep() {
        QuestStep step = questService.getStep("win");

        assertNotNull(step, "Шаг 'win' не должен быть null");
        // "Тебя вернули домой. Победа!"
        assertTrue(step.getText().contains("Победа"), "Текст должен содержать слово `Победа`");
        // На этом шаге не должно быть вариантов ответа
        assertNull(step.getOption1(), "Не должно быть вариантов ответа");
        assertNull(step.getOption2(), "Не должно быть вариантов ответа");
    }

    //  Тест получения несуществующего шага
    @Test
    @DisplayName("Тест получения несуществующего шага")
    void testGetNonExistentStep() {
        QuestStep step = questService.getStep("non-existent");
        assertNull(step, "Несуществующий шаг должен возвращать null");
    }

    // Тест проверки финального шага
    // id шага: "start", "bridge", "captain", "win", "lose1", "lose2", "lose3"
    @Test
    @DisplayName("Тест проверки финального шага")
    void testIsFinalStep() {
        // "win", "lose1", "lose2", "lose3"
        assertTrue(questService.isFinalStep("win"), "`win` должен быть финальным шагом");
        assertTrue(questService.isFinalStep("lose1"), "`lose1` должен быть финальным шагом");
        assertTrue(questService.isFinalStep("lose2"), "`lose2` должен быть финальным шагом");
        assertTrue(questService.isFinalStep("lose3"), "`lose3` должен быть финальным шагом");

        // "start", "bridge", "captain"
        assertFalse(questService.isFinalStep("start"), "`start` не должен быть финальным шагом");
        assertFalse(questService.isFinalStep("bridge"), "`lose1` не должен быть финальным шагом");
        assertFalse(questService.isFinalStep("captain"), "`lose2` не должен быть финальным шагом");
    }

    // Тест логики ветвления
    @Test
    @DisplayName("Тест логики ветвления")
    void testQuestBranching() {
        QuestStep start = questService.getStep("start");
        QuestStep bridge = questService.getStep("bridge");
        QuestStep captain = questService.getStep("captain");
        QuestStep win = questService.getStep("win");

        assertNotNull(start);
        assertNotNull(bridge);
        assertNotNull(captain);
        assertNotNull(win);

        // Проверка корректностей связи
        assertEquals("bridge", start.getNextStepId1());
        assertEquals("lose1", start.getNextStepId2());

        assertEquals("captain", bridge.getNextStepId1());
        assertEquals("lose2", bridge.getNextStepId2());

        assertEquals("win", captain.getNextStepId1());
        assertEquals("lose3", captain.getNextStepId2());

    }

}