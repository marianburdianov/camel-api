# mburdianov-camel-api

This API project was generated using MS3's [Camel OpenAPI Archetype](https://github.com/MS3Inc/camel-archetypes), version 0.2.7.

## Kubernetes

### Prerequisites:

* Create a folder with path: d/tmp/mysql/data.
* If your database has other password and username than:  password, root, then you should change
  it application.yaml and mysql-secrets.yaml files.
* If your database uses other port than 3306, you should change it in files: mysql-config-map.yaml, mysql-persistence.yaml,
  application.yaml.
* Install on your machine Docker and enable there Kubernetes.
* Create an image of your api: ```docker build -t <docker-hub-account-name>/<repo-name> .```

### Process of starting application with Kubernetes (no Ingress):

1) Make sure kubernetes is ready, type in the terminal: <br>```kubectl cluster-info ```
2) Now, when we know that kubernetes is working properly, you have to be in the same directory as your
   kubernetes yaml files.

3) Create secret keys, type in the terminal:  
   ```kubectl apply -f ./mysql-secrets.yaml```

4) Create configuration-map:  
   ```kubectl apply -f ./mysql-config-map.yaml```

5) Create database:  
   ```kubectl apply -f ./mysql-persistence.yaml```

6) Create deployment:
   ```kubectl apply -f ./deployment.yaml```
   <br>

7) Your application is starting, but you will not be able to access it from the outside. What we need to do
   is port-forwarding. Type in the terminal: <br>```kubectl get pods```
   <br> You should see something like:
```
NAME                            READY   STATUS    RESTARTS   AGE
camel-project-59545c685-9r2b5   1/1     Running   0          135m
mysql-sfs-0                     1/1     Running   0          157m

```

8) You need only the first pod for port-forwarding: <br>
   ```kubectl port-forward pod/camel-project-59545c685-9r2b5 9000:9000```

9) You should be able to send requests by this url: localhost:9000/api/...
<br>

   -----------------------
11) *Ingress, install controller:
    <br>```kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/controller-v0.45.0/deploy/static/provider/cloud/deploy.yaml```

12) To check which port you have to use, write in command port: ```kubectl get ingress```
    Under the column PORTS you will see port, which you have to use.

13) When you use ingress to access api, always add header ```host: camel-api.com```


### Getting Started

**Running on the Command Line**

```
mvn spring-boot:run
```

<!-- 
**Running Locally using IDE**

This project uses Spring profiles, and corresponding sow-sys-api-<env>.yaml files.

Use the following environment variables: 
   * ```spring.profiles.active=<env>```
   * ```spring.config.name=sow-sys-api```

**Running on Command Line**

```
mvn spring-boot:run -Dspring-boot.run.profiles=<env> -Dspring-boot.run.arguments="--spring.config.name=sow-sys-api"
```
-->

### Actuator Endpoints

To access the list of available Actuator endpoints, go to: http://localhost:8080/actuator or `{{url}}/actuator`

The available endpoints are as follows:

* `/health`
* `/metrics`
* `/info`

#### Metrics

List of available metrics can be found here: http://localhost:8080/actuator/metrics/

Add the metric name in `/metrics/<metric name>` to access the metric for that particular topic.

Sample metric: http://localhost:8080/actuator/metrics/jvm.memory.used

```
{
    "name": "jvm.memory.used",
    "description": "The amount of used memory",
    ...
}
```

### Contact

* Name (email)