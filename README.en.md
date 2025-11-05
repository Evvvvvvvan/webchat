<div id="readme-top" ></div>
<div align="center">

<img height="350" src="./images/logo.png" alt="WebChat Logo">

Web Chat is an open-source chat system, supporting one-click free deployment of private Chat web applications.

**English** · [简体中文](./README.md)

![](./images/webchat.png)
![](./images/login.png)
![](./images/console.png)
![](./images/audit.png)

</div>

<details>
<summary><kbd>Table of Contents</kbd></summary>

#### TOC

- [🛳 Ready to Use](#-ready-to-use)
  - [`A` Deployment with Docker](#a-deployment-with-docker)
  - [`B` Deployment with Docker-compose](#b-deployment-with-docker-compose)
  - [`C` Local Deployment with Jar Package](#c-local-deployment-with-jar-package)
- [⌨️ Local Development](#️-local-development)

####

<br/>

</details>

## 🛳 Ready to Use
WebChat provides a Docker image that lets you build your own chat service in minutes with minimal effort.

> Database initialization: `sql/webcaht.sql` can be imported into MySQL directly, or you can use docker-compose for one-click deployment which will automatically initialize the database.

> Initial users (default password for all is `123456`):
> - `admin` (administrator)
> - `user1` (regular user)
> - `audit2` (regular user)

> Image tags:
> 1. **latest**: packaged as a single jar; **configuration is not customizable**. Defaults: MySQL/Redis password `loks666`, MySQL user `root`.
> 2. **customer**: packaged via Dockerfile; **configuration is customizable** (mount `application.yml` / `application-dev.yml` to override).
> 3. **v1.0**: legacy version, can usually be ignored.

### `A` Deployment with Docker

Start WebChat using the customer image (supports custom config):
docker run -d --name webchat -p 8101:8101 general9527/webchat:customer

If your MySQL and Redis also run in containers, place them in the same Docker network (and set hostnames accordingly in your config):
# create a dedicated network
docker network create chat_network

    # MySQL (example root password)
    docker run -d --name mysql --network chat_network \
      -e MYSQL_ROOT_PASSWORD=loks666 \
      -p 3306:3306 \
      mysql:8

    # Redis
    docker run -d --name redis --network chat_network \
      -p 6379:6379 \
      redis:7

    # WebChat (mount configs if you need to override defaults)
    docker run -d --name webchat --network chat_network \
      -p 8101:8101 \
      -v "$(pwd)"/webchat/application.yml:/src/main/resources/application.yml \
      -v "$(pwd)"/webchat/application-dev.yml:/src/main/resources/application-dev.yml \
      general9527/webchat:customer

### `B` Deployment with Docker-compose

One-click deployment in the project root (includes Redis & MySQL):
cd webchat
docker-compose up -d

WebChat only (when you already have MySQL & Redis):
version: '3.8'

    volumes:
      mysql:
      redis:

    networks:
      chat_network:
        name: chat_network

    services:
      webchat:
        image: general9527/webchat:customer
        container_name: webchat
        ports:
          - "8101:8101"
        volumes:
          - ./webchat/application.yml:/src/main/resources/application.yml
          - ./webchat/application-dev.yml:/src/main/resources/application-dev.yml
        networks:
          - chat_network

Before deployment, update Redis/MySQL settings in `application-dev.yml` to match your environment (when in the same Docker network, you can use service names like `mysql` and `redis` as hostnames).

### `C` Local Deployment with Jar Package
Recommended to build via IDE plugin. If using CLI, ensure Maven is in your system PATH.
cd webchat
mvn clean package
java -jar webchat.jar

<br/>

## ⌨️ Local Development

    git clone https://github.com/loks666/webchat.git
    cd webchat
    mvn clean install
    java -jar webchat.jar
