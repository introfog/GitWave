#include <windows.h>


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
        CloseHandle(ProcessInfo.hThread);
        CloseHandle(ProcessInfo.hProcess);
    }
  
    return 0;
}