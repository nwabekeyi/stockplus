package com.stockmgmt.api.service;

import com.stockmgmt.api.entity.dto.request.CreateStockTransferRequest;
import com.stockmgmt.api.entity.dto.response.StockTransferResponse;

import java.util.List;
import java.util.UUID;

public interface StockTransferService {
    StockTransferResponse createTransfer(UUID fromStoreId, CreateStockTransferRequest request);
    StockTransferResponse receiveTransfer(UUID transferId);
    List<StockTransferResponse> getTransfers(UUID storeId);
    List<StockTransferResponse> getTransfersByStatus(com.stockmgmt.api.entity.enumeration.TransferStatus status);
}
