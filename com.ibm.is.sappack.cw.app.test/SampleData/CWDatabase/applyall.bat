REM  Creating the sample data database in 4 steps:
REM  1. create a DB2 database with 16k pages and UTF-8 codeset
REM  2. open a db2cmd window and connect to the database
REM  3. From the DBSetup component in the CW_VNext stream extract and run 
REM     dbsetup\DatabaseSupport\GeneralTables\DDLs\DB2\CW_GeneralTables_DB2_97.sql
REM     and 
REM     dbsetup\DatabaseSupport\GeneralTables\DDLs\DB2\Setup_CW_SCHEMAS_DB2.sql                
REM  4. extract this file (applyall.bat) and all .sql and .csv files in the same RTC folder and run applyall.bat   
REM
REM  NOTE: if you run into a SQLCODE: -964 increase the number of secondary log files with:
REM  db2 update db cfg for <your database name> using logsecond 50 

db2 -td! -vf CUST_ALG0_PFUETZE_DB2.sql

db2 -td! -vf CUST_PLD_PFUETZE_DB2.sql

db2 -td! -vf MAT_ALG0_PFUETZE_DB2.sql

db2 -td! -vf MAT_PLD_PFUETZE_DB2.sql

DB2 IMPORT FROM "ALG0_MAKT.csv" OF DEL METHOD P (1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14) MESSAGES "applyall.msg" INSERT INTO ALG0.MAKT (MANDT, MATNR, SPRAS, MAKTX, MAKTG, CW_LEGACY_ID, CW_LEGACY_UK, CW_LOB, CW_RLOUT, CW_CATEGORY, CW_VERSION, CW_LOAD_ID, PK_MAKT, FK_MARA_MATNR)

DB2 IMPORT FROM "ALG0_MARA.csv" OF DEL METHOD P (1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95, 96, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 123, 124, 125, 126, 127, 128, 129, 130, 131, 132, 133, 134, 135, 136, 137, 138, 139, 140, 141, 142, 143, 144, 145, 146, 147, 148, 149, 150, 151, 152, 153, 154, 155, 156, 157, 158, 159, 160, 161, 162, 163, 164, 165, 166, 167, 168, 169, 170, 171, 172, 173, 174, 175, 176, 177, 178, 179, 180, 181, 182, 183, 184, 185, 186, 187, 188, 189, 190, 191, 192, 193, 194, 195, 196, 197, 198, 199, 200, 201, 202, 203, 204, 205, 206, 207, 208, 209, 210, 211, 212, 213, 214, 215, 216, 217, 218, 219, 220, 221, 222, 223, 224) MESSAGES "applyall.msg" INSERT INTO ALG0.MARA (MANDT, MATNR, ERSDA, ERNAM, LAEDA, AENAM, VPSTA, PSTAT, LVORM, MTART, MBRSH, MATKL, BISMT, MEINS, BSTME, ZEINR, ZEIAR, ZEIVR, ZEIFO, AESZN, BLATT, BLANZ, FERTH, FORMT, GROES, WRKST, NORMT, LABOR, EKWSL, BRGEW, NTGEW, GEWEI, VOLUM, VOLEH, BEHVO, RAUBE, TEMPB, DISST, TRAGR, STOFF, SPART, KUNNR, EANNR, WESCH, BWVOR, BWSCL, SAISO, ETIAR, ETIFO, ENTAR, EAN11, NUMTP, LAENG, BREIT, HOEHE, MEABM, PRDHA, AEKLK, CADKZ, QMPUR, ERGEW, ERGEI, ERVOL, ERVOE, GEWTO, VOLTO, VABME, KZREV, KZKFG, XCHPF, VHART, FUELG, STFAK, MAGRV, BEGRU, DATAB, LIQDT, SAISJ, PLGTP, MLGUT, EXTWG, SATNR, ATTYP, KZKUP, KZNFM, PMATA, MSTAE, MSTAV, MSTDE, MSTDV, TAKLV, RBNRM, MHDRZ, MHDHB, MHDLP, INHME, INHAL, VPREH, ETIAG, INHBR, CMETH, CUOBF, KZUMW, KOSCH, SPROF, NRFHG, MFRPN, MFRNR, BMATN, MPROF, KZWSM, SAITY, PROFL, IHIVI, ILOOS, SERLV, KZGVH, XGCHP, KZEFF, COMPL, IPRKZ, RDMHD, PRZUS, MTPOS_MARA, BFLME, MATFI, CMREL, BBTYP, SLED_BBD, GTIN_VARIANT, GENNR, RMATP, GDS_RELEVANT, WEORA, HUTYP_DFLT, PILFERABLE, WHSTC, WHMATGR, HNDLCODE, HAZMAT, HUTYP, TARE_VAR, MAXC, MAXC_TOL, MAXL, MAXB, MAXH, MAXDIM_UOM, HERKL, MFRGR, QQTIME, QQTIMEUOM, QGRP, SERIAL, PS_SMARTFORM, LOGUNIT, CWQREL, CWQPROC, CWQTOLGR, ADPROF, ABEV1_LULEINH, ABEV1_LULDEGRP, ABEV1_NESTRUCCAT, ADSD_SL_TOLTYP, ADSD_SV_CNT_GRP, ADSD_VC_GROUP, AVSO_R_TILT_IND, AVSO_R_STACK_IND, AVSO_R_BOT_IND, AVSO_R_TOP_IND, AVSO_R_STACK_NO, AVSO_R_PAL_IND, AVSO_R_PAL_OVR_D, AVSO_R_PAL_OVR_W, AVSO_R_PAL_B_HT, AVSO_R_PAL_MIN_H, AVSO_R_TOL_B_HT, AVSO_R_NO_P_GVH, AVSO_R_QUAN_UNIT, AVSO_R_KZGVH_IND, PACKCODE, DG_PACK_STATUS, MCOND, RETDELC, LOGLEV_RETO, NSNID, ADSPC_SPC, IMATN, PICNUM, BSTAT, COLOR_ATINN, SIZE1_ATINN, SIZE2_ATINN, COLOR, SIZE1, SIZE2, FREE_CHAR, CARE_CODE, BRAND_ID, FIBER_CODE1, FIBER_PART1, FIBER_CODE2, FIBER_PART2, FIBER_CODE3, FIBER_PART3, FIBER_CODE4, FIBER_PART4, FIBER_CODE5, FIBER_PART5, FASHGRD, CW_LEGACY_ID, CW_LEGACY_UK, CW_LOB, CW_RLOUT, CW_CATEGORY, CW_VERSION, CW_LOAD_ID, PK_MARA, FK_MARA_BMATN, FK_MARA_GENNR, FK_MARM_LOGUNIT, FK_MARA_PMATA, FK_MARA_RMATP, FK_MARA_SATNR)

DB2 IMPORT FROM "ALG0_MARC.csv" OF DEL METHOD P (1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95, 96, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 123, 124, 125, 126, 127, 128, 129, 130, 131, 132, 133, 134, 135, 136, 137, 138, 139, 140, 141, 142, 143, 144, 145, 146, 147, 148, 149, 150, 151, 152, 153, 154, 155, 156, 157, 158, 159, 160, 161, 162, 163, 164, 165, 166, 167, 168, 169, 170, 171, 172, 173, 174, 175, 176, 177, 178, 179, 180, 181, 182, 183, 184, 185, 186, 187, 188, 189, 190, 191, 192, 193, 194, 195, 196, 197, 198, 199, 200, 201, 202, 203, 204, 205, 206, 207, 208, 209, 210, 211, 212, 213, 214, 215, 216, 217, 218, 219, 220, 221, 222, 223, 224, 225, 226, 227, 228, 229, 230, 231, 232, 233, 234, 235, 236) MESSAGES "applyall.msg" INSERT INTO ALG0.MARC (MANDT, MATNR, WERKS, PSTAT, LVORM, BWTTY, XCHAR, MMSTA, MMSTD, MAABC, KZKRI, EKGRP, AUSME, DISPR, DISMM, DISPO, KZDIE, PLIFZ, WEBAZ, PERKZ, AUSSS, DISLS, BESKZ, SOBSL, MINBE, EISBE, BSTMI, BSTMA, BSTFE, BSTRF, MABST, LOSFX, SBDKZ, LAGPR, ALTSL, KZAUS, AUSDT, NFMAT, KZBED, MISKZ, FHORI, PFREI, FFREI, RGEKZ, FEVOR, BEARZ, RUEZT, TRANZ, BASMG, DZEIT, MAXLZ, LZEIH, KZPRO, GPMKZ, UEETO, UEETK, UNETO, WZEIT, ATPKZ, VZUSL, HERBL, INSMK, SPROZ, QUAZT, SSQSS, MPDAU, KZPPV, KZDKZ, WSTGH, PRFRQ, NKMPR, UMLMC, LADGR, XCHPF, USEQU, LGRAD, AUFTL, PLVAR, OTYPE, OBJID, MTVFP, PERIV, KZKFK, VRVEZ, VBAMG, VBEAZ, LIZYK, BWSCL, KAUTB, KORDB, STAWN, HERKL, HERKR, EXPME, MTVER, PRCTR, TRAME, MRPPP, SAUFT, FXHOR, VRMOD, VINT1, VINT2, VERKZ, STLAL, STLAN, PLNNR, APLAL, LOSGR, SOBSK, FRTME, LGPRO, DISGR, KAUSF, QZGTP, QMATV, TAKZT, RWPRO, COPAM, ABCIN, AWSLS, SERNP, CUOBJ, STDPD, SFEPR, XMCNG, QSSYS, LFRHY, RDPRF, VRBMT, VRBWK, VRBDT, VRBFK, AUTRU, PREFE, PRENC, PRENO, PREND, PRENE, PRENG, ITARK, SERVG, KZKUP, STRGR, CUOBV, LGFSB, SCHGT, CCFIX, EPRIO, QMATA, RESVP, PLNTY, UOMGR, UMRSL, ABFAC, SFCPF, SHFLG, SHZET, MDACH, KZECH, MEGRU, MFRGR, VKUMC, VKTRW, KZAGL, FVIDK, FXPRU, LOGGR, FPRFM, GLGMG, VKGLG, INDUS, MOWNR, MOGRU, CASNR, GPNUM, STEUC, FABKZ, MATGR, VSPVB, DPLFS, DPLPU, DPLHO, MINLS, MAXLS, FIXLS, LTINC, COMPL, CONVT, SHPRO, AHDIS, DIBER, KZPSP, OCMPF, APOKZ, MCRUE, LFMON, LFGJA, EISLO, NCOST, ROTATION_DATE, UCHKZ, UCMAT, BWESB, AMRSS_PROF_GUID, AMRSS_PROF_KEY, AMRSS_DATACHANGE, ASAPMP_TOLPRPL, ASAPMP_TOLPRMI, AVSO_R_PKGRP, AVSO_R_LANE_NUM, AVSO_R_PAL_VEND, AVSO_R_FORK_DIR, IUID_RELEVANT, IUID_TYPE, UID_IEA, CONS_PROCG, GI_PR_TIME, MULTIPLE_EKGRP, REF_SCHEMA, MIN_TROC, MAX_TROC, TARGET_STOCK, CW_LEGACY_ID, CW_LEGACY_UK, CW_LOB, CW_RLOUT, CW_CATEGORY, CW_VERSION, CW_LOAD_ID, PK_MARC, FK_MARA_MATNR, FK_MARA_NFMAT, FK_MARA_STDPD, FK_MARA_UCMAT, FK_MARA_VRBMT)

DB2 IMPORT FROM "ALG0_MARD.csv" OF DEL METHOD P (1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60) MESSAGES "applyall.msg" INSERT INTO ALG0.MARD (MANDT, MATNR, WERKS, LGORT, PSTAT, LVORM, LFGJA, LFMON, SPERR, LABST, UMLME, INSME, EINME, SPEME, RETME, VMLAB, VMUML, VMINS, VMEIN, VMSPE, VMRET, KZILL, KZILQ, KZILE, KZILS, KZVLL, KZVLQ, KZVLE, KZVLS, DISKZ, LSOBS, LMINB, LBSTF, HERKL, EXPPG, EXVER, LGPBE, KLABS, KINSM, KEINM, KSPEM, DLINL, PRCTL, ERSDA, VKLAB, VKUML, LWMKB, BSKRF, MDRUE, MDJIN, CW_LEGACY_ID, CW_LEGACY_UK, CW_LOB, CW_RLOUT, CW_CATEGORY, CW_VERSION, CW_LOAD_ID, PK_MARD, FK_MARA_MATNR, FK_MARC_WERKS)

DB2 IMPORT FROM "ALG0_MARM.csv" OF DEL METHOD P (1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38) MESSAGES "applyall.msg" INSERT INTO ALG0.MARM (MANDT, MATNR, MEINH, UMREZ, UMREN, EANNR, EAN11, NUMTP, LAENG, BREIT, HOEHE, MEABM, VOLUM, VOLEH, BRGEW, GEWEI, MESUB, ATINN, MESRT, XFHDW, XBEWW, KZWSO, MSEHI, BFLME_MARM, GTIN_VARIANT, NEST_FTR, MAX_STACK, CAPAUSE, TY2TQ, CW_LEGACY_ID, CW_LEGACY_UK, CW_LOB, CW_RLOUT, CW_CATEGORY, CW_VERSION, CW_LOAD_ID, PK_MARM, FK_MARA_MATNR)

DB2 IMPORT FROM "ALG0_MBEW.csv" OF DEL METHOD P (1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95, 96, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118) MESSAGES "applyall.msg" INSERT INTO ALG0.MBEW (MANDT, MATNR, BWKEY, BWTAR, LVORM, LBKUM, SALK3, VPRSV, VERPR, STPRS, PEINH, BKLAS, SALKV, VMKUM, VMSAL, VMVPR, VMVER, VMSTP, VMPEI, VMBKL, VMSAV, VJKUM, VJSAL, VJVPR, VJVER, VJSTP, VJPEI, VJBKL, VJSAV, LFGJA, LFMON, BWTTY, STPRV, LAEPR, ZKPRS, ZKDAT, TIMESTAMP, BWPRS, BWPRH, VJBWS, VJBWH, VVJSL, VVJLB, VVMLB, VVSAL, ZPLPR, ZPLP1, ZPLP2, ZPLP3, ZPLD1, ZPLD2, ZPLD3, PPERZ, PPERL, PPERV, KALKZ, KALKL, KALKV, KALSC, XLIFO, MYPOL, BWPH1, BWPS1, ABWKZ, PSTAT, KALN1, KALNR, BWVA1, BWVA2, BWVA3, VERS1, VERS2, VERS3, HRKFT, KOSGR, PPRDZ, PPRDL, PPRDV, PDATZ, PDATL, PDATV, EKALR, VPLPR, MLMAA, MLAST, LPLPR, VKSAL, HKMAT, SPERW, KZIWL, WLINL, ABCIW, BWSPA, LPLPX, VPLPX, FPLPX, LBWST, VBWST, FBWST, EKLAS, QKLAS, MTUSE, MTORG, OWNPR, XBEWM, BWPEI, MBRUE, OKLAS, OIPPINV, CW_LEGACY_ID, CW_LEGACY_UK, CW_LOB, CW_RLOUT, CW_CATEGORY, CW_VERSION, CW_LOAD_ID, PK_MBEW, FK_MARA_MATNR)

DB2 IMPORT FROM "ALG0_MVKE.csv" OF DEL METHOD P (1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78) MESSAGES "applyall.msg" INSERT INTO ALG0.MVKE (MANDT, MATNR, VKORG, VTWEG, LVORM, VERSG, BONUS, PROVG, SKTOF, VMSTA, VMSTD, AUMNG, LFMNG, EFMNG, SCMNG, SCHME, VRKME, MTPOS, DWERK, PRODH, PMATN, KONDM, KTGRM, MVGR1, MVGR2, MVGR3, MVGR4, MVGR5, SSTUF, PFLKS, LSTFL, LSTVZ, LSTAK, LDVFL, LDBFL, LDVZL, LDBZL, VDVFL, VDBFL, VDVZL, VDBZL, PRAT1, PRAT2, PRAT3, PRAT4, PRAT5, PRAT6, PRAT7, PRAT8, PRAT9, PRATA, RDPRF, MEGRU, LFMAX, RJART, PBIND, VAVME, MATKC, PVMSO, ABEV1_EMLGRP, ABEV1_EMDRCKSPL, ABEV1_RPBEZME, ABEV1_RPSNS, ABEV1_RPSFA, ABEV1_RPSKI, ABEV1_RPSCO, ABEV1_RPSSO, PLGTP, CW_LEGACY_ID, CW_LEGACY_UK, CW_LOB, CW_RLOUT, CW_CATEGORY, CW_VERSION, CW_LOAD_ID, PK_MVKE, FK_MARA_MATNR, FK_MARA_PMATN)