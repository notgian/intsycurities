import random
import time
from typing import Dict
import numpy as np
import pygame
from utility import play_q_table
from cat_env import make_env
#############################################################################
# TODO: YOU MAY ADD ADDITIONAL IMPORTS OR FUNCTIONS HERE.                   #
#############################################################################








#############################################################################
# END OF YOUR CODE. DO NOT MODIFY ANYTHING BEYOND THIS LINE.                #
#############################################################################

def train_bot(cat_name, render: int = -1):
    env = make_env(cat_type=cat_name)
    
    # Initialize Q-table with all possible states (0-9999)
    # Initially, all action values are zero.
    q_table: Dict[int, np.ndarray] = {
        state: np.zeros(env.action_space.n) for state in range(10000)
    }

    # Training hyperparameters
    episodes = 5000 # Training is capped at 5000 episodes for this project
    
    #############################################################################
    # TODO: YOU MAY DECLARE OTHER VARIABLES AND PERFORM INITIALIZATIONS HERE.   #
    #############################################################################
    # Hint: You may want to declare variables for the hyperparameters of the    #
    # training process such as learning rate, exploration rate, etc.            #
    #############################################################################
    
    learning_rate = 0.5
    discount_factor = 0.9

    
    #############################################################################
    # END OF YOUR CODE. DO NOT MODIFY ANYTHING BEYOND THIS LINE.                #
    #############################################################################
    
    for ep in range(1, episodes + 1):
        ##############################################################################
        # TODO: IMPLEMENT THE Q-LEARNING TRAINING LOOP HERE.                         #
        ##############################################################################
        # Hint: These are the general steps you must implement for each episode.     #
        # 1. Reset the environment to start a new episode.                           #
        # 2. Decide whether to explore or exploit.                                   #
        # 3. Take the action and observe the next state.                             #
        # 4. Since this environment doesn't give rewards, compute reward manually    #
        # 5. Update the Q-table accordingly based on agent's rewards.                #
        ############################################################################## 
               
        state = env.reset()[0]
        done = False

        while not done:
            # choose action
            if np.max(q_table[state]) > 0: 
                action = np.argmax(q_table[state])
            else:
                action = env.action_space.sample()
            
            # take next step
            new_state, reward, terminated, truncated, info = env.step(action)

            # compute reward manually
            bot_pos_prev = state // 100
            bot_x_prev = bot_pos_prev // 10
            bot_y_prev = bot_pos_prev % 10

            cat_pos_prev = state - (bot_pos_prev * 100)
            cat_x_prev = cat_pos_prev // 10
            cat_y_prev = cat_pos_prev % 10

            bot_pos = new_state // 100
            bot_x = bot_pos // 10
            bot_y = bot_pos % 10

            cat_pos = new_state - (bot_pos * 100) 
            cat_x = cat_pos // 10
            cat_y = cat_pos % 10

            manhattan_prev = abs(cat_x_prev - bot_x_prev) + abs(cat_y_prev - bot_y_prev)
            manhattan = abs(cat_x - bot_x) + abs(cat_y - bot_y)

            if bot_pos == cat_pos:
                reward = 10000
            elif manhattan > manhattan_prev:
                reward = -2
            elif manhattan <= manhattan_prev:
                reward = -1
            
            # update Q(s,a)
            q_table[state][action] = q_table[state][action] + \
                                     learning_rate * (reward + discount_factor * np.max(q_table[new_state]) - q_table[state][action])

            state = new_state
            done = terminated or truncated
        
        #############################################################################
        # END OF YOUR CODE. DO NOT MODIFY ANYTHING BEYOND THIS LINE.                #
        #############################################################################

        # If rendering is enabled, play an episode every 'render' episodes
        if render != -1 and (ep == 1 or ep % render == 0):
            viz_env = make_env(cat_type=cat_name)
            play_q_table(viz_env, q_table, max_steps=100, move_delay=0.02, window_title=f"{cat_name}: Training Episode {ep}/{episodes}")
            print('episode', ep)

    return q_table
