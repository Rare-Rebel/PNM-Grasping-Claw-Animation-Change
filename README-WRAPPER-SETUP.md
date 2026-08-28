# Gradle Wrapper Files

These 4 files were missing from the plugin repo, which is why IntelliJ showed
"Invalid Gradle JDK configuration found" / "'gradle-wrapper.properties' not
found". Every real Gradle project (including RuneLite's own plugin template)
ships these — they let anyone build the project with the exact right Gradle
version automatically, without installing Gradle themselves.

## Where each file needs to end up in your GitHub repo

```
PNM-Grasping-Claw-Animation-Change/      <- repo root (same level as build.gradle)
├── gradlew
├── gradlew.bat
└── gradle/
    └── wrapper/
        ├── gradle-wrapper.jar
        └── gradle-wrapper.properties
```

`gradlew` and `gradlew.bat` sit directly at the repo root, next to
`build.gradle`. The other two go inside a `gradle/wrapper/` folder.

## How to upload these via GitHub's website

1. Go to your repo's main page.
2. Click "Add file" > "Upload files".
3. Drag in `gradlew` and `gradlew.bat` from this folder directly onto the
   upload page (these two are plain text/script files, easy to drag).
4. Commit that.
5. For the `gradle/wrapper/` folder: click "Add file" > "Upload files" again,
   then drag in the whole `gradle` folder (containing `wrapper/` with the
   two files inside) from this download. GitHub's uploader generally handles
   one level of nesting fine when dragging a folder directly from Finder/
   Explorer onto the page — if it flattens or drops the folder structure,
   use "Create new file" instead, type the full path
   `gradle/wrapper/gradle-wrapper.properties` into the filename box (GitHub
   auto-creates the folders), and paste in the properties file's content
   (it's plain text, shown below). The `.jar` file is binary and can't be
   pasted as text — it must go through the drag-and-drop upload, not
   "Create new file".

## gradle-wrapper.properties content (for reference / manual paste)

```
distributionBase=GRADLE_USER_HOME
distributionPath=wrapper/dists
distributionUrl=https\://services.gradle.org/distributions/gradle-8.5-bin.zip
zipStoreBase=GRADLE_USER_HOME
zipStorePath=wrapper/dists
```

## After uploading

1. Download the repo as a ZIP again (or pull the new files into your
   existing local copy), replacing your local project folder.
2. In IntelliJ, re-open/re-sync the project (File > Sync Project with
   Gradle Files, or reload from the Gradle tool window).
3. In Settings > Build, Execution, Deployment > Build Tools > Gradle, the
   "Distribution" dropdown can stay on "Wrapper" now — the error should be
   gone since `gradle-wrapper.properties` will actually exist.
4. Set "Gradle JVM" to a JDK 11 if it still shows "<No Project SDK>".
