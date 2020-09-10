FROM bde2020/spark-submit:3.0.0-hadoop3.2

LABEL maintainer="Gezim Sejdiu <g.sejdiu@gmail.com>, Giannis Mouchakis <gmouchakis@gmail.com>"

ARG SBT_VERSION
ENV SBT_VERSION=${SBT_VERSION:-1.3.12}

RUN wget -O - https://piccolo.link/sbt-1.3.12.tgz | gunzip | tar -x -C /usr/local

ENV PATH /usr/local/sbt/bin:${PATH}

WORKDIR /app

# Pre-install base libraries
ADD build.sbt /app/
ADD project /app/project/
ADD common /app/common/
ADD snapshotComposer /app/snapshotComposer/
ADD visitLogGenerator /app/visitLogGenerator/

RUN sbt compile

CMD ["sbt test"]