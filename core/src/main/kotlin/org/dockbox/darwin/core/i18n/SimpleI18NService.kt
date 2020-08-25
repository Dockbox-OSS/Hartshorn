package org.dockbox.darwin.core.i18n

class SimpleI18NService : I18nService {
    
    override fun injectDocumentedTranslations() {
        // TODO
    }

    override fun getEntry(key: String): I18NRegistry {
        val shadow = key
                .replace("-".toRegex(), "_")
                .replace("\\.".toRegex(), "_")
                .replace("/".toRegex(), "_")
                .toUpperCase()

        try {
            return I18N.valueOf(shadow)
        } catch (ignored: IllegalArgumentException) {
        } catch (ignored: NullPointerException) {
        }

        try {
            return Permission.valueOf(shadow)
        } catch (ignored: IllegalArgumentException) {
        } catch (ignored: NullPointerException) {
        }

        return object : I18NRegistry {
            override fun getValue(): String {
                return "$$shadow"
            }
        }
    }
}
