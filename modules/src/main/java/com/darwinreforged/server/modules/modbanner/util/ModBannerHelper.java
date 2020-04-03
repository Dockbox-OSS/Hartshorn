package com.darwinreforged.server.modules.modbanner.util;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

public class ModBannerHelper {
	public static Text format(String string){
		return TextSerializers.FORMATTING_CODE.deserialize(string);
	}
}
