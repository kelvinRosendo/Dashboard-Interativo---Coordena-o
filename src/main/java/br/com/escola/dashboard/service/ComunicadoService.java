package br.com.escola.dashboard.service;

import br.com.escola.dashboard.entity.Comunicado;
import br.com.escola.dashboard.exception.ResourceNotFoundException;
import br.com.escola.dashboard.repository.ComunicadoRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class ComunicadoService {

    private final ComunicadoRepository comunicadoRepository;

    public ComunicadoService(ComunicadoRepository comunicadoRepository) {
        this.comunicadoRepository = comunicadoRepository;
    }

    public Comunicado criar(String titulo, String conteudo) {
        if (!StringUtils.hasText(titulo)) {
            throw new IllegalArgumentException("Titulo e obrigatorio.");
        }

        Comunicado comunicado = new Comunicado();
        comunicado.setTitulo(titulo.trim());
        comunicado.setConteudo(StringUtils.hasText(conteudo) ? conteudo.trim() : null);

        return comunicadoRepository.save(comunicado);
    }


    public List<Comunicado> listarTodos() {
        return comunicadoRepository.findAllByOrderByDataCriacaoDesc();
    }

    public void excluir(Long id) {
        if (id == null) {
            throw new ResourceNotFoundException("ID nao pode ser nulo");
        }

        comunicadoRepository.deleteById(id);
    }
}
