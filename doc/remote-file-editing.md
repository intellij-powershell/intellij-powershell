<!-- SPDX-FileCopyrightText: 2018 Andrey Dernov <https://github.com/ant-druha/> -->
<!-- SPDX-FileCopyrightText: 2026 intellij-powershell contributors <https://github.com/intellij-powershell/intellij-powershell> -->
<!-- SPDX-License-Identifier: Apache-2.0 -->

# Remote File Editing

intellij-powershell supports remote file editing using the [psedit][] tool.

To edit remote files, use the integrated PowerShell Console: **Tools | PowerShell Console…** action.

## Example

1. Open console: **Tools | PowerShell Console…** and in this PowerShell session log in to a remote host with `Enter-PSSession` PowerShell command, for example:

   ```powershell
   Enter-PSSession -HostName 127.0.0.1 -UserName Andrey
   ```

   ![New PowerShell Console session](images/powershell-console.png)

2. Then open a remote file with `psedit` command, for example:

   ```powershell
   psedit ./script1.ps1
   ```

   ![Opening a remote script in the PowerShell Console with psedit](images/powershell-console-psedit-open-file.png)

Now you can edit the file (which is actually opened in a temporary location on your local host), and the console terminal session will automatically save it on a remote host.

Here is a short video recap:

![Remote file editing demo](images/remote-file-editing.gif)

[psedit]: https://github.com/ironmansoftware/psedit
