' * 
' */
'
' A script that helps prism hide console window when calling zdctl.vbs
' This is to workaround a limitation of XPCOM's nsIProcess

Dim oFso, oShell, sCScript, sScriptPath, sZdCtl, sCmd

Set oFso = CreateObject("Scripting.FileSystemObject")
sCScript = Chr(34) & oFso.GetSpecialFolder(1).Path & "\cscript.exe" & Chr(34)

sScriptPath = WScript.ScriptFullName
sZdCtl = Chr(34) & Left(sScriptPath, InStrRev(sScriptPath, WScript.ScriptName) - 2) & "\zdctl.vbs" & Chr(34)

Set oShell = CreateObject("WScript.Shell")
sCmd = sCScript & " " & sZdCtl & " " & WScript.Arguments.Item(0)
oShell.Run sCmd, 0, false
