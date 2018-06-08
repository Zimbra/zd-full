#pragma once

#include <VersionHelpers.h>

#if !defined (IsWindows10OrGreater)

#define _WIN32_WINNT_WIN10 0x0A00

VERSIONHELPERAPI IsWindows10OrGreater()
{
    return IsWindowsVersionOrGreater(HIBYTE(_WIN32_WINNT_WIN10), LOBYTE(_WIN32_WINNT_WIN10), 0);
}

#endif