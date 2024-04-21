# Using debian:bullseye-slim as base for the compilator stage
FROM debian:bullseye-slim AS compilator

LABEL maintainer "Infer team"

# Fix for Debian bug and install necessary packages
RUN apt-get update && \
    mkdir -p /usr/share/man/man1 && \
    apt-get install --yes --no-install-recommends \
      autoconf \
      automake \
      bzip2 \
      cmake \
      curl \
      g++ \
      gcc \
      git \
      libc6-dev \
      libgmp-dev \
      libmpfr-dev \
      libsqlite3-dev \
      sqlite3 \
      make \
      opam \
      openjdk-11-jdk \
      patch \
      patchelf \
      pkg-config \
      python3 \
      python3-distutils \
      unzip \
      xz-utils \
      vim \
      zlib1g-dev && \
    rm -rf /var/lib/apt/lists/*

# Set JAVA_HOME and update PATH
ENV JAVA_HOME /usr/lib/jvm/java-11-openjdk-amd64
ENV PATH $JAVA_HOME/bin:$PATH

# Verify that Java is installed
RUN java -version && javac -version

# Disable sandboxing for opam
RUN opam init --reinit --bare --disable-sandboxing --yes --auto-setup 

# Clone and build Infer
RUN cd / && \
    git clone --depth 1 https://github.com/facebook/infer/ && \
    cd /infer && ./build-infer.sh java --only-setup-opam && \
    ./build-infer.sh java && \
    make install-with-libs BUILD_MODE=opt PATCHELF=patchelf DESTDIR="/infer-release" libdir_relative_to_bindir="../lib"

# Using debian:bullseye-slim as base for the executor stage
FROM debian:bullseye-slim AS executor

# Install runtime dependencies
RUN apt-get update && apt-get install --yes --no-install-recommends sqlite3 vim

# Set JAVA_HOME and update PATH for executor stage
ENV JAVA_HOME /usr/lib/jvm/java-11-openjdk-amd64
ENV PATH $JAVA_HOME/bin:$PATH

# Get the infer release
COPY --from=compilator /infer-release/usr/local /infer

# Install infer
ENV PATH /infer/bin:${PATH}

# If called with /infer-host mounted then copy infer there
RUN if test -d /infer-host; then \
      cp -av /infer/. /infer-host; \
    fi

# Ensure Java is installed in the executor stage
RUN apt-get install --yes openjdk-11-jdk && \
    java -version && javac -version
