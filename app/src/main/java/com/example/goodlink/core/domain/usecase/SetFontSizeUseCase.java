package com.example.goodlink.core.domain.usecase;

import com.example.goodlink.core.domain.settings.FontSettingsGateway;
import com.example.goodlink.core.domain.settings.FontSizePolicy;

public class SetFontSizeUseCase {
    private final FontSettingsGateway gateway;

    public SetFontSizeUseCase(FontSettingsGateway gateway) {
        this.gateway = gateway;
    }

    public void execute(float size) {
        gateway.setFontSize(FontSizePolicy.clamp(size));
    }
}
