package com.stockmgmt.api.service.impl;

import com.stockmgmt.api.entity.Store;
import com.stockmgmt.api.entity.Subscription;
import com.stockmgmt.api.entity.SubscriptionPlan;
import com.stockmgmt.api.entity.User;
import com.stockmgmt.api.entity.dto.request.CreateStoreRequest;
import com.stockmgmt.api.entity.dto.response.StoreResponse;
import com.stockmgmt.api.entity.enumeration.PaymentStatus;
import com.stockmgmt.api.entity.enumeration.SubscriptionStatus;
import com.stockmgmt.api.repository.StoreRepository;
import com.stockmgmt.api.repository.SubscriptionPlanRepository;
import com.stockmgmt.api.repository.SubscriptionRepository;
import com.stockmgmt.api.repository.UserRepository;
import com.stockmgmt.api.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StoreServiceImpl implements StoreService {

    private final StoreRepository storeRepository;
    private final UserRepository userRepository;
    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final SubscriptionRepository subscriptionRepository;

    @Override
    @Transactional
    public Store createStore(Store store) {
        Store savedStore = storeRepository.save(store);

        SubscriptionPlan freeTier = subscriptionPlanRepository.findByName("Free Tier")
                .orElseThrow(() -> new RuntimeException("Free Tier plan not found"));

        Subscription subscription = Subscription.builder()
                .store(savedStore)
                .plan(freeTier)
                .status(SubscriptionStatus.ACTIVE)
                .paymentStatus(PaymentStatus.SUCCESS)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusMonths(1))
                .autoRenew(true)
                .build();

        subscriptionRepository.save(subscription);

        return savedStore;
    }

    @Override
    @Transactional
    public StoreResponse createStoreForUser(UUID userId, CreateStoreRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Store store = Store.builder()
                .name(request.getName())
                .logo(request.getLogo())
                .addressNumber(request.getAddressNumber())
                .addressStreet(request.getAddressStreet())
                .addressArea(request.getAddressArea())
                .addressLga(request.getAddressLga())
                .addressState(request.getAddressState())
                .addressCountry(request.getAddressCountry())
                .phoneNumber(request.getPhoneNumber())
                .contactInfo(request.getContactInfo())
                .operatingHours(request.getOperatingHours())
                .taxNumber(request.getTaxNumber())
                .currency(request.getCurrency() != null ? request.getCurrency() : "NGN")
                .owner(user)
                .active(true)
                .build();

        Store savedStore = createStore(store);

        return StoreResponse.builder()
                .id(savedStore.getId())
                .name(savedStore.getName())
                .logo(savedStore.getLogo())
                .addressNumber(savedStore.getAddressNumber())
                .addressStreet(savedStore.getAddressStreet())
                .addressArea(savedStore.getAddressArea())
                .addressLga(savedStore.getAddressLga())
                .addressState(savedStore.getAddressState())
                .addressCountry(savedStore.getAddressCountry())
                .phoneNumber(savedStore.getPhoneNumber())
                .contactInfo(savedStore.getContactInfo())
                .operatingHours(savedStore.getOperatingHours())
                .taxNumber(savedStore.getTaxNumber())
                .currency(savedStore.getCurrency())
                .active(savedStore.isActive())
                .build();
    }

    @Override
    public Store getStore(UUID storeId) {
        return storeRepository.findById(storeId)
                .orElseThrow(() -> new RuntimeException("Store not found"));
    }

    @Override
    public Store getStoreByOwner(UUID ownerId) {
        return storeRepository.findByOwner_Id(ownerId)
                .orElseThrow(() -> new RuntimeException("Store not found"));
    }
}
