import json

with open('.eneik/records/philosophical-falsification-ad0c1fb7-8a8f-4f23-b641-55e54b33e366.json', 'r') as f:
    data = json.load(f)

# Keep only the original 6 + the exact 18 specified above
allowed_new = {
    "BARCAN-TAG-03": ["Andy Clark", "Alva Noë", "Thomas Metzinger", "David Velleman", "Shaun Gallagher", "Susan Hurley"],
    "BARCAN-TAG-04": ["Frank Ramsey", "Richard Jeffrey", "Isaac Levi", "Bas van Fraassen", "Ian Hacking", "Elliott Sober"],
    "BARCAN-TAG-05": ["Derek Parfit", "J. L. Mackie", "Theodore Sider", "Wesley Salmon", "Peter van Inwagen", "Katherine Hawley"]
}

filtered_critiques = []
for c in data['critiques']:
    tag = c.get('roleTag')
    phil = c.get('philosopher')
    if tag == "BARCAN-TAG-09":
        filtered_critiques.append(c)
    elif tag in allowed_new and phil in allowed_new[tag]:
        filtered_critiques.append(c)

data['critiques'] = filtered_critiques

with open('.eneik/records/philosophical-falsification-ad0c1fb7-8a8f-4f23-b641-55e54b33e366.json', 'w') as f:
    json.dump(data, f, indent=2)
