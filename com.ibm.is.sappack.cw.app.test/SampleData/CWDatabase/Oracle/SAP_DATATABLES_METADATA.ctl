-- Oracle sqlldr utility control file
-- run the utility via sqlldr.exe <user>/<password> and specify this file name when asked for 'control' 
load data
 infile 'SAP_DATATABLES_METADATA.csv'
 into table AUX.SAP_DATATABLES_METADATA
 fields terminated by "," optionally enclosed by '"'
 (CW_LEGACY_ID, SAP_TABNAME, CW_TABNAME, OBJECT_NAME, KEYFIELD, PARENTTABLE, ISROOT, HIERARCHY_LEVEL, FOREIGNKEYFIELD, CW_LOB, CW_RLOUT, DDTEXT)