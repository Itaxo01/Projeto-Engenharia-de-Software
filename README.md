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

Tentei fazer o scrapping funcionar mas estou falhando miseravelmente. Ele não está avançando corretamente as páginas mas isso deve ser tranquilo de resolver, mas está acontecendo algum erro ao salvar os professores/disciplinas no banco de dados (O banco de dados não retorna que aquela disciplina já existe, não sei se é pq não foi criada ou só retorna errado mesmo). A funcionalidade que implementa o scrapping está na aba de administrador.

---
## TO DO (Iteração 2)
- [X] Implementar as páginas das disciplinas (localhost:8080/disciplina/{CODE})
  - com isso também colocar algumas informações padrão de alguma disciplina, só para exemplo (A interação com a disciplina e avaliação ainda não será feita)

- [X] Fetch das disciplinas e professores da UFSC

- [ ] Barra de pesquisa de disciplinas

- [ ] Mapa curricular
	- O usuário guardará uma lista das matérias divididas por semestre. Cada matéria será uma tupla(String código, Boolean cursada, Boolean avaliada), e será usada para compor o mapa curricular.
	- Inicialmente o usuário irá inserir as matérias de cada semestre, posteriormente pode haver algum sistema que capture as matérias padrões do curso, porém ainda permite a adição de matérias.
