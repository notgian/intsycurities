

import os
from typing import List

import joblib


BASE_DIRECTORY = os.path.dirname(
    os.path.abspath(__file__)
)

MODEL_PATH = os.path.join(
    BASE_DIRECTORY,
    "trained_model.pkl"
)

def load_model():
    if not os.path.exists(MODEL_PATH):
        raise FileNotFoundError(
            f"Model not found at: {MODEL_PATH}\n"
            "Run intsycure.ipynb first to create "
            "trained_model.pkl."
        )

    return joblib.load(MODEL_PATH)


model = load_model()


def tag_language(tokens: List[str]) -> List[str]:
  
    if not isinstance(tokens, list):
        raise TypeError(
            "tokens must be a list"
        )

    if not all(
        isinstance(token, str)
        for token in tokens
    ):
        raise TypeError(
            "Every token must be a string"
        )

    if len(tokens) == 0:
        return []

    predictions = model.predict(tokens)

    return predictions.tolist()


if __name__ == "__main__":
    tokens = [
        "Love",
        "kita",
        "."
    ]

    print("Tokens:", tokens)
    print("Tags:", tag_language(tokens))