-- Oracle sqlldr utility control file
-- run the utility via sqlldr.exe <user>/<password> and specify this file name when asked for 'control' 
load data
 infile 'SAP_DOMAINTABLES_METADATA.csv'
 into table AUX.SAP_DOMAINTABLES_METADATA
 fields terminated by "," optionally enclosed by '"'
 (CW_LEGACY_ID, DOMNAME, CW_DOMAIN_TABLE, CW_DOMAIN_TRANSCODING_TABLE, CW_LOB, CW_RLOUT, DDTEXT)