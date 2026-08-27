# Attack Reskin

A RuneLite plugin that cosmetically re-renders Phosani's Nightmare's "black
hole" ground effect using the look of Hueycoatl's ground lightning strike.
Purely visual — no change to hitboxes, timing, or damage.

## Before you do anything else: verify the two graphic IDs

The two spotanim IDs in `AttackReskinPlugin.java` are my best-effort guesses,
matched by *name* against RuneLite's public `SpotanimID` constant list —
I was not able to confirm them against a live capture:

- `SOURCE_GRAPHIC_ID = 1767` — guessed as `NIGHTMARE_RIFT`
- `TARGET_GRAPHIC_ID = 2429` — guessed as `LIGHTNING_TILE_BLUE`

If either is wrong, the plugin will just silently do nothing for that effect.
To check:

1. Build and sideload the plugin locally (see below).
2. Turn on **"Log graphic IDs to chat"** in the plugin's settings panel.
3. Fight Hueycoatl and watch the chatbox when the lightning strikes — note the
   ID it logs.
4. Fight Phosani's Nightmare and watch for the black hole — note that ID too.
5. If either differs from the constants above, edit them directly in
   `AttackReskinPlugin.java` (`SOURCE_GRAPHIC_ID` / `TARGET_GRAPHIC_ID`),
   turn the debug logging back off, and rebuild.

**Do this before opening a Plugin Hub PR.** These are fixed constants on
purpose (see "Why the IDs aren't a config option" below) — ship them correct.

## How it works

RuneLite's public API doesn't expose a way to overwrite the ID of an
already-spawned `GraphicsObject` (there's no `setId()`, it's read-only by
design). So instead of hacking the real effect, the plugin:

1. Listens for `GraphicsObjectCreated`.
2. The first time it sees the **target** graphic (Huey's lightning) spawn
   anywhere, it grabs a live reference to that effect's `Model` and
   `Animation`.
3. Every time it then sees the **source** graphic (the black hole) spawn, it
   calls `setFinished(true)` on it to stop it rendering, and spawns a
   `RuneLiteObject` (RuneLite's supported custom-render API) at the same tile
   using the captured model/animation.

One consequence: the plugin needs to have seen Huey's lightning at least once
in your current session before it can reskin the black hole. Until then the
black hole renders normally.

## Why the IDs aren't a config option

RuneLite's Plugin Hub review explicitly does not accept "ID based" plugins —
ones where a user can type in arbitrary graphic/animation/item IDs to drive
the plugin's core behaviour. The precedent they cite for what *is* allowed is
a plugin like "Vardorvis Projectiles," which only lets you pick from a fixed,
developer-defined set of alternate looks. This plugin follows that pattern:
the mapping is a hardcoded constant, not a text field.

## Building and testing locally

1. Install IntelliJ IDEA (Community is fine) and a JDK 11 (Eclipse Temurin
   works well — IntelliJ can install it for you).
2. Open this folder as a Gradle project in IntelliJ and let it sync.
3. Run `AttackReskinPluginTest` (in `src/test/java/com/reskin/`) — this
   launches a full RuneLite client with only this plugin side-loaded, exactly
   like the official `example-plugin` template does. If you run it from the
   command line instead of the IDE, add `-ea` to enable assertions.
4. Log in, open the plugin panel, confirm "Attack Reskin" is there and
   configurable, and do the ID verification steps above.

## Submitting to the Plugin Hub

The Plugin Hub doesn't host your code — it hosts a *pointer* to a commit in
your own public GitHub repo. Steps:

1. Create a new public GitHub repo (e.g. `attack-reskin`) and push this
   folder's contents to it as the repo root (so `build.gradle` sits at the
   top level, not nested).
2. Edit `runelite-plugin.properties`: replace `YOUR_GITHUB_USERNAME` with
   your actual GitHub username in both the `author` and `support` lines.
3. Commit and push. Note the full commit hash of that push
   (`git rev-parse HEAD`).
4. Fork `https://github.com/runelite/plugin-hub`.
5. In your fork, add a new file at `plugins/attack-reskin` (no extension)
   containing:
   ```
   repository=https://github.com/YOUR_GITHUB_USERNAME/attack-reskin.git
   commit=<the full commit hash from step 3>
   ```
6. Open a pull request from your fork's branch into `runelite/plugin-hub`.
7. Their CI will try to build your plugin, and a bot + human reviewers will
   check it against their rules. If they request changes, push a new commit
   to your plugin repo, update the `commit=` line in your PR, and push again.
8. Once merged, it becomes installable from the in-client Plugin Hub within
   a short delay.

Optional: add a `icon.png` (≤48×72px) to the repo root to have an icon show
up next to your plugin's listing.

## Files

- `AttackReskinPlugin.java` — plugin logic, and where `SOURCE_GRAPHIC_ID` /
  `TARGET_GRAPHIC_ID` live.
- `AttackReskinConfig.java` — the one setting, the diagnostic ID logger.
- `AttackReskinPluginTest.java` — local test/launch harness.
- `runelite-plugin.properties` — Plugin Hub listing metadata.
- `build.gradle`, `settings.gradle` — standard RuneLite external plugin build,
  matching `runelite/example-plugin`.
