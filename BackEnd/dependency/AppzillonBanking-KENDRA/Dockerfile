FROM registry.access.redhat.com/ubi8/openjdk-17-runtime:1.23-3.1756174615 as builder

USER root

WORKDIR /source
COPY appzillonbankingkendra.jar ./
RUN java -Djarmode=layertools -jar appzillonbankingkendra.jar extract

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

RUN adduser appzillon; chown -R appzillon:appzillon .;

USER appzillon

EXPOSE 9290
ENTRYPOINT ["java", "--add-opens=java.base/java.time=ALL-UNNAMED", "org.springframework.boot.loader.launch.JarLauncher"]
CMD ["--spring.config.location=/app/properties/", "--spring.profiles.active=${profile_name}"]
