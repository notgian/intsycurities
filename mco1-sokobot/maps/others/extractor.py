filenames = ['microban-maps.txt', 'microcosmos-maps.txt', 'nabokosmos-maps.txt']

for filename in filenames:
    with open(filename, 'r') as f: 
        lines = f.readlines()
        maps = list()

        current_map = None

        for line in lines:
            if line.startswith('--'):
                continue

            if line.strip() == '' and current_map == None:
                continue

            if line.startswith('Level'):
                current_map = ''
                continue

            if line.strip() == '' and current_map != None:
                maps.append(current_map)
                current_map = None
                continue

            current_map += f'{line}'
        
        map_group_name = filename.split('-')[0].strip()

        i = 1
        for m in maps:
            with open(f'{map_group_name}_{i}.txt', 'w') as f:
                f.write(m)
            i += 1
