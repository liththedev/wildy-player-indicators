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

import java.awt.Color;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("playerindicators")
public interface WildyPlayerIndicatorsConfig extends Config
{

	@ConfigItem(
		position = 1,
		keyName = "drawPlayerTiles",
		name = "Draw tiles under players",
		description = "Configures whether or not tiles under highlighted players should be drawn"
	)
	default boolean drawTiles()
	{
		return false;
	}

	@ConfigItem(
		position = 2,
		keyName = "playerNamePosition",
		name = "Name position",
		description = "Configures the position of drawn player names, or if they should be disabled"
	)
	default PlayerNameLocation playerNamePosition()
	{
		return PlayerNameLocation.ABOVE_HEAD;
	}

	@ConfigItem(
		position = 3,
		keyName = "drawMinimapNames",
		name = "Draw names on minimap",
		description = "Configures whether or not minimap names for players with rendered names should be drawn"
	)
	default boolean drawMinimapNames()
	{
		return false;
	}

	@ConfigItem(
		position = 4,
		keyName = "colorPlayerMenu",
		name = "Colorize player menu",
		description = "Color right click menu for players"
	)
	default boolean colorPlayerMenu()
	{
		return true;
	}

	@ConfigItem(
		position = 5,
		keyName = "excludeFriends",
		name = "Exclude Friends",
		description = "Hide indicators for Friends"
	)
	default boolean excludeFriends()
	{
		return true;
	}

	@ConfigItem(
		position = 6,
		keyName = "excludeFriendsChat",
		name = "Exclude Friends Chat",
		description = "Hide indicators for Friends Chat"
	)
	default boolean excludeFriendsChat()
	{
		return true;
	}

	@ConfigItem(
		position = 7,
		keyName = "excludeTeamMembers",
		name = "Exclude Team Members",
		description = "Hide indicators for Team Members"
	)
	default boolean excludeTeamMembers()
	{
		return true;
	}

	@ConfigItem(
		position = 8,
		keyName = "excludeClanMembers",
		name = "Exclude Clan Members",
		description = "Hide indicators for Clan Members"
	)
	default boolean excludeClanMembers()
	{
		return true;
	}

	@ConfigItem(
		position = 9,
		keyName = "attackableColor",
		name = "Attackable",
		description = "Color of attackable players' names"
	)
	default Color getAttackableColor()
	{
		return Color.RED;
	}

	@ConfigItem(
		position = 10,
		keyName = "drawNearAttackableNames",
		name = "Highlight nearly attackable players",
		description = "Configures whether or not nearly attackable players should be highlighted"
	)
	default boolean highlightNearAttackable()
	{
		return false;
	}

	@ConfigItem(
		position = 11,
		keyName = "nearAttackableTolerance",
		name = "Nearly attackable tolerance",
		description = "Configures how near a players level can be to be highlighted"
	)
	default int nearAttackableTolerance()
	{
		return 5;
	}

	@ConfigItem(
		position = 12,
		keyName = "nearAttackableColor",
		name = "NearAttackable",
		description = "Color of nearly attackable players' names"
	)
	default Color getNearAttackableColor()
	{
		return Color.ORANGE;
	}
}
