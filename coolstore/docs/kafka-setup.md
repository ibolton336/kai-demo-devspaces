# Kafka Infrastructure Setup Guide

## Prerequisites
- Kafka broker (version 2.x or higher)
- ZooKeeper ensemble
- Access to configure topics and permissions

## Required Kafka Topics
1. `orders` - Main orders topic for production
2. `orders-test` - Topic for testing environment

## Security Configuration
- Enable SASL/SCRAM authentication
- Configure ACLs for producer/consumer access
- Set up TLS for broker communication

## Monitoring
- Enable JMX metrics
- Configure Prometheus metrics endpoint
- Set up log aggregation

## Environment-Specific Configurations

### Development
```properties
kafka.bootstrap.servers=localhost:9092
kafka.security.protocol=PLAINTEXT
```

### Testing
```properties
kafka.bootstrap.servers=test-kafka:9092
kafka.security.protocol=SASL_SSL
```

### Production
```properties
kafka.bootstrap.servers=prod-kafka:9092
kafka.security.protocol=SASL_SSL
kafka.sasl.mechanism=SCRAM-SHA-512
```

## Monitoring Configuration
- Enable Microprofile metrics
- Configure health checks
- Set up alerting rules

## Required Environment Variables
- `KAFKA_BOOTSTRAP_SERVERS`
- `KAFKA_SECURITY_PROTOCOL`
- `KAFKA_SASL_MECHANISM`
- `KAFKA_USERNAME`
- `KAFKA_PASSWORD`