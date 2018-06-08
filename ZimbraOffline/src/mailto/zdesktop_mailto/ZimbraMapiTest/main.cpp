#include "stdafx.h"

int WINAPI WinMain(HINSTANCE hInstance, HINSTANCE hPrevInstance, LPSTR lpCmdLine, int nShowCmd)
{
    HINSTANCE hMAPI = ::LoadLibrary(L"ZimbraMapi.dll");
    HRESULT nSent = 0;

    /*if (hMAPI != NULL) {
        LPMAPISENDMAILW lpfnMAPISendMail = (LPMAPISENDMAILW)::GetProcAddress(hMAPI, "MAPISendMailW");

        // Add recipient info
        MapiRecipDescW recipient[1] = { 0 };
        recipient[0].ulRecipClass = MAPI_TO;
        recipient[0].lpszAddress = L"someone@somewhere.com";
        recipient[0].lpszName = L"someone@somewhere.com";

        // Add attachments
        MapiFileDescW file[2] = { 0 };
        file[0].nPosition = (ULONG)-1;
        file[0].lpszPathName = L"C:\\Users\\zimbra\\Desktop\\看一下這個.txt";
        //file[0].lpszPathName = L"C:\\Users\\zimbra\\Desktop\\Paris.jpg";

        file[1].nPosition = (ULONG)-1;
        file[1].lpszFileName = L"thunderbird.pdf";
        file[1].lpszPathName = L"C:\\Users\\zimbra\\Desktop\\thunderbird.pdf";

        MapiMessageW msg = { 0 };
        msg.lpRecips = recipient;
        msg.nRecipCount = 1;
        msg.lpFiles = file;
        msg.nFileCount = 2;

        nSent = lpfnMAPISendMail(0, 0, &msg, MAPI_LOGON_UI | MAPI_DIALOG, 0);

        FreeLibrary(hMAPI);
    }*/

    if (hMAPI != NULL) {
        LPMAPISENDMAIL lpfnMAPISendMail = (LPMAPISENDMAIL)::GetProcAddress(hMAPI, "MAPISendMail");

        // Add recipient info
        MapiRecipDesc recipient[1] = { 0 };
        recipient[0].ulRecipClass = MAPI_TO;
        recipient[0].lpszAddress = "someone@somewhere.com";
        recipient[0].lpszName = "someone@somewhere.com";

        // Add attachments
        MapiFileDesc file[2] = { 0 };
        file[0].nPosition = (ULONG)-1;
        //file[0].lpszPathName = "C:\\Users\\zimbra\\Desktop\\看一下這個.txt";
        file[0].lpszPathName = "C:\\Users\\zimbra\\Desktop\\Paris.jpg";

        file[1].nPosition = (ULONG)-1;
        file[1].lpszFileName = "thunderbird.pdf";
        file[1].lpszPathName = "C:\\Users\\zimbra\\Desktop\\thunderbird.pdf";

        MapiMessage msg = { 0 };
        msg.lpRecips = recipient;
        msg.nRecipCount = 1;
        msg.lpFiles = file;
        msg.nFileCount = 2;

        nSent = lpfnMAPISendMail(0, 0, &msg, MAPI_LOGON_UI | MAPI_DIALOG, 0);

        FreeLibrary(hMAPI);
    }

    return nSent;
}