# Desafio Serasa Experian - Java

### Como rodar
1. Requisitos: Java 17+, Maven 3.9+
2. `mvn spring-boot:run`
3. Swagger UI: http://localhost:8181/swagger-ui/index.html
4. H2 Console: http://localhost:8181/h2-console (JDBC URL: `jdbc:h2:mem:desafio`)

### Autenticação
- Login: `POST /auth/login` com body:
```json
{ "username":"admin", "password":"admin123" }
```
ou
```json
{ "username":"user", "password":"user123" }
```
- Use o token Bearer no Swagger (botão **Authorize**).

### Regras
- Somente ADMIN pode **criar/atualizar/excluir** pessoas.
- Todos podem **listar** com filtros (nome, idade, cep) + paginação.

### Endpoints principais
- `POST /pessoas` (ADMIN) - cria pessoa (preenche endereço via ViaCEP)
- `GET /pessoas?nome=...&idade=...&cep=...` - lista paginada
- `PUT /pessoas/{id}` (ADMIN) - atualiza
- `DELETE /pessoas/{id}` (ADMIN) - exclusão lógica

### Observação
A classificação de score é calculada no getter `getScoreDescricao()` da entidade.
