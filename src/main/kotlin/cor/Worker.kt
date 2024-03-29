package cor

class WorkerBuilder<T> {
	var blockRun: T.() -> Unit = {}
	private var blockOn: T.() -> Boolean = { true }

	fun on(block: T.() -> Boolean) {
		blockOn = block
	}

	fun exec(function: T.() -> Unit) {
		blockRun = function
	}

	fun build(): Worker<T> {
		return Worker(on = blockOn, runBlock = blockRun)
	}
}

class Worker<T>(
	val on: T.() -> Boolean,
	val runBlock: T.() -> Unit,
) : ICorExec<T> {

	override suspend fun execute(context: T) {

		if (on(context)) {
			runBlock(context)
		}
	}
}