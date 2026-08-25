# 테스트 가이드

## 시나리오

- `scenario-a-ws-capacity.js`: minikube 클러스터 ws 세션 연결 부하 측정 시나리오
- `scenario-b-transfer-flow.js`: 통합 이송 흐름(transfer) 간 앱 부하 측정 시나리오 


## 구성

- k6, Docker, Minikube, WireGuard(docker-mac-net-connect)


## 구축 (macOS)

### 1. minikube 설치
- [minikube start](https://minikube.sigs.k8s.io/docs/start/?arch=%2Fmacos%2Farm64%2Fstable%2Fbinary+download)

### 2. L3 WireGuard 설치
```
brew install chipmk/tap/docker-mac-net-connect
```

### 3. minikube 시작 (docker driver)
```shell
# 로컬 컴퓨터 리소스에 따라 docker 리소스 조정 후 minikube 몫으로 할당
minikube start --driver=docker --cpus=10 --memory=14g

# ingress controller 추가
minikube addons enable ingress

# 메트릭 집계용 서버 추가
minikube addons enable metrics-server
```

### 4. WireGuard 기동

```shell
# WireGuard 기동
sudo brew services start docker-mac-net-connect

# 확인
ping 192.168.49.2             # 응답 확인
netstat -rn | grep 192.168.49 # `utun` 경로 확인

# minikube 재실행 시,
sudo brew services restart docker-mac-net-connect
```

### 5. MSA App Deploy

```shell
# minikube 내부 도커로 전환
eval $(minikube -p minikube docker-env)

# 전 서비스 jar 빌드 (각 services/<svc>/build/libs/*-SNAPSHOT.jar 생성)
./gradlew bootJar

# 서비스별 이미지 빌드 (매니페스트가 aiems-local-<svc>:latest 를 IfNotPresent로 참조)
docker build -t aiems-local-gateway:latest       services/gateway
docker build -t aiems-local-auth:latest          services/auth
docker build -t aiems-local-transfer:latest      services/transfer
docker build -t aiems-local-hospital:latest      services/hospital
docker build -t aiems-local-ambulance:latest     services/ambulance
docker build -t aiems-local-control:latest       services/control
docker build -t aiems-local-ai:latest            services/ai
docker build -t aiems-local-notification:latest  services/notification
docker build -t aiems-local-bed-ingestion:latest services/bed-ingestion

# cert-manager
kubectl apply -f https://github.com/cert-manager/cert-manager/releases/latest/download/cert-manager.yaml
# RabbitMQ cluster-operator
kubectl apply -f https://github.com/rabbitmq/cluster-operator/releases/latest/download/cluster-operator.yml

# manifests 적용
kubectl apply -f scripts/local/k8s/
```

### 6. TARGET 설정

```shell
export TARGET="$(minikube ip):$(kubectl -n ingress-nginx get svc ingress-nginx-controller -o jsonpath='{.spec.ports[?(@.port==80)].nodePort}')"
echo "$TARGET"   # 예: 192.168.49.2:31148
```


## 시나리오 실행

> 시나리오 A

```shell
k6 run -e MAX=10000 -e RAMP=100s -e HOLD=60s -e TARGET=$TARGET scripts/loadtest/scenario-a-ws-capacity.js
```

> 시나리오 B

```shell
k6 run -e HOSPITALS=527 -e AMBULANCES=1600 -e RATE=30 -e RAMP=30s -e HOLD=25s -e WS_RAMP=30s -e TARGET=$TARGET scripts/loadtest/scenario-b-transfer-flow.js
```