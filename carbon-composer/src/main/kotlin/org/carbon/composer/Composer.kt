package org.carbon.composer

abstract class Composable<T> {
    protected var context = Context()
    private lateinit var childComposer: Composable<T>
    protected fun callChild() = childComposer.invoke()

    private fun setChild(child: Composable<T>): Composable<T> {
        this.childComposer = child
        this.context = child.context.merge(this.context)
        return this
    }

    fun compose(child: Composable<T>): Composable<T> {
        setChild(child)
        return this
    }

    abstract fun invoke(): T
}

private class ExpressionComposer<T>(private val expression: Context.() -> T) : Composable<T>() {
    override fun invoke(): T = expression(context)
}

fun <T> compose(vararg composers: Composable<T>, expression: Context.() -> T): T =
    listOf(
        *composers,
        ExpressionComposer(expression)
    )
        .reduceRight { rootComposer, acc -> rootComposer.compose(acc) }
        .invoke()
