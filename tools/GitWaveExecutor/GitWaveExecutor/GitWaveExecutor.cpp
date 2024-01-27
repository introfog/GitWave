#include <windows.h>


BOOL CALLBACK EnumWindowsProc(HWND hwnd, LPARAM lParam)
{
    DWORD dwPID;

    GetWindowThreadProcessId(hwnd, &dwPID);

    if (dwPID == lParam)
    {
        SetWindowPos(hwnd, HWND_TOP, 0, 0, 0, 0, SWP_NOMOVE | SWP_NOSIZE);
        return FALSE;
    }

    return TRUE;
}

int WINAPI WinMain(HINSTANCE hInstance, HINSTANCE hPrevInstance, LPSTR lpCmdLine, int nCmdShow)
{
    PROCESS_INFORMATION ProcessInfo;

    STARTUPINFO StartupInfo; //This is an [in] parameter
    ZeroMemory(&StartupInfo, sizeof(StartupInfo));
    StartupInfo.cb = sizeof StartupInfo; //Only compulsory field


    if (CreateProcess(NULL, "javaImage\\bin\\javaw.exe \"-m\" \"com.github.introfog.gitwave/com.github.introfog.gitwave.GitWaveLauncher\"",
                      NULL, NULL, FALSE, 0, NULL,
                      NULL, &StartupInfo, &ProcessInfo))
    {
        WaitForSingleObject(ProcessInfo.hProcess, INFINITE);

        // To open app window on top of all other windows
        EnumWindows(EnumWindowsProc, reinterpret_cast<LPARAM>(&ProcessInfo.dwProcessId));

        CloseHandle(ProcessInfo.hThread);
        CloseHandle(ProcessInfo.hProcess);
    }
  
    return 0;
}