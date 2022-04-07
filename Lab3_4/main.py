import numpy as np
import random
import pandas as pd
import os
import nickname_generator


# generating txt file with dates.txt since the release date till the end of 2022
def generate_dates_file():
    with open("dates.txt", 'w') as file:
        for year in range(2019, 2023):
            for month in range(1, 13):
                if month in {1, 3, 5, 7, 8, 10, 12}:
                    end_range = 31
                elif month in {4, 6, 9, 11}:
                    end_range = 30
                else:
                    end_range = 28
                for day in range(1, end_range + 1):
                    file.write(f'{day}-{month}-{year}\n')


# list of characters
CHARACTERS = ['Bangalore', 'Bloodhound', 'Gibraltar', 'Lifeline',
              'Pathfinder', 'Wraith', 'Caustic', 'Mirage', 'Octane',
              'Wattson', 'Crypto', 'Revenant', 'Rampart', 'Horizon',
              'Fuse', 'Valkyrie', 'Ash', 'Mad Maggie']

DATES = pd.read_csv('dates.txt', sep='\n', header=None)
NICKS = pd.read_csv('nicks.txt', sep='\n', header=None)


def get_random_player_characters():
    """
    Player has 6 start characters + random number of other random characters
    :return: list of characters owned by player
    """
    player_characters = CHARACTERS[:6]
    for index in random.sample(range(6, 18), np.random.randint(0, 13)):
        player_characters.append(CHARACTERS[index])

    return player_characters


def get_random_battle_royal(player_characters, i):
    """
    Generates random battle_royal entry (date, timeInGame, hero(character), kills, assists, damage, position)
    :param player_characters:
    :return: string with game stats in csv format (item1,item2,item3,...,item_n\n)
    """
    date = DATES[0][np.random.randint(0, len(DATES))]
    time_in_game = round(np.random.random() * 30, 2)
    hero = player_characters[np.random.randint(0, len(player_characters))]
    kills_n = np.random.randint(0, 10)
    assists_n = np.random.randint(0, 10)
    kills_p = random.random()
    assists_p = random.random()
    kills_xd = random.random() / 10
    assists_xd = random.random() / 10
    kills = int(time_in_game * kills_xd) + np.random.binomial(kills_n, kills_p) + i % 7
    assists = int(time_in_game * assists_xd) + np.random.binomial(assists_n, assists_p) + i % 7
    damage_ranges = (np.random.randint(120, 476), np.random.randint(51, 243))
    damage = sum([np.random.binomial(damage_ranges[0], 0.5) + i % 200 for _ in range(kills)] + [np.random.binomial(damage_ranges[0], 0.4) + i % 200 for _ in range(assists)])
    position_list = [pos for pos in range(20, 0, -1)]
    position = position_list[np.random.randint(0, 20)]  # spróbuj coś lepszego wymyśleć xD
    return f'{date},{time_in_game},{hero},{kills},{assists},{damage},{position}\n'


def generate_player_battle_royal_history():
    """
    Generates player's battle royal history (2500 games per player) for all 571 players.
    Creates 571 txt files with battle royal game history (one for each player).
    """
    # with open('nicks_test.txt', 'r') as players:
    with open('player_info.txt', 'r') as players:
        for line in players.readlines():
            player = line.split(',')[0]
            player_characters = get_random_player_characters()
            # with open(os.path.join(f'{player.strip()}.csv'), 'w') as game_history:
            with open(os.path.join('game_history_files', f'{player.strip()}.csv'), 'w') as game_history:
                for i in range(2500):
                    game_history.write(get_random_battle_royal(player_characters, i))


def generate_player_arena_history():
    pass


def generate_players():
    """
    Returns txt file with entries for instantiating players (nick, platform, rank)
    :return: player info txt
    """
    platforms = ['PC', 'Playstation', 'Xbox', 'Nintendo Switch']
    ranks = ['Bronze', 'Silver', 'Gold', 'Platinum']
    with open('player_info.txt', 'w') as player_info:
        for player in NICKS[0]:
            print(player)
            player_info.write(f'{player},{np.random.choice(platforms)},{np.random.choice(ranks)}\n')


def test_normalization(scores):
    for score in scores:
        yield 5 * (score - min(scores)) / (max(scores) - min(scores))


scores = [10, 5, 7, 9, 11, 12, 18, 3, 2]


def add_new_nicks():
    with open('nicks.txt', 'a') as file:
        for _ in range(1500):
            file.write(f'{nickname_generator.generate()}\n')


def get_grades_percentage(ids, points, max_points):
    return {id: grade for id, grade in zip(ids, filter(lambda point: point > 50, map(lambda point: (point / max_points * 100), points)))}


def test_reader():
    with open('player_info.txt', 'r') as file:
        for line in file.readlines():
            parsed_line = line.split(',')
            print(f'{parsed_line[0]=} {parsed_line[1]=} {parsed_line[2]=}')


if __name__ == '__main__':
    generate_player_battle_royal_history()