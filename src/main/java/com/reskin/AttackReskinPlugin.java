package com.reskin;

import com.google.inject.Provides;
import java.util.ArrayDeque;
import java.util.Deque;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Animation;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GraphicsObject;
import net.runelite.api.Model;
import net.runelite.api.RuneLiteObject;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.GraphicsObjectCreated;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

/**
 * Cosmetically replaces the look of Phosani's Nightmare's "black hole" ground effect
 * with the look of Hueycoatl's ground lightning strike.
 * <p>
 * This is purely client-side rendering. It hides the original {@link GraphicsObject} and
 * draws a {@link RuneLiteObject} in its place using a model/animation captured live from a
 * real spawn of the target effect. Nothing about hitboxes, timing, or damage changes -
 * the underlying mechanic is completely untouched, only what you see on your own screen
 * is different.
 * <p>
 * The two spotanim IDs below are fixed constants rather than user-configurable fields.
 * This is intentional: RuneLite's Plugin Hub does not accept "ID based" plugins that let
 * end users type in arbitrary graphic IDs for the entirety of their functionality, only
 * plugins that ship a fixed, developer-chosen mapping (see the accepted "Vardorvis
 * Projectiles" plugin for precedent).
 */
@Slf4j
@PluginDescriptor(
	name = "PNM Grasping Claw Animation Change",
	description = "Replaces the visual of Phosani's Nightmare's black hole with Huey's ground lightning strike",
	tags = {"nightmare", "phosani", "hueycoatl", "huey", "graphics", "cosmetic"}
)
public class AttackReskinPlugin extends Plugin
{
	/**
	 * Phosani's Nightmare "black hole" ground effect.
	 * Best-effort match against NIGHTMARE_RIFT in RuneLite's SpotanimID list.
	 * VERIFY THIS against a live capture (turn on "Log graphic IDs to chat" and fight
	 * Phosani's Nightmare) before publishing - update this constant if it's wrong.
	 */
	private static final int SOURCE_GRAPHIC_ID = 1754;

	/**
	 * Hueycoatl's ground lightning strike.
	 * Best-effort match against LIGHTNING_TILE_BLUE in RuneLite's SpotanimID list.
	 * VERIFY THIS against a live capture (turn on "Log graphic IDs to chat" and fight
	 * Hueycoatl) before publishing - update this constant if it's wrong.
	 */
	private static final int TARGET_GRAPHIC_ID = 55209;

	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private AttackReskinConfig config;

	// Captured "look" of the target effect, grabbed live the first time we see it spawn.
	private Model capturedModel;
	private Animation capturedAnimation;

	// Active replacement objects, so we can clean them up on shutdown.
	private final Deque<RuneLiteObject> activeReplacements = new ArrayDeque<>();

	@Provides
	AttackReskinConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(AttackReskinConfig.class);
	}

	@Override
	protected void shutDown()
	{
		for (RuneLiteObject obj : activeReplacements)
		{
			obj.setActive(false);
		}
		activeReplacements.clear();
		capturedModel = null;
		capturedAnimation = null;
	}

	@Subscribe
	public void onGraphicsObjectCreated(GraphicsObjectCreated event)
	{
		GraphicsObject go = event.getGraphicsObject();
		int id = go.getId();

		if (config.debugLogIds())
		{
			client.addChatMessage(
				ChatMessageType.GAMEMESSAGE,
				"",
				"[Attack Reskin] graphic id " + id + " spawned at " + go.getLocation(),
				null
			);
		}

		// Grab a fresh copy of the target's look every time it's seen, so we're never
		// stuck with a stale reference if the client garbage-collects unused models.
		if (id == TARGET_GRAPHIC_ID)
		{
			capturedModel = go.getModel();
			capturedAnimation = go.getAnimation();
			return;
		}

		if (id == SOURCE_GRAPHIC_ID)
		{
			if (capturedModel == null)
			{
				log.debug("Attack Reskin: haven't captured the target graphic ({}) yet, "
					+ "so source graphic {} is showing unmodified.", TARGET_GRAPHIC_ID, id);
				return;
			}

			// Hide the original effect...
			go.setFinished(true);

			// ...and spawn our stand-in at the same spot.
			RuneLiteObject replacement = client.createRuneLiteObject();
			replacement.setModel(capturedModel);
			if (capturedAnimation != null)
			{
				replacement.setAnimation(capturedAnimation);
				replacement.setShouldLoop(false);
			}
			replacement.setLocation(go.getLocation(), go.getLevel());
			replacement.setActive(true);

			activeReplacements.add(replacement);
		}
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		// Clean up replacements whose object has despawned itself (non-looping anims
		// deactivate on their own via RuneLiteObject), so the deque doesn't grow forever.
		activeReplacements.removeIf(obj -> !obj.isActive());
	}
}
