## Run
```bash
mvn spring-boot:run
```
## or for debug 
```
mvn spring-boot:run -Dspring-boot.run.arguments="--logging.level.com.example.scrapper=DEBUG"
```

## Then open http://localhost:8080/

## or
### Check https://96acb17de87a.ngrok-free.app/ for the already hosted project 
#### (Link may have changed or be offline).

---
## Recent changes

Scrapper já funciona aparentemente sempre. A funcionalidade está implementada no front-end na aba de administrador, e o status do scrapping fica guardado em uma tabela, com o último status sendo exibido no front-end.

Fiz a barra de buscas meio bosta, o matching é quase exato e ainda diferencia letras especiais como acentos ou c de ç, mas até que funciona bem.

---
## TO DO (Iteração 2)
- [X] Implementar as páginas das disciplinas (localhost:8080/disciplina/{CODE})


- [X] Fetch das disciplinas e professores da UFSC

- [X] Barra de pesquisa de disciplinas

- [ ] Mapa curricular
	- O usuário guardará uma lista das matérias divididas por semestre. Cada matéria será uma tupla(String código, Boolean cursada, Boolean avaliada), e será usada para compor o mapa curricular.
	- Inicialmente o usuário irá inserir as matérias de cada semestre, posteriormente pode haver algum sistema que capture as matérias padrões do curso, porém ainda permite a adição de matérias.


## TO DO (Iteração 3)
- [ ] Fetch dos cursos da UFSC (Necessário para montar o grafo das dependências entre disciplinas)
	- Tentar usar isso aqui: https://cagr.sistemas.ufsc.br/relatorios/curriculoCurso?curso=603, separar o id do curso, nome e criar o grafo que relaciona as disciplinas.