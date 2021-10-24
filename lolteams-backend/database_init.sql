DROP TABLE IF EXISTS GameServer;

CREATE TABLE GameServer (
    Id serial PRIMARY KEY,
    Name VARCHAR(255) UNIQUE NOT NULL,
    Abbreviation VARCHAR(16) UNIQUE NOT NULL
);

INSERT INTO GameServer (Name, Abbreviation)
VALUES
  ('Brazil', 'BR'),
  ('Europe Nordic & East', 'EUNE'),
  ('Europe West', 'EUW'),
  ('Latin America North', 'LAN'),
  ('Latin America South', 'LAS'),
  ('North America', 'NA'),
  ('Oceania', 'OCE'),
  ('Russia', 'RU'),
  ('Turkey', 'TR'),
  ('Japan', 'JP'),
  ('Republic of Korea', 'KR');

DROP TABLE IF EXISTS UserAccount;

CREATE TABLE UserAccount (
    Id serial PRIMARY KEY,
    Username VARCHAR(16) UNIQUE NOT NULL,
    Password VARCHAR(162) NOT NULL,
    Email VARCHAR(255) UNIQUE NOT NULL,
    GameServerId INTEGER REFERENCES GameServer (Id) NOT NULL,
    InGameName VARCHAR(16) UNIQUE NOT NULL
);

INSERT INTO UserAccount (Username, Password, Email, GameServerId, InGameName)
VALUES
  ('landonjw', 'bcrypt+sha512$6551ad566a5a72fe735c634030b656fe$12$47b760161dceae78e9529138dd33cdfa05c19a34e2acea6d', 'landonjwdev@gmail.com', 6, 'Landaddy');