/**
 *@author Nikolaus Knop
 */

package reaktive.event.mocks

import com.natpryce.hamkrest.equalTo
import reaktive.event.EventHandler
import reaktive.event.EventStream
import reaktive.util.shouldMatch

internal class TestEventHandler<T> : EventHandler<T> {
    private val firedEvents = mutableListOf<Pair<EventStream<T>, T>>()

    override fun invoke(stream: EventStream<T>, value: T) {
        firedEvents.add(stream to value)
    }

    fun testFired(events: List<Pair<EventStream<T>, T>>) {
        firedEvents shouldMatch equalTo(events)
        firedEvents.clear()
    }

    fun testFired(vararg events: Pair<EventStream<T>, T>) {
        testFired(events.asList())
    }
}