### Features:

 * Go source code editor, with:
   * Syntax highlighting (configurable). 
   * Automatic indent/de-indent and brace completion on certain keypresses (Enter, Backspace).
 * Go Project wizard and project builder.
   * With in-editor build errors reporting.

| [![sample_basic](screenshots/sample_basic.thumb.png)](screenshots/sample_basic.png?raw=true)<br/>`Workbench and editor` |
|----|

   * Editor outline and Quick-Outline (`Ctrl+O`).
   
| [![sample_basic](screenshots/Feature_QuickOutline.thumb.png)](screenshots/Feature_QuickOutline.png?raw=true)<br/>`Outline (right) and Quick-Outline (filtered to "pr")` |
|----|

 * Open Definition (and "Ctrl-click") via `guru` or `godef`.
 * Content Assist (auto-complete) via `gocode`. 
  * Content Assist code snippets (configurable).

| [![sample_basic](screenshots/Feature_ContentAssist.thumb.png)](screenshots/Feature_ContentAssist.png?raw=true)<br/>`Content Assist` |
|----| 

#### Debugging functionality. 
Fully featured GDB debugger support (reusing Eclipse CDT's GDB integration)
  * Stop/resume program execution. Listing program threads and stack frame contents.
  * Setting breakpoints, watchpoints (breakpoint on data/variables), tracepoints. Breakpoint conditions.
  * Stack variables inspection view. Expression watch and view. Disassembly view.
  * Non-stop mode (for supported GBDs). Reverse debugging (for supported GDB targets).

| [![sample_debug](screenshots/sample_debug.thumb.png)](screenshots/sample_debug.png?raw=true)<br/>`Execution stopped on a breakpoint` |
|----|
