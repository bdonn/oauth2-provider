package utils.string

data class RandomStringSpec(val length: Int = DEFAULT_LENGTH, val pool: List<Char>? = null) {

    companion object {
        const val DEFAULT_LENGTH = 10
        val DEFAULT_POOL = ('a' .. 'z') + ('A' .. 'Z') + ('0' .. '9')
        val DEFAULT_SPEC = RandomStringSpec()
    }

    fun isValid(str: String): Boolean = str.length == this.length && this.pool?.containsAll(str.toList()) ?: true
}

interface RandomStringGenerator {
    fun generate(spec: RandomStringSpec?): String
}

object RandomStringGeneratorImpl: RandomStringGenerator{
    override fun generate(spec: RandomStringSpec?): String {
        val pool = spec?.pool ?: RandomStringSpec.DEFAULT_POOL
        return (1..(spec?.length ?: RandomStringSpec.DEFAULT_LENGTH))
            .map {
                kotlin.random.Random.nextInt(0, pool.size)
            }
            .map(pool::get)
            .joinToString("")
    }
}