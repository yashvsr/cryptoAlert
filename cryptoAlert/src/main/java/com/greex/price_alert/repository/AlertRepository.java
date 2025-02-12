package com.greex.price_alert.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.greex.price_alert.entity.Alert;

public interface AlertRepository extends JpaRepository<Alert, Long> {
}
