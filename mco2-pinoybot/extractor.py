# This file will hold all the functions used for feature extraction
# and will help ensure the integrity of the data pipeline in case 
# any changes will be made to any of the extraction functions at
# any point in time.

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