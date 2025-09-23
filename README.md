## Run
```bash
mvn spring-boot:run
```

## Then open http://localhost:8080/

## or
### Check https://96acb17de87a.ngrok-free.app/ for the already hosted project 
#### (Link may have changed or be offline).

---
## Recent changes
Acabei de tirar alguns arquivos importantes do git, como os usuários e atestados de matricula. Tive que alterar o histórico dos commits para tirar os arquivos de lá também. Pode ter dado algo de errado mas aparentemente não, qualquer coisa tenho um backup.

---
## TO DO
Implementar as páginas das disciplinas (localhost:8080/disciplina/{CODE})
com isso também colocar algumas informações padrão de alguma disciplina, só para exemplo (A interação com a disciplina e avaliação ainda não será feita)

Mapa curricular, onde o usuário guardará uma lista das matérias divididas por semestre. Cada matéria será uma tupla(String código, Boolean cursada, Boolean avaliada), e será usada para compor o mapa curricular.
Inicialmente o usuário irá inserir as matérias de cada semestre, posteriormente pode haver algum sistema que capture as matérias padrões do curso, porém ainda permite a adição de matérias.
