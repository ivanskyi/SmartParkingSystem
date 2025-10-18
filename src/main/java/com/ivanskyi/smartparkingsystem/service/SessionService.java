package com.ivanskyi.smartparkingsystem.service;

import com.ivanskyi.smartparkingsystem.model.Session;
import com.ivanskyi.smartparkingsystem.repository.SessionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SessionService {
    private final SessionRepository sessionRepository;

    @Transactional
    public Session save(Session session) {
        return sessionRepository.save(session);
    }

    public Optional<Session> findActiveSessionByVehicleId(Long vehicleId) {
        return sessionRepository.findByVehicleIdAndExitTimeIsNull(vehicleId);
    }

    public List<Session> findAllActiveSessions() {
        return sessionRepository.findByExitTimeIsNull();
    }

    public List<Session> findAllInactiveSessions() {
        return sessionRepository.findByExitTimeIsNotNull();
    }

    public List<Session> findAllSessions() {
        return sessionRepository.findAll();
    }
}
