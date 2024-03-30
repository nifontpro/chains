package chains

class Worker<T>(
	val blockOn: T.() -> Boolean,
	val blockExec: T.() -> Unit,
	val blockExcept: T.(Exception) -> Unit,
) : IBaseExecutor<T> {

	override suspend fun execute(context: T) {
		if (blockOn(context)) {
			try {
				blockExec(context)
			} catch (e: Exception) {
				blockExcept(context, e)
			}
		}
	}
}