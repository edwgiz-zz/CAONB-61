# Questions:

1. Git hangs by credentials asking when repository is private 
2. What a desired coverade by unit-tests, in percents ?
2. Should the docker has possibility to be built from Gradle ?
3. Should teh Scitools Understand works through java API or through Pyton ?

# Troubles

## Setup of openssh-server
```
RUN apt-get install -y openssh-server
RUN ssh-keygen -f ~/.ssh/id_rsa -N ''
RUN cp ~/.ssh/id_rsa.pub ~/.ssh/authorized_keys

ENTRYPOINT service ssh restart && echo $PATH > /root/path.txt && sleep 1d
```

# Deploy

http://webserver.devfactory.com:17961/swagger-ui.html


## Docker suggestions

```
docker build -t registry2.swarm.devfactory.com/devfactory/egizatullin:0.2 .

docker push registry2.swarm.devfactory.com/devfactory/egizatullin:0.2
rem check at https://registry2.swarm.devfactory.com/v2/devfactory/egizatullin/tags/list

docker -H tcp://webserver.devfactory.com service rm egizatullin

docker -H tcp://webserver.devfactory.com service create -p 17962:22 -p 17961:80 -p 17963:17963 --name egizatullin --replicas 1 --limit-memory=1G registry2.swarm.devfactory.com/devfactory/egizatullin:0.2

docker -H tcp://webserver.devfactory.com service ls -f name=egizatullin
```