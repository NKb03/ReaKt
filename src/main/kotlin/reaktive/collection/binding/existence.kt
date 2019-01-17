/**
 * @author Nikolaus Knop
 */

package reaktive.collection.binding

import reaktive.Observer
import reaktive.collection.ReactiveCollection
import reaktive.collection.observeCollection
import reaktive.value.ReactiveBoolean
import reaktive.value.binding.*
import reaktive.value.now


/**
 * @return A [ReactiveBoolean] which holds `true` only when all [elements] are contained in this collection
 */
fun <E> ReactiveCollection<E>.containsAll(elements: ReactiveCollection<@UnsafeVariance E>) {
    TODO("not implemented")
}

/**
 * @return A [ReactiveBoolean] which holds `true` only when all [elements] are contained in this collection
 */
fun <E> ReactiveCollection<E>.containsAll(elements: Collection<@UnsafeVariance E>) {
    TODO("not implemented")
}

/**
 * @return a [ReactiveBoolean] which holds `true` only
 * when all elements of this [ReactiveCollection] fulfill the given [predicate]
 */
fun <E> ReactiveCollection<E>.all(predicate: (E) -> ReactiveBoolean): ReactiveBoolean =
    binding(true) {
        val nonFulfilling = mutableSetOf<E>()
        fun observeElement(el: E): Observer {
            val pred = predicate(el)
            if (!pred.now) {
                nonFulfilling.add(el)
            }
            val obs = pred.observe { _, _, fulfills ->
                if (fulfills) nonFulfilling.remove(el) else nonFulfilling.add(el)
                set(nonFulfilling.isEmpty())
            }
            addObserver(obs)
            return obs
        }

        val map = now.associateTo(mutableMapOf()) { it to observeElement(it) }
        set(nonFulfilling.isEmpty())
        val obs = this@all.observeCollection(added = { _, element ->
            val obs = observeElement(element)
            map[element] = obs
            set(nonFulfilling.isEmpty())
        },
            removed = { _, element ->
                val obs = map[element]
                obs?.kill()
                nonFulfilling.remove(element)
                set(nonFulfilling.isEmpty())
            })
        addObserver(obs)
    }

fun <E> ReactiveCollection<E>.count(pred: (E) -> Boolean): Binding<Int> {
    val matchingElements = now.filterTo(mutableSetOf(), pred)
    return binding(matchingElements.size) {
        observeCollection(
            added = { _, e ->
                if (pred(e)) {
                    matchingElements.add(e)
                    withValue { set(it + 1) }
                }
            },
            removed = { _, e ->
                if (e in matchingElements) withValue { set(it + 1) }
            }
        )
    }
}

fun <E> ReactiveCollection<E>.any(pred: (E) -> Boolean): Binding<Boolean> =
    count(pred).greaterThan(0)

/**
 * @return A [ReactiveBoolean] which holds `true` only when [element] is contained in this collection
 */
fun <E> ReactiveCollection<E>.contains(element: E): ReactiveBoolean = any { it == element }