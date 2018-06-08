#ifndef __MAIN_H__
#define __MAIN_H__

#include <windows.h>
#include <string>

// All neeeded info copied from mapi.h
#define SUCCESS_SUCCESS         0
#define MAPI_E_USER_ABORT       1
#define MAPI_E_FAILURE          2
#define MAPI_E_LOGIN_FAILURE    3

typedef unsigned long FLAGS;
typedef unsigned long LHANDLE;
typedef unsigned long FAR *LPLHANDLE, FAR *LPULONG;

typedef struct {
  ULONG ulReserved;
  ULONG ulRecipClass;
  LPSTR lpszName;
  LPSTR lpszAddress;
  ULONG ulEIDSize;
  LPVOID lpEntryID;
} MapiRecipDesc, *lpMapiRecipDesc;

typedef struct {
  ULONG ulReserved;
  ULONG flFlags;
  ULONG nPosition;
  LPSTR lpszPathName;
  LPSTR lpszFileName;
  LPVOID lpFileType;
} MapiFileDesc, *lpMapiFileDesc;

typedef struct {
  ULONG ulReserved;
  LPSTR lpszSubject;
  LPSTR lpszNoteText;
  LPSTR lpszMessageType;
  LPSTR lpszDateReceived;
  LPSTR lpszConversationID;
  FLAGS flFlags;
  lpMapiRecipDesc lpOriginator;
  ULONG nRecipCount;
  lpMapiRecipDesc lpRecips;
  ULONG nFileCount;
  lpMapiFileDesc lpFiles;
} MapiMessage, *lpMapiMessage;

typedef struct {
    ULONG ulReserved;
    ULONG ulRecipClass;
    PWSTR lpszName;
    PWSTR lpszAddress;
    ULONG ulEIDSize;
    PVOID lpEntryID;
} MapiRecipDescW, *lpMapiRecipDescW;

typedef struct {
    ULONG ulReserved;
    ULONG flFlags;
    ULONG nPosition;
    PWSTR lpszPathName;
    PWSTR lpszFileName;
    PVOID lpFileType;
} MapiFileDescW, *lpMapiFileDescW;

typedef struct {
    ULONG            ulReserved;
    PWSTR            lpszSubject;
    PWSTR            lpszNoteText;
    PWSTR            lpszMessageType;
    PWSTR            lpszDateReceived;
    PWSTR            lpszConversationID;
    FLAGS            flFlags;
    lpMapiRecipDescW lpOriginator;
    ULONG            nRecipCount;
    lpMapiRecipDescW lpRecips;
    ULONG            nFileCount;
    lpMapiFileDescW  lpFiles;
} MapiMessageW, *lpMapiMessageW;

#endif // __MAIN_H__
