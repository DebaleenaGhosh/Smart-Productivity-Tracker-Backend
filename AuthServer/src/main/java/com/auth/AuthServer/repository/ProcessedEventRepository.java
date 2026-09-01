package com.auth.AuthServer.repository;

import com.auth.AuthServer.entity.ProcessedEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessedEventRepository extends JpaRepository<ProcessedEvent, String>
{
}