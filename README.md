# Desafio Serasa - API de Gestão de Pessoas

Projeto desenvolvido em **Spring Boot 3**, com autenticação **JWT**, documentação via **Swagger/OpenAPI** e integração com a API externa **ViaCEP** para consulta de endereços.

---

## 📋 Funcionalidades

- Autenticação JWT (`/auth/login`) com perfis **ADMIN** e **USER**.
- CRUD de Pessoas (`/pessoas`) com regras de autorização:
    - `USER`: pode **listar**.
    - `ADMIN`: pode **criar, atualizar e excluir**.
- Integração com **ViaCEP** para preenchimento automático de endereço.
- Validações de entrada (nome obrigatório, idade 0–120, CEP com 8 dígitos, etc.).
- Exclusão **lógica** (flag `ativo`).
- Tratamento global de erros (400, 401, 403, 404, 500).
- Documentação com Swagger (UI em `/swagger-ui/index.html`).
- Testes unitários e de integração com cobertura >90%.

---

## 📂 Estrutura de Pacotes

```
src/main/java/com/serasa/desafio
 ├── config/        # Configurações globais (Swagger, Security, etc.)
 ├── controller/    # Controllers REST
 ├── dto/           # Data Transfer Objects
 ├── entity/        # Entidades JPA
 ├── repository/    # Interfaces Spring Data
 ├── service/       # Regras de negócio
 │    └── impl/     # Implementações de serviços
 ├── client/        # Consumo de APIs externas (ViaCEP)
 ├── security/      # JWT, filtros e autenticação
 └── exception/     # ExceptionHandler global
```

---

## 🚀 Como executar

### Pré-requisitos
- Java 17+
- Maven 3.9+

### Passos
```bash
# Clonar repositório
git clone https://github.com/jeffreyutfpr/desafio-serasa-java.git
cd desafio-serasa-java

# Executar em perfil dev
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

Aplicação disponível em:  
👉 `http://localhost:8181`

Swagger UI:  
👉 `http://localhost:8181/swagger-ui/index.html`

---

## 🔐 Usuários padrão

| Usuário | Senha    | Role  |
|---------|----------|-------|
| admin   | admin123 | ADMIN |
| user    | user123  | USER  |

---

## 🧪 Testes

Executar testes:
```bash
  mvn clean test
```

Relatório de cobertura (JaCoCo):
```bash
  mvn clean test jacoco:report
```
Abrir em navegador:  
`target/site/jacoco/index.html`

### Cobertura atual
- **Linhas**: ~93%
- **Branches**: ~68%
- **Classes**: 100%

👉 Cobertura consolidada com testes de:
- Controller (`PessoaControllerTest`)
- Service (`PessoaServiceImplTest`)
- Security (`JwtAuthenticationFilterTest`)
- Client (`CepClientTest`)
- Exceptions (`GlobalExceptionHandlerTest`)
- Entidade (`PessoaTest`)
- Contexto (`DesafioApplicationTest`)

---

## 📈 Qualidade

- Validações robustas via Bean Validation.
- Tratamento centralizado de erros.
- Segurança JWT integrada ao Swagger.
- Testes cobrindo cenários de sucesso, falha e exceções.
- Estrutura modular e de fácil manutenção.

---

## ⚡ Extra (opcional)

Para testes de desempenho:
- Pode ser usado **Postman Runner** ou **Apache JMeter**.
- Endpoints principais a validar:
    - `POST /auth/login`
    - `GET /pessoas`
    - `POST /pessoas`

---

## 📌 Conclusão

O projeto foi refatorado em 8 etapas, cobrindo desde organização de pacotes até testes de qualidade.  
Atualmente apresenta **arquitetura limpa, segurança, documentação integrada, testes completos e cobertura acima do mercado**.

A aplicação está pronta para ser expandida com novas funcionalidades conforme necessário.
