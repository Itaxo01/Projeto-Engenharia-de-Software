package com.example.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.example.event.ComentarioRepliedEvent;
import com.example.model.Comentario;
import com.example.model.Notificacao;
import com.example.repository.NotificacaoRepository;

@Component
public class ComentarioReplyListener {

    @Autowired
    private NotificacaoRepository notificacaoRepository;

    @EventListener
    public void onComentarioReplied(ComentarioRepliedEvent event) {
        Comentario resposta = event.getResposta();
        Comentario pai = event.getPai();
        
        // Create notification
        Notificacao notificacao = pai.getUsuario().generateAlert(resposta);
        notificacaoRepository.save(notificacao);
    }
}