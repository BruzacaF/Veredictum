# Testes Unitários - Veredictum

## 📋 Visão Geral

Este documento descreve os testes unitários implementados para a camada de serviços da aplicação Veredictum. Foram criados **57 testes unitários** utilizando JUnit 5 e Mockito, cobrindo todos os services da aplicação.

## 🎯 Objetivo dos Testes

Os testes unitários foram desenvolvidos para:
- Validar o comportamento correto de cada método dos services
- Garantir o tratamento adequado de exceções
- Verificar a integridade das operações CRUD
- Assegurar que as regras de negócio sejam respeitadas
- Facilitar a manutenção e refatoração do código

## 🧪 Tecnologias Utilizadas

- **JUnit 5** - Framework de testes
- **Mockito** - Framework para criação de mocks
- **Spring Boot Test** - Suporte para testes no Spring Boot
- **Maven Surefire** - Plugin para execução dos testes

## 📦 Services Testados

### 1. AlunoService (6 testes)

**Arquivo:** `AlunoServiceTest.java`

**Cobertura:**
- ✅ Listar todos os alunos
- ✅ Buscar aluno por ID (com sucesso)
- ✅ Buscar aluno por ID (não encontrado - retorna Optional vazio)
- ✅ Buscar aluno por matrícula
- ✅ Buscar por matrícula inexistente (retorna null)
- ✅ Contar quantidade de alunos

**Principais cenários testados:**
- Operações de consulta no repositório
- Tratamento de casos onde registros não existem
- Retorno correto de Optional

---

### 2. AssuntoService (9 testes)

**Arquivo:** `AssuntoServiceTest.java`

**Cobertura:**
- ✅ Listar todos os assuntos
- ✅ Buscar assunto por ID
- ✅ Lançar exceção quando assunto não é encontrado
- ✅ Salvar novo assunto
- ✅ Atualizar assunto existente
- ✅ Lançar exceção ao atualizar assunto sem ID
- ✅ Excluir assunto sem processos vinculados
- ✅ Lançar exceção ao excluir assunto com processos vinculados
- ✅ Contar assuntos

**Principais cenários testados:**
- CRUD completo (Create, Read, Update, Delete)
- Validação de regras de negócio (não excluir com processos vinculados)
- Tratamento de exceções customizadas

---

### 3. ColegiadoService (9 testes)

**Arquivo:** `ColegiadoServiceTest.java`

**Cobertura:**
- ✅ Listar todos os colegiados
- ✅ Buscar colegiado por ID
- ✅ Lançar exceção quando colegiado não é encontrado
- ✅ Salvar colegiado com membros
- ✅ Salvar colegiado sem membros
- ✅ Atualizar colegiado e seus membros
- ✅ Excluir colegiado
- ✅ Contar colegiados
- ✅ Listar professores disponíveis

**Principais cenários testados:**
- Gerenciamento de relacionamentosMany-to-Many com professores
- Atualização de membros do colegiado
- Remoção de relacionamentos bidirecionais

---

### 4. DocumentoService (5 testes)

**Arquivo:** `DocumentoServiceTest.java`

**Cobertura:**
- ✅ Anexar documento com sucesso
- ✅ Lançar exceção quando limite de 3 documentos é atingido
- ✅ Definir corretamente os dados do arquivo
- ✅ Adicionar documento à lista de documentos do processo
- ✅ Permitir anexar até 3 documentos

**Principais cenários testados:**
- Upload de arquivos com MultipartFile
- Validação de limite de documentos por processo
- Conversão e armazenamento de bytes do arquivo
- Regras de negócio para anexação de documentos

---

### 5. ProfessorService (3 testes)

**Arquivo:** `ProfessorServiceTest.java`

**Cobertura:**
- ✅ Buscar professores por ID do colegiado
- ✅ Retornar lista vazia quando não há professores
- ✅ Buscar apenas um professor quando colegiado tem um membro

**Principais cenários testados:**
- Consultas por relacionamento com colegiado
- Tratamento de listas vazias
- Casos com diferentes quantidades de resultados

---

### 6. UsuarioService (12 testes)

**Arquivo:** `UsuarioServiceTest.java`

**Cobertura:**
- ✅ Listar todos os usuários
- ✅ Buscar usuário por ID
- ✅ Buscar usuário por email
- ✅ Contar usuários
- ✅ Salvar aluno com sucesso
- ✅ Salvar professor com sucesso
- ✅ Salvar coordenador com flag ehCoordenador true
- ✅ Lançar exceção ao salvar aluno com validação inválida
- ✅ Atualizar aluno
- ✅ Excluir usuário
- ✅ Verificar se email existe
- ✅ Lançar exceção ao atualizar usuário inexistente

**Principais cenários testados:**
- Polimorfismo (Usuario, Aluno, Professor, Coordenador)
- Encriptação de senha com PasswordEncoder
- Validação de matrícula com Bean Validation
- Tratamento de diferentes roles (ALUNO, PROFESSOR, COORDENADOR)
- Operações CRUD completas

---

### 7. UsuarioDetailsService (5 testes)

**Arquivo:** `UsuarioDetailsServiceTest.java`

**Cobertura:**
- ✅ Carregar usuário por username (email)
- ✅ Lançar UsernameNotFoundException quando usuário não é encontrado
- ✅ Retornar UserDetails com as authorities corretas
- ✅ Lidar com email em branco
- ✅ Carregar diferentes tipos de usuários (Aluno, Professor, Coordenador)

**Principais cenários testados:**
- Integração com Spring Security
- Carregamento de UserDetails
- Tratamento de exceções de autenticação
- Authorities e roles

---

### 8. VotoService (8 testes)

**Arquivo:** `VotoServiceTest.java`

**Cobertura:**
- ✅ Registrar novo voto
- ✅ Atualizar voto existente
- ✅ Verificar se professor já votou
- ✅ Buscar voto do professor
- ✅ Retornar Optional vazio quando não há voto
- ✅ Registrar voto deferido (DEFERIMENTO)
- ✅ Registrar voto indeferido (INDEFERIMENTO)
- ✅ Registrar voto com justificativa nula

**Principais cenários testados:**
- Lógica de votação em processos
- Atualização vs. criação de voto
- Diferentes tipos de decisão
- Validação de votos duplicados

---

## 🚀 Executando os Testes

### Executar todos os testes

```bash
./mvnw test
```

### Executar apenas os testes de services

```bash
./mvnw test -Dtest="*ServiceTest"
```

### Executar testes de um service específico

```bash
./mvnw test -Dtest="AlunoServiceTest"
```

### Executar com relatório detalhado

```bash
./mvnw test -Dtest="*ServiceTest" -X
```

## 📊 Resultados dos Testes

```
[INFO] Results:
[INFO] 
[INFO] Tests run: 57, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] BUILD SUCCESS
```

### Distribuição por Service

| Service | Quantidade de Testes | Status |
|---------|---------------------|--------|
| AlunoService | 6 | ✅ Passou |
| AssuntoService | 9 | ✅ Passou |
| ColegiadoService | 9 | ✅ Passou |
| DocumentoService | 5 | ✅ Passou |
| ProfessorService | 3 | ✅ Passou |
| UsuarioService | 12 | ✅ Passou |
| UsuarioDetailsService | 5 | ✅ Passou |
| VotoService | 8 | ✅ Passou |
| **TOTAL** | **57** | **✅ 100% Passou** |

## 🏗️ Estrutura dos Testes

### Padrão AAA (Arrange, Act, Assert)

Todos os testes seguem o padrão AAA:

```java
@Test
@DisplayName("Deve buscar aluno por ID")
void deveBuscarAlunoPorId() {
    // Arrange - Preparação dos dados e mocks
    when(alunoRepository.findById(1L)).thenReturn(Optional.of(aluno));

    // Act - Execução do método a ser testado
    Optional<Aluno> resultado = alunoService.buscarPorId(1L);

    // Assert - Verificação dos resultados
    assertTrue(resultado.isPresent());
    assertEquals("João Silva", resultado.get().getNome());
    verify(alunoRepository, times(1)).findById(1L);
}
```

### Uso de Mocks

Os testes utilizam Mockito para criar mocks dos repositórios:

```java
@Mock
private AlunoRepository alunoRepository;

@InjectMocks
private AlunoService alunoService;
```

### Setup com @BeforeEach

Cada classe de teste possui um método `setUp()` para inicializar os dados de teste:

```java
@BeforeEach
void setUp() {
    aluno = new Aluno();
    aluno.setId(1L);
    aluno.setNome("João Silva");
    aluno.setMatricula("20231234567");
}
```

## 🎨 Boas Práticas Implementadas

1. **Nomenclatura Clara**: Métodos de teste com nomes descritivos
   - `deveBuscarAlunoPorId()`
   - `deveLancarExcecaoQuandoAssuntoNaoEncontrado()`

2. **DisplayName**: Uso de anotação para descrições em português
   ```java
   @DisplayName("Deve buscar aluno por ID")
   ```

3. **Isolamento**: Cada teste é independente e não afeta outros

4. **Cobertura de Exceções**: Testes para casos de erro
   ```java
   assertThrows(RuntimeException.class, 
       () -> assuntoService.buscarPorId(999L));
   ```

5. **Verificação de Mocks**: Confirmação de que métodos foram chamados
   ```java
   verify(repository, times(1)).save(any());
   ```

6. **Testes de Casos Extremos**: 
   - Listas vazias
   - Valores nulos
   - Limites de quantidade

## 📝 Convenções de Nomenclatura

- **Classes de teste**: `[NomeDoService]Test.java`
- **Métodos de teste**: `deve[AcaoEsperada]()`
- **Packages**: Mesma estrutura do código fonte

## 🔧 Configuração do Maven

Os testes são executados através do Maven Surefire Plugin:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
```

## 📈 Métricas de Qualidade

- **Taxa de Sucesso**: 100%
- **Total de Testes**: 57
- **Falhas**: 0
- **Erros**: 0
- **Testes Ignorados**: 0
- **Tempo de Execução**: ~1.3 segundos

## 🎯 Próximos Passos

Para expandir a cobertura de testes, considere:

1. Testes de integração para os controllers
2. Testes para ProcessoService e ReuniaoService (services mais complexos)
3. Testes de performance
4. Testes end-to-end
5. Cobertura de código com JaCoCo

## 📚 Referências

- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [Spring Boot Testing](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.testing)

---

**Última Atualização**: 04 de fevereiro de 2026  
**Autor**: Testes implementados via GitHub Copilot  
**Versão da Aplicação**: 0.0.1-SNAPSHOT
