package com.inventario.service;

import com.inventario.model.LogSistema;
import com.inventario.repository.LogSistemaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LogService {

    @Autowired
    private LogSistemaRepository logRepository;

    public void registrarLog(String usuario, String acao, String detalhe) {
        LogSistema log = LogSistema.builder()
                .usuario(usuario != null ? usuario : "SISTEMA")
                .acao(acao)
                .detalhe(detalhe)
                .dataHora(LocalDateTime.now())
                .build();
        logRepository.save(log);
    }

    public List<LogSistema> listarTodos() {
        return logRepository.findAllByOrderByDataHoraDesc();
    }
}