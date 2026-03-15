package com.example.goodlink.core.domain.model.rating;

public enum RatingValue {
    HORRIVEL(1),
    RUIM(2),
    REGULAR(3),
    BOM(4),
    EXCELENTE(5);

    private final int score;
    RatingValue(int score) { this.score = score; }
    public int score() { return score; }

    public static RatingValue fromSpinnerIndex(int index) {
        // seu spinner hoje tem 5 opções (sem placeholder).
        // index 0 => PESIMO ... index 4 => EXCELENTE
        return switch (index) {
            case 0 -> HORRIVEL;
            case 1 -> RUIM;
            case 2 -> REGULAR;
            case 3 -> BOM;
            case 4 -> EXCELENTE;
            default -> throw new IllegalArgumentException("Índice inválido: " + index);
        };
    }
}