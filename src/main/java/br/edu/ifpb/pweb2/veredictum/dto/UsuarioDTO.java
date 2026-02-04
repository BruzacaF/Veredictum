package br.edu.ifpb.pweb2.veredictum.dto;

import br.edu.ifpb.pweb2.veredictum.enums.RoleEnum;
import br.edu.ifpb.pweb2.veredictum.validation.MatriculaValida;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para criação e atualização de usuários (Aluno, Professor, Coordenador, Admin).
 * Contém validações customizadas incluindo a validação de matrícula.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDTO {
    
    private Long id;
    
    @NotBlank(message = "O nome é obrigatório")
    private String nome;
    
    @NotBlank(message = "O email é obrigatório")
    @Email(message = "Email inválido")
    private String email;
    
    private String telefone;
    
    private String senha;
    
    @NotNull(message = "A função é obrigatória")
    private RoleEnum role;
    
    /**
     * Matrícula é obrigatória para ALUNO, PROFESSOR e COORDENADOR.
     * A validação customizada @MatriculaValida será aplicada quando o valor não for nulo.
     */
    @MatriculaValida(message = "Matrícula inválida. Use o formato YYYYNNNN (ex: 20221001)")
    private String matricula;
    
    /**
     * Verifica se a role requer matrícula
     */
    public boolean requiresMatricula() {
        return role != null && 
               (role == RoleEnum.ALUNO || 
                role == RoleEnum.PROFESSOR || 
                role == RoleEnum.COORDENADOR);
    }
}
