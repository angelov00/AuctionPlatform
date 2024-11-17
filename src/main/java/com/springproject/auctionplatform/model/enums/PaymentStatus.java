package com.springproject.auctionplatform.model.enums;

public enum PaymentStatus {
    PENDING,        // Плащането е в изчакване
    COMPLETED,      // Плащането е успешно завършено
    FAILED,         // Плащането е неуспешно
    CANCELED,       // Плащането е отменено
    REFUNDED,       // Платената сума е възстановена
    CHARGEBACK,     // Плащането е оспорено и възстановено чрез chargeback
    IN_PROGRESS     // Плащането се обработва
}

