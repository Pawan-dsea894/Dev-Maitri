FROM registry.access.redhat.com/ubi8/openjdk-17-runtime:1.23-3.1756174615 as builder

USER root

WORKDIR /source
COPY appzillonbankingdocument.jar ./
RUN java -Djarmode=layertools -jar appzillonbankingdocument.jar extract

FROM registry.access.redhat.com/ubi8/openjdk-17-runtime:1.23-3.1756174615 as main

USER root

#Base Package - openjdk-17-runtime:1.23-3.1756174615
RUN microdnf update; microdnf install --nodocs procps unzip fontconfig -y; microdnf clean all;

#Patch Update override by adding packages that needs upgrade
#RUN microdnf update; microdnf install --nodocs avahi-libs openssl-libs nss nss-softokn nss-softokn-freebl nss-sysinit nss-util -y; microdnf clean all;
ENV TZ="Asia/Kolkata"

WORKDIR /app
ARG PROFILE
ENV profile_name=$PROFILE

COPY --from=builder source/dependencies/ ./
COPY --from=builder source/spring-boot-loader/ ./
COPY --from=builder source/snapshot-dependencies/ ./
COPY --from=builder source/application/ ./

COPY reports /app/reports

COPY reports/NotoSansKannada-Regular.ttf reports/Tunga.ttf /usr/share/fonts

RUN chmod -R 755 /usr/share/fonts;

WORKDIR /opt
ADD glowroot-0.14.0-dist.zip .
RUN \ 
    unzip -q glowroot-0.14.0-dist.zip; \
    rm glowroot-0.14.0-dist.zip;

COPY admin.json glowroot/
COPY config.json glowroot/

WORKDIR /app

RUN adduser appzillon; chown -R appzillon:appzillon .;
RUN chmod -R 755  /opt/glowroot/admin.json
RUN chmod -R 755  /opt/glowroot/config.json
RUN chown -R appzillon:appzillon /opt/glowroot

USER appzillon

EXPOSE 9286
ENTRYPOINT ["java", "--add-opens=java.base/java.time=ALL-UNNAMED", "-javaagent:/opt/glowroot/glowroot.jar", "org.springframework.boot.loader.launch.JarLauncher"]
CMD ["--spring.config.location=/app/properties/", "--spring.profiles.active=${profile_name}"]
