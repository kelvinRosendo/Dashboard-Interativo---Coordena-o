package br.com.escola.dashboard.repository;

import br.com.escola.dashboard.entity.Usuario;
import br.com.escola.dashboard.enums.PerfilUsuario;
import br.com.escola.dashboard.enums.StatusUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    boolean existsByEmail(String email);

    List<Usuario> findByPerfil(PerfilUsuario perfil);

    List<Usuario> findByAtivoTrue();

    List<Usuario> findByPerfilAndAtivoTrue(PerfilUsuario perfil);

    List<Usuario> findByStatusOrderByDataCriacaoDesc(StatusUsuario status);

    List<Usuario> findAllByOrderByDataCriacaoDesc();

    @Query("SELECT u FROM Usuario u WHERE " +
           "(:termo IS NULL OR :termo = '' OR " +
           "LOWER(u.nome) LIKE LOWER(CONCAT('%', :termo, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :termo, '%'))) " +
           "AND (:perfil IS NULL OR u.perfil = :perfil) " +
           "AND (:status IS NULL OR u.status = :status) " +
           "ORDER BY u.dataCriacao DESC")
    List<Usuario> buscarComFiltros(
            @Param("termo") String termo,
            @Param("perfil") PerfilUsuario perfil,
            @Param("status") StatusUsuario status);

    long countByStatus(StatusUsuario status);

    long countByPerfil(PerfilUsuario perfil);
}
