package com.example.goodlink.core.domain.usecase;

import com.example.goodlink.core.domain.settings.FontSettingsGateway;
import com.example.goodlink.core.domain.settings.FontSizePolicy;

public class GetFontSizeUseCase {
    private final FontSettingsGateway gateway;

    public GetFontSizeUseCase(FontSettingsGateway gateway) {
        this.gateway = gateway;
    }

    public float execute() {
        return FontSizePolicy.clamp(gateway.getFontSize());
    }
}
