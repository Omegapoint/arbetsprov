from fastapi import FastAPI, HTTPException
import httpx
import uuid
import uvicorn
import threading
from typing import Union
from consumer import start_kafka_consumer, message_cache, thread_lock


app = FastAPI(title="Public API for Async Addition")


# FastAPI endpoint to receive two numbers and forward them to the internal API
@app.post("/add")
async def add_numbers(numberOne: Union[int, float], numberTwo: Union[int, float]):
    try:
        async with httpx.AsyncClient() as client:
            response = await client.post("http://localhost:3000/add", json={"numberOne": numberOne, "numberTwo": numberTwo})
            response.raise_for_status()
            uuid = response.json()['asyncId']
            return {"asyncId": uuid}
    except httpx.ConnectError:
        raise HTTPException(
            status_code=503, detail="Unable to connect to addition-service")
    except httpx.HTTPStatusError as e:
        # Handle specific HTTP errors (e.g., 4xx, 5xx responses)
        raise HTTPException(status_code=e.response.status_code, detail=str(e))
    except Exception as e:
        # Catch other exceptions and handle them
        raise HTTPException(
            status_code=500, detail="An unexpected error occurred")


# Used to validate that the input string is an UUID
def is_valid_uuid(uuid_to_test, version=4):
    try:
        uuid_obj = uuid.UUID(uuid_to_test, version=version)
        return str(uuid_obj) == uuid_to_test
    except ValueError:
        return False

# FastAPI endpoint to get the result using UUID


@app.get("/result/{uuid}")
async def get_result(uuid: str):
    if not is_valid_uuid(uuid):
        raise HTTPException(status_code=400, detail="Invalid UUID format")

    with thread_lock:
        cached_result = message_cache.get(uuid)
        if cached_result:
            print("Found value stored in cache")
            return {"result": cached_result}

    raise HTTPException(status_code=404, detail="Result not found")


def run_kafka_consumer_thread():
    kafka_thread = threading.Thread(target=start_kafka_consumer)
    kafka_thread.start()


if __name__ == "__main__":
    run_kafka_consumer_thread()
    uvicorn.run(app, host="0.0.0.0", port=8443, log_level="debug")
