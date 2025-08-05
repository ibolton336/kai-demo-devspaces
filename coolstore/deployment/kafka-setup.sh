#!/bin/bash

# Kafka deployment script
set -e

# Create required topics
kafka-topics.sh --create --if-not-exists \
  --bootstrap-server ${KAFKA_BOOTSTRAP_SERVERS} \
  --replication-factor 3 \
  --partitions 6 \
  --topic orders

# Create test topic
kafka-topics.sh --create --if-not-exists \
  --bootstrap-server ${KAFKA_BOOTSTRAP_SERVERS} \
  --replication-factor 3 \
  --partitions 3 \
  --topic orders-test

# Configure topic retention and cleanup policy
kafka-configs.sh --bootstrap-server ${KAFKA_BOOTSTRAP_SERVERS} \
  --entity-type topics \
  --entity-name orders \
  --alter \
  --add-config retention.ms=604800000

# Set up monitoring
echo "Setting up Prometheus JMX exporter..."
cp config/kafka-prometheus-jmx.yml /opt/kafka/config/

# Verify topic creation
kafka-topics.sh --bootstrap-server ${KAFKA_BOOTSTRAP_SERVERS} --list