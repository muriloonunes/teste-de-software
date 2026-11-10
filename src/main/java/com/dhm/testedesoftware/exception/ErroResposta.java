package com.dhm.testedesoftware.exception;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErroResposta(
        int status,
        String mensagem,
        LocalDateTime timestamp,
        Map<String, String> erros
) {
    public ErroResposta(int status, String mensagem, LocalDateTime timestamp) {
        this(status, mensagem, timestamp, null);
    }
}
