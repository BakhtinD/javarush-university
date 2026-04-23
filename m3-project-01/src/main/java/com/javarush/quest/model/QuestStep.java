package com.javarush.quest.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class QuestStep {
    private String id;
    private String text;
    private String option1;
    private String option2;
    private String nextStepId1;
    private String nextStepId2;
}
