package com.javarush.quest.service;

import com.javarush.quest.model.QuestStep;

import java.util.HashMap;
import java.util.Map;


public class QuestService {

    private final Map<String, QuestStep> steps = new HashMap<>();

    public QuestService() {
        initStep();
    }

    private void initStep() {
        // Стартовый шаг
        steps.put("start", new QuestStep(
                 // id
                 "start",
                 // text
                 "Ты потерял память. Принять вызов НЛО?",
                 // option1
                 "Принять вызов",
                 // option2
                 "Отклонить вызов",
                 // nextStepId1
                 "bridge",
                 // nextStepId2
                 "lose1"
                ));

        // Второй шаг
        steps.put("bridge", new QuestStep(
                        // id
                        "bridge",
                        // text
                        "Ты принял вызов. Поднимаешься на мостик к Капитану?",
                        // option1
                        "Подняться на мостик",
                        // option2
                        "Отказаться",
                        // nextStepId1
                        "captain",
                        // nextStepId2
                        "lose2"
                ));

        // Третий шаг
        steps.put("captain", new QuestStep(
                // id
                "captain",
                // text
                "Ты поднялся на мостик. Ты кто?",
                // option1
                "Рассказать правду о себе",
                // option2
                "Солгать о себе",
                // nextStepId1
                "win",
                // nextStepId2
                "lose3"
        ));

        // Победа
        steps.put("win", new QuestStep(
                // id
                "win",
                // text
                "Тебя вернули домой. Победа!",
                // option1
                 null,
                // option2
                null,
                // nextStepId1
                null,
                // nextStepId2
                null
        ));

        // Поражения lose1
        steps.put("lose1", new QuestStep(
                // id
                "lose1",
                // text
                "Ты отклонил вызов. Поражение!",
                // option1
                null,
                // option2
                null,
                // nextStepId1
                null,
                // nextStepId2
                null
        ));

        // Поражения lose2
        steps.put("lose2", new QuestStep(
                // id
                "lose2",
                // text
                "Ты не пошел на переговоры. Поражение!",
                // option1
                null,
                // option2
                null,
                // nextStepId1
                null,
                // nextStepId2
                null
        ));

        // Поражения lose3
        steps.put("lose3", new QuestStep(
                // id
                "lose3",
                // text
                "Твоя ложь разоблачена. Поражение!",
                // option1
                null,
                // option2
                null,
                // nextStepId1
                null,
                // nextStepId2
                null
        ));


    }

    public QuestStep getStep(String id) {
        return steps.get(id);
    }

    public boolean isFinalStep(String stepId) {
        QuestStep step = steps.get(stepId);
        return step != null && step.getOption1() == null;
    }

}
