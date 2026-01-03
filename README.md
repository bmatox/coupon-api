# Desafio técnico (Projeto Tenda) - Coupon API

## 1. Decisões técnicas e de arquitetura

Durante o desenvolvimento, fui tomando algumas decisões técnicas que considerei importante documentar. De cara, em relação à arquitetura do desafio, optei por implementar em camadas (Controller, Service, Repository), para que cada peça do projeto tenha responsabilidades bem definidas. Abaixo, cito mais algumas decisões importantes:

### 1.1 Separação entre domínio e contrato (DTOs)
Como boa prática, utilizei a estratégia de desacoplar a entidade JPA (`Coupon`) dos (`DTOs`), para evitar a exposição de detalhes internos do banco de dados e para que API evolua obedecendo as melhores práticas.
-  Os DTOs foram implementados utilizando `Records` (do Java 14+), que, por sua vez, garante imutabilidade, código muito mais conciso e legível para o transporte dos dados.

### 1.2 Validação de dados de acordo com o contrato da API
Seguindo a recomendação de rigor no contrato da API, implementei a primeira camada de validação diretamente nos DTOs utilizando Bean Validation, sendo assim:
- Campos como data de expiração (`@Future`) e valor de desconto (`@DecimalMin`) já são validados na chegada da requisição.
- Para garantir que a aplicação se comunique exatamente no formato exigido (com milissegundos e fuso horário Z), utilizei a anotação `@JsonFormat` nos campos de data. Dessa forma, assegurei que fica coerente com a especificação do desafio.

### 1.3 Regras de negócio separadas na camada service
- Na camada de serviço foi implementei o método `normalizarCodigo` que utiliza regex para remover caracteres especiais e garante que apenas caracteres alfanuméricos sejam persistidos.
- OBS.: Foi criada uma classe `BusinessException` com o intuito de centralizar os erros semânticos (ex: tentativa de deletar cupom já deletado) e evitar um possível vazamento de exceções genéricas ou de banco de dados para o cliente.

## 2. Validação e testes implementados

*(Em desenvolvimento: os testes serão detalhados depois, conforme a implementação das regras de negócio no Service)*

## 3. O que eu faria diferente com mais tempo

## 4. Como executar
1.  Clone o repositório.
2.  Na raiz do projeto, execute:
    ```bash
    ./mvnw spring-boot:run
    ```
3.  A API estará disponível em `http://localhost:8080`.