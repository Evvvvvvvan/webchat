<div id="readme-top" ></div>
<div align="center">

<img height="350" src="./images/logo.png" alt="WebChat Logo">

Web Chat 是开源的聊天系统，支持一键免费部署私人Chat网页的应用程序。

**简体中文**

</div>

<details>
<summary><kbd>目录树</kbd></summary>

#### TOC

- [🛳 开箱即用](#-开箱即用)
    - [`A` 使用 Docker 部署（整合版，一次跑全套）](#a-使用-docker-部署整合版一次跑全套)
    - [`B` 使用 Docker-compose 部署](#b-使用-docker-compose-部署)
    - [`C` 使用 Jar包 本地部署](#c-使用-jar包-本地部署)
- [⌨️ 本地开发](#️-本地开发)

####

<br/>

</details>

## 🛳 开箱即用

有关 Docker 部署的详细说明，详见项目 Wiki（如有）。  
<br/>
WebChat 提供了 Docker 镜像，这使你可以在几分钟内构建自己的聊天服务，无需任何基础知识。

> 本项目的数据库初始化脚本在 `sql/webcaht.sql` 中，可以直接导入到 MySQL 中，也可以使用 docker-compose 一键部署，会自动初始化数据库
>
> 会有三个初始化用户：`admin`（管理员）、`user1`（普通用户）、`audit2`（普通用户），密码都是 `123456`
>
> 本项目目前有三个 tag，分别为 `latest`，`customer` 和 `v1.0`
> 1. **latest**：打包方式为 jar 包打入，**不支持修改配置**：MySQL 和 Redis 的密码都是 `loks666`，MySQL 用户是 `root`
> 2. **customer**：打包方式为 Dockerfile，**支持修改配置**：可以修改 MySQL 和 Redis 的配置，需添加下面的挂载
>
> ```fish
> volumes:
>   - ./webchat/application.yml:/src/main/resources/application.yml
>   - ./webchat/application-dev.yml:/src/main/resources/application-dev.yml
> ```
>
> 3. **v1.0**：为早期版本，可忽略

### `A` 使用 Docker 部署（整合版，一次跑全套）

> 下面是一体化命令：在同一自定义网络中**依次**启动 MySQL、Redis、WebChat。无需分段拷贝，直接整段执行即可（已考虑网络已存在等情况）。

```bash
# 1) 创建独立网络（已存在则忽略报错）
docker network create chat_network 2>/dev/null || true

# 2) 启动 MySQL（示例 root 密码：loks666；如机器上已有 MySQL 且端口占用，可修改映射或跳过本步）
docker run -d --name mysql --network chat_network \
  -e MYSQL_ROOT_PASSWORD=loks666 \
  -p 3306:3306 \
  mysql:8

# 3) 启动 Redis（如已有 Redis 可跳过）
docker run -d --name redis --network chat_network \
  -p 6379:6379 \
  redis:7

# 4) 启动 WebChat（customer 版支持挂载自定义配置；
#    若不需要自定义配置，可删除两行 -v；若使用上面的容器 MySQL/Redis，
#    请在 application-dev.yml 中把主机名写成 mysql / redis）
docker run -d --name webchat --network chat_network \
  -p 8101:8101 \
  -v "$(pwd)"/webchat/application.yml:/src/main/resources/application.yml \
  -v "$(pwd)"/webchat/application-dev.yml:/src/main/resources/application-dev.yml \
  general9527/webchat:customer
