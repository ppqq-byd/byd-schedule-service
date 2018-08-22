ALTER TABLE ethereum_scan_cursor
	ADD current_account BIGINT(15) NOT NULL DEFAULT -1 COMMENT 'currentAccount';
ALTER TABLE ethereum_scan_cursor
	ADD sync_status INT(11) NOT NULL DEFAULT -1 COMMENT 'syncStatus';ALTER TABLE ethereum_scan_cursor DROP COLUMN current_account;
