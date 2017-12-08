package fi.minedu.oiva.backend.cache

import java.util.concurrent.{CompletionStage, CompletableFuture}
import java.util.function.BiFunction

import dispatch._
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.{Cache, CacheManager}

import scala.concurrent.{ExecutionContext, Promise}

trait CacheAware {
    @Autowired
    var cacheManager: CacheManager = null

    var cache: Cache = null
    def setCache(name: String) { this.cache = cacheManager.getCache(name) }

    def cache[V](key: String)(genValue: => Future[V])(implicit ec: ExecutionContext): Future[V] = {
        require(cache != null, "Cache must be set")
        val promise = Promise[V]()
        cache.get(key) match {
            case null =>
                val future = genValue
                future.onComplete { value =>
                    promise.complete(value)
                    value.map(x => cache.put(key, x))
                }
                future
            case existingValue =>
                Future.successful(existingValue.get().asInstanceOf[V])
        }
    }

    def cacheRx[V](key: String, versio: Integer, versionRequired: Boolean = false)(genValue: => CompletionStage[V]): CompletionStage[V] =
        if(versionRequired && null == versio) generateValue(key, false)(genValue)
        else cacheRx(withVersion(key, versio))(genValue)

    def cacheRx[V](key: String)(genValue: => CompletionStage[V]): CompletionStage[V] = {
        require(cache != null, "Cache must be set")
        cache.get(key) match {
            case null =>
                generateValue(key, true)(genValue)
            case existingValue =>
                val cf = new CompletableFuture[V]()
                cf.complete(existingValue.get().asInstanceOf[V])
                cf
        }
    }

    private def generateValue[V](key: String, writeCache: Boolean = true)(genValue: => CompletionStage[V]): CompletionStage[V] = {
        genValue.handle(new BiFunction[V, Throwable, V]() {
            override def apply(t: V, u: Throwable): V = {
                if(writeCache) cache.put(key, t)
                t
            }
        })
    }

    private def withVersion(key: String, versio: Integer) = if(null != versio) s"${key}:${versio}" else key
}
