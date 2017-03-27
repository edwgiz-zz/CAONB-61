FROM ubuntu:14.04

RUN apt-get update

# Install Java 8
RUN apt-get install -y wget
RUN mkdir /usr/lib/java
WORKDIR /usr/lib/java
RUN wget \
  --no-check-certificate \
  --no-cookies \
  --header "Cookie: oraclelicense=accept-securebackup-cookie" \
  http://download.oracle.com/otn-pub/java/jdk/8u121-b13/e9e7ea248e2c4826b92b3f075a80e441/server-jre-8u121-linux-x64.tar.gz
RUN tar zxvf server-jre-8u121-linux-x64.tar.gz
RUN rm -f server-jre-8u121-linux-x64.tar.gz
ENV PATH /usr/lib/java/jdk1.8.0_121/bin/:$PATH

#Install aws cli
RUN apt-get install -y awscli

# Clean up
RUN apt-get remove -y wget
RUN apt-get autoremove -y


#Install application components
RUN apt-get install -y git

RUN mkdir /app
RUN mkdir /app/work
RUN mkdir /app/work/github

WORKDIR /app

COPY api.jar .

EXPOSE 8080

ENTRYPOINT ["java", \
  "-server", \
  "-Dapp.git.exe=/usr/lib/git-core/git", \
  "-Dapp.git.workdir.root=/app/work/github", \
  "-jar", "api.jar"]