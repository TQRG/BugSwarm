﻿## Reporting issues

 Notes:

 * Please report the GoClipse version you are using (can be found on `"Help/About/Installation Details/Installed Software"`),
 and operating system and architecture.
 * Check the Eclipse Error Log for errors or Java exceptions (see 
 `"Help/About/Installation Details/Configuration/Error Log"`). 
 If you see any error entries that you think might be relevant to the issue, post them.
 * Optional: you might be asked to post/attach the Configuration Log: This can be found at `"Help/About/Installation Details/Configuration"`
 
 ▶An easy way to attach big text files in Github issues is to use https://gist.github.com/.

##### Debugger issues

Debbugger issues can be hard to track down, because of all the components involved: Goclipse, CDT, GDB, OS binary format, and Go toolchain.

> _**Note:** The Go toolchain does not properly support debugging in Windows. While setting breakpoints and step/continue seems to work, inspecting variables doesn't work._

If you think you've found a problem with the debugger integration, please try the following first:
 * Use latest version of Goclipse.
 * Use recommended/tested version of CDT (this is the CDT version that GoClipse has most recently been tested against). This is listed in the changelog for each GoClipse release.
 * Use latest stable GDB, and the latest stable Go compiler.
 * Make sure you are compiling with inline optimizations turned off, ie, compile with `-gcflags "-N -l"`.

And most importantly: 
 * **Try to reproduce the problem outside of Eclipse, using just the command-line GDB interface. If the problem also occurs there, then it's an issue in either GDB or the Go compiler tools, not GoClipse. For more info on how to use command-line GDB, see http://golang.org/doc/gdb.** 

If it does seem to be a GoClipse problem, open a new issue in Github, providing the following info:
 * Configuration Log: `Help/About/Installation Details/Configuration`
 * Minimal source used to reproduce the issue (if possible).
 * The contents of `gdb` and `gdb traces` console in Eclipse, for your debug launch. 

> (if you're looking for a place to upload large files, you can use https://gist.github.com)

## Contributing
The Goclipse project welcomes contributions. TODO more info
