/**
 *@author Nikolaus Knop
 */

package reaktive.value.binding

import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.should.shouldMatch
import org.jetbrains.spek.api.dsl.*
import reaktive.binding.AbstractBindingsTestBody
import reaktive.value.now

internal fun <T> SpecBody.testBinding(
    binding: Binding<T>,
    expectedValue: () -> T,
    block: BindingTestBody<T>.() -> Unit
) {
    val body = BindingTestBody(this, binding, expectedValue)
    with(body) {
        "initially" { }
        block()
    }
}

internal class BindingTestBody<T>(
    spec: SpecBody,
    private val binding: Binding<T>,
    private val expectedValue: () -> T
) : AbstractBindingsTestBody(spec) {
    override fun ActionBody.check() {
        val expected = expectedValue()
        it("should be $expected") {
            binding.now shouldMatch equalTo(expected)
        }
    }
}