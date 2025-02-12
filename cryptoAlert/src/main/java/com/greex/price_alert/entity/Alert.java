package com.greex.price_alert.entity;

import java.time.LocalDateTime;

import javax.validation.constraints.NotNull;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

@Data
@Entity
@Table(name = "alerts")
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "User ID cannot be null")
    private Long userId;

    @NotNull(message = "Symbol cannot be null or empty")
    private String symbol;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Basis cannot be null")
    private Basis basis;

    @NotNull(message = "Default value stored as 5")
    private Integer maLength; // Moving Average window size (optional)

    @NotNull(message = "Value cannot be null")
    private Double value;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Direction cannot be null")
    private Direction direction;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Status cannot be null")
    private Status status;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public Alert() {
        this.maLength=5;
        this.status = Status.PENDING;
    }

    @Builder  // Lombok annotation for building objects with optional parameters
    public Alert(Long userId, String symbol, Basis basis, Integer maLength, Double value, Direction direction) {
        this.userId = userId;
        this.symbol = symbol;
        this.basis = basis;
        this.maLength = maLength != null ? maLength : 5;;
        this.value = value;
        this.direction = direction;
        this.status = Status.PENDING;
    }

    public enum Basis {
        PRICE,
        MOVING_AVERAGE
    }

    public enum Direction {
        UP,
        DOWN
    }

    public enum Status {
        PENDING,
        TRIGERRED,
        EXPIRED, //TODO : To be implemented
        CANCELLED
    }
}