package cor

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch

class Chain<T>(
	val on: T.() -> Boolean = { true },
	val executors: List<IBaseExecutor<T>> = emptyList(),
	val isParallel: Boolean = false,
	val repeatIf: T.() -> Boolean = { true },
) : IBaseExecutor<T> {

	override suspend fun execute(context: T) {
		if (on(context)) {
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

	private suspend fun looper(context: T, function: suspend () -> Unit) {
		val startTime = System.currentTimeMillis()
		do {
			function()
		} while (repeatIf(context) && (System.currentTimeMillis() - startTime < MAX_LOOP_MS))
	}

	companion object {
		// Максимально допустимое время задержки в цикле loop
		private const val MAX_LOOP_MS = 3000
	}

}
