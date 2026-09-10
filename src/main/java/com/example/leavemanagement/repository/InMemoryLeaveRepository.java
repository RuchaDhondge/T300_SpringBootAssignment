package com.example.leavemanagement.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Repository;

import com.example.leavemanagement.model.LeaveRequest;

/**
 * In-memory implementation of {@link LeaveRepository} that stores leave
 * requests without an external database.
 */
@Repository
public class InMemoryLeaveRepository implements LeaveRepository {

    private final Map<Long, LeaveRequest> storage = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1L);

    @Override
    public LeaveRequest save(LeaveRequest leaveRequest) {
        if (leaveRequest.getId() == null) {
            leaveRequest.setId(idGenerator.getAndIncrement());
        }
        storage.put(leaveRequest.getId(), leaveRequest);
        return leaveRequest;
    }

    @Override
    public Optional<LeaveRequest> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<LeaveRequest> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public boolean deleteById(Long id) {
        return storage.remove(id) != null;
    }

    @Override
    public boolean existsById(Long id) {
        return storage.containsKey(id);
    }
}
