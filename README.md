## Итоговый проект курса "Microservices Architecture"

# Социальная сеть

## ТЗ и Функциональные Требования
Необходимо разработать социальную сеть для обмена текстовыми сообщениями. Сеть должна быть рассчитана на большое количество пользователей, потенциально миллионы. Сеть должна выдерживать высокую нагрузку.<br/>
В первой итерации необходимо реализовать следующий функционал:
- регистрацию и логин пользователя в соцсеть
- редактирования своего профайла и просмотра профайлов пользователей
- добавление постов и просмотр постов других пользователей
- добавление друзей
- просмотр ленты постов друзей
- создание отправка сообщений пользователям, просмотр диалогов

## Домен 
- пользователи
- профили пользователей
- посты
- диалоги и сообщения в диалогах
![](drawbacks/domain.png)

### Функциональная декомпозиция и диаграмма контейнеров
![](drawbacks/container_diagram.png)
#### Сервисы
- **NGINX** - API-Gateway - роутинг и балансировка запросов
- **Auth service** - сервис авторизации. Хранит UUID пользователей, их пароли и роли. Ответственность - login, signup, аутентификация и авторизация 
- **Core service** - хранит профили и посты пользователей. Ответвенность - профили, друзья, посты, ленты постов друзей.
- **Post service** - сервис для отправки постов и сообщений диалогов к "онлайн" подключенным пользователям. Отправка осуществляется по websockets
- **Dialogs service** - сотвечает за сохранение и предоставление сообщений диалогов
#### Хранилища, кеши, очереди
- **Auth DB** - Postgre SQL - классическая mater/slave, синхронная репликация. При регистрации данные пишутся в мастер, затем при логине и запросах авторизации все считывается с реплик. Репликация нужна, поскольку БД высоконагружена запросами авторизации со всех сервисов.
- **Core DB** - Postgre SQL - классическая mater/slave, асинхронная репликация. Данные пишутся в мастер, затем читаются из реплик. Чтение постов - довольно тяжелые и долгие запросы, поэтому чтение с реплик оптимально. Шардирование также можно сделать для разбиения объемов постов. Ключ шардирования - UUID пользователя
- **Dialogs DB** - Postgre SQL - с шардами. Ключ шардирования - хеш от конкатенации отсортрованных по алвавиту UUID пользователей, участвующих в диалоге.
- **Сore Cache** - Redis PV - персистентный редис кеш с шардами прекрасно подходит для хранения лент постов друзей пользователя. Командами редис при чтении и записи регулируется количество постов в кеше пользователя. Также можно выставить TTL чтобы старые посты сами удалялись из ленты со временем для экономии места хранилища.
- **Message Broker** - Redis Pub/Sub. Ответвенность
  - асинхронная отправка постов на сохранение в кеш
  - асинхронная отправка постов пользователям, присоединенным к post-service по вебсокету
  - асинхронная отправка сообщений диалогов пользователям, присоединенным к post-service по вебсокету

### Posts Flow - Activity BPMN Diagram
![](drawbacks/Posts_flow.camunda8.png)
- User 1 добавляет пост
- пост сохраняется в БД, далее прооисходит его асинхронная обработка и через Redis PUB/SUB отправляется на 
- пост через Redis PUB/SUB попадает на обработчик, который добавляет этот пост ко всем френд-лентам друзей в Redis Cache
- в пост добавляется список всех друзей и через Redis PUB/SUB попадает в post service, который рассылает этот пост всем из списка друзей, если они в данный помент присоединены по вебсокету к post-service
- Any User может посмотреть посты любого юзера, с реплик БД считываются списки постов
- User 2 у которого User 1 в друзьях может просмотреть свою френд-ленту, прочитав ее из Redis Cache
- User 2 также открывает вебсокет соединение с post-service и новые сообщения от его друзей (на диаграмме от User 1) будут приходить ему по вебсокету онлайн

### Dialogs Flow - Sequence Diagram
![](drawbacks/dialogs_sequence.png)

### Authentication/Authorization - Sequence Diagram
Авторизация показана на примере Сохранения и Чтения профиля пользователя

![](drawbacks/auth_requests.png)

### Sequence Diagram и описание тестового сценария
![](drawbacks/tests_sequence.png)
Сценарий разделен на две фазы
#### Взаимодействие с АПИ (задействованы авторизация, кеши и очереди)
#### Взаимодействие с очередями и вебсокетами (демонстрация в браузере)
#### Запуск нагрузки и демонстрация мониторинга RPC, Latency, Errors и Alerts
###### Для подключения по вебсокету необходимо построить URL-ы на базе uuid и токенов пользователей
```
http://localhost:8082/?uuid={user_uuid}&token={token}
```
- user_uuid - UUID пользователя, который подключается по вебсокету
- token - его токен
- токен должен принадлежать пользователю, иначе приложение не откроет вебсокет соединение
- приложение отправляет в вебсокет только посты друзей пользователя
```
Пользователь 3
http://arch.homework/?uuid=c50a4e56-dbb5-4ee6-9e63-42f921792d88&token=eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJuZXczOTEyOEBuZXcucnUiLCJyb2xlIjoiVVNFUiIsInV1aWQiOiJjNTBhNGU1Ni1kYmI1LTRlZTYtOWU2My00MmY5MjE3OTJkODgiLCJpYXQiOjE2OTc2NjYyNTgsImV4cCI6MTY5Nzc2Njc1OH0.RSUN1U228ZpiqDQ1_Uz01YnwmfnxBPzRUavHAS9oN2M
Пользователь 4
http://arch.homework/?uuid=32f84432-8364-4b29-a49c-4ec708ea7d0e&token=eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJuZXc4MTQ4NEBuZXcucnUiLCJyb2xlIjoiVVNFUiIsInV1aWQiOiIzMmY4NDQzMi04MzY0LTRiMjktYTQ5Yy00ZWM3MDhlYTdkMGUiLCJpYXQiOjE2OTc2NjYzNzUsImV4cCI6MTY5Nzc2Njg3NX0.G7Tu8gkEX4uQhhVS8_lRKn0UFhNnYo2iKeRaaLz-2yE
```
Поскольку nginx не настроен на вебсокеты, для показа сделаем порт форвард на сервис
```bash
kubectl port-forward service/social-post 8080:8080
```
```
Пользователь 3
http://localhost:8080/?uuid=c50a4e56-dbb5-4ee6-9e63-42f921792d88&token=eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJuZXczOTEyOEBuZXcucnUiLCJyb2xlIjoiVVNFUiIsInV1aWQiOiJjNTBhNGU1Ni1kYmI1LTRlZTYtOWU2My00MmY5MjE3OTJkODgiLCJpYXQiOjE2OTc2NjYyNTgsImV4cCI6MTY5Nzc2Njc1OH0.RSUN1U228ZpiqDQ1_Uz01YnwmfnxBPzRUavHAS9oN2M
Пользователь 4
http://localhost:8080/?uuid=32f84432-8364-4b29-a49c-4ec708ea7d0e&token=eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJuZXc4MTQ4NEBuZXcucnUiLCJyb2xlIjoiVVNFUiIsInV1aWQiOiIzMmY4NDQzMi04MzY0LTRiMjktYTQ5Yy00ZWM3MDhlYTdkMGUiLCJpYXQiOjE2OTc2NjYzNzUsImV4cCI6MTY5Nzc2Njg3NX0.G7Tu8gkEX4uQhhVS8_lRKn0UFhNnYo2iKeRaaLz-2yE
```


### Развертывание приложения в кластере
- Команды приведены для кластера minikube (запускался на windows)
- Команды запускаются из директории kubernetes, где лежат файлы манифестов

```bash
minikube start --cpus=4 --memory=12gb --disk-size=8gb
```

#### 1 - Сделаем маппинг arch.homework на IP minikube кластера:
```bash
minikube ip
```
add to hosts: `172.23.222.239 arch.homework`<br/>
<br/>
#### 2 - Добавим HELM репозитории:
```bash
helm repo add bitnami https://charts.bitnami.com/bitnami
helm repo add ingress-nginx https://kubernetes.github.io/ingress-nginx
helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
helm repo add stable https://charts.helm.sh/stable
helm repo update
```
#### 3 - Перейдем в директорию kubernetes проекта
```bash
cd <project_dir>/kubernetes
```

#### 4 - Установим kube-prometheus-stack через HELM:
```bash
helm install prom prometheus-community/kube-prometheus-stack -f prometheus.yaml --atomic
```

#### 5 - Установим Ingress NGINX через HELM:
```bash
kubectl create namespace ngx
helm install nginx ingress-nginx/ingress-nginx --namespace ngx -f nginx-daemon.yaml --atomic
```

#### 6 - Установим PostgreSQL через HELM:
```bash
helm install pgcore bitnami/postgresql --set global.postgresql.auth.username=postgres --set global.postgresql.auth.password=pass --set global.postgresql.auth.database=pgcore
helm install pgauth bitnami/postgresql --set global.postgresql.auth.username=postgres --set global.postgresql.auth.password=pass --set global.postgresql.auth.database=pgauth
helm install pgdialog bitnami/postgresql --set global.postgresql.auth.username=postgres --set global.postgresql.auth.password=pass --set global.postgresql.auth.database=pgdialog
```
```
jdbc:postgresql://pgcore-postgresql:5432/postgres
jdbc:postgresql://pgauth-postgresql:5432/postgres
jdbc:postgresql://pgdialog-postgresql:5432/postgres
```

#### 7 - Установим Redis через Helm
- согласно https://docs.bitnami.com/kubernetes/get-started-kubernetes/ используем --set serviceType=NodePort
```bash
helm install redis oci://registry-1.docker.io/bitnamicharts/redis --set serviceType=NodePort --set auth.password=redis
```

#### 8 - Запускаем манифесты - Config Map, Roles, Deployment и Nginx:
```bash
kubectl apply -f secret.yaml
kubectl apply -f config_map.yaml
kubectl apply -f role-bindings.yaml
kubectl apply -f deployment.yaml
kubectl apply -f nginx-ingress.yaml
kubectl apply -f service_monitor.yaml
```

#### 6 - Из корневой директории newman-ом запускаем postman коллекцию ms-hw-9.postman.json:
```bash
cd ..
newman run ms-hw-9.postman.json
```

#### 7 - Проверяем установку прометеус:
```
kubectl port-forward service/prom-kube-prometheus-stack-prometheus 9090:9090
```
В Status -> Service Discovery должны быть:
```
serviceMonitor/default/social-auth/0 (1 / 37 active targets)
serviceMonitor/default/social-core/0 (1 / 37 active targets)
serviceMonitor/default/social-dialog/0 (1 / 37 active targets)
serviceMonitor/default/social-post/0 (1 / 37 active targets)
```

#### 8 - Проверяем установку графаны:
```
kubectl port-forward service/prom-grafana  3000:80
```
Логин / пароль: `admin / prom-operator` <br/>

#### 9 - Импортируем дашборд:
$ `./grafana/DASBOARD__RPC_Latency_Errors.json` <br/>

#### 10 - Скриншоты дашбора под нагрузкой и алерты:
$ `./grafana/` <br/>
