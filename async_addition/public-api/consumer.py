import json
import threading
import os
import traceback
import time
from kafka.errors import NoBrokersAvailable
from kafka import KafkaConsumer, errors
from components import CalculationResult


message_cache = {} # Cache dictionary. Ideally we would want to use a database for storing this information
thread_lock = threading.Lock() # For threadsafe IO
new_message_event = threading.Event() # For notifications when we get a new message to the cahce


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
        calculation_data = message.value
        calculation_result = CalculationResult(**calculation_data)

        # Threadsafe caching
        with thread_lock:
            message_cache[calculation_data['asyncId']] = calculation_result

        new_message_event.set() # Notifies the waiting threads that we have a new message to read
        print(f"Stored result for UUID {calculation_data['asyncId']}: {calculation_result}")


# Blocks until receives a new result in the cache is available or until timeout is reached
async def wait_for_new_message(timeout=30):
    new_message_event.wait(timeout)
    new_message_event.clear()


# Retrieves the calculation result associated with a particular asyncId from the message_cache
async def wait_for_result(async_id, timeout=300):
    start_time = time.time()
    while True:
        if time.time() - start_time > timeout: # Just in case we never get a result
            break
        await wait_for_new_message()
        with thread_lock:
            if async_id in message_cache:
                return message_cache[async_id]


def start_kafka_consumer():
    # Kafka consumer setup and message processing loop
    consumer = create_kafka_consumer()
    consume_messages(consumer)
