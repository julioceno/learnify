# Learnify

![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)
![MongoDB](https://img.shields.io/badge/MongoDB-%234ea94b.svg?style=for-the-badge&logo=mongodb&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-%23336791.svg?style=for-the-badge&logo=postgresql&logoColor=white)
![Redis](https://img.shields.io/badge/Redis-%23DC382D.svg?style=for-the-badge&logo=redis&logoColor=white)
![AWS](https://img.shields.io/badge/AWS-%23FF9900.svg?style=for-the-badge&logo=amazon-aws&logoColor=white)
![Stripe](https://img.shields.io/badge/Stripe-%23646EFB.svg?style=for-the-badge&logo=stripe&logoColor=white)
[![Licence](https://img.shields.io/github/license/Ileriayo/markdown-badges?style=for-the-badge)](./LICENSE)

O projeto é uma API construída usando **Java, Java Spring, AWS Simple Queue Service, Mongo DB, Postgres, Redis, Localstack e Stripe**

O **Learnify** tem a ideia de ser uma plataforma de estudos online. Mas no caso da aplicação, foi feito apenas a parte de assinatura dos planos disponiveis

## Índice

- [Pré Requisitos](#pré-requisitos)
- [Instalação](#instalação)
- [Configuração](#configuração)
- [Uso](#uso)
- [API Endpoints](#api-endpoints)
- [Licença](#licença)

## Pré Requisitos

1. [Java](https://www.oracle.com/java/technologies/javase-jdk17-downloads.html)
2. [Docker](https://docs.docker.com/get-docker/)
3. [Stripe CLI](https://docs.stripe.com/stripe-cli?locale=pt-BR)
5. [Git](https://git-scm.com/)
6. [Baixar Insomnia](https://insomnia.rest/) Recomendado para testar os endpoints da API com o arquivo `learnify-collection-insomnia.json`  
7. [AWS CLI](https://docs.aws.amazon.com/cli/latest/userguide/getting-started-install.html)

## Instalação

1. Clone o Repositório:

```bash
git clone https://github.com/julioceno/learnify.git
```

2. Entre dentro de cada projeto e instale as dependencias
3. Tenha instalado e configurado o [Stripe CLI](https://docs.stripe.com/stripe-cli?locale=pt-BR). Pegue a sua api key do stripe, vamos precisar posteriormente

## Configuração

1. Rode no terminal:

```bash
docker compose up -d
```

2. Rode o arquivo de scripts:

```bash
./localstack.sh
```

## Uso

1. Inicie todas as aplicações
2. As rotas do gateway estarão disponivéis http://localhost:8080

## API Endpoints

Os endpoints estão disponíveis no arquivo [learnify-collection-insomnia.json](./learnify-collection-insomnia.json) , **LEMBRE-SE** de ter configurado a variavel stripe.api-key nas enviroments.

## Licença

Este projeto está licenciado sob a [MIT License](./LICENSE).
