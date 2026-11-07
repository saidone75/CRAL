FROM clojure:temurin-21-tools-deps-bookworm-slim
RUN apt-get update && apt-get install -y --no-install-recommends curl coreutils git \
    && rm -rf /var/lib/apt/lists/*
WORKDIR /work
