package com.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.repository.ScrapperStatusRepository;
import com.example.model.ScrapperStatus;

@Service
public class ScrapperStatusService {
    @Autowired
    private final ScrapperStatusRepository scrapperStatusRepository;

    public ScrapperStatusService(ScrapperStatusRepository scrapperStatusRepository) {
        this.scrapperStatusRepository = scrapperStatusRepository;
    }

    public ScrapperStatus getUltimoStatus() {
        ScrapperStatus status =scrapperStatusRepository.ultimoStatus();
        if (status == null) {
            status = new ScrapperStatus(); // tabela vazia, não vai ocorrer a não ser que seja resetado
        }
        return status;
    }
    public ScrapperStatus salvar(ScrapperStatus scrapperStatus) {
        return scrapperStatusRepository.salvar(scrapperStatus);
    }

    public void marcarInicioExecucao(String administrador) {
        ScrapperStatus status = new ScrapperStatus();
        status.marcarInicioExecucao(administrador);
        scrapperStatusRepository.salvar(status);
    }

    public void marcarFimExecucao(boolean sucesso, int disciplinasCapturadas, int professoresCapturados, String erro) {
        ScrapperStatus status = getUltimoStatus();
        status.marcarFimExecucao(sucesso, disciplinasCapturadas, professoresCapturados, erro);
        scrapperStatusRepository.salvar(status);
    }

    public void setExecucao(boolean executando) {
        ScrapperStatus status = getUltimoStatus();
        status.setExecutando(executando);
        scrapperStatusRepository.salvar(status);
    }
}
