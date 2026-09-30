package com.tecnologico.minirag.repository;

import com.tecnologico.minirag.model.QueryLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QueryLogRepository extends JpaRepository<QueryLog, Long> {
}
