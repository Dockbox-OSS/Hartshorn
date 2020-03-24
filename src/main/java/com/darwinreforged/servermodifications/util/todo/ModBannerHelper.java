package com.darwinreforged.servermodifications.util.todo;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

public class ModBannerHelper {
	public static Text format(String string){
		return TextSerializers.FORMATTING_CODE.deserialize(string);
	}
}
