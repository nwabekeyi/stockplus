package com.stockmgmt.api.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stockmgmt.api.entity.Store;
import com.stockmgmt.api.entity.StoreWorker;
import com.stockmgmt.api.entity.Subscription;
import com.stockmgmt.api.entity.SubscriptionPlan;
import com.stockmgmt.api.entity.User;
import com.stockmgmt.api.entity.dto.request.CreateWorkerRequest;
import com.stockmgmt.api.entity.dto.response.WorkerLimitResponse;
import com.stockmgmt.api.entity.dto.response.WorkerResponse;
import com.stockmgmt.api.entity.enumeration.SubscriptionStatus;
import com.stockmgmt.api.entity.enumeration.UserRole;
import com.stockmgmt.api.entity.enumeration.WorkerRole;
import com.stockmgmt.api.exception.ResourceNotFoundException;
import com.stockmgmt.api.repository.StoreRepository;
import com.stockmgmt.api.repository.StoreWorkerRepository;
import com.stockmgmt.api.repository.UserRepository;
import com.stockmgmt.api.service.WorkerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WorkerServiceImpl implements WorkerService {
    private static final Map<WorkerRole, List<String>> ROLE_PERMISSIONS = Map.of(
            WorkerRole.MANAGER, List.of("dashboard:view", "products:write", "sales:write", "purchases:write", "expenses:write", "reports:view", "workers:manage", "settings:manage"),
            WorkerRole.CASHIER, List.of("dashboard:view", "sales:write"),
            WorkerRole.INVENTORY, List.of("dashboard:view", "products:write", "purchases:write"),
            WorkerRole.ACCOUNTANT, List.of("dashboard:view", "expenses:write", "reports:view"),
            WorkerRole.VIEWER, List.of("dashboard:view", "reports:view")
    );

    private final StoreRepository storeRepository;
    private final StoreWorkerRepository workerRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper;

    @Override
    public List<WorkerResponse> listWorkers(UUID storeId, User owner) {
        Store store = getOwnedStore(storeId, owner);
        return workerRepository.findByStore_IdOrderByCreatedAtDesc(store.getId()).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public WorkerLimitResponse getWorkerLimits(UUID storeId, User owner) {
        Store store = getOwnedStore(storeId, owner);
        SubscriptionPlan plan = getActiveCloudPlan(store);
        long workers = workerRepository.countByStore_IdAndActiveTrue(store.getId());
        int maxUsers = plan.getMaxUsers();
        long remainingWorkers = maxUsers < 0 ? Long.MAX_VALUE : Math.max(maxUsers - 1L - workers, 0L);
        return WorkerLimitResponse.builder()
                .maxUsers(maxUsers)
                .usedUsers(workers + 1)
                .remainingWorkers(remainingWorkers)
                .canAddWorker(maxUsers < 0 || remainingWorkers > 0)
                .planName(plan.getName())
                .build();
    }

    @Override
    @Transactional
    public WorkerResponse createWorker(UUID storeId, CreateWorkerRequest request, User owner) {
        Store store = getOwnedStore(storeId, owner);
        WorkerLimitResponse limits = getWorkerLimits(storeId, owner);
        if (!limits.isCanAddWorker()) {
            throw new ResponseStatusException(HttpStatus.PAYMENT_REQUIRED, "Your current subscription does not allow additional workers");
        }
        if (workerRepository.findByStore_IdAndEmail(storeId, request.getEmail()).isPresent() || userRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A user or worker already exists with this email");
        }

        User workerUser = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .role(UserRole.ROLE_USER)
                .active(true)
                .build();
        userRepository.save(workerUser);

        List<String> permissions = request.getPermissions() == null || request.getPermissions().isEmpty()
                ? ROLE_PERMISSIONS.get(request.getRole())
                : request.getPermissions().stream().filter(ROLE_PERMISSIONS.get(request.getRole())::contains).toList();

        try {
            StoreWorker worker = StoreWorker.builder()
                    .store(store)
                    .user(workerUser)
                    .firstName(request.getFirstName())
                    .lastName(request.getLastName())
                    .email(request.getEmail())
                    .role(request.getRole())
                    .permissions(objectMapper.writeValueAsString(permissions))
                    .active(true)
                    .build();
            return toResponse(workerRepository.save(worker));
        } catch (JsonProcessingException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid permissions");
        }
    }

    private Store getOwnedStore(UUID storeId, User owner) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Store not found"));
        if (!store.getOwner().getId().equals(owner.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the store owner can manage workers");
        }
        return store;
    }

    private SubscriptionPlan getActiveCloudPlan(Store store) {
        Subscription subscription = store.getSubscription();
        if (subscription == null || subscription.getStatus() != SubscriptionStatus.ACTIVE || "Free Offline".equalsIgnoreCase(subscription.getPlan().getName())) {
            throw new ResponseStatusException(HttpStatus.PAYMENT_REQUIRED, "Subscribe to a cloud plan before adding workers");
        }
        return subscription.getPlan();
    }

    private WorkerResponse toResponse(StoreWorker worker) {
        try {
            return WorkerResponse.builder()
                    .id(worker.getId())
                    .storeId(worker.getStore().getId())
                    .userId(worker.getUser().getId())
                    .firstName(worker.getFirstName())
                    .lastName(worker.getLastName())
                    .email(worker.getEmail())
                    .role(worker.getRole())
                    .permissions(objectMapper.readValue(worker.getPermissions(), new TypeReference<>() {}))
                    .active(worker.isActive())
                    .createdAt(worker.getCreatedAt())
                    .build();
        } catch (JsonProcessingException ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to read worker permissions");
        }
    }
}
