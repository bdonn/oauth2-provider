package oauth2.context

import oauth2.request.OAuth2Request
import oauth2.request.Scope
import oauth2.request.State

interface OAuth2Context {
    val key: String
    val scope: Scope?
    val state: State?

    operator fun get(key: String): Any?
    operator fun set(key: String, value: Any)
}

data class SimpleMapContext(
    override val key: String,
    override val scope: Scope?,
    override val state: State?
): OAuth2Context {
    private val map = mutableMapOf<String, Any>()

    override operator fun get(key: String): Any? = when (key) { Scope.NAME -> scope; State.NAME -> state; else -> map[key] }

    override operator fun set(key: String, value: Any) { map[key] = value}
}

interface OAuth2ContextManager {
    fun saveContext(request: OAuth2Request): OAuth2Context?
    fun saveContext(request: OAuth2Request, property: Pair<String, Any>): OAuth2Context?
    fun saveContext(request: OAuth2Request, property: Map<String, Any>): OAuth2Context?
    fun retrieveContext(request: OAuth2Request): OAuth2Context?
    fun destroyContext(request: OAuth2Request)
}

object InMemoryContextManager: OAuth2ContextManager {

    private val map = mutableMapOf<String, OAuth2Context>()

    override fun saveContext(request: OAuth2Request): OAuth2Context? {

        // FIXME: context key
        val key = request.hashCode().toString()

        val context = SimpleMapContext(
            key = key,
            scope = request.params.find { it?.name == Scope.NAME } as Scope?,
            state = request.params.find { it?.name == State.NAME } as State?
        )

        return map.putIfAbsent(key, context)
    }

    override fun saveContext(request: OAuth2Request, property: Pair<String, Any>): OAuth2Context? {
        val context = this.saveContext(request)

        context?.set(property.first, property.second)

        return context
    }

    override fun saveContext(request: OAuth2Request, property: Map<String, Any>): OAuth2Context? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun retrieveContext(request: OAuth2Request): OAuth2Context? {

        // FIXME: context key
        val key = request.hashCode().toString()

        return map[key]
    }

    override fun destroyContext(request: OAuth2Request) {

        // FIXME: context key
        val key = request.hashCode().toString()

        map.remove(key)
    }
}

