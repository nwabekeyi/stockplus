package com.stockmgmt.api.config;

import com.stockmgmt.api.entity.dto.response.ApiResponse;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalResponseWrapper implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {
        if (body == null) {
            return ApiResponse.builder()
                    .message("Success")
                    .error(List.of())
                    .data(null)
                    .build();
        }

        if (body instanceof ApiResponse) {
            return body;
        }

        String path = request.getURI().getPath();
        String message = "Success";
        if (path.contains("/login")) message = "Login successful";
        else if (path.contains("/register")) message = "Registration successful";
        else if (path.contains("/refresh")) message = "Token refreshed";
        else if (path.contains("/logout")) message = "Logged out successfully";
        else if (path.contains("/stores") && request.getMethod().toString().equals("POST")) message = "Store created successfully";
        else if (path.contains("/me")) message = "User fetched successfully";

        return ApiResponse.builder()
                .message(message)
                .error(List.of())
                .data(body)
                .build();
    }
}
