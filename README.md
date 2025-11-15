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

Por enquanto a usabilidade da página da disciplina não está ideal, após receber a confirmação do servidor é feito um reload na página. Tentei fazer ser mais interativo, porém acabou dando trabalho demais e ainda gerando erros. 
---
## TO DO (Iteração 2)
- [X] Implementar as páginas das disciplinas (localhost:8080/class/{CODE})


- [X] Fetch das disciplinas e professores da UFSC

- [X] Barra de pesquisa de disciplinas

- [X] Mapa curricular
	- O usuário guardará uma lista das matérias divididas por semestre. Cada matéria será uma tupla(String código, Boolean cursada, Boolean avaliada), e será usada para compor o mapa curricular.
	- Inicialmente o usuário irá inserir as matérias de cada semestre, posteriormente pode haver algum sistema que capture as matérias padrões do curso, porém ainda permite a adição de matérias.


## TO DO (Iteração 3)
- [ ] Fetch dos cursos da UFSC (Necessário para montar o grafo das dependências entre disciplinas)
	- Tentar usar isso aqui: https://cagr.sistemas.ufsc.br/relatorios/curriculoCurso?curso=603, separar o id do curso, nome e criar o grafo que relaciona as disciplinas.

- [ ] O comentário precisa ter uma relação de muitos pra muitos com usuários para lidar com o upvote e downvote (O mesmo usuário não pode ter votos repetidos)




## TO DO DIAGRAMAS
- 2 Diagramas de sequência baseados em história de usuário
	- Fazer sobre:
		 Admin exclui comentário (HU 4) - Luam
		 interação com comentário (Upvote, DownVote, comentar) - Kauan