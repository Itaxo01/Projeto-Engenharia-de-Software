package com.example.event;

import org.springframework.context.ApplicationEvent;

import com.example.model.Comentario;

public class ComentarioRepliedEvent extends ApplicationEvent{
	private final Comentario resposta;
    private final Comentario pai;

    public ComentarioRepliedEvent(Object source, Comentario resposta, Comentario pai) {
        super(source);
        this.resposta = resposta;
        this.pai = pai;
    }

    public Comentario getResposta() { return resposta; }
    public Comentario getPai() { return pai; }
}
