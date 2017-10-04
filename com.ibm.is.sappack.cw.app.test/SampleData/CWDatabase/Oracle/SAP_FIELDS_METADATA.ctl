-- Oracle sqlldr utility control file
-- run the utility via sqlldr.exe <user>/<password> and specify this file name when asked for 'control' 
load data
 CHARACTERSET UTF8
 infile 'SAP_FIELDS_METADATA.csv'
 into table AUX.SAP_FIELDS_METADATA
 fields terminated by "," optionally enclosed by '"'
 (CW_LEGACY_ID, SAP_TABNAME, SAP_FIELDNAME, CW_TABNAME, CW_FIELDNAME, TRANSCODING_TABLE_SOURCE_FIELD, TRANSCODING_TABLE_TARGET_FIELD, ROLLNAME, DOMNAME, POSITION, KEYFLAG, SAP_CHECKTABLE, CW_CHECKTABLE, INTTYPE, REFTABLE, REFFIELD, DATATYPE, LENG, CW_LENG, DECIMALS, LOWERCASE, VALEXI, ENTITYTAB, CW_LOB, CW_RLOUT, DDTEXT)