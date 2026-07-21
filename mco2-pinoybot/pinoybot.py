"""
pinoybot.py

PinoyBot: Filipino Code-Switched Language Identifier

This module provides the main tagging function for the PinoyBot project, which identifies the language of each word in a code-switched Filipino-English text. The function is designed to be called with a list of tokens and returns a list of tags ("ENG", "FIL", "CS", or "OTH").

Model training and feature extraction should be implemented in a separate script. The trained model should be saved and loaded here for prediction.
"""

import os
import pickle
import pandas as pd
from typing import List

# NOTE: if any changes are made to these extraction functions in
#       the notebook, they must also be changed here. as they are
#       used by the data pipeline of the model

def extract_has_consec_aui(mydf):
    df = mydf.copy()

    df['consec_aui'] = False

    for index, row in df.iterrows():
        word = row.loc['word'].strip()

        found = True
        prev_char = None

        char_checks = ['a', 'i', 'u']

        for char in word:
            if prev_char == char and char in char_checks:
                df.loc[index, 'consec_aui'] = True
                found = True
                break
            prev_char = char

    return df

def extract_has_xz(mydf):
    df = mydf.copy()

    df['has_x'] = False
    df['has_z'] = False

    for index, row in df.iterrows():
        word = row.loc['word'].strip()

        for char in word:
            match char:
                case 'x':
                    df.loc[index, 'has_x'] = True
                case 'z':
                    df.loc[index, 'has_z'] = True
    
    return df

def extract_repeating_prefix(mydf):
    df = mydf.copy()

    df['rpt_prfx'] = False

    for index, row in df.iterrows():
        word = row.loc['word'].strip()

        for i in range(2,5):
            if word[0:i] == word[i:i+i]:
                df.loc[index, 'rpt_prfx'] = True

    return df

def extract_repeating_first_letter(mydf):
    df = mydf.copy()

    df['rpt_fchr'] = False

    for index, row in df.iterrows():
        word = row.loc['word'].strip()

        if word[0:1] == word[1:2]:
            df.loc[index, 'rpt_prfx'] = True

    return df

def extract_has_num_or_spec_char(mydf):
    df = mydf.copy()

    df['has_num'] = False
    df['has_spec'] = False

    special_characters = [
        '!', '"', '#', '$', '%', '&', "'", '(', ')', '*', 
        '+', ',', '-', '.', '/', ':', ';', '<', '=', '>', 
        '?', '@', '[', '\\', ']', '^', '_', '`', '{', '|', 
        '}', '~'
    ]

    numbers = [str(i) for i in range(0, 10)]

    for index, row in df.iterrows():
        word = row.loc['word'].strip()

        for char in word:
            if char in numbers:
                df.loc[index, 'has_num'] = True
            if char in special_characters:
                df.loc[index, 'has_spec'] = True

    return df

def extract_features(df):
    df = extract_has_consec_aui(df)
    df = extract_has_num_or_spec_char(df)
    df = extract_has_xz(df)
    df = extract_repeating_first_letter(df)
    df = extract_repeating_prefix(df)
    return df

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