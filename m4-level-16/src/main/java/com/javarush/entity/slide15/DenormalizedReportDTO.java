package com.javarush.dto.slide15;

import lombok.Data;
import java.math.BigDecimal;

// Еще один DTO для демонстрации работы с денормализованными данными
@Data
public class DenormalizedReportDTO {
    // Представьте, что эти данные приходят из огромной отчетной таблицы
    private String departmentName;
    private String managerName;
    private Long employeeCount;
    private BigDecimal totalSalary;
    private BigDecimal avgSalary;
    private BigDecimal maxSalary;
    private Long projectsActive;
    private Long projectsCompleted;

    // Добавляем сеттеры для совместимости с разными типами
    public void setEmployeeCount(Object employeeCount) {
        if (employeeCount instanceof Number) {
            this.employeeCount = ((Number) employeeCount).longValue();
        }
    }

    public void setProjectsActive(Object projectsActive) {
        if (projectsActive instanceof Number) {
            this.projectsActive = ((Number) projectsActive).longValue();
        }
    }

    public void setProjectsCompleted(Object projectsCompleted) {
        if (projectsCompleted instanceof Number) {
            this.projectsCompleted = ((Number) projectsCompleted).longValue();
        }
    }

}