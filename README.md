# Questions:

1. Git hangs by credentials asking when repository is private 
2. What a desired coverade by unit-tests, in percents ?
2. Should the docker has possibility to be built from Gradle ?
3. Should teh Scitools Understand works through java API or through Pyton ?

# Troubles


```
 c:\Projects\Aurea\mydockerbuild>docker service create -p 8280:8080 --name egizatullin --replicas 1 --limit-memory=1G registry2.swarm.devfactory.com/v2/devfactory/egizatullin:latest
 9e1bl3nj62akyghfia8t7x0jl 
```

# Deploy
1. Fails by aws cli and git installations

```
E: Failed to fetch http://archive.ubuntu.com/ubuntu/pool/main/f/freetype/libfreetype6_2.5.2-1ubuntu2.5_amd64.deb  404  Not Found [IP: 91.189.88.152 80]
E: Failed to fetch http://archive.ubuntu.com/ubuntu/pool/main/g/git/git-man_1.9.1-1ubuntu0.3_all.deb  404  Not Found [IP: 91.189.88.152 80]
```


http://webserver.devfactory.com:8280/swagger-ui.html


# Docker suggestions

```
docker build -t registry2.swarm.devfactory.com/v2/devfactory/egizatullin:0.1 .
docker push registry2.swarm.devfactory.com/devfactory/egizatullin:0.1
rem check at https://registry2.swarm.devfactory.com/v2/devfactory/egizatullin/tags/list

docker -H tcp://webserver.devfactory.com service rm egizatullin

docker -H tcp://webserver.devfactory.com service create -p 8280:8080 --name egizatullin --replicas 1 --limit-memory=1G registry2.swarm.devfactory.com/devfactory/egizatullin:0.1
```


```
docker -H tcp://build.swarm.devfactory.com build -t registry2.swarm.devfactory.com/v2/devfactory/egizatullin:latest C:\Projects\Aurea\CAONB-61


docker stop egizatullin
docker rm egizatullin
docker rmi -f egizatullin:latest

rem docker build -t egizatullin C:\Projects\Aurea\CAONB-61
rem docker run --name egizatullin -p 8080:8080 egizatullin:latest

docker -H tcp://build.swarm.devfactory.com pull registry2.swarm.devfactory.com/v2/devfactory/egizatullin
docker -H tcp://webserver.devfactory.com service rm egizatullin
docker -H tcp://webserver.devfactory.com service create -p 8280:8080 --name egizatullin --replicas 1 --limit-memory=1G registry2.swarm.devfactory.com/v2/devfactory/egizatullin:latest

rem docker tag egizatullin:latest registry2.swarm.devfactory.com/devfactory/egizatullin:latest
rem docker push registry2.swarm.devfactory.com/v2/devfactory/egizatullin:latest
rem https://registry2.swarm.devfactory.com/v2/devfactory/egizatullin/tags/list
```