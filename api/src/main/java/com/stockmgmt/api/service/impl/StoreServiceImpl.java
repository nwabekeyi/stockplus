package com.stockmgmt.api.service.impl;

import com.stockmgmt.api.entity.DashboardStats;
import com.stockmgmt.api.entity.Store;
import com.stockmgmt.api.entity.Subscription;
import com.stockmgmt.api.entity.SubscriptionPlan;
import com.stockmgmt.api.entity.User;
import com.stockmgmt.api.entity.dto.request.CreateStoreRequest;
import com.stockmgmt.api.entity.dto.response.StoreResponse;
import com.stockmgmt.api.entity.enumeration.PaymentStatus;
import com.stockmgmt.api.entity.enumeration.SubscriptionStatus;
import com.stockmgmt.api.repository.DashboardStatsRepository;
import com.stockmgmt.api.repository.StoreRepository;
import com.stockmgmt.api.repository.SubscriptionPlanRepository;
import com.stockmgmt.api.repository.SubscriptionRepository;
import com.stockmgmt.api.repository.UserRepository;
import com.stockmgmt.api.service.CloudinaryService;
import com.stockmgmt.api.service.StoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class StoreServiceImpl implements StoreService {

    private final StoreRepository storeRepository;
    private final UserRepository userRepository;
    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final DashboardStatsRepository dashboardStatsRepository;
    private final CloudinaryService cloudinaryService;

    @Override
    @Transactional
    public Store createStore(Store store) {
        Store savedStore = storeRepository.save(store);

        SubscriptionPlan freeTier = subscriptionPlanRepository.findByName("Starter")
                .orElseThrow(() -> new RuntimeException("Starter plan not found"));

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

        seedDefaultDashboardData(savedStore.getId());

        return savedStore;
    }

    private void seedDefaultDashboardData(UUID storeId) {
        try {
            DashboardStats defaultStats = DashboardStats.builder()
                    .storeId(storeId)
                    .totalProducts(0)
                    .lowStockCount(0)
                    .totalSalesToday(0)
                    .revenueToday(java.math.BigDecimal.ZERO)
                    .revenueThisMonth(java.math.BigDecimal.ZERO)
                    .totalSalesThisMonth(0)
                    .customerDebt(java.math.BigDecimal.ZERO)
                    .supplierDebt(java.math.BigDecimal.ZERO)
                    .expensesToday(java.math.BigDecimal.ZERO)
                    .totalCustomers(0)
                    .totalSuppliers(0)
                    .lastUpdated(java.time.Instant.now())
                    .build();
            dashboardStatsRepository.save(defaultStats);
        } catch (Exception e) {
            log.warn("Failed to seed default dashboard data for store {}: {}", storeId, e.getMessage());
        }
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
                .operatingDaysFrom(request.getOperatingDaysFrom())
                .operatingDaysTo(request.getOperatingDaysTo())
                .openTime(request.getOpenTime())
                .closeTime(request.getCloseTime())
                .taxNumber(request.getTaxNumber())
                .currency("NGN")
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
                .operatingDaysFrom(savedStore.getOperatingDaysFrom())
                .operatingDaysTo(savedStore.getOperatingDaysTo())
                .openTime(savedStore.getOpenTime())
                .closeTime(savedStore.getCloseTime())
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

    @Override
    @Transactional
    public StoreResponse updateStore(UUID storeId, CreateStoreRequest request) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new RuntimeException("Store not found"));

        String oldLogo = store.getLogo();
        if (request.getLogo() != null && !request.getLogo().equals(oldLogo)) {
            cloudinaryService.deleteImage(oldLogo);
        }

        store.setName(request.getName());
        store.setLogo(request.getLogo());
        store.setAddressNumber(request.getAddressNumber());
        store.setAddressStreet(request.getAddressStreet());
        store.setAddressArea(request.getAddressArea());
        store.setAddressLga(request.getAddressLga());
        store.setAddressState(request.getAddressState());
        store.setAddressCountry(request.getAddressCountry());
        store.setPhoneNumber(request.getPhoneNumber());
        store.setContactInfo(request.getContactInfo());
        store.setOperatingHours(request.getOperatingHours());
        store.setOperatingDaysFrom(request.getOperatingDaysFrom());
        store.setOperatingDaysTo(request.getOperatingDaysTo());
        store.setOpenTime(request.getOpenTime());
        store.setCloseTime(request.getCloseTime());
        store.setTaxNumber(request.getTaxNumber());
        store.setCurrency("NGN");

        Store savedStore = storeRepository.save(store);

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
                .operatingDaysFrom(savedStore.getOperatingDaysFrom())
                .operatingDaysTo(savedStore.getOperatingDaysTo())
                .openTime(savedStore.getOpenTime())
                .closeTime(savedStore.getCloseTime())
                .taxNumber(savedStore.getTaxNumber())
                .currency(savedStore.getCurrency())
                .active(savedStore.isActive())
                .build();
    }

    @Override
    @Transactional
    public void deleteStore(UUID storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new RuntimeException("Store not found"));

        cloudinaryService.deleteImage(store.getLogo());

        storeRepository.delete(store);
    }
}
