package com.deu.java.backend.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "dorm_meals")
public class DormMeal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate mealDate;

    @Column(name = "dorm_type", nullable = false)
    private String dormType; // "hyomin", "happy", "information", "suduck"

    @Column(name = "meal_type")
    private String mealType; // "breakfast", "lunch", "dinner", "lunch_s", "dinner_s"

    @Lob
    @Column(columnDefinition = "TEXT")
    private String mealContent;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public DormMeal() {}

    public DormMeal(LocalDate mealDate, String dormType, String mealType, String mealContent) {
        this.mealDate = mealDate;
        this.dormType = dormType;
        this.mealType = mealType;
        this.mealContent = mealContent;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }


    public LocalDate getMealDate() { return mealDate; }
    public void setMealDate(LocalDate mealDate) { this.mealDate = mealDate; }

    public String getDormType() { return dormType; }
    public void setDormType(String dormType) { this.dormType = dormType; }

    public String getMealType() { return mealType; }
    public void setMealType(String mealType) { this.mealType = mealType; }

    public String getMealContent() { return mealContent; }
    public void setMealContent(String mealContent) { this.mealContent = mealContent; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}