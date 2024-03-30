package chains

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch

class Chain<T>(
	val blockOn: suspend T.() -> Boolean = { true },
	val executors: List<IBaseExecutor<T>> = emptyList(),
	val isParallel: Boolean = false,
	val repeatIf: suspend T.() -> Boolean = { true },
) : IBaseExecutor<T> {

	override suspend fun execute(context: T) {
		if (blockOn(context)) {
			if (!isParallel) {
				looper(context) {
					executors.forEach {
						it.execute(context)
					}
				}
			} else {
				looper(context) {
					coroutineScope {
						val jobs = executors.map {
							launch { it.execute(context) }
						}
						jobs.joinAll()
					}
				}
			}
		}
	}

	private suspend inline fun looper(context: T, function: () -> Unit) {
		do {
			function()
		} while (repeatIf(context))
	}

}
