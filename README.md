# Desafio técnico (Projeto Tenda) - Coupon API

### Como executar
1.  Clone o repositório.
2.  Na raiz do projeto, execute:
    ```bash
    ./mvnw spring-boot:run
    ```
3.  A API estará disponível em `http://localhost:8080`.

## Decisões técnicas e de arquitetura

Para este desafio, optei por uma arquitetura clássica em camadas (Controller, Service, Repository), para que cada peça do projeto tenha uma responsabilidade única e bem definida.

### Separação entre Domínio e Contrato (DTOs)
Como boa prática, utilizei a estratégia de desacoplar a entidade JPA (`Coupon`) dos (`DTOs`), para evitar a exposição de detalhes internos do banco de dados e para que API evolua obedecendo as melhores práticas.
- **Java Records:** Os DTOs foram implementados utilizando `Records` (do Java 14+), que, por sua vez, garante imutabilidade, código muito mais conciso e legível para o transporte dos dados.

### Validação de Dados e Conformidade com o Contrato
Seguindo a recomendação de rigor no contrato da API, implementei a primeira camada de validação diretamente nos DTOs utilizando Bean Validation, sendo assim:
- Campos como data de expiração (`@Future`) e valor de desconto (`@DecimalMin`) já são validados na chegada da requisição.
- Para garantir que a aplicação se comunique exatamente no formato exigido (com milissegundos e fuso horário Z), utilizei a anotação `@JsonFormat` nos campos de data. Dessa forma, assegurei que fica coerente com a especificação do desafio.

## Validação e testes implementados

*(Em desenvolvimento: os testes serão detalhados depois, conforme a implementação das regras de negócio no Service)*

## O que faria diferente com mais tempo