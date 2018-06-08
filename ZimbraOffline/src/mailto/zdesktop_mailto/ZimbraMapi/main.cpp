#include "main.h"

std::wstring GetZDCommand(void)
{
    DWORD dwType = 0;
    TCHAR szData[255];
    DWORD cbData = 255;
    HKEY hKeyRoot = NULL;

    if (RegOpenKeyEx(HKEY_LOCAL_MACHINE, TEXT("SOFTWARE\\Clients\\Mail\\Zimbra Desktop\\shell\\open\\command"), 0, KEY_READ, &hKeyRoot) != ERROR_SUCCESS) {
        return L"";
    }

    if (RegQueryValueEx(hKeyRoot, NULL, NULL, &dwType, (LPBYTE)szData, &cbData) != ERROR_SUCCESS || dwType != REG_SZ) {
        return L"";
    }

    if (hKeyRoot) {
        RegCloseKey(hKeyRoot);
    }

    return std::wstring(szData);
}

// Amazingly I can't find a Win32 API to escape an arbitrary string
const wchar_t hexChars[] = L"0123456789ABCDEF";

BOOL ShouldEscape(wchar_t c)
{
    if (c >= '0' && c <= '9')
        return FALSE;
    if (c >= 'A' && c <= 'Z')
        return FALSE;
    if (c >= 'a' && c <= 'z')
        return FALSE;
    if (c == '/' || c == '\\')
        return FALSE;
    return TRUE;
}

wchar_t* DecimalToHex(wchar_t c, wchar_t* out)
{
    char utf8[4];
    wchar_t hex[13];
    int index = 0;
    int written;
    if (written = WideCharToMultiByte(CP_UTF8, 0, &c, 1, utf8, 4, NULL, NULL)) {
        for (int i = 0; i<written; i++) {
            hex[index++] = '%';
            hex[index++] = hexChars[((unsigned char)utf8[i]) / 16];
            hex[index++] = hexChars[((unsigned char)utf8[i]) % 16];
        }
    }
    else {
        hex[index++] = '%';
        hex[index++] = 'F';
        hex[index++] = 'F';
    }
    hex[index] = 0;
    *out = 0;
    wcscat_s(out, index + 1, hex);
    return out + index;
}

void EscapeString(wchar_t* input, wchar_t* output, size_t outlen)
{
    wchar_t* escaped = output;
    while (*input != 0 && (size_t)(escaped - output) < outlen - 1) {
        if (!ShouldEscape(*input)) {
            *(escaped++) = *input;
        }
        else {
            if ((size_t)(escaped - output) >= outlen - 4) {
                // Not enough room in the buffer
                break;
            }
            escaped = DecimalToHex(*input, escaped);
        }
        input++;
    }
    *escaped = 0;
}

LPWSTR convertToUnicode(LPSTR str)
{
    LPWSTR wstr;
    int len;

    len = MultiByteToWideChar(CP_UTF8, MB_ERR_INVALID_CHARS, str, -1, NULL, 0);
    if (len != 0) {
        wstr = (LPWSTR)HeapAlloc(GetProcessHeap(), 0, len * sizeof(TCHAR));
        MultiByteToWideChar(CP_UTF8, 0, str, -1, wstr, len);
        return wstr;
    }
    len = MultiByteToWideChar(CP_ACP, MB_ERR_INVALID_CHARS, str, -1, NULL, 0);
    wstr = (LPWSTR)HeapAlloc(GetProcessHeap(), 0, len * sizeof(TCHAR));
    MultiByteToWideChar(CP_ACP, 0, str, -1, wstr, len);
    return wstr;
}

ULONG WINAPI MAPILogon(ULONG_PTR ulUIParam, LPSTR lpszProfileName, LPSTR lpszPassword, FLAGS flFlags, ULONG ulReserved, LPLHANDLE lplhSession)
{
    #ifdef _DEBUG
        MessageBox(NULL, L"MAPILogon", L"MAPILogon", MB_OK | MB_ICONINFORMATION);
    #endif

    // Always return success as we can't logon
    return SUCCESS_SUCCESS;
}

ULONG WINAPI MAPILogoff(LHANDLE lhSession, ULONG_PTR ulUIParam, FLAGS flFlags, ULONG ulReserved)
{
    #ifdef _DEBUG
        MessageBox(NULL, L"MAPILogoff", L"MAPILogoff", MB_OK | MB_ICONINFORMATION);
    #endif

    // Always return success
    return SUCCESS_SUCCESS;
}

ULONG WINAPI MAPISendDocuments(ULONG_PTR ulUIParam, LPSTR lpszDelimChar, LPSTR lpszFilePaths, LPSTR lpszFileNames, ULONG ulReserved)
{
    #ifdef _DEBUG
        MessageBox(NULL, L"MAPISendDocuments", L"MAPISendDocuments", MB_OK | MB_ICONINFORMATION);
    #endif

    // Always return success
    return SUCCESS_SUCCESS;
}

// Used by windows 7 and below
ULONG WINAPI MAPISendMail(LHANDLE lhSession, ULONG_PTR ulUIParam, lpMapiMessage lpMessage, FLAGS flFlags, ULONG ulReserved)
{
    #ifdef _DEBUG
        MessageBox(NULL, L"MAPISendMail", L"MAPISendMail", MB_OK | MB_ICONINFORMATION);
    #endif

    std::wstring sCommand, sAttachments, sFileName, sOrigPath, sTmpPath;
    LPSTR lpParameters = NULL;
    TCHAR lpTempPathBuffer[MAX_PATH] = { 0 };
    int copyRes;

    sCommand = GetZDCommand();
    if (sCommand.empty())
        return MAPI_E_FAILURE;

    if (lpMessage->nFileCount > 0) {
        GetTempPath(MAX_PATH, lpTempPathBuffer);

        for (unsigned int i=0; i<lpMessage->nFileCount; i++) {
            // Copy file to the temporary directory first
            // to make sure even if original file id deleted our upload process will not be affected

            #ifdef _DEBUG
                MessageBoxA(NULL, lpMessage->lpFiles[i].lpszFileName, "lpszFileName", MB_OK | MB_ICONINFORMATION);
                MessageBoxA(NULL, lpMessage->lpFiles[i].lpszPathName, "lpszPathName", MB_OK | MB_ICONINFORMATION);
            #endif

            if (lpMessage->lpFiles[i].lpszFileName != NULL) {
                sFileName = std::wstring(convertToUnicode(lpMessage->lpFiles[i].lpszFileName));
                sOrigPath = std::wstring(convertToUnicode(lpMessage->lpFiles[i].lpszPathName));
                sTmpPath = std::wstring(lpTempPathBuffer) + sFileName;

                copyRes = CopyFile(sOrigPath.c_str(), sTmpPath.c_str(), false);
                
                if (copyRes == 0) {
                    #ifdef _DEBUG
                        MessageBox(NULL, L"File copy failed with error code: " + GetLastError(), L"Error", MB_OK | MB_ICONINFORMATION);
                    #else
                        MessageBox(NULL, L"There was some issue in file copy", L"Error", MB_OK | MB_ICONINFORMATION);
                    #endif

                    return MAPI_E_FAILURE;
                }
            }
            else {
                sTmpPath = convertToUnicode(lpMessage->lpFiles[i].lpszPathName);
            }

            wchar_t lszEscapedPath[MAX_PATH] = { 0 };
            EscapeString(&sTmpPath[0], lszEscapedPath, MAX_PATH);

            sAttachments = sAttachments.empty() ? lszEscapedPath : sAttachments + L";" + lszEscapedPath;
        }

        sCommand += L" mailto:?attachments=";
        sCommand += sAttachments;
    }

    #ifdef _DEBUG
        MessageBox(NULL, &sCommand[0], L"MAPISendMail", MB_OK | MB_ICONINFORMATION);
    #endif

    PROCESS_INFORMATION pi = { 0 };
    STARTUPINFO si = { sizeof(si) };

    if (!CreateProcess(NULL, &sCommand[0], NULL, NULL, FALSE, 0, NULL, NULL, &si, &pi)) {
        MessageBox(NULL, L"Failed starting Zimbra Desktop application", L"Error", 0);

        return MAPI_E_FAILURE;
    }

    // When attaching images, it takes some time to upload image in ZD
    // so wait for some time till the image gets uploaded properly
    Sleep(15000);

    return SUCCESS_SUCCESS;
}

// Used by windows 8 and above
ULONG WINAPI MAPISendMailW(LHANDLE lhSession, ULONG_PTR ulUIParam, lpMapiMessageW lpMessage, FLAGS flFlags, ULONG ulReserved)
{
    #ifdef _DEBUG
        MessageBox(NULL, L"MAPISendMailW", L"MAPISendMailW", MB_OK | MB_ICONINFORMATION);
    #endif

    std::wstring sCommand, sAttachments, sFileName, sOrigPath, sTmpPath;
    LPSTR lpParameters = NULL;
    TCHAR lpTempPathBuffer[MAX_PATH] = { 0 };
    int copyRes;

    sCommand = GetZDCommand();
    if (sCommand.empty())
        return MAPI_E_FAILURE;

    if (lpMessage->nFileCount > 0) {
        GetTempPath(MAX_PATH, lpTempPathBuffer);

        for (unsigned int i = 0; i<lpMessage->nFileCount; i++) {
            // Copy file to the temporary directory first
            // to make sure even if original file id deleted our upload process will not be affected

            #ifdef _DEBUG
                MessageBox(NULL, lpMessage->lpFiles[i].lpszFileName, L"lpszFileName", MB_OK | MB_ICONINFORMATION);
                MessageBox(NULL, lpMessage->lpFiles[i].lpszPathName, L"lpszPathName", MB_OK | MB_ICONINFORMATION);
            #endif

            if (lpMessage->lpFiles[i].lpszFileName != NULL) {
                sFileName = std::wstring(lpMessage->lpFiles[i].lpszFileName);
                sOrigPath = std::wstring(lpMessage->lpFiles[i].lpszPathName);
                sTmpPath = std::wstring(lpTempPathBuffer) + sFileName;

                copyRes = CopyFile(sOrigPath.c_str(), sTmpPath.c_str(), false);
                
                if (copyRes == 0) {
                    #ifdef _DEBUG
                        MessageBox(NULL, L"File copy failed with error code: ", L"Error", MB_OK | MB_ICONINFORMATION);
                    #else
                        MessageBox(NULL, L"There was some issue in file copy", L"Error", MB_OK | MB_ICONINFORMATION);
                    #endif

                    return MAPI_E_FAILURE;
                }
            }
            else {
                sTmpPath = std::wstring(lpMessage->lpFiles[i].lpszPathName);
            }

            wchar_t lszEscapedPath[MAX_PATH] = { 0 };
            EscapeString(&sTmpPath[0], lszEscapedPath, MAX_PATH);

            sAttachments = sAttachments.empty() ? lszEscapedPath : sAttachments + L";" + lszEscapedPath;
        }

        sCommand += L" mailto:?attachments=";
        sCommand += sAttachments;
    }

    #ifdef _DEBUG
        MessageBox(NULL, &sCommand[0], L"MapiSendMailW", MB_OK | MB_ICONINFORMATION);
    #endif

    PROCESS_INFORMATION pi = { 0 };
    STARTUPINFO si = { sizeof(si) };

    if (!CreateProcess(NULL, &sCommand[0], NULL, NULL, FALSE, 0, NULL, NULL, &si, &pi)) {
        MessageBox(NULL, L"Failed starting Zimbra Desktop application", L"Error", 0);

        return MAPI_E_FAILURE;
    }

    // When attaching images, it takes some time to upload image in ZD
    // so wait for some time till the image gets uploaded properly
    Sleep(15000);

    return SUCCESS_SUCCESS;
}

BOOL WINAPI DllMain(HINSTANCE hinstDLL, DWORD fdwReason, LPVOID lpvReserved)
{
    #ifdef _DEBUG
        MessageBox(NULL, L"DllMain", L"DllMain", MB_OK | MB_ICONINFORMATION);
    #endif

    return TRUE; // succesful
}