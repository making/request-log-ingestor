

```
./mvnw clean package -DskipTests=true

cf create-user-provided-service rl-kafka -p '{"bootstrap-servers":"kafka.example.com:9200", "username":"admin", "password":"secret"}'
cf create-user-provided-service rl-elasticsearch -p '{"url":"https://elasticsearch.example.com", "username":"admin", "password":"secret"}'
cf create-user-provided-service rl-zipkin -p '{"url":"https://zipkin.example.com"}'

cf push
```