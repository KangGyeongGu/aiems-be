# 로컬 환경 MSA 실행 가이드

## 구성

- Docker · Minikube · kubectl · WireGuard(docker-mac-net-connect)
- 앱별 리소스 조정: `scripts/local/k8s/` manifests

## 구축

```shell
# 1) minikube 기동 (호스트 여유에 맞춰 리소스 조정, 기동 후 메모리 변경 불가)
minikube start --driver=docker --cpus=10 --memory=14g
minikube addons enable ingress
minikube addons enable metrics-server

# 2) 노드 네트워크 직접 라우팅 (tunnel/port-forward 대체)
sudo brew services start docker-mac-net-connect   # 확인: ping 192.168.49.2

# 3) 이미지 빌드 (minikube 내부 도커)
eval $(minikube -p minikube docker-env)
./gradlew bootJar
docker build -t aiems-local-gateway:latest       services/gateway
docker build -t aiems-local-auth:latest          services/auth
docker build -t aiems-local-transfer:latest      services/transfer
docker build -t aiems-local-hospital:latest      services/hospital
docker build -t aiems-local-ambulance:latest     services/ambulance
docker build -t aiems-local-control:latest       services/control
docker build -t aiems-local-ai:latest            services/ai
docker build -t aiems-local-notification:latest  services/notification
docker build -t aiems-local-bed-ingestion:latest services/bed-ingestion

# 4) 오퍼레이터 전제 + 매니페스트 적용
kubectl apply -f https://github.com/cert-manager/cert-manager/releases/latest/download/cert-manager.yaml
kubectl apply -f https://github.com/rabbitmq/cluster-operator/releases/latest/download/cluster-operator.yml
kubectl apply -f scripts/local/k8s/
```

## 담당 서비스 재배포

`<app>` 자리를 각 도메인 앱 네임으로 치환 후 명령 실행

```shell
eval $(minikube -p minikube docker-env)
./gradlew :services:<app>:bootJar
docker build -t aiems-local-<app>:latest services/<app>
kubectl -n aiems rollout restart deployment/<app>
kubectl -n aiems rollout status deployment/<app>
```

## 요청 경로

### 1. IP 및 포트번호 조회

```shell
# 노드 IP 조회 (보통 192.168.49.2)
minikube ip

# ingress 포트 조회 (예: 31148)
kubectl -n ingress-nginx get svc ingress-nginx-controller -o jsonpath='{.spec.ports[?(@.port==80)].nodePort}'
```

### 2. 요청 경로

`http://<node IP>:<ingress port IP>` + 앱별 엔드포인트

| 경로 | 서비스 |
|---|---|
| `/api/v1/auth/**` | auth |
| `/api/v1/ambulance/**` | ambulance |
| `/api/v1/hospital/**` | hospital |
| `/api/v1/transfers/**` | transfer |
| `/ws` | notification (WebSocket) |