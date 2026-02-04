# Validação Customizada de Matrícula - Requisito REQNAOFUNC 10

## 📋 Descrição

Implementação de uma anotação específica customizada para validar matrículas de alunos e professores no sistema Veredictum, cumprindo integralmente o **REQNAOFUNC 10**.

## 🎯 Objetivo

Criar uma validação robusta que garante a integridade dos dados de matrícula, seguindo um formato padronizado e aplicando regras de negócio específicas.

## 📐 Formato da Matrícula

A matrícula deve seguir o formato: **YYYYNNNN**

- **YYYY**: Ano de ingresso (4 dígitos)
- **NNNN**: Número sequencial (4 dígitos)

**Exemplo**: `20221001` (Ano: 2022, Número: 0001)

## ✅ Regras de Validação

### 1. Formato Obrigatório
- ✔️ Exatamente 8 dígitos numéricos
- ❌ Não permite letras, caracteres especiais ou espaços

### 2. Validação do Ano
- ✔️ Ano mínimo: 2000 (configurável)
- ✔️ Ano máximo: Ano atual + 1 (permite matrícula antecipada)
- ❌ Anos fora deste intervalo são rejeitados

### 3. Validação do Número Sequencial
- ✔️ Deve estar entre 0001 e 9999
- ❌ Não permite 0000

### 4. Obrigatoriedade
- ✔️ Obrigatória para: ALUNO, PROFESSOR, COORDENADOR
- ⚪ Opcional para: ADMIN

## 🏗️ Arquitetura da Implementação

### 1. Anotação Customizada
**Arquivo**: `MatriculaValida.java`
```java
@MatriculaValida(message = "Matrícula inválida. Use o formato YYYYNNNN (ex: 20221001)")
```

**Características**:
- Pode ser aplicada em campos (`@Target(ElementType.FIELD)`)
- Pode ser aplicada em parâmetros (`@Target(ElementType.PARAMETER)`)
- Parâmetros configuráveis: `anoMinimo`, `anoMaximo`
- Mensagem de erro customizável

### 2. Validador (MatriculaValidaValidator.java)
**Implementação**: `ConstraintValidator<MatriculaValida, String>`

**Fluxo de Validação**:
```
1. Verifica se é nulo/vazio → ✅ (permite, para usar com @NotBlank)
2. Remove espaços em branco
3. Valida formato (8 dígitos numéricos)
4. Extrai ano (primeiros 4 dígitos)
5. Extrai número sequencial (últimos 4 dígitos)
6. Valida ano dentro do intervalo
7. Valida número sequencial (1-9999)
```

**Mensagens de Erro Contextuais**:
- "A matrícula deve conter exatamente 8 dígitos numéricos"
- "O ano da matrícula deve estar entre 2000 e [ano atual + 1]"
- "O número sequencial da matrícula deve estar entre 0001 e 9999"

### 3. Aplicação nas Entidades

**Aluno.java**:
```java
@NotBlank(message = "A matrícula é obrigatória")
@MatriculaValida(message = "Matrícula inválida. Use o formato YYYYNNNN (ex: 20221001)")
private String matricula;
```

**Professor.java**:
```java
@NotBlank(message = "A matrícula é obrigatória")
@MatriculaValida(message = "Matrícula inválida. Use o formato YYYYNNNN (ex: 20241001)")
private String matricula;
```

### 4. Validação no Service Layer
**UsuarioService.java**:
- Injeção do `Validator` do Bean Validation
- Validação manual antes de salvar/atualizar
- Lança `IllegalArgumentException` com mensagem específica em caso de erro

```java
Set<ConstraintViolation<Aluno>> violations = validator.validate(aluno);
if (!violations.isEmpty()) {
    String errorMsg = violations.iterator().next().getMessage();
    throw new IllegalArgumentException(errorMsg);
}
```

### 5. Validação no Controller
**AdminController.java**:
- Uso de `@Valid` no DTO
- Captura de `BindingResult` para erros de validação
- Validação adicional de obrigatoriedade baseada na role
- Retorno de mensagens de erro amigáveis via `RedirectAttributes`

### 6. DTO para Formulários
**UsuarioDTO.java**:
```java
@MatriculaValida(message = "Matrícula inválida. Use o formato YYYYNNNN (ex: 20221001)")
private String matricula;

public boolean requiresMatricula() {
    return role == RoleEnum.ALUNO || role == RoleEnum.PROFESSOR || role == RoleEnum.COORDENADOR;
}
```

## 🎨 Feedback Visual no Front-end

### 1. Validação em Tempo Real (JavaScript)
**Arquivo**: `usuarios.html`

**Funcionalidades**:
- ✨ Validação enquanto o usuário digita (`oninput`)
- ✨ Validação ao sair do campo (`onblur`)
- ✨ Feedback visual instantâneo (classes Bootstrap `is-valid` / `is-invalid`)
- ✨ Mensagens de erro contextuais
- ✨ Restrição de caracteres (apenas números, máximo 8)
- ✨ Validação antes do submit do formulário

**Função Principal**:
```javascript
function validarMatricula(input) {
    // Remove caracteres não numéricos
    // Valida formato (8 dígitos)
    // Valida ano (2000 até ano atual + 1)
    // Valida número sequencial (0001-9999)
    // Aplica classes CSS de feedback
}
```

### 2. Interface do Usuário
**Campo de Matrícula**:
```html
<input type="text" 
       name="matricula" 
       class="form-control" 
       placeholder="Ex: 20221001 (YYYYNNNN)"
       maxlength="8"
       oninput="validarMatricula(this)"
       onblur="validarMatricula(this)">
<div class="invalid-feedback">
    Matrícula inválida. Use o formato YYYYNNNN (ex: 20221001)
</div>
<small class="text-muted">
    Formato: Ano (4 dígitos) + Número (4 dígitos)
</small>
```

### 3. Feedback de Erro do Backend
**Via Flash Messages**:
- ✅ Sucesso: "Usuário criado com sucesso!"
- ❌ Erro de validação: "Erro de validação: [mensagem específica]"
- ❌ Email duplicado: "Email já cadastrado!"

## 🧪 Testes Unitários

**Arquivo**: `MatriculaValidaValidatorTest.java`

**Cobertura de Testes**:
- ✅ Matrículas válidas (vários formatos)
- ✅ Matrícula com ano atual e ano atual + 1
- ✅ Matrícula vazia/nula (permite por ser opcional)
- ❌ Matrícula com menos/mais de 8 dígitos
- ❌ Matrícula com caracteres não numéricos
- ❌ Ano inválido (< 2000 ou > ano atual + 1)
- ❌ Número sequencial zero ou > 9999
- ✅ Números sequenciais limites (0001, 9999)
- ✅ Matrícula com espaços (removidos automaticamente)

**Execução**:
```bash
mvn test -Dtest=MatriculaValidaValidatorTest
```

## 📊 Fluxo Completo de Validação

```
┌─────────────────────┐
│   Usuário digita    │
│    no formulário    │
└──────────┬──────────┘
           │
           ▼
┌─────────────────────┐
│  Validação JS       │◄─── Feedback visual imediato
│  (Front-end)        │     ✓ Formato
└──────────┬──────────┘     ✓ Ano
           │                ✓ Número sequencial
           ▼
┌─────────────────────┐
│   Submit form       │
└──────────┬──────────┘
           │
           ▼
┌─────────────────────┐
│  @Valid DTO         │◄─── Validação automática
│  (Controller)       │     pelo Spring
└──────────┬──────────┘
           │
           ▼
┌─────────────────────┐
│  Validação manual   │◄─── Validator.validate()
│  (Service)          │     nas entidades
└──────────┬──────────┘
           │
           ▼
┌─────────────────────┐
│  Salvamento BD      │◄─── @MatriculaValida
│  (Repository)       │     ativa antes de salvar
└─────────────────────┘
```

## 📦 Arquivos Modificados/Criados

### Criados
1. ✨ `validation/MatriculaValida.java` - Anotação customizada
2. ✨ `validation/MatriculaValidaValidator.java` - Implementação do validador
3. ✨ `dto/UsuarioDTO.java` - DTO para formulários
4. ✨ `validation/MatriculaValidaValidatorTest.java` - Testes unitários
5. ✨ `VALIDACAO_MATRICULA.md` - Esta documentação

### Modificados
1. 🔧 `model/Aluno.java` - Aplicação da anotação
2. 🔧 `model/Professor.java` - Aplicação da anotação
3. 🔧 `service/UsuarioService.java` - Validação manual
4. 🔧 `controller/AdminController.java` - Uso do DTO e validação
5. 🔧 `templates/admin/fragments/usuarios.html` - Validação JavaScript

## 💡 Exemplos de Uso

### Matrículas Válidas ✅
- `20221001` - Aluno de 2022, número 0001
- `20241234` - Aluno de 2024, número 1234
- `20005678` - Aluno de 2000, número 5678
- `20279999` - Aluno de 2027 (ano atual + 1), número 9999

### Matrículas Inválidas ❌
- `2022001` - Apenas 7 dígitos (falta um)
- `202210011` - 9 dígitos (sobra um)
- `2022ABC1` - Contém letras
- `19991001` - Ano anterior a 2000
- `20300001` - Ano muito no futuro
- `20220000` - Número sequencial zero

## 🔍 Como Testar

### 1. Via Interface Web
1. Acesse: http://localhost:8080/admin
2. Login: `admin@ifpb.edu.br` / `admin123`
3. Clique em "Novo Usuário"
4. Selecione role: ALUNO, PROFESSOR ou COORDENADOR
5. Digite uma matrícula no campo
6. Observe o feedback visual em tempo real
7. Tente submeter com valores inválidos

### 2. Via Testes Unitários
```bash
mvn test -Dtest=MatriculaValidaValidatorTest
```

### 3. Testes Manuais Sugeridos
- [ ] Criar aluno com matrícula válida
- [ ] Tentar criar aluno com matrícula inválida (< 8 dígitos)
- [ ] Tentar criar aluno com letras na matrícula
- [ ] Tentar criar aluno com ano inválido
- [ ] Tentar criar aluno sem matrícula (deve ser obrigatória)
- [ ] Criar admin sem matrícula (deve permitir)
- [ ] Editar matrícula existente para valor válido
- [ ] Editar matrícula existente para valor inválido

## 🎓 Benefícios da Implementação

1. **Integridade de Dados**: Garante formato consistente no banco
2. **Experiência do Usuário**: Feedback imediato durante digitação
3. **Manutenibilidade**: Validação centralizada e reutilizável
4. **Testabilidade**: Fácil de testar unitariamente
5. **Documentação**: Código autodocumentado com anotações
6. **Flexibilidade**: Parâmetros configuráveis (anoMinimo, anoMaximo)
7. **Segurança**: Previne injeção de dados malformados

## 🔄 Compatibilidade

- ✅ Spring Boot 3.x
- ✅ Jakarta Validation API 3.0+
- ✅ Hibernate Validator 8.0+
- ✅ Bootstrap 5.x (para feedback visual)
- ✅ Thymeleaf 3.x

## 📚 Referências

- [Jakarta Bean Validation Specification](https://beanvalidation.org/3.0/)
- [Hibernate Validator Documentation](https://hibernate.org/validator/)
- [Spring Validation Guide](https://spring.io/guides/gs/validating-form-input/)
- [Bootstrap Form Validation](https://getbootstrap.com/docs/5.3/forms/validation/)

---

**Requisito Atendido**: ✅ REQNAOFUNC 10 - "Utilizou uma anotação específica com regra própria para validar a matrícula"

**Status**: ✅ Implementado e testado integralmente, do back-end ao front-end com feedback visual.
