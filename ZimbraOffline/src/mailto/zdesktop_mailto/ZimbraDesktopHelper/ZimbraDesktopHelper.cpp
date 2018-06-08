// ZimbraDesktopHelper.cpp
// This file is used to set/check Zimbra Desktop as default mailto client
// This functionality is only supported on Vista and above OS

#include "stdafx.h"
#include "ZimbraDesktopHelper.h"

/*
 * IsDefaultMailApp
 * 
 * Check if Zimbra Desktop is default mail client
 * @return {BOOL} true if Zimbra Desktop is default mail client
 */
BOOL IsDefaultMailApp(void)
{
    if (IsWindowsVistaOrGreater()) { // Windows Vista and up
        LPWSTR szMailtoHandler, szMAPIClient;
        BOOL isMailtoHandler = FALSE, isMAPIClient = FALSE;

        IApplicationAssociationRegistration* pAAR;
        HRESULT hr = CoCreateInstance(CLSID_ApplicationAssociationRegistration, NULL, CLSCTX_INPROC,
            IID_IApplicationAssociationRegistration, (void**)&pAAR);

        if (SUCCEEDED(hr)) {
            hr = pAAR->QueryCurrentDefault(L"mailto", AT_URLPROTOCOL, AL_EFFECTIVE, &szMailtoHandler);
            if (SUCCEEDED(hr)) {
                isMailtoHandler = (wcscmp(szMailtoHandler, L"ZimbraDesktop.Url.mailto") == 0) ? TRUE : FALSE;
                CoTaskMemFree(szMailtoHandler);
            }

            hr = pAAR->QueryCurrentDefault(L"Mail", AT_STARTMENUCLIENT, AL_EFFECTIVE, &szMAPIClient);
            if (SUCCEEDED(hr)) {
                isMAPIClient = (wcscmp(szMAPIClient, L"Zimbra Desktop") == 0) ? TRUE : FALSE;
                CoTaskMemFree(szMAPIClient);
            }

            pAAR->Release();

            return isMailtoHandler && isMAPIClient;
        }
    }
    else {
        MessageBox(NULL, L"This version of windows is not compatible with this program", L"Error", 0);
    }

    return FALSE;
}

/*
 * setAsDefaultMailApp
 * 
 * Set Zimbra Desktop as default mail client
 * For Windows 10 and above, it will open default settings app, user needs to click on Mail part and set zdclient as default mail application
 * For Windows 8 and 8.1, it will open default settings window for zimbra dekstop, where user need to click on select all checkbox and click on save
 * For Vista and Windows 7, it will automatically set as default mail client without any user interaction
 */
void setAsDefaultMailApp(void)
{
    if (IsWindows10OrGreater()) { // For Windows 10 and up open Default App Dialog
        // This code only works for mailto protocol, but we need to set send to associations also
        /*IApplicationActivationManager* pActManager;
        HRESULT hr = CoCreateInstance(CLSID_ApplicationActivationManager, nullptr, CLSCTX_INPROC,
            IID_IApplicationActivationManager, (void**)&pActManager);

        if (SUCCEEDED(hr)) {
            DWORD pid;

            pActManager->ActivateApplication(
                L"windows.immersivecontrolpanel_cw5n1h2txyewy"
                L"!microsoft.windows.immersivecontrolpanel",
                L"page=SettingsPageAppsDefaults", AO_NONE, &pid);
            pActManager->Release();
        }*/

        IOpenControlPanel *pOpenControlPanel;
        HRESULT hr = CoCreateInstance(CLSID_OpenControlPanel, NULL, CLSCTX_INPROC, __uuidof(IOpenControlPanel), (void**)&pOpenControlPanel);

        if (SUCCEEDED(hr)) {
            const wchar_t *page = L"pageDefaultProgram\\pageAdvancedSettings?pszAppName=Zimbra%20Desktop";

            pOpenControlPanel->Open(L"Microsoft.DefaultPrograms", page, NULL);
            pOpenControlPanel->Release();
        }
    }
    else if (IsWindows8OrGreater()) { // For Windows 8 and up open association dialog
        IApplicationAssociationRegistrationUI *pAARUI = 0;
        HRESULT hr = CoCreateInstance(CLSID_ApplicationAssociationRegistrationUI, NULL, CLSCTX_INPROC_SERVER,
            IID_IApplicationAssociationRegistrationUI, (LPVOID*)&pAARUI);

        if (SUCCEEDED(hr)) {
            pAARUI->LaunchAdvancedAssociationUI(L"Zimbra Desktop");
            pAARUI->Release();
        }
    }
    else if (IsWindowsVistaOrGreater()) { // For Windows Vista and 7 directly set as default
        IApplicationAssociationRegistration* pAAR;
        HRESULT hr = CoCreateInstance(CLSID_ApplicationAssociationRegistration, NULL, CLSCTX_INPROC,
            IID_IApplicationAssociationRegistration, (void**)&pAAR);

        if (SUCCEEDED(hr)) {
            pAAR->SetAppAsDefaultAll(L"Zimbra Desktop");
            pAAR->Release();
        }
    }
}

/**
 * Main entry point for zimbra desktop helper
 * It will get command line arguments and check for arguments /SetAsDefault or /IsDefault
 */
int WINAPI WinMain(HINSTANCE hInstance, HINSTANCE hPrevInstance, LPSTR lpCmdLine, int nShowCmd)
{
    if (lpCmdLine == NULL || lpCmdLine[0] == 0) {
        MessageBox(NULL, L"No arguments provided, please pass /SetAsDefault or /IsDefault as argument", L"Error", 0);
        return 1;
    }

    HRESULT hrInit = ::CoInitialize(NULL);

    if (strcmp(lpCmdLine, "/SetAsDefault") == 0) {
        if (!IsDefaultMailApp()) {
            setAsDefaultMailApp();
        }
    }
    else if (strcmp(lpCmdLine, "/IsDefault") == 0) {
        std::cout << std::boolalpha;
        // Convert 0/1 to false/true
        std::cout << (IsDefaultMailApp() == TRUE);
    }

    if (SUCCEEDED(hrInit)) {
        ::CoUninitialize();
    }

    return 0;
}