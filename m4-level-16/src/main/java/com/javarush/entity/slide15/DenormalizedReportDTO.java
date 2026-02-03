package com.javarush.dto.slide15;

import lombok.Data;
import java.math.BigDecimal;

// Еще один DTO для демонстрации работы с денормализованными данными
@Data
public class DenormalizedReportDTO {
    // Представьте, что эти данные приходят из огромной отчетной таблицы
    private String departmentName;
    private String managerName;
    private Integer employeeCount;
    private BigDecimal totalSalary;
    private BigDecimal avgSalary;
    private BigDecimal maxSalary;
    private Integer projectsActive;
    private Integer projectsCompleted;
}