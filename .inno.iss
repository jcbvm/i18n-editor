#define AppName "i18n-editor"
#define AppVersion "2.0.0-beta.1"
#define AppPublisher "JvMs Software"
#define AppURL "https://github.com/jcbvm/i18n-editor"
#define AppExeName "i18n-editor.exe"

[Setup]
AppId={{16A49296-8A8D-4BDA-A743-5F1BF02953D5}
AppName={#AppName}
AppVersion={#AppVersion}
AppPublisher={#AppPublisher}
AppPublisherURL={#AppURL}
AppSupportURL={#AppURL}
AppUpdatesURL={#AppURL}
DefaultDirName={pf}\{#AppPublisher}\{#AppName}
DisableProgramGroupPage=auto
DisableDirPage=auto
AlwaysShowDirOnReadyPage=yes
LicenseFile=LICENSE
OutputBaseFilename={#AppName}-{#AppVersion}-setup
OutputDir=target\{#AppName}-{#AppVersion}
SetupIconFile=src\main\resources\images\icon.ico
UninstallDisplayIcon={uninstallexe}
Compression=lzma
SolidCompression=yes
ArchitecturesAllowed=x86 x64 ia64
ArchitecturesInstallIn64BitMode=x64 ia64

[Languages]
Name: "english"; MessagesFile: "compiler:Default.isl"
Name: "dutch"; MessagesFile: "compiler:Languages\Dutch.isl"
Name: "brazilianportuguese"; MessagesFile: "compiler:Languages\BrazilianPortuguese.isl"

[Tasks]
Name: "desktopicon"; Description: "{cm:CreateDesktopIcon}"; GroupDescription: "{cm:AdditionalIcons}"; Flags: unchecked
Name: "quicklaunchicon"; Description: "{cm:CreateQuickLaunchIcon}"; GroupDescription: "{cm:AdditionalIcons}"; Flags: unchecked; OnlyBelowVersion: 0,6.1

[Files]
Source: "target\{#AppName}-{#AppVersion}\{#AppExeName}"; DestDir: "{app}"; Flags: ignoreversion
; NOTE: Don't use "Flags: ignoreversion" on any shared system files

[Icons]
Name: "{commonprograms}\{#AppPublisher}\{#AppName}"; Filename: "{app}\{#AppExeName}"
Name: "{commondesktop}\{#AppPublisher}\{#AppName}"; Filename: "{app}\{#AppExeName}"; Tasks: desktopicon
Name: "{userappdata}\Microsoft\Internet Explorer\Quick Launch\{#AppPublisher}\{#AppName}"; Filename: "{app}\{#AppExeName}"; Tasks: quicklaunchicon

[Run]
Filename: "{app}\{#AppExeName}"; Description: "{cm:LaunchProgram,{#StringChange(AppName, '&', '&&')}}"; Flags: nowait postinstall skipifsilent
