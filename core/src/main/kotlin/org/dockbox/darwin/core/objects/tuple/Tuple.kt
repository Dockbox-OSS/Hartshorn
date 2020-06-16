package org.dockbox.darwin.core.objects.tuple

class Tuple<K, V>(var first: K, var second: V) {

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other == null || javaClass != other.javaClass) {
            return false
        }
        val other = other as Tuple<*, *>
        return first == other.first && second == other.second
    }

    override fun hashCode(): Int {
        var result = first?.hashCode() ?: 0
        result = 31 * result + (second?.hashCode() ?: 0)
        return result
    }

    companion object {
        fun <K, V> of(first: K, second: V): Tuple<K, V> {
            return Tuple(first, second)
        }
    }

}
