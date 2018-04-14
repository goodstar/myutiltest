############################################################
# Dockerfile 构建web app镜像：tomcat8 + web war
# 基础镜像基于：centos7
############################################################

# 设置基础镜像
FROM base/centos:0.1

# 设置镜像文件作者
MAINTAINER star

# 声明变量
ENV PROJECT_NAME="myutiltest" \
    PACKAGE_NAME="ROOT.war" \
    WEBAPPS_DIR="/apache-tomcat-8.5.30/webapps"


COPY "$PACKAGE_NAME" "$WEBAPPS_DIR"

# 输出一下当前war包md5指纹
RUN echo ">>>>>>>>>>ROOT SIGIN=" &&\
    md5sum "$WEBAPPS_DIR/$PACKAGE_NAME"

# 设置默认工作目录（设置到日志目录，根据习惯自己改）
WORKDIR "$WEBAPPS_DIR/"