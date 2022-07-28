-- H2 query format.

-- #makeTable
-- Creating main table.
CREATE TABLE IF NOT EXISTS `authentic_data` (
    `playerUniqueId`    VARCHAR(255)    NOT NULL,
    `playerName`        VARCHAR(255)    NOT NULL,

    `hashedPassword`    VARCHAR(255)    DEFAULT NULL,
    `licenseId`         VARCHAR(255)    DEFAULT NULL,

    `lastLoggedAddress` VARCHAR(255)    NULL            DEFAULT NULL,
    `sessionEndDate`    TIMESTAMP       NULL            DEFAULT NULL,

    PRIMARY KEY (`playerUniqueId`)
);

-- #updateAccount
-- Updates account in database.
UPDATE `authentic_data` SET `playerUniqueId` = ?, `playerName` = ?, `hashedPassword` = ?, `licenseId` = ?, `lastLoggedAddress` = ?, `sessionEndDate` = ? WHERE playerName = ?;

-- #mapIntoCache
-- Add all accounts into cache.
SELECT * FROM `authentic_data`;

-- #accountByName
-- Returns for an account named a player.
SELECT * FROM `authentic_data` WHERE `playerName` = '%s' LIMIT 1;

-- #accountByUniqueId
-- Returns for an account of the player's identifier.
SELECT * FROM `authentic_data` WHERE `playerUniqueId` = '%s' LIMIT 1;

-- #makeAccount
-- Creating a new account.
INSERT INTO `authentic_data` (
    playerUniqueId,
    playerName,

    hashedPassword,
    licenseId,

    lastLoggedAddress,
    sessionEndDate
) VALUES (?, ?, ?, ?, ?, ?);

-- #dropAccount
-- Removes account from database.
DELETE FROM `authentic_data` WHERE `playerUniqueId` = ?;