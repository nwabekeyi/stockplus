package com.stockmgmt.api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {
    private Jwt jwt;
    private Paystack paystack;
    private WhatsApp whatsapp;
    private Sms sms;
    private Webhook webhook;

    public Jwt getJwt() {
        return jwt;
    }

    public void setJwt(Jwt jwt) {
        this.jwt = jwt;
    }

    public Paystack getPaystack() {
        return paystack;
    }

    public void setPaystack(Paystack paystack) {
        this.paystack = paystack;
    }

    public WhatsApp getWhatsapp() {
        return whatsapp;
    }

    public void setWhatsapp(WhatsApp whatsapp) {
        this.whatsapp = whatsapp;
    }

    public Sms getSms() {
        return sms;
    }

    public void setSms(Sms sms) {
        this.sms = sms;
    }

    public Webhook getWebhook() {
        return webhook;
    }

    public void setWebhook(Webhook webhook) {
        this.webhook = webhook;
    }

    public static class Jwt {
        private String secret;
        private long accessExpiration;
        private long refreshExpiration;

        public String getSecret() {
            return secret;
        }

        public void setSecret(String secret) {
            this.secret = secret;
        }

        public long getAccessExpiration() {
            return accessExpiration;
        }

        public void setAccessExpiration(long accessExpiration) {
            this.accessExpiration = accessExpiration;
        }

        public long getRefreshExpiration() {
            return refreshExpiration;
        }

        public void setRefreshExpiration(long refreshExpiration) {
            this.refreshExpiration = refreshExpiration;
        }
    }

    public static class Paystack {
        private String secretKey;
        private String publicKey;
        private String baseUrl;

        public String getSecretKey() {
            return secretKey;
        }

        public void setSecretKey(String secretKey) {
            this.secretKey = secretKey;
        }

        public String getPublicKey() {
            return publicKey;
        }

        public void setPublicKey(String publicKey) {
            this.publicKey = publicKey;
        }

        public String getBaseUrl() {
            return baseUrl;
        }

        public void setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
        }
    }

    public static class WhatsApp {
        private String apiKey;
        private String phoneNumberId;
        private String baseUrl;

        public String getApiKey() {
            return apiKey;
        }

        public void setApiKey(String apiKey) {
            this.apiKey = apiKey;
        }

        public String getPhoneNumberId() {
            return phoneNumberId;
        }

        public void setPhoneNumberId(String phoneNumberId) {
            this.phoneNumberId = phoneNumberId;
        }

        public String getBaseUrl() {
            return baseUrl;
        }

        public void setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
        }
    }

    public static class Sms {
        private String apiKey;
        private String senderId;
        private String baseUrl;

        public String getApiKey() {
            return apiKey;
        }

        public void setApiKey(String apiKey) {
            this.apiKey = apiKey;
        }

        public String getSenderId() {
            return senderId;
        }

        public void setSenderId(String senderId) {
            this.senderId = senderId;
        }

        public String getBaseUrl() {
            return baseUrl;
        }

        public void setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
        }
    }

    public static class Webhook {
        private String whatsappVerifyToken;
        private String whatsappAppSecret;

        public String getWhatsappVerifyToken() {
            return whatsappVerifyToken;
        }

        public void setWhatsappVerifyToken(String whatsappVerifyToken) {
            this.whatsappVerifyToken = whatsappVerifyToken;
        }

        public String getWhatsappAppSecret() {
            return whatsappAppSecret;
        }

        public void setWhatsappAppSecret(String whatsappAppSecret) {
            this.whatsappAppSecret = whatsappAppSecret;
        }
    }
}
