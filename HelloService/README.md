# CBC Demo Loan Service - Backend API


## To Deploy

1. Create a OCP Project if not done:
    ```
    oc new-project backend-api

    ```
2. Go to project folder and run the following command to deploy:
    
    ```
    ./mvnw clean && \
    ./mvnw install \
    -Dquarkus.kubernetes.deploy=true \
    -Dquarkus.container-image.group=backend-api \
    -Dquarkus.openshift.namespace=backend-api \
    -Dquarkus.log.category.\"blog.braindose\".level=DEBUG \
    -Dquarkus.container-image.registry=image-registry.openshift-image-registry.svc:5000 \
    -Dquarkus.openshift.ports.http.container-port=8080 \
    -Dquarkus.openshift.ports.http.host-port=8080
    
    ```
3. Test the API internally:

    ```
    curl "https://hello-api-user1-apicast-staging.apps.cluster-hk42t.hk42t.sandbox2512.opentlc.com:443/hello/get/gan?user_key=fb6c1be79331ee4bc395c3f7d1c7036d"

    ```
