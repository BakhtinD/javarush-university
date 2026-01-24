package com.javarush.entity.discriminator;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;

@Entity
@DiscriminatorValue("REPORT")
// Для числового: @DiscriminatorValue("3")
// Для символьного: @DiscriminatorValue("R")
@Getter
@Setter
public class Report extends Document {
    @Column(name = "report_period")
    private String period; // "2024-Q1", "2024-01" и т.д.

    @Column(name = "pages_count")
    private Integer pagesCount;

    @Column(name = "has_charts")
    private Boolean hasCharts;

    @Column(name = "has_summary")
    private Boolean hasSummary = true;

    @Column(name = "approved_by")
    private String approvedBy;
}