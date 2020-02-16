package utils.string

import io.kotlintest.specs.FunSpec
import io.kotlintest.koin.KoinListener
import io.kotlintest.matchers.boolean.shouldBeFalse
import io.kotlintest.matchers.boolean.shouldBeTrue
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject

class RandomStringSpecTest: FunSpec({
    test("""
        Random String Spec Test #1
        - single character pool
    """) {
        val spec = RandomStringSpec(length = 5 , pool = listOf('a'))

        spec.isValid("aaaaa").shouldBeTrue()
        spec.isValid("aaaa").shouldBeFalse()
        spec.isValid("aaaaaa").shouldBeFalse()
        spec.isValid("aaaab").shouldBeFalse()
    }

    test("""
        Random String Spec Test #2
        - same multiple characters pool
    """) {
        val spec = RandomStringSpec(length = 5 , pool = listOf('a' , 'a'))

        spec.isValid("aaaaa").shouldBeTrue()
        spec.isValid("aaaa").shouldBeFalse()
        spec.isValid("aaaaaa").shouldBeFalse()
        spec.isValid("aaaab").shouldBeFalse()
    }

    test("""
        Random String Spec Test #3
        - multiple characters pool
    """) {
        val spec = RandomStringSpec(length = 5 , pool = listOf('a' , 'b'))

        spec.isValid("aaaaa").shouldBeTrue()
        spec.isValid("aaaa").shouldBeFalse()
        spec.isValid("aaaaaa").shouldBeFalse()
        spec.isValid("aaaab").shouldBeTrue()
    }

    test("""
        Random String Spec Test #4
        - multiple characters pool
        - default length
    """) {
        val pool = listOf('a' , 'b')
        val spec = RandomStringSpec(pool = pool)

        spec.isValid(
            (0 until RandomStringSpec.DEFAULT_LENGTH)
            .map {
                i -> if(i < pool.size) pool[i] else pool[i % pool.size]
            }
            .joinToString("")
        ).shouldBeTrue()
        spec.isValid((0 until RandomStringSpec.DEFAULT_LENGTH).map { 'a' }.joinToString("")).shouldBeTrue()
        spec.isValid((0 until RandomStringSpec.DEFAULT_LENGTH).map { 'b' }.joinToString("")).shouldBeTrue()
        spec.isValid((0 until RandomStringSpec.DEFAULT_LENGTH).map { 'c' }.joinToString("")).shouldBeFalse()
    }
})

class RandomStringTest: FunSpec() , KoinTest {

    private val testModule = module {
        single<RandomStringGenerator> { RandomStringGeneratorImpl }
    }

    override fun listeners() = listOf(KoinListener(testModule))

    private val randomStringGenerator by inject<RandomStringGenerator>()

    init {
        test("""
            Random String Generator Test #1
            - default spec
        """) {
            // [GIVEN]
            val spec = RandomStringSpec()

            // [WHEN]
            val result1 = randomStringGenerator.generate(null)
            val result2 = randomStringGenerator.generate(spec)

            // [THEN]
            RandomStringSpec.DEFAULT_SPEC.isValid(result1).shouldBeTrue()
            spec.isValid(result2).shouldBeTrue()
        }

        test("""
            Random String Generator Test #2
            - only length specified
        """) {
            // [GIVEN]
            val length = 8
            val spec = RandomStringSpec(length = length)

            // [WHEN]
            val result = randomStringGenerator.generate(spec)

            // [THEN]
            spec.isValid(result).shouldBeTrue()
        }

        test("""
            Random String Generator Test #3
            - only pool specified
        """) {
            // [GIVEN]
            val pool = ('a' .. 'z').toList()
            val spec = RandomStringSpec(pool = pool)

            // [WHEN]
            val result = randomStringGenerator.generate(spec)

            // [THEN]
            spec.isValid(result).shouldBeTrue()
        }

        test("""
            Random String Generator Test #4
            - all specified
        """) {
            // [GIVEN]
            val length = 8
            val pool = ('a' .. 'z').toList()
            val spec = RandomStringSpec(length = length , pool = pool)

            // [WHEN]
            val result = randomStringGenerator.generate(spec)

            // [THEN]
            spec.isValid(result).shouldBeTrue()
        }
    }
}