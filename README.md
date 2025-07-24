# Delivery Route Optimization Service

Serviço para otimização de rotas de entrega, construído com Spring Boot e programação reativa. O serviço recebe um ponto de origem e múltiplos destinos, e retorna uma sequência otimizada de paradas para minimizar a distância total da viagem, resolvendo uma versão do **Problema do Caixeiro Viajante (TSP)**.

-----

## Sobre o Projeto

Este serviço foi projetado para resolver um desafio logístico comum: encontrar a rota mais eficiente para realizar múltiplas entregas. Ele utiliza uma arquitetura moderna e reativa para garantir alta performance e escalabilidade.

Suas principais funcionalidades incluem:

* **Geocodificação:** Converte endereços textuais em coordenadas geográficas (latitude/longitude) usando a API do **[LocationIQ](https://locationiq.com/)**. Inclui lógica de resiliência com retentativas e controle de limites de taxa.
* **Matriz de Distância/Duração:** Calcula a distância e o tempo de viagem entre todos os pontos usando a API do **[OpenRouteService](https://openrouteservice.org/)**, considerando diferentes perfis de rota (carro, caminhada, bicicleta).
* **Otimização de Rota:** Aplica o algoritmo heurístico do **Vizinho Mais Próximo (Nearest Neighbor)** para determinar a ordem de visitação mais curta.
* **API RESTful:** Expõe um endpoint simples para receber os dados e retornar a rota otimizada em formato JSON.

-----

## Tecnologias Utilizadas

* **Java 21**
* **Spring Boot 3**
* **Project Reactor** (Programação Reativa)
* **Spring WebFlux**
* **Gradle**
* **LocationIQ API** (Geocodificação)
* **OpenRouteService API** (Matriz de Rotas)


-----

## Como Começar

Siga os passos abaixo para configurar e executar o projeto localmente.

### Pré-requisitos

* **JDK 21** ou superior instalado.
* **Gradle 8.5** ou superior.
* **Chaves de API** válidas para:
    * [LocationIQ](https://locationiq.com/)
    * [OpenRouteService](https://openrouteservice.org/)

### Configuração

1.  Clone o repositório:

    ```sh
    git clone https://github.com/seu-usuario/delivery-route-optimization-service.git
    cd delivery-route-optimization-service
    ```

2.  Crie o arquivo de segredos `.env` na **raiz** do projeto

    ```
    LOCATIONIQ_API_KEY=SUA_CHAVE_AQUI_DO_LOCATIONIQ

    OPEN_ROUTE_SERVICE_API_KEY=SUA_CHAVE_AQUI_DO_OPENROUTESERVICE
    ```

3.  Verifique se o arquivo `src/main/resources/application.yml` está configurado para ler as variáveis de ambiente:

    ```yaml
    geocoding:
      locationIQ:
        api:
          key: ${LOCATIONIQ_API_KEY}
          baseurl: https://us1.locationiq.com/v1/search 
      open-route-service:
        api:
          key: ${OPEN_ROUTE_SERVICE_API_KEY}
          baseurl: https://api.openrouteservice.org/v2/matrix
    ```

4.  Construa o projeto:

    ```sh
    ./gradlew build
    ```

### Execução

Use o Gradle Wrapper para executar a aplicação. As variáveis do `.env` serão carregadas automaticamente no início.

```sh
./gradlew bootRun
```

O serviço estará disponível em `http://localhost:8080`.

-----

## Uso da API

### Endpoint

`POST /routes/optimize`

### Exemplo de Requisição

Envie um JSON no corpo (body) da requisição com a seguinte estrutura. Recomenda-se usar endereços completos e claros (rua, número, bairro, cidade, estado e país) para obter a melhor precisão da geocodificação.

```json
{
  "profile": "driving-car",
  "origin": {
    "street": "Avenida Mário Ypiranga",
    "number": "1300",
    "neighborhood": "Adrianópolis",
    "city": "Manaus"
  },
  "destinations": [
    {
      "street": "Avenida Coronel Teixeira",
      "number": "5705",
      "neighborhood": "Ponta Negra",
      "city": "Manaus"
    },
    {
      "street": "Avenida Eduardo Ribeiro",
      "number": "659",
      "neighborhood": "Centro",
      "city": "Manaus"
    },
    {
      "street": "Avenida Constantino Nery",
      "number": "5001",
      "neighborhood": "Flores",
      "city": "Manaus"
    },
    {
      "street": "Avenida Rodrigo Otávio",
      "number": "3555",
      "neighborhood": "Distrito Industrial I",
      "city": "Manaus"
    }
  ]
}
```

**Observação:** Consulte a API do Open Route Service para mais opções de `profile`

### Exemplo de Resposta de Sucesso (200 OK)

```json
{
  "route": [
    {
      "id": "origin",
      "street": "Avenida Mário Ypiranga",
      "number": "1300",
      "neighborhood": "Adrianópolis",
      "city": "Manaus",
      "coordinates": {
        "latitude": -3.10413065,
        "longitude": -60.01135290467943
      }
    },
    {
      "id": "client_2",
      "street": "Avenida Eduardo Ribeiro",
      "number": "659",
      "neighborhood": "Centro",
      "city": "Manaus",
      "coordinates": {
        "latitude": -3.1307869,
        "longitude": -60.0242028
      }
    },
    {
      "id": "client_4",
      "street": "Avenida Rodrigo Otávio",
      "number": "3555",
      "neighborhood": "Distrito Industrial I",
      "city": "Manaus",
      "coordinates": {
        "latitude": -3.1253641,
        "longitude": -59.9824177
      }
    },
    {
      "id": "client_3",
      "street": "Avenida Constantino Nery",
      "number": "5001",
      "neighborhood": "Flores",
      "city": "Manaus",
      "coordinates": {
        "latitude": -3.0725953,
        "longitude": -60.026544
      }
    },
    {
      "id": "client_1",
      "street": "Avenida Coronel Teixeira",
      "number": "5705",
      "neighborhood": "Ponta Negra",
      "city": "Manaus",
      "coordinates": {
        "latitude": -3.0741848,
        "longitude": -60.0883464
      }
    },
    {
      "id": "origin",
      "street": "Avenida Mário Ypiranga",
      "number": "1300",
      "neighborhood": "Adrianópolis",
      "city": "Manaus",
      "coordinates": {
        "latitude": -3.10413065,
        "longitude": -60.01135290467943
      }
    }
  ],
  "totalDistanceKm": 47.64,
  "totalDurationMinutes": 56.5965,
  "distanceUnit": "km",
  "timeUnit": "minute"
}
```

-----

## Fluxo da Arquitetura

![diagram](diagram.svg)

O processo de uma requisição segue os seguintes passos:

1.  **Controller:** O `RouteController` recebe a requisição HTTP POST.
2.  **Service de Otimização:** O `RouteOptimizationService` orquestra o fluxo principal.
3.  **Geocodificação (Sequencial):** Para cada endereço fornecido, o `GeocodingService` é chamado **sequencialmente** (um por um, com um atraso entre as chamadas) para evitar limites de taxa da API, consultando o LocationIQ.
4.  **Cálculo da Matriz:** Com as coordenadas de todos os endereços em mãos, o `OrsMatrixService` faz uma única chamada ao OpenRouteService para obter uma matriz com as distâncias e durações entre todos os pontos.
5.  **Algoritmo TSP:** O `RouteOptimizationService` constrói o grafo de distâncias/durações e aplica o algoritmo heurístico do Vizinho Mais Próximo para encontrar a ordem otimizada das paradas.
6.  **Resposta:** O `RouteController` retorna a rota final e as métricas totais como uma resposta JSON.

-----

## Roadmap (Próximos Passos)

* [ ] Implementar algoritmos de otimização mais avançados
* [ ] Adicionar um cache para os resultados de geocodificação e matriz de rotas para reduzir chamadas repetidas às APIs externas.
* [ ] Integrar dados de trânsito em tempo real (se disponíveis na API) para estimativas de tempo mais precisas.
* [ ] Estender para otimização com múltiplos veículos e janelas de tempo de entrega.
* [ ] Criar uma interface de usuário (frontend) para visualizar a rota em um mapa.

-----

## Licença

Distribuído sob a Licença MIT. Veja o arquivo `LICENSE` para mais informações.

-----

## 🧑‍💻 Autor

[Eduardo Rezende](https://github.com/EduardoRez3nde)

-----