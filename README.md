## Setup


### Prerequisites
- Java 21
- Maven 3.9+
- Docker (If running on production mode)

### Environment Variables

Create a `.env` file on root with default the values for the project
```bash
# Admin User Configuration (Required)
ADMIN_EMAIL=defaultadmin@admin.com
ADMIN_PASSWORD=admin123
ADMIN_NOME=admin
ADMIN_MATRICULA=000000
ADMIN_CURSO=admin

# Database Configuration
DATABASE_URL=jdbc:postgresql://localhost:5432/academic_system
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=postgres
```
**Note:** Load the variables before running with:
```bash
source .env
```

## Development Mode (H2 database)
Just run the application with
```bash
mvn spring-boot:run
```
The H2 database is set by default and should not require further configuration

## Production Mode (PostgreSQL)
### 1. Start PostgreSQL with Docker

```bash
# Start PostgreSQL container
sudo docker compose up -d postgres

# Verify it's running
sudo docker ps
```

### 2. Run the application in production profile

```bash
# Load environment variables
source .env

# Run with production profile
SPRING_PROFILES_ACTIVE=prod mvn spring-boot:run
```
#### Useful info for the postgres terminal 

```bash
# Connect to database via terminal
sudo docker exec -it academic-system-db psql -U postgres -d academic_system

# Common psql commands:
# \dt                    - List all tables
# \d table_name         - Describe table structure
# SELECT * FROM usuarios; - Query
# \q                    - Exit


# stops the PostgreSQL
sudo docker compose down
```


## Then open http://localhost:8080/

## or
### Check https://96acb17de87a.ngrok-free.app/ for the already hosted project 
#### (Link may have changed or be offline).

---
## Recent changes

Fiz as mudanças necessárias para migrar para o postgreSQL. O método para rodar está no run.

---
## TO DO (Iteração 2)
- [X] Implementar as páginas das disciplinas (localhost:8080/class/{CODE})


- [X] Fetch das disciplinas e professores da UFSC

- [X] Barra de pesquisa de disciplinas

- [X] Mapa curricular
	- O usuário guardará uma lista das matérias divididas por semestre. Cada matéria será uma tupla(String código, Boolean cursada, Boolean avaliada), e será usada para compor o mapa curricular.
	- Inicialmente o usuário irá inserir as matérias de cada semestre, posteriormente pode haver algum sistema que capture as matérias padrões do curso, porém ainda permite a adição de matérias.


## TO DO (Iteração 3)

- [X] O comentário precisa ter uma relação de muitos pra muitos com usuários para lidar com o upvote e downvote (O mesmo usuário não pode ter votos repetidos). Implementar os upvotes e downvotes do comentário

- [X] Implementar Arquivos do comentário (Incluso visualização dos mesmos no frontend). Limitado os tipos de arquivos aceitos para assegurar uma segurança ao usuário.

- [X] Implementar edição do comentário (Com arquivos)

- [X] Implementar deleção do comentário (Feito com soft delete e deleção periódica)

- [X] Implementar rating dos professores/disciplina

- [ ] Implementar resposta de comentário


- [ ] (Optional) Melhorar a resposta das ações de comentário e rating no frontend (Atualmente se recarrega a página para assegurar as mudanças)

- [ ] (Optional) Modificar a relação Disciplina Professor para uma entidade intermediária isolada. Com isso, implementar no scrapper o getter do semestre da relação, no frontend mostrar apenas os DisciplinaProfessor cuja distância de semestres em relação ao atual for menor igual a, por exemplo, cinco semestres.

- [ ] (Optional )Fetch dos cursos da UFSC (Necessário para montar o grafo das dependências entre disciplinas)
	- Tentar usar isso aqui: https://cagr.sistemas.ufsc.br/relatorios/curriculoCurso?curso=603, separar o id do curso, nome e criar o grafo que relaciona as disciplinas.


## TO DO DIAGRAMAS
- 2 Diagramas de sequência baseados em história de usuário
	- Fazer sobre:
		 Admin exclui comentário (HU 4) - Luam
		 interação com comentário (Upvote, DownVote, comentar) - Kauan