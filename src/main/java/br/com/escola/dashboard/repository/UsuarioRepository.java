package br.com.escola.dashboard.repository;

import br.com.escola.dashboard.entity.Usuario;
import br.com.escola.dashboard.enums.PerfilUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
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
}
