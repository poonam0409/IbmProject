-- Oracle sqlldr utility control file
-- run the utility via sqlldr.exe <user>/<password> and specify this file name when asked for 'control' 
load data
 infile 'SAP_CHECKTABLES_METADATA.csv'
 into table AUX.SAP_CHECKTABLES_METADATA
 fields terminated by "," optionally enclosed by '"'
 (CW_LEGACY_ID, SAP_CHECKTABLE, CW_CHECKTABLE, CT_CHECKTABLE, TEXTTABLE, CW_TEXTTABLE, TRANSCODING_TABLE, TABLE_TYPE, CW_LOB, CW_RLOUT, DDTEXT)