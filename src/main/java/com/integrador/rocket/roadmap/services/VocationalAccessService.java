package com.integrador.rocket.roadmap.services;

import com.integrador.rocket.roadmap.infra.exceptions.VocationalTestRequiredException;
import com.integrador.rocket.roadmap.repositories.VocationalTestResultRepository;
import org.springframework.stereotype.Service;

@Service
public class VocationalAccessService {
    private final VocationalTestResultRepository results;

    public VocationalAccessService(VocationalTestResultRepository results) {
        this.results = results;
    }

    public void requireCompletedTest(Long userId) {
        if (userId == null || results.findByUserId(userId).isEmpty()) {
            throw new VocationalTestRequiredException();
        }
    }
}
