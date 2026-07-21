"""
pinoybot.py

PinoyBot: Filipino Code-Switched Language Identifier

This module provides the main tagging function for the PinoyBot project, which identifies the language of each word in a code-switched Filipino-English text. The function is designed to be called with a list of tokens and returns a list of tags ("ENG", "FIL", "CS", or "OTH").

Model training and feature extraction should be implemented in a separate script. The trained model should be saved and loaded here for prediction.
"""

import os
import cloudpickle as pickle
import pandas as pd
from typing import List

# NOTE: if any changes are made to these extraction functions in
#       the notebook, they must also be changed here. as they are
#       used by the data pipeline of the model


# Main tagging function
def tag_language(tokens: List[str]) -> List[str]:
    """
    Tags each token in the input list with its predicted language.
    Args:
        tokens: List of word tokens (strings).
    Returns:
        tags: List of predicted tags ("ENG", "FIL", "CS", or "OTH"), one per token.
    """
    model = pickle.load(open('pinoybot_model_pipeline.pk1', 'rb'))
    tags =  model.predict(pd.DataFrame(tokens, columns=['word']))

    return tags

if __name__ == "__main__":
    # Example usage
    example_tokens = ["Love", "kita", ".", "But", "mas", "mahal", "ko", "ang", "Kopiko", "Blanca", "3", "-", "in", "1", "coffee"]
    print("Tokens:", example_tokens)
    tags = tag_language(example_tokens)

    for i in range(0, len(tags)):
        print(f'{example_tokens[i]} : {tags[i]}')