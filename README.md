# Projeto Backend - Vitor Xavier Correia

Este repositório contém Docker containers para facilitar o setup do ambiente para o bootcamp. Os containers incluídos são:

- PostgreSQL: Banco de dados relacional para persistência de dados
- RabbitMQ: Sistema de mensageria para comunicação assíncrona entre serviços.
- App User API: API de gerenciamento de usuários.
- App Integration API: API para integrações externas.

## Pré-requisitos
Antes de começar, você precisará ter o Docker instalado em sua máquina. Você pode encontrar instruções de instalação [aqui](https://www.docker.com/get-started/).

## Como usar

Para utilizar esses containers, siga estas etapas:

1. **Pull dos containers**: Execute os seguintes comandos em seu terminal para baixar os containers necessários:

   ```bash
   docker pull vxc333/postgres_bootcamp
   docker pull vxc333/rabbitmq_bootcamp
   docker pull vxc333/app-user-api
   docker pull vxc333/app-integration-api
    ```
    
2. **Executar os containers**: Após baixar os containers, você pode executá-los individualmente usando o comando docker run. Por exemplo:

   ```bash
    docker run -d --name postgres_bootcamp -p 5432:5432 vxc333/postgres_bootcamp
    ```
    
3. **Acessar os serviços**: Após executar os containers, você poderá acessar os serviços conforme necessário. Certifique-se de verificar as portas em que os serviços estão sendo executados e utilize as URLs correspondentes para acessar as APIs.

## Notas adicionais

- Certifique-se de verificar a documentação de cada serviço para obter informações sobre como usá-los.
- Para interromper a execução dos containers, você pode usar o comando docker stop <nome_do_container>.

# Ou caso preferir

# Clone o repositório
```bash
git clone https://github.com/bc-fullstack-04/vitor-xavier-backend.git
```

# Instale as dependências
```bash
cd vitor-xavier-backend/app-integration-api
mvn clean install

cd vitor-xavier-backend/app-user-api
mvn clean install
```

# Construa os contêineres Docker
```bash
docker-compose -f docker-compose.yml build
```

# Lance os contêineres

```bash
docker-compose -f docker-compose.yml up
```

# Documentação
A documentação estará no
http://localhost:8081/api/swagger-ui/index.html#/
e no http://localhost:8082/api/swagger-ui/index.html#/
