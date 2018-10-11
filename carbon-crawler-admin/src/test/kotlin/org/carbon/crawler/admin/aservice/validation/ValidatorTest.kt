package org.carbon.crawler.admin.aservice.validation

import org.carbon.crawler.test.case
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * @author Soda 2018/10/08.
 */
@RunWith(Parameterized::class)
class ValidatorTest(
        private val input: Input,
        private val expected: Expected
) {
    data class Person(
            val name: String,
            val password: String,
            val password2: String,
            val emails: List<String>
    ) : Validated<Person> by PersonSchema

    object PersonSchema : Validated<Person> {
        override val def: Definition<Person> = {
            it.name should { and(it min 5, it max 10) } otherwise Specify("name")
            //it.password should { it minEq 8 } otherwise Specify("password")
            //it.password2 should { it minEq 8 } otherwise Specify("password2")
            val p2 = it.password2
            it.password should { it eq p2 } otherwise Specify("password")

            it.emails.forEachIndexed { i, email ->
                email should { it be Email } otherwise Specify("emails", at = i)
            }
        }
    }

    class Input(private val suite: String) {
        private var _name: String = "shota oda"
        private var _password: String = "password"
        private var _password2: String = "password"
        private var _emails: List<String> = listOf("shota@cbn.org")

        fun name(s: String): Input {
            _name = s
            return this
        }

        fun password(p: String): Input {
            _password = p
            return this
        }

        fun password2(p: String): Input {
            _password2 = p
            return this
        }

        fun emails(vararg s: String): ValidatorTest.Input {
            _emails = s.toList()
            return this
        }

        fun person() = Person(_name, _password, _password2, _emails)

        override fun toString(): String {
            return suite
        }
    }

    class Expected {
        private lateinit var _assert: (vr: ValidationResult) -> Unit

        fun toBe(assert: (ValidationResult) -> Unit): Expected {
            _assert = assert
            return this
        }

        val assert get() = _assert

        fun toBeObservance() = toBe { res -> assertTrue { res is ObservanceResult<*> } }

        fun toBeViolation(assertDetail: (vs: ViolationList) -> Unit = {}) = toBe { res ->
            assertTrue { res is ViolationResult }
            val vr = res as ViolationResult
            vr.violations.forEach(::println)
            assertDetail(vr.violations)
        }
    }

    companion object {
        @JvmStatic
        @Parameters(name = "{0}")
        fun people(): List<Array<*>> = listOf(
                case(Input("noc violation"), Expected().toBeObservance()),
                case(Input("single violation").name("too long name..."), Expected().toBeViolation()),
                case(Input("composite violation").password("password").password2("hogehoge"), Expected().toBeViolation()),
                case(Input("list violation").emails("email@valid.com", "email@..invalid"), Expected().toBeViolation())
        )
    }

    @Test
    fun validate() {
        val person = input.person()
        val result = Validator.validate(person)
        expected.assert(result)
    }
}