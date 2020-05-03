/**
 * @author Nikolaus Knop
 */

package reaktive.set

import reaktive.Observer
import reaktive.set.binding.setBinding
import reaktive.value.ReactiveValue
import reaktive.value.now

/**
 * Observes each element of this [ReactiveSet]
 * * When an element is added it is automatically observed
 * * When an element is removed its observer is killed
 * * When the returned observer is killed the observation of all elements is stopped
 */
fun <E> ReactiveSet<E>.observeEach(observe: (E) -> Observer): Observer {
    val observers = mutableMapOf<E, Observer>()
    for (e in now) observers[e] = observe(e)
    val o = observeSet { ch ->
        if (ch.wasAdded) observers[ch.element] = observe(ch.element)
        else if (ch.wasRemoved) observers.remove(ch.element)!!.kill()
    }
    return Observer {
        o.kill()
        observers.values.forEach { it.kill() }
        observers.clear()
    }
}

/**
 * Creates a list that holds the item if it is not null or is empty otherwise.
 */
fun <E> ReactiveValue<E?>.toSet() = setBinding<E>(if (now != null) mutableSetOf(now!!) else mutableSetOf()) {
    val o = observe { _, _, new ->
        clear()
        if (new != null) add(new)
    }
    addObserver(o)
}