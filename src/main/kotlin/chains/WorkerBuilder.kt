package chains

class WorkerBuilder<T> {
	private var blockOn: T.() -> Boolean = { true }
	var blockExec: T.() -> Unit = {}
	private var blockExcept: T.(Exception) -> Unit = {}

	fun on(block: T.() -> Boolean) {
		blockOn = block
	}

	@RunDsl
	fun exec(function: T.() -> Unit) {
		blockExec = function
	}

	fun except(function: T.(Exception) -> Unit) {
		blockExcept = function
	}

	fun build(): Worker<T> {
		return Worker(blockOn = blockOn, blockExec = blockExec, blockExcept = blockExcept)
	}
}

@DslMarker
annotation class RunDsl
