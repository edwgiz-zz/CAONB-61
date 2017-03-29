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
ENV PATH $PATH:/usr/lib/java/jdk1.8.0_121/jre/bin:/usr/lib/java/jdk1.8.0_121/jre/lib/amd64

#Install aws cli
RUN apt-get install -y awscli

#Install application components - SciTools Understand
RUN mkdir /usr/lib/scitools
WORKDIR /usr/lib/scitools
RUN wget \
  --no-cookies \
  http://builds.scitools.com/all_builds/b882/Understand/Understand-4.0.882-Linux-64bit.tgz
RUN tar zxvf Understand-4.0.882-Linux-64bit.tgz
RUN rm -f Understand-4.0.882-Linux-64bit.tgz
RUN mv scitools und
ENV PATH $PATH:/usr/lib/scitools/und/bin/linux64:/usr/lib/scitools/und/bin/linux64/Java
RUN apt-get install -y libglib2.0-0 libxtst6 libxrender1 libxi6
RUN echo '* 3A82C9D4EA7F # any' > /usr/lib/scitools/und/conf/license/users.txt

# Clean up
RUN apt-get remove -y wget
RUN apt-get autoremove -y


RUN mkdir /app
RUN mkdir /app/workdir

WORKDIR /app

COPY api.jar .

EXPOSE 8080

ENTRYPOINT ["java", \
  "-server", \
  "-Dapp.workdir=/app/workdir", \
  "-Dscitools.und.exe.path=/usr/lib/scitools/und/bin/linux64/und", \
  "-jar", "api.jar"]