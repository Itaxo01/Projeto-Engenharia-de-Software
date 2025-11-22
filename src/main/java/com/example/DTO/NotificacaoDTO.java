package com.example.DTO;

import java.time.Instant;

import com.example.model.Notificacao;

public record NotificacaoDTO (Long id, String texto, Long comentarioId, String disciplinaCodigo, Boolean isRead, Instant createdAt) {

    public static NotificacaoDTO from(Notificacao notificacao) {
        return new NotificacaoDTO(notificacao.getId(),
                                notificacao.getComentario().getTexto(),
                                notificacao.getComentario().getComentarioId(),
                                notificacao.getComentario().getDisciplina().getCodigo(),
                                notificacao.getRead(),
                                notificacao.getComentario().getCreatedAt());
    }
}