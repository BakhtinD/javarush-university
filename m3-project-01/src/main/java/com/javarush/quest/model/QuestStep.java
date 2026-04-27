package com.javarush.quest.model;

// import lombok.AllArgsConstructor;
// import lombok.Data;
// import lombok.NoArgsConstructor;
// import lombok.ToString;

//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//@ToString
public class QuestStep {
    private String id;
    private String text;
    private String option1;
    private String option2;
    private String nextStepId1;
    private String nextStepId2;

    public QuestStep() {
    }

    public QuestStep(String id, String text, String option1, String option2, String nextStepId1, String nextStepId2) {
        this.id = id;
        this.text = text;
        this.option1 = option1;
        this.option2 = option2;
        this.nextStepId1 = nextStepId1;
        this.nextStepId2 = nextStepId2;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getOption1() {
        return option1;
    }

    public void setOption1(String option1) {
        this.option1 = option1;
    }

    public String getOption2() {
        return option2;
    }

    public void setOption2(String option2) {
        this.option2 = option2;
    }

    public String getNextStepId1() {
        return nextStepId1;
    }

    public void setNextStepId1(String nextStepId1) {
        this.nextStepId1 = nextStepId1;
    }

    public String getNextStepId2() {
        return nextStepId2;
    }

    public void setNextStepId2(String nextStepId2) {
        this.nextStepId2 = nextStepId2;
    }

    @Override
    public String toString() {
        return "QuestStep{" +
                "id='" + id + '\'' +
                ", text='" + text + '\'' +
                ", option1='" + option1 + '\'' +
                ", option2='" + option2 + '\'' +
                ", nextStepId1='" + nextStepId1 + '\'' +
                ", nextStepId2='" + nextStepId2 + '\'' +
                '}';
    }
}
