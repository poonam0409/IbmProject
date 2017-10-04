-- Oracle sqlldr utility control file
-- run the utility via sqlldr.exe <user>/<password> and specify this file name when asked for 'control'
load data
 infile 'SAP_FOREIGN_KEYS.csv'
 into table AUX.SAP_FOREIGN_KEYS
 fields terminated by "," optionally enclosed by '"'
 (CW_LEGACY_ID, SAP_TABNAME, SAP_FIELDNAME, CW_TABNAME, CW_FIELDNAME, PRIMPOS, FORTABLE, FORKEY, CHECKTABLE, CHECKFIELD, SAP_FORTABLE, SAP_FORKEY, SAP_CHECKTABLE, SAP_CHECKFIELD, CW_LOB, CW_RLOUT)