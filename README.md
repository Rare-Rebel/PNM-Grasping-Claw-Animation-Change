# Attack Reskin

A RuneLite plugin that cosmetically re-renders Phosani's Nightmare's "black
hole" ground effect using the look of Hueycoatl's ground lightning strike.
Purely visual — no change to hitboxes, timing, or damage.

## Files

- `AttackReskinPlugin.java` — plugin logic, and where `SOURCE_GRAPHIC_ID` /
  `TARGET_GRAPHIC_ID` live.
- `AttackReskinConfig.java` — the one setting, the diagnostic ID logger.
- `AttackReskinPluginTest.java` — local test/launch harness.
- `runelite-plugin.properties` — Plugin Hub listing metadata.
- `build.gradle`, `settings.gradle` — standard RuneLite external plugin build,
  matching `runelite/example-plugin`.
