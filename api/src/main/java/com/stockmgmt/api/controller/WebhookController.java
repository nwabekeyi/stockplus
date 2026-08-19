package com.stockmgmt.api.controller;

import com.stockmgmt.api.config.AppProperties;
import com.stockmgmt.api.entity.Subscription;
import com.stockmgmt.api.entity.SubscriptionPlan;
import com.stockmgmt.api.entity.enumeration.PaymentStatus;
import com.stockmgmt.api.entity.enumeration.SubscriptionStatus;
import com.stockmgmt.api.exception.ResourceNotFoundException;
import com.stockmgmt.api.repository.SubscriptionPlanRepository;
import com.stockmgmt.api.repository.SubscriptionRepository;
import com.stockmgmt.api.repository.StoreRepository;
import com.stockmgmt.api.service.StoreService;
import com.stockmgmt.api.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/v1/webhooks")
@RequiredArgsConstructor
public class WebhookController {

    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionPlanRepository planRepository;
    private final StoreRepository storeRepository;
    private final AppProperties appProperties;
    private final StoreService storeService;

    @GetMapping("/whatsapp")
    public ResponseEntity<?> verifyWhatsAppWebhook(@RequestParam("hub.mode") String mode,
                                                    @RequestParam("hub.verify_token") String verifyToken,
                                                    @RequestParam("hub.challenge") String challenge) {
        if ("subscribe".equals(mode) && appProperties.getWebhook().getWhatsappVerifyToken().equals(verifyToken)) {
            return ResponseEntity.ok(challenge);
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid verify token");
    }

    @PostMapping("/whatsapp")
    public ResponseEntity<?> handleWhatsAppWebhook(@RequestBody Map<String, Object> payload,
                                                    @RequestHeader("X-Hub-Signature-256") String signature) {
        try {
            if (!isValidWhatsAppSignature(payload, signature)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid signature");
            }

            Map<String, Object> entry = ((List<Map<String, Object>>) payload.get("entry")).get(0);
            Map<String, Object> changes = ((List<Map<String, Object>>) entry.get("changes")).get(0);
            Map<String, Object> value = (Map<String, Object>) changes.get("value");

            if (value.containsKey("messages")) {
                List<Map<String, Object>> messages = (List<Map<String, Object>>) value.get("messages");
                for (Map<String, Object> message : messages) {
                    processWhatsAppMessage(message);
                }
            }

            return ResponseEntity.ok(Map.of("status", "received"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    private boolean isValidWhatsAppSignature(Map<String, Object> payload, String signature) {
        try {
            String payloadString = new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(payload);
            SecretKeySpec keySpec = new SecretKeySpec(appProperties.getWebhook().getWhatsappAppSecret().getBytes(), "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(keySpec);
            byte[] hash = mac.doFinal(payloadString.getBytes());
            String expectedSignature = "sha256=" + Base64.getEncoder().encodeToString(hash);
            return expectedSignature.equals(signature);
        } catch (Exception e) {
            return false;
        }
    }

    private void processWhatsAppMessage(Map<String, Object> message) {
        String from = (String) message.get("from");
        String type = (String) message.get("type");

        if ("text".equals(type)) {
            Map<String, Object> text = (Map<String, Object>) message.get("text");
            String body = (String) text.get("body");
        }
    }

    @PostMapping("/payments/webhook/paystack")
    public ResponseEntity<?> handlePaystackWebhook(@RequestBody String rawPayload,
                                                    @RequestHeader("x-paystack-signature") String signature) {
        if (!isValidPaystackSignature(rawPayload, signature)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Invalid Paystack signature"));
        }

        try {
            Map<String, Object> payload = new com.fasterxml.jackson.databind.ObjectMapper().readValue(rawPayload, Map.class);
            String event = (String) payload.get("event");
            Map<String, Object> data = (Map<String, Object>) payload.get("data");

            if (data == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Missing Paystack data"));
            }

            if ("subscription.create".equals(event) || "charge.success".equals(event) || "invoice.payment_failed".equals(event)) {
                String reference = (String) data.get("reference");
                String status = (String) data.get("status");
                Map<String, Object> metadata = (Map<String, Object>) data.get("metadata");
                String subscriptionCode = data.get("subscription_code") instanceof String code ? code : reference;
                String authorizationCode = data.get("authorization") instanceof Map<?, ?> authorization
                        ? Objects.toString(authorization.get("authorization_code"), null)
                        : null;

                Optional<Subscription> subOpt = Optional.empty();
                if (metadata != null && metadata.get("subscription_id") != null) {
                    subOpt = subscriptionRepository.findById(UUID.fromString(metadata.get("subscription_id").toString()));
                }
                if (subOpt.isEmpty() && reference != null) {
                    subOpt = subscriptionRepository.findByPaystackSubscriptionCode(reference);
                }

                if (subOpt.isPresent()) {
                    Subscription subscription = subOpt.get();
                    subscription.setPaystackSubscriptionCode(subscriptionCode);
                    if (authorizationCode != null) {
                        subscription.setPaystackAuthorizationCode(authorizationCode);
                    }

                    if ("success".equalsIgnoreCase(status) || "subscription.create".equals(event)) {
                        subscription.setStatus(SubscriptionStatus.ACTIVE);
                        subscription.setPaymentStatus(PaymentStatus.SUCCESS);
                    } else {
                        subscription.setStatus(SubscriptionStatus.EXPIRED);
                        subscription.setPaymentStatus(PaymentStatus.FAILED);
                    }
                    subscriptionRepository.save(subscription);
                }
            }

            return ResponseEntity.ok(Map.of("status", "success"));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    private boolean isValidPaystackSignature(String rawPayload, String signature) {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(appProperties.getPaystack().getSecretKey().getBytes(), "HmacSHA512");
            Mac mac = Mac.getInstance("HmacSHA512");
            mac.init(keySpec);
            byte[] hash = mac.doFinal(rawPayload.getBytes());
            StringBuilder expected = new StringBuilder();
            for (byte b : hash) {
                expected.append(String.format("%02x", b));
            }
            return expected.toString().equals(signature);
        } catch (Exception e) {
            return false;
        }
    }

    @GetMapping("/payments/webhook/paystack/verify")
    public ResponseEntity<?> verifyPaystackPayment(@RequestParam String reference) {
        String url = appProperties.getPaystack().getBaseUrl() + "/transaction/verify/" + reference;

        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.setBearerAuth(appProperties.getPaystack().getSecretKey());

        org.springframework.http.HttpEntity<Void> entity = new org.springframework.http.HttpEntity<>(headers);
        org.springframework.http.ResponseEntity<Map> response = new org.springframework.web.client.RestTemplate()
                .exchange(url, org.springframework.http.HttpMethod.GET, entity, Map.class);

        Map<String, Object> data = (Map<String, Object>) response.getBody().get("data");
        String status = (String) data.get("status");

        return ResponseEntity.ok(Map.of("status", status));
    }
}
