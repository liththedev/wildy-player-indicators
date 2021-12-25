/*
 * Copyright (c) 2018, Tomas Slusny <slusnucky@gmail.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.wildyplayerindicators;

import static com.wildyplayerindicators.WildyPlayerIndicatorsService.WILDERNESS_LEVEL_PATTERN;
import static net.runelite.api.MenuAction.ITEM_USE_ON_PLAYER;
import static net.runelite.api.MenuAction.PLAYER_EIGTH_OPTION;
import static net.runelite.api.MenuAction.PLAYER_FIFTH_OPTION;
import static net.runelite.api.MenuAction.PLAYER_FIRST_OPTION;
import static net.runelite.api.MenuAction.PLAYER_FOURTH_OPTION;
import static net.runelite.api.MenuAction.PLAYER_SECOND_OPTION;
import static net.runelite.api.MenuAction.PLAYER_SEVENTH_OPTION;
import static net.runelite.api.MenuAction.PLAYER_SIXTH_OPTION;
import static net.runelite.api.MenuAction.PLAYER_THIRD_OPTION;
import static net.runelite.api.MenuAction.RUNELITE_PLAYER;
import static net.runelite.api.MenuAction.SPELL_CAST_ON_PLAYER;
import static net.runelite.api.MenuAction.WALK;

import java.awt.Color;
import java.util.regex.Matcher;

import javax.inject.Inject;

import com.google.inject.Provides;

import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.api.Player;
import net.runelite.api.WorldType;
import net.runelite.api.events.ClientTick;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.ColorUtil;;

@PluginDescriptor(
	name = "Wildy Player Indicators",
	description = "Highlight players in the wilderness on-screen and/or on the minimap",
	tags = {"highlight", "minimap", "overlay", "players", "wilderness"}
)
public class WildyPlayerIndicatorsPlugin extends Plugin
{
	@Inject
	private OverlayManager overlayManager;

	@Inject
	private WildyPlayerIndicatorsConfig config;

	@Inject
	private WildyPlayerIndicatorsOverlay playerIndicatorsOverlay;

	@Inject
	private WildyPlayerIndicatorsTileOverlay playerIndicatorsTileOverlay;

	@Inject
	private WildyPlayerIndicatorsMinimapOverlay playerIndicatorsMinimapOverlay;

	@Inject
	private Client client;

	@Provides
	WildyPlayerIndicatorsConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(WildyPlayerIndicatorsConfig.class);
	}

	@Override
	protected void startUp() throws Exception
	{
		overlayManager.add(playerIndicatorsOverlay);
		overlayManager.add(playerIndicatorsTileOverlay);
		overlayManager.add(playerIndicatorsMinimapOverlay);
	}

	@Override
	protected void shutDown() throws Exception
	{
		overlayManager.remove(playerIndicatorsOverlay);
		overlayManager.remove(playerIndicatorsTileOverlay);
		overlayManager.remove(playerIndicatorsMinimapOverlay);
	}

	@Subscribe
	public void onClientTick(ClientTick clientTick)
	{
		if (client.isMenuOpen())
		{
			return;
		}

		MenuEntry[] menuEntries = client.getMenuEntries();

		for (MenuEntry entry : menuEntries)
		{
			MenuAction type = entry.getType();

			if (type == WALK
				|| type == SPELL_CAST_ON_PLAYER
				|| type == ITEM_USE_ON_PLAYER
				|| type == PLAYER_FIRST_OPTION
				|| type == PLAYER_SECOND_OPTION
				|| type == PLAYER_THIRD_OPTION
				|| type == PLAYER_FOURTH_OPTION
				|| type == PLAYER_FIFTH_OPTION
				|| type == PLAYER_SIXTH_OPTION
				|| type == PLAYER_SEVENTH_OPTION
				|| type == PLAYER_EIGTH_OPTION
				|| type == RUNELITE_PLAYER)
			{
				Player[] players = client.getCachedPlayers();
				Player player = null;

				int identifier = entry.getIdentifier();

				// 'Walk here' identifiers are offset by 1 because the default
				// identifier for this option is 0, which is also a player index.
				if (type == WALK)
				{
					identifier--;
				}

				if (identifier >= 0 && identifier < players.length)
				{
					player = players[identifier];
				}

				if (player == null)
				{
					continue;
				}

				Color color = getDecorations(player);

				if (color == null)
				{
					continue;
				}

				String oldTarget = entry.getTarget();
				String newTarget = decorateTarget(oldTarget, color);

				entry.setTarget(newTarget);
			}
		}
	}

	private Color getDecorations(Player player)
	{
		final Widget wildernessLevelWidget = client.getWidget(WidgetInfo.PVP_WILDERNESS_LEVEL);
		if (wildernessLevelWidget == null)
		{
			return null;
		}

		final String wildernessLevelText = wildernessLevelWidget.getText();
		final Matcher m = WILDERNESS_LEVEL_PATTERN.matcher(wildernessLevelText);
		if (!m.matches()
			|| WorldType.isPvpWorld(client.getWorldType()))
		{
			return null;
		}

		final Player localPlayer = client.getLocalPlayer();

		final int wildernessLevel = Integer.parseInt(m.group(1));
		final int localPlayerLevel = localPlayer.getCombatLevel();
		final int minAttackableLevel = localPlayerLevel - wildernessLevel;
		final int maxAttackableLevel = localPlayerLevel + wildernessLevel;
		final int minNearAttackableLevel = localPlayerLevel - wildernessLevel - config.nearAttackableTolerance();
		final int maxNearAttackableLevel = localPlayerLevel + wildernessLevel + config.nearAttackableTolerance();

		if (player == null || player.getName() == null)
		{
			return null;
		}

		boolean isFriendsChatMember = player.isFriendsChatMember();
		boolean isClanMember = player.isClanMember();

		if (player == localPlayer)
		{
			return null;
		}
		else if (config.excludeFriends() && player.isFriend())
		{
			return null;
		}
		else if (config.excludeFriendsChat() && isFriendsChatMember)
		{
			return null;
		}
		else if (config.excludeTeamMembers() && localPlayer.getTeam() > 0 && localPlayer.getTeam() == player.getTeam())
		{
			return null;
		}
		else if (config.excludeClanMembers() && isClanMember)
		{
			return null;
		}

		final int otherLevel = player.getCombatLevel();
		if (otherLevel <= maxAttackableLevel && otherLevel >= minAttackableLevel) {
			return config.getAttackableColor();
		}
		if (config.highlightNearAttackable() 
					&& otherLevel <= maxNearAttackableLevel && otherLevel >= minNearAttackableLevel) {
			return config.getNearAttackableColor();
		}

		return null;
	}

	private String decorateTarget(String oldTarget, Color color)
	{
		String newTarget = oldTarget;

		if (color != null && config.colorPlayerMenu())
		{
			// strip out existing <col...
			int idx = oldTarget.indexOf('>');
			if (idx != -1)
			{
				newTarget = oldTarget.substring(idx + 1);
			}

			newTarget = ColorUtil.prependColorTag(newTarget, color);
		}

		return newTarget;
	}
}
