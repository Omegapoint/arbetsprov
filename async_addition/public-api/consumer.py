import json
import threading
import os
import traceback
from kafka.errors import NoBrokersAvailable
from kafka import KafkaConsumer

# Cache dictionary. Ideally we would want to use a database for storing this information
message_cache = {}
thread_lock = threading.Lock()


# Creates the Kafka consumer
def create_kafka_consumer():
    try:
        print("Attempting to initialize Kafka consumer")
        consumer = KafkaConsumer(
            'addition-service.results',
            group_id='public-api-group',
            bootstrap_servers=['localhost:9092'],
            value_deserializer=lambda m: json.loads(m.decode('utf-8')),
            auto_offset_reset='earliest',
            api_version=(3, 6, 0)
        )
        print("Kafka Consumer is connected and ready.")
        return consumer
    except NoBrokersAvailable as e:
        print(f"An exception occurred: {type(e).__name__}")
        print(f"Error message: {str(e)}")
        traceback.print_exc()
        os._exit(1)  # Sort of hacky way to exit; ungraceful shutdown

# Query Kafka for new messages and stores them with their UUID and result in the cache


def consume_messages(consumer):
    for message in consumer:
        print(message)
        uuid = message.value['asyncId']
        result = message.value['result']

        # Threadsafe caching
        with thread_lock:
            message_cache[uuid] = result
        print(f"Stored result for UUID {uuid}: {result}")


def start_kafka_consumer():
    # Kafka consumer setup and message processing loop
    consumer = create_kafka_consumer()
    consume_messages(consumer)
