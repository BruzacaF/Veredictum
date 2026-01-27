package br.edu.ifpb.pweb2.veredictum.repository;

import br.edu.ifpb.pweb2.veredictum.model.Documento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DocumentoRepository extends JpaRepository<Documento, Long> {

    Optional<Documento> findFirstByProcessoId(Long processoId);

    boolean existsByProcessoId(Long processoId);
}
