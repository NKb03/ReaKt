/**
 *@author Nikolaus Knop
 */

package reaktive.list

import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature
import reaktive.random.Gen
import reaktive.util.testSameEffects

object ReactiveListSpec : Spek({
    Feature("a reactive list") {
        Scenario("operations on now") {
            val l = reactiveList(0, 1, 2)
            val test = mutableListOf(0, 1, 2)
            testSameEffects(test, l.now) {
                "add an element at tail" {
                    add(3)
                }
                "add an element at a specified index" {
                    add(2, 4)
                }
                "add an element at an index out of range" {
                    add(10, 3)
                }
                "add an element at a negative index" {
                    add(-10, 3)
                }
                "add an element at the head" {
                    add(0, 5)
                }
                "remove an element at a specified index" {
                    removeAt(4)
                }
                "remove at an index out of range" {
                    removeAt(100)
                }
                "remove at a negative index" {
                    removeAt(-1)
                }
                "remove an element" {
                    remove(5)
                }
                "remove an element not present in the list" {
                    remove(101)
                }
                "add many elements" {
                    addAll(listOf(5, 6, 7))
                }
                "add many elements at a specified index" {
                    addAll(4, listOf(8, 9, 10))
                }
                "add many elements at negative index" {
                    addAll(-1, listOf(1, 2, 3))
                }
                "add many elements at an index out of range" {
                    addAll(100, listOf(5, 6, 7))
                }
                "remove many elements" {
                    removeAll(listOf(4, 20, 40, 10))
                }
                "remove many elements that were not in the list before" {
                    removeAll(listOf(-101, -102))
                }
                "clearing" {
                    clear()
                }
            }
        }
        Scenario("map") {
            val list = reactiveList(0, 1, 2)
            val powers = list.map { it * it }
            fun expected() = list.now.map { it * it }
            testListBinding(powers, ::expected) {
                with(list.now) {
                    "add an element" {
                        add(4)
                    }
                    "add an element at the front" {
                        add(0, 5)
                    }
                    "add an element in the middle" {
                        add(2, 7)
                    }
                    "remove an element not present" {
                        remove(-100)
                    }
                    "remove an element" {
                        removeAt(3)
                    }
                    "clear" {
                        clear()
                    }
                    repeat(5) {
                        mutateRandomly("the source list", list.now, Gen.int(0, 1000))
                    }
                }
            }
        }
        Scenario("filter binding") {
            val list = reactiveList(1, 2, 3)
            val powers = list.filter { it % 2 == 0 }
            fun expected() = list.now.filter { it % 2 == 0 }
            testListBinding(powers, ::expected) {
                with(list.now) {
                    "add an even element" {
                        add(4)
                    }
                    "remove an even element" {
                        remove(2)
                    }
                    "add an odd element" {
                        add(7)
                    }
                    "remove an odd element" {
                        remove(1)
                    }
                    repeat(5) {
                        mutateRandomly("the source set", list.now, Gen.int(0, 1000))
                    }
                    "clear" {
                        clear()
                    }
                }
            }
        }
        if (false) Scenario("flatMap binding") {
            val part1 = reactiveList(1, 2, 3)
            val part2 = reactiveList(3, 4, 5)
            val part3 = reactiveList(5, 0, 5)
            val set = reactiveList(part1, part2, part3)
            val flat = set.flatMap { it }
            fun expected() = set.now.flatMap { it.now }
            testListBinding(flat, ::expected) {
                val part4 = reactiveList(6, 2, 7)
                "add new part" {
                    set.now.add(part4)
                }
                "then add element to it" {
                    part4.now.add(8)
                }
                "remove element from it" {
                    part4.now.remove(6)
                }
                "again remove it" {
                    set.now.remove(part4)
                }
                "remove another part" {
                    set.now.remove(part2)
                }
                "remove element from initial part" {
                    part1.now.remove(1)
                }
                "add element to initial part" {
                    part2.now.add(9)
                }
                val partGen = Gen.choose(part1, part2, part3, part4)
                repeat(10) {
                    mutateRandomly("a part", partGen.next().now, Gen.int(0, 1000))
                }
            }
        }
    }
})