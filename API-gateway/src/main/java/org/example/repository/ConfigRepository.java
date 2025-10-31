package org.example.repository;

import org.example.model.ServiceConfig;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfigRepository extends JpaRepository<ServiceConfig,Long> {
}
