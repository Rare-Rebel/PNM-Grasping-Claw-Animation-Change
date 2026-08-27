package com.reskin;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("attackreskin")
public interface AttackReskinConfig extends Config
{
	/**
	 * Deliberately NOT a user-editable graphic ID field. RuneLite's Plugin Hub review
	 * rejects "ID based plugins" that let end users type in arbitrary graphic/animation
	 * IDs (see the "Rejected or Rolled Back Features" wiki page). The source/target IDs
	 * for this plugin are fixed constants in {@link AttackReskinPlugin} instead, the same
	 * way the accepted "Vardorvis Projectiles" plugin only offers a fixed set of looks.
	 */

	@ConfigItem(
		keyName = "developerMode",
		name = "Log graphic IDs to chat",
		description = "Diagnostic only: prints the ID of every ground graphic/spotanim that "
			+ "spawns near you, for confirming the hardcoded IDs still match the live game. "
			+ "Does not change plugin behaviour.",
		position = 1
	)
	default boolean developerMode()
	{
		return false;
	}
}
