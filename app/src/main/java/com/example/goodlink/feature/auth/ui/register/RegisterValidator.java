package com.example.goodlink.feature.auth.ui.register;

public final class RegisterValidator {
    private RegisterValidator() {}

    public static ValidationResult validate(String nome, String email, String senha, boolean aceitouTermos) {
        if (isBlank(nome) || isBlank(email) || isBlank(senha)) {
            return ValidationResult.error("Por favor, preencha todos os campos");
        }
        if (senha.length() < 12) {
            return ValidationResult.error("A senha deve ter no mínimo 12 caracteres");
        }
        if (!aceitouTermos) {
            return ValidationResult.error("Por favor, aceite os termos e condições");
        }
        return ValidationResult.ok();
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    public static final class ValidationResult {
        public final boolean ok;
        public final String message;

        private ValidationResult(boolean ok, String message) {
            this.ok = ok;
            this.message = message;
        }

        public static ValidationResult ok() { return new ValidationResult(true, null); }
        public static ValidationResult error(String msg) { return new ValidationResult(false, msg); }
    }
}
