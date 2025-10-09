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
Fiz o JPA de algumas classes e fiquei com preguiça e mandei a IA fazer o resto. Não revisei ainda, tem erros, precisa revisar. No momento não está compilando, qualquer coisa da pra voltar com um commit antigo.

---
## TO DO
Implementar as páginas das disciplinas (localhost:8080/disciplina/{CODE})
com isso também colocar algumas informações padrão de alguma disciplina, só para exemplo (A interação com a disciplina e avaliação ainda não será feita)

Mapa curricular, onde o usuário guardará uma lista das matérias divididas por semestre. Cada matéria será uma tupla(String código, Boolean cursada, Boolean avaliada), e será usada para compor o mapa curricular.
Inicialmente o usuário irá inserir as matérias de cada semestre, posteriormente pode haver algum sistema que capture as matérias padrões do curso, porém ainda permite a adição de matérias.


Sobre o fetch das disciplinas: O MatrUFSC já fez, porém é antigo https://github.com/ramiropolla/matrufsc_dbs/tree/master. O nosso método provavelmente vai ser o mesmo, simplesmente faz um scrapper que acessa a página da ufsc e separa todas as matérias de todos os campi em um json. Outro cara faz o parse.
Da para utilizar o dado do LATTES desses campos para garantir o ID único de cada professor.