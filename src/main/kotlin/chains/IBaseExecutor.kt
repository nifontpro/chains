package chains

interface IBaseExecutor<T> {
	suspend fun execute(context: T)
}