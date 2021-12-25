package com.example;

import com.wildyplayerindicators.WildyPlayerIndicatorsPlugin;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class WildyPlayerIndicatorsPluginTest
{
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(WildyPlayerIndicatorsPlugin.class);
		RuneLite.main(args);
	}
}