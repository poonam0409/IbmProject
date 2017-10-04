#include "com_ibm_is_sappack_gen_jco_panel_jni_DLLVersion.h"

#include <windows.h>
#include <tchar.h>
#include <stdio.h>
#include <stdlib.h>
#include <malloc.h>

JNIEXPORT jstring JNICALL Java_com_ibm_is_sappack_gen_jco_panel_jni_DLLVersion_getVersion__Ljava_lang_String_2
(
	JNIEnv *env,
	jclass clazz,
	jstring jFileName
)
{
	const char *fileName = env->GetStringUTFChars(jFileName, 0);

	printf( "Java_com_ibm_is_sappack_gen_jco_panel_jni_DLLVersion_getVersion__Ljava_lang_String_2: File Name from Java: %s\n" , fileName );

	TCHAR szVer[ 512 ] = { 0 };
	LPTSTR lpszFilePath = "C:\\java\\SAP\\sapjco3-NTintel-3.0.5\\sapjco3.dll";
	DWORD dwDummy;
	DWORD dwFVISize = GetFileVersionInfoSize( (LPTSTR) fileName , &dwDummy );

	if (0 == dwFVISize) {
		jclass DLLVersionException = env->FindClass("DLLVersionException");
		env->ThrowNew(DLLVersionException,"GetFileVersionInfoSize returned zero.");
		return NULL;
	}

	LPBYTE lpVersionInfo = new BYTE[dwFVISize];
	BOOL vInfoRet = GetFileVersionInfo( (LPTSTR) fileName , 0 , dwFVISize , lpVersionInfo );

	if (0 == vInfoRet) {
		jclass DLLVersionException = env->FindClass("DLLVersionException");
		env->ThrowNew(DLLVersionException,"GetFileVersionInfo returned zero.");
		return NULL;
	}

	UINT uLen;
	VS_FIXEDFILEINFO *lpFfi;
	VerQueryValue( lpVersionInfo , _T("\\") , (LPVOID *)&lpFfi , &uLen );
	DWORD dwFileVersionMS = lpFfi->dwFileVersionMS;
	DWORD dwFileVersionLS = lpFfi->dwFileVersionLS;
	delete [] lpVersionInfo;
	//printf( "Higher: %x\n" , dwFileVersionMS );
	//printf( "Lower: %x\n" , dwFileVersionLS );
	DWORD dwLeftMost = HIWORD(dwFileVersionMS);
	DWORD dwSecondLeft = LOWORD(dwFileVersionMS);
	DWORD dwSecondRight = HIWORD(dwFileVersionLS);
	DWORD dwRightMost = LOWORD(dwFileVersionLS);
	::wsprintf( szVer,
	        _T( "%d.%d.%d.%d" ), dwLeftMost, dwSecondLeft,
	dwSecondRight, dwRightMost );

	// cleanup
	env->ReleaseStringUTFChars(jFileName, fileName);

	printf( "Java_com_ibm_is_sappack_gen_jco_panel_jni_DLLVersion_getVersion__Ljava_lang_String_2: Returning Version: %s\n" , szVer );

	return env->NewStringUTF(szVer);
}

JNIEXPORT jstring JNICALL Java_com_ibm_is_sappack_gen_jco_panel_jni_DLLVersion_getVersion__Ljava_lang_String_2Ljava_lang_String_2
(
	JNIEnv *env,
	jclass clazz,
	jstring jPath,
	jstring jFileName
)
{
	const char* fileName = env->GetStringUTFChars(jFileName, 0);
	const char* path = env->GetStringUTFChars(jPath, 0);
	const char* separator = "\\";

	printf( "Java_com_ibm_is_sappack_gen_jco_panel_jni_DLLVersion_getVersion__Ljava_lang_String_2Ljava_lang_String_2: File Name from Java: %s\n" , fileName );
	printf( "Java_com_ibm_is_sappack_gen_jco_panel_jni_DLLVersion_getVersion__Ljava_lang_String_2Ljava_lang_String_2: Path from Java: %s\n" , path );

	char completeFileName[_MAX_PATH];
	//char *completeFileName = malloc(strlen(path) + strlen(separator) + strlen(fileName) + 2);

	//strcat (completeFileName, path);
	//strcat (completeFileName, "\\");
	//strcat (completeFileName, fileName);

	strcpy (completeFileName, path);
	strncat (completeFileName, separator, 2);
	strncat (completeFileName, fileName, strlen(fileName));

	printf( "Java_com_ibm_is_sappack_gen_jco_panel_jni_DLLVersion_getVersion__Ljava_lang_String_2Ljava_lang_String_2: Complete path constructed: %s\n" , completeFileName );

	jstring jCompleteFileName = env->NewStringUTF(completeFileName);

	jstring retVal = Java_com_ibm_is_sappack_gen_jco_panel_jni_DLLVersion_getVersion__Ljava_lang_String_2
	(
		env,
		clazz,
		jCompleteFileName
	);

	// cleanup
	env->ReleaseStringUTFChars(jFileName, fileName);
	env->ReleaseStringUTFChars(jPath, path);

	return retVal;
}

JNIEXPORT jboolean JNICALL Java_com_ibm_is_sappack_gen_jco_panel_jni_DLLVersion_validateVersion__Ljava_lang_String_2Ljava_lang_String_2
(
	JNIEnv *env,
	jclass clazz,
	jstring jVersion,
	jstring jFileName
)
{
	const char *fileName = env->GetStringUTFChars(jFileName, 0);
	const char *version = env->GetStringUTFChars(jVersion, 0);
	jboolean retVal= JNI_FALSE;

	printf( "Java_com_ibm_is_sappack_gen_jco_panel_jni_DLLVersion_validateVersion__Ljava_lang_String_2Ljava_lang_String_2: File Name from Java: %s\n" , fileName );
	printf( "Java_com_ibm_is_sappack_gen_jco_panel_jni_DLLVersion_validateVersion__Ljava_lang_String_2Ljava_lang_String_2: Version from Java: %s\n" , version );

	jstring jVersionFromDLL = Java_com_ibm_is_sappack_gen_jco_panel_jni_DLLVersion_getVersion__Ljava_lang_String_2
	(
		env,
		clazz,
		jFileName
	);

	const char *versionFromDLL = env->GetStringUTFChars(jVersionFromDLL, 0);

	// compare                                                             
	if ( strcmp( version,versionFromDLL ) == 0 ) {                                
		retVal = JNI_TRUE;                                                  
	} 

	// cleanup
	env->ReleaseStringUTFChars(jFileName, fileName);
	env->ReleaseStringUTFChars(jVersion, version);
	env->ReleaseStringUTFChars(jVersionFromDLL, versionFromDLL);

	printf( "Java_com_ibm_is_sappack_gen_jco_panel_jni_DLLVersion_validateVersion__Ljava_lang_String_2Ljava_lang_String_2: Comparison result: %d\n" , retVal );

	return retVal;
}

JNIEXPORT jboolean JNICALL Java_com_ibm_is_sappack_gen_jco_panel_jni_DLLVersion_validateVersion__Ljava_lang_String_2Ljava_lang_String_2Ljava_lang_String_2
(
	JNIEnv *env,
	jclass clazz,
	jstring jVersion,
	jstring jPath,
	jstring jFileName
)
{
	const char *fileName = env->GetStringUTFChars(jFileName, 0);
	const char *path = env->GetStringUTFChars(jPath, 0);
	const char *version = env->GetStringUTFChars(jVersion, 0);
	jboolean retVal= JNI_FALSE;

	printf( "Java_com_ibm_is_sappack_gen_jco_panel_jni_DLLVersion_validateVersion__Ljava_lang_String_2Ljava_lang_String_2Ljava_lang_String_2: File Name from Java: %s\n" , fileName );
	printf( "Java_com_ibm_is_sappack_gen_jco_panel_jni_DLLVersion_validateVersion__Ljava_lang_String_2Ljava_lang_String_2Ljava_lang_String_2: Path from Java: %s\n" , path );
	printf( "Java_com_ibm_is_sappack_gen_jco_panel_jni_DLLVersion_validateVersion__Ljava_lang_String_2Ljava_lang_String_2Ljava_lang_String_2: Version from Java: %s\n" , version );

	jstring jVersionFromDLL = Java_com_ibm_is_sappack_gen_jco_panel_jni_DLLVersion_getVersion__Ljava_lang_String_2Ljava_lang_String_2
	(
		env,
		clazz,
		jPath,
		jFileName
	);

	const char *versionFromDLL = env->GetStringUTFChars(jVersionFromDLL, 0);

	// compare                                                             
	if ( strcmp( version,versionFromDLL ) == 0 ) {                                
		retVal = JNI_TRUE;                                                  
	}

	// cleanup
	env->ReleaseStringUTFChars(jFileName, fileName);
	env->ReleaseStringUTFChars(jPath, path);
	env->ReleaseStringUTFChars(jVersion, version);
	env->ReleaseStringUTFChars(jVersionFromDLL, versionFromDLL);

	return retVal;
}