package br.com.escola.dashboard.repository;

import br.com.escola.dashboard.entity.UsuarioSegmento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UsuarioSegmentoRepository extends JpaRepository<UsuarioSegmento, Long> {

    List<UsuarioSegmento> findByUsuarioId(Long usuarioId);

    @Query("SELECT us.segmento.id FROM UsuarioSegmento us WHERE us.usuario.id = :usuarioId")
    List<Long> findSegmentoIdsByUsuarioId(@Param("usuarioId") Long usuarioId);

    @Modifying
    @Query("DELETE FROM UsuarioSegmento us WHERE us.usuario.id = :usuarioId AND us.segmento.id = :segmentoId")
    void deleteByUsuarioIdAndSegmentoId(@Param("usuarioId") Long usuarioId, @Param("segmentoId") Long segmentoId);

    boolean existsByUsuarioIdAndSegmentoId(Long usuarioId, Long segmentoId);
}
