# Documentação das Classes de Model e Repository - JPA

## Visão Geral

Implementamos as classes de Model e Repository para `Disciplina` e `ArquivoComentario` utilizando JPA/Hibernate, mantendo a compatibilidade com a arquitetura existente do projeto.

## Estrutura Implementada

### Entidades (Models)

#### 1. Disciplina
- **Arquivo**: `src/main/java/com/example/model/Disciplina.java`
- **Tabela**: `disciplinas`
- **Relacionamentos**:
  - Many-to-Many com `Professor` (tabela junction: `professor_disciplina`)
  - One-to-Many com `Avaliacao`

**Campos principais**:
- `id` (Long) - Chave primária auto-incrementada
- `codigo` (String) - Código único da disciplina
- `nome` (String) - Nome da disciplina
- `descricao` (String) - Descrição opcional

#### 2. ArquivoComentario
- **Arquivo**: `src/main/java/com/example/model/ArquivoComentario.java`
- **Tabela**: `arquivos_comentario`
- **Relacionamentos**:
  - Many-to-One com `Comentario`

**Campos principais**:
- `id` (Long) - Chave primária auto-incrementada
- `nomeOriginal` (String) - Nome original do arquivo
- `nomeArquivo` (String) - Nome único no sistema de arquivos
- `tipoMime` (String) - Tipo MIME do arquivo
- `tamanho` (Long) - Tamanho em bytes
- `caminhoArquivo` (String) - Caminho completo do arquivo

#### 3. Entidades Atualizadas

**Professor**: Adicionado relacionamento Many-to-Many com Disciplina e One-to-Many com Avaliacao.

**Avaliacao**: Corrigido para usar `Disciplina` ao invés de `Subject`, adicionado relacionamento com `Comentario`.

**Comentario**: Adicionado relacionamento com `Avaliacao` e `ArquivoComentario`.

### Repositories

#### Padrão de Repository Mantido

Mantivemos o padrão existente onde cada entidade possui:
1. **Interface JPA Repository** (`*JpaRepository`): Interface Spring Data JPA com queries específicas
2. **Repository de Negócio** (`*Repository`): Classe que encapsula a lógica de negócio e usa o JPA Repository internamente

#### Repositories Implementados

1. **DisciplinaJpaRepository** + **DisciplinaRepository**
2. **ArquivoComentarioJpaRepository** + **ArquivoComentarioRepository**
3. **ProfessorJpaRepository** + **ProfessorRepository**
4. **AvaliacaoJpaRepository** + **AvaliacaoRepository**

## Funcionalidades Implementadas

### Disciplina Repository

```java
// Buscar disciplina por código
Optional<Disciplina> disciplina = disciplinaRepository.findByCodigo("ENG001");

// Buscar disciplinas por nome (busca parcial)
List<Disciplina> disciplinas = disciplinaRepository.findByNome("Engenharia");

// Buscar disciplinas de um professor
List<Disciplina> disciplinasProfessor = disciplinaRepository.findByProfessor("12345678901");

// Buscar com relacionamentos carregados
Optional<Disciplina> disciplinaComAvaliacoes = disciplinaRepository.findByIdWithAvaliacoes(1L);
```

### ArquivoComentario Repository

```java
// Buscar arquivos de um comentário
List<ArquivoComentario> arquivos = arquivoComentarioRepository.findByComentarioId(1L);

// Buscar arquivo por nome único
Optional<ArquivoComentario> arquivo = arquivoComentarioRepository.findByNomeArquivo("arquivo_123.pdf");

// Buscar arquivos por tipo MIME
List<ArquivoComentario> pdfs = arquivoComentarioRepository.findByTipoMime("application/pdf");

// Contar arquivos de um comentário
long totalArquivos = arquivoComentarioRepository.countByComentarioId(1L);
```

### Relacionamentos e Helper Methods

#### Disciplina + Professor
```java
// Adicionar professor à disciplina (bidirectional)
disciplina.addProfessor(professor);
disciplinaRepository.save(disciplina);

// Remover professor da disciplina
disciplina.removeProfessor(professor);
disciplinaRepository.save(disciplina);
```

#### Comentário + Arquivo
```java
// Adicionar arquivo ao comentário
ArquivoComentario arquivo = new ArquivoComentario(
    "documento.pdf", "arquivo_123.pdf", "application/pdf", 1024L, "/uploads/arquivo_123.pdf", comentario
);
comentario.addArquivo(arquivo);
comentarioRepository.save(comentario);
```

#### Avaliação + Comentário
```java
// Adicionar comentário à avaliação
Comentario comentario = new Comentario(user, "Excelente professor!");
avaliacao.addComentario(comentario);
avaliacaoRepository.save(avaliacao);
```

## Queries Avançadas Implementadas

### Cálculo de Médias
```java
// Média geral de um professor
Double media = avaliacaoRepository.calcularMediaProfessor("12345678901");

// Média de um professor em uma disciplina específica
Double mediaDisciplina = avaliacaoRepository.calcularMediaProfessorDisciplina("12345678901", "ENG001");
```

### Busca com Fetch Joins
```java
// Buscar disciplina com professores carregados
Optional<Disciplina> disciplinaComProfessores = disciplinaRepository.findByCodigoWithProfessores("ENG001");

// Buscar avaliação com comentários carregados
Optional<Avaliacao> avaliacaoComComentarios = avaliacaoRepository.findByIdWithComentarios(1L);
```

### Contadores e Estatísticas
```java
// Contar professores que lecionam uma disciplina
long totalProfessores = professorRepository.countByDisciplina("ENG001");

// Contar avaliações de um professor
long totalAvaliacoes = avaliacaoRepository.countByProfessor(professor);
```

## Configuração do Banco de Dados

As tabelas serão criadas automaticamente pelo JPA/Hibernate com base nas anotações das entidades:

- `disciplinas`
- `professores`
- `professor_disciplina` (tabela de junção)
- `avaliacoes`
- `comentarios`
- `arquivos_comentario`
- `users`

## Benefícios da Implementação

1. **Mantém Compatibilidade**: A interface de Repository mantém os métodos existentes
2. **Adiciona Funcionalidades**: Queries específicas e otimizadas
3. **Performance**: Lazy loading e fetch joins configurados apropriadamente
4. **Flexibilidade**: Fácil extensão com novas queries conforme necessário
5. **Integridade**: Relacionamentos bidirecionais com helper methods para manter consistência

## Próximos Passos

1. Testes unitários para os repositories
2. Testes de integração para verificar relacionamentos
3. Configuração de cache se necessário
4. Implementação de serviços que utilizem estes repositories
