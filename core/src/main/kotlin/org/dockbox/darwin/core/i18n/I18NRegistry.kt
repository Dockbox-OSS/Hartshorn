package org.dockbox.darwin.core.i18n

import org.dockbox.darwin.core.text.Text
import java.util.*

interface I18NRegistry {

    fun getValue(): String

    fun plain(): String {
        return this.getValue().replace("[$|&][0-9a-fklmnor]", "")
    }

    fun asString(): String {
        return parseColors(this.getValue())
    }

    fun asText(): Text {
        return Text.of(asString())
    }

    fun format(vararg args: Any?): String {
        return format(getValue(), args)
    }

    fun shortFormat(vararg args: Any?): String {
        val diff = asString().length - plain().length
        val formatted = format(*args)
        return if (49 + diff > formatted.length) formatted else format(*args).substring(0, 50 + diff)
    }

    fun short(): String {
        return if (this.getValue().length > 49) this.getValue().substring(0, 50) else this.getValue()
    }

    // Format value placeholders and colors
    fun format(m: String, vararg args: Any): String {
        var m = m
        if (args.isEmpty()) return m
        val map: MutableMap<String, String?> = LinkedHashMap()

        if (args.isNotEmpty()) {
            for (i in args.size - 1 downTo 0) {
                val arg = "" + args[i]
                if (arg.isEmpty()) map[String.format("{%d}", i)] = "" else map[String.format("{%d}", i)] = arg
                if (i == 0) map["%s"] = arg
            }
        }
        m = replaceFromMap(m, map)
        return parseColors(m)
    }

    private fun parseColors(m: String): String {
        var m = m
        val nativeFormats = "abcdef1234567890klmnor".toCharArray()
        for (c in nativeFormats) m = m.replace(String.format("&%s", c).toRegex(), String.format("\u00A7%s", c))
        return "\u00A7r" + m
                .replace("\\$1".toRegex(), java.lang.String.format("\u00A7%s", this.getValue()))
                .replace("\\$2".toRegex(), java.lang.String.format("\u00A7%s", this.getValue()))
                .replace("\\$3".toRegex(), java.lang.String.format("\u00A7%s", this.getValue()))
                .replace("\\$4".toRegex(), java.lang.String.format("\u00A7%s", this.getValue()))
    }

//    open fun collect() {
//        CoreServer.getModuleLoader().getModuleInstance(DarwinServerModule::class.java)
//
//        DarwinCore.getModule(DarwinServerModule::class.java).ifPresent({ module ->
//            val configMap: MutableMap<String, Any>
//            val file = File(DarwinServer.getUtilChecked(FileUtils::class.java).getConfigDirectory(module).toFile(), "translations.yml")
//            if (!file.exists()) {
//                configMap = HashMap<String, Any>()
//                Arrays.stream(Translations.values()).forEach { translation -> configMap[translation.name().toLowerCase().replaceAll("_", ".")] = translation.u() }
//                DarwinServer.getUtilChecked(FileUtils::class.java).writeYamlDataToFile(configMap, file)
//            } else configMap = DarwinServer.getUtilChecked(FileUtils::class.java).getYamlDataFromFile(file)
//            configMap.forEach { (k: String, v: Any) ->
//                val t: Translations = Translations.valueOf(k.toUpperCase().replace("\\.".toRegex(), "_"))
//                if (t != null) t.c(v.toString())
//            }
//        })
//    }

    @Suppress("UNCHECKED_CAST")
    fun replaceFromMap(string: String, replacements: MutableMap<String, String?>): String {
        val sb = StringBuilder(string)
        var size = string.length
        val var4: Iterator<*> = replacements.entries.iterator()
        while (var4.hasNext()) {
            val entry: Map.Entry<String, String> = var4.next() as Map.Entry<String, String>
            if (size == 0) {
                break
            }
            val key = entry.key
            val value = entry.value
            var nextSearchStart: Int
            var start = sb.indexOf(key, 0)
            while (start > -1) {
                val end = start + key.length
                nextSearchStart = start + value.length
                sb.replace(start, end, value)
                size -= end - start
                start = sb.indexOf(key, nextSearchStart)
            }
        }
        return sb.toString()
    }

}
