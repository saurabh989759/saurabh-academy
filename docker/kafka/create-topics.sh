#!/bin/bash

# Script to create Kafka topics after broker is ready
# This script waits for Kafka to be available and then creates required topics

KAFKA_BOOTSTRAP_SERVER="${KAFKA_BOOTSTRAP_SERVER:-localhost:9092}"
MAX_RETRIES=30
RETRY_INTERVAL=5

echo "Waiting for Kafka broker to be ready at $KAFKA_BOOTSTRAP_SERVER..."

# Wait for Kafka to be ready
for i in $(seq 1 $MAX_RETRIES); do
  if kafka-broker-api-versions --bootstrap-server "$KAFKA_BOOTSTRAP_SERVER" > /dev/null 2>&1; then
    echo "Kafka broker is ready!"
    break
  fi
  if [ $i -eq $MAX_RETRIES ]; then
    echo "ERROR: Kafka broker is not ready after $MAX_RETRIES attempts"
    exit 1
  fi
  echo "Attempt $i/$MAX_RETRIES: Kafka broker not ready, waiting ${RETRY_INTERVAL}s..."
  sleep $RETRY_INTERVAL
done

# Function to create topic if it doesn't exist
create_topic() {
  local topic_name=$1
  local partitions=$2
  local replication_factor=$3
  
  echo "Checking if topic '$topic_name' exists..."
  
  # Check if topic exists
  if kafka-topics --bootstrap-server "$KAFKA_BOOTSTRAP_SERVER" --list | grep -q "^${topic_name}$"; then
    echo "Topic '$topic_name' already exists, skipping creation."
  else
    echo "Creating topic '$topic_name' with $partitions partitions and replication factor $replication_factor..."
    kafka-topics --create \
      --bootstrap-server "$KAFKA_BOOTSTRAP_SERVER" \
      --topic "$topic_name" \
      --partitions "$partitions" \
      --replication-factor "$replication_factor" \
      --if-not-exists
    
    if [ $? -eq 0 ]; then
      echo "Successfully created topic '$topic_name'"
    else
      echo "ERROR: Failed to create topic '$topic_name'"
      exit 1
    fi
  fi
}

# Create all required topics
echo "Creating Kafka topics..."

create_topic "student.registered" 3 1
create_topic "mentor.session.created" 3 1
create_topic "batch.created" 3 1

echo "All topics created successfully!"
echo "Listing all topics:"
kafka-topics --bootstrap-server "$KAFKA_BOOTSTRAP_SERVER" --list

exit 0

